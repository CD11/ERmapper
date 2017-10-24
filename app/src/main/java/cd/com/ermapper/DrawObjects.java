package cd.com.ermapper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.R.attr.max;
import static android.R.color.white;
import static android.graphics.Color.WHITE;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


public class DrawObjects extends View {
    /* this class Draws the objects onto a canvas
       It takes in an ERDiagram, and then looks at the coordinates of each object to draw it in place
       - > When drawing a conneciton it also lets you see the line and connect two objects

     */
    final int MAX_DURATION = 500;//constant for defining the time duration between the click that can be considered as double-tap
    int clickCount = 0;
    long startTime;    //variable for storing the time of first click
    long duration;    //variable for calculating the total time
    ERDiagram d;
    int state;
    float startX, startY, endX, endY;
    ShapeObject curr;
    ShapeObject curr1;
    Relationship rCurr;
    Paint paint;

    public DrawObjects(Context context, ERDiagram diagram, int state){
        super(context);
        this.d = diagram;
        this.state = state;

    }

    public void setState(int state) {
     this.state = state;
     Log.d("setState", "setting"  + Integer.toString(state));
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();

        // If this is a new relationship, draw a line that follows the mouse
        if (state == 3 && rCurr != null) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawLine(rCurr.getCoordinates().x, rCurr.getCoordinates().y, rCurr.getCoordinates().width, rCurr.getCoordinates().height, paint);

            if(rCurr.isRelationship()) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(WHITE);
                canvas.drawPath(rCurr.drawDiamond(), paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawPath(rCurr.drawDiamond(), paint);
            }

        }
        // check each object in the diagram
        for (ShapeObject e : d.getSortedObjects()) {
            Coordinates c = e.getCoordinates();
            Log.d("DrawingER", "Searching " + e.getClass());

            // draw  relationships objects
            if (e.getClass() == Relationship.class) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawLine(c.x, c.y, c.width, c.height, paint);
                e.getEditId().setVisibility(INVISIBLE);
                if(((Relationship)e).isRelationship()) {
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(WHITE);
                    canvas.drawPath(((Relationship) e).drawDiamond(), paint);

                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.BLACK);
                    canvas.drawPath(((Relationship) e).drawDiamond(), paint);
                    e.getEditId().setVisibility(VISIBLE);
                    Log.d("DrawingER", "Relationship");

                    if(((Entity)((Relationship)e).getObj1()).isWeak() ||((Entity)((Relationship)e).getObj2()).isWeak()){
                        canvas.drawPath(((Relationship) e).drawOuterDiamond(), paint);
                    }
                }
            }

            // draw entities pbjects
            else if (e.getClass() == Entity.class) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(WHITE);
                canvas.drawRect(c.x, c.y, c.width, c.height, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawRect(c.x, c.y, c.width, c.height, paint);
                if(((Entity) e).isWeak()){
                    canvas.drawRect(c.x-15, c.y-15, c.width+15, c.height+15, paint);

                }


                Log.d("DrawingER", "Entity");
            }

            // draw attribute objects
            else if (e.getClass() == Attribute.class) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(WHITE);
                canvas.drawOval(c.x, c.y, c.width, c.height, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawOval(c.x, c.y, c.width, c.height, paint);
                Log.d("DrawingER", "Attribute");
            }

        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
            this method allows for you to move objects around the screen.
            it checks where your mouse has touched the screen,  and updates the coordinates of the
            object selected by following the mouse.
         */

           int eventaction = event.getAction();

            switch (eventaction) {

                case MotionEvent.ACTION_DOWN: {
                    /* check for double click */
                    duration = 0;
                    clickCount++;
                    if(clickCount == 1) {
                        startTime = System.currentTimeMillis(); // only start if  first click
                    }

                    Log.d("clicks ", String.valueOf(clickCount));
                    long time = System.currentTimeMillis() - startTime;
                    duration = duration + time;
                    Log.d("clicks ", String.valueOf(clickCount) +" " + duration);
                    if(duration > MAX_DURATION || clickCount >2){
                        clickCount =0;
                        duration = 0;
                    }
                    boolean doublepress = false;
                    //TODO: double click attribute to set primary key
                    if(clickCount == 2 && state != 3 && curr != null && duration <= MAX_DURATION) {
                        if (curr.getClass() == Entity.class) {
                            ((Entity) curr).setWeak();
                        }
                        if (curr.getClass() == Attribute.class) {
                            ((Attribute) curr).setPrimary();
                        }
                        if (curr.getClass() == Relationship.class) {
                            d.deleteO(curr);
                        }
                        doublepress = true;

                        clickCount = 0;
                        duration = 0;
                    }

                    Log.d("Click object", String.valueOf(clickCount));
                    /*** check for moving ****/
                    // Remember where we started (for dragging)
                    startX = event.getX();
                    startY = event.getY();

                    // check of an object was clicked
                    for (ShapeObject i : d.getObjects()) {
                        if (i.getCoordinates().contains(startX, startY)) {
                            curr = i;
                            // set x,y for a new relationship
                            if (state == 3 && rCurr != null) {
                                rCurr.setCoordinateX(startX);
                                rCurr.setCoordinateY(startY);

                            }
                        }
                    }

                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    // touch down so check if the finger is on
                    endX = event.getX();
                    endY = event.getY();

                    // update coordinates of the objects
                    if (curr != null && (state != 3) && curr.getClass() != Relationship.class ) {
                        curr.setCoordinateX(endX);
                        curr.setCoordinateY(endY);
                        curr.moveName();

                       for(Relationship r : d.getRelationships()){
                            r.update();
                        }

                        // update the relationship line coordinates to follow the mouse
                    } else if (rCurr != null && (state == 3)) {
                        rCurr.setCoordinateW(endX);
                        rCurr.setCoordinateH(endY);
                        invalidate();

                    }
                    invalidate();
                    break;
                }

                case MotionEvent.ACTION_UP: {


                    // get even position
                    endX = event.getX();
                    endY = event.getY();

                         /* Get Movement */

                        //  Find connecting relationship
                        if (rCurr != null && curr != null) {
                            if (curr.getClass() != Relationship.class) {
                                for (ShapeObject i : d.getObjects()) {
                                    if (i.getCoordinates().contains(endX + 10, endY + 10) && !(i == curr)) {
                                        curr1 = i;

                                        Log.d("TouchEvent", "Connecting" + String.valueOf(i.getClass()));
                                        if (curr.getClass() == Entity.class && curr1.getClass() == Attribute.class) {
                                            ((Entity) curr).addAttribute((Attribute) curr1);


                                        } else if (curr1.getClass() == Entity.class && curr.getClass() == Attribute.class) {
                                            ((Entity) curr1).addAttribute((Attribute) curr);
                                        }

                                        rCurr.setObj1(curr);
                                        rCurr.setObj2(curr1);
                                        rCurr.update();
                                        if (rCurr.getObj1() != null && rCurr.getObj2() != null) {
                                            // only add the relationship to the diagram if it is valid
                                            d.addObject(rCurr);
                                        } else {
                                            // remove the edit text for an invalid relationship
                                            rCurr.getEditId().setVisibility(INVISIBLE);
                                            rCurr = null;
                                        }


                                        break;
                                    }
                                }
                            }


                        // set final position of objects (except relationships)
                        if (curr != null && state != 3 && curr.getClass() != Relationship.class) {
                            curr.setCoordinateX(endX);
                            curr.setCoordinateY(endY);
                            this.getContext();
                            curr.moveName();
                            for (Relationship r : d.getRelationships()) {
                                    r.update();
                            }
                        }
                        invalidate();
                        curr = null;
                        curr1 = null;
                        rCurr = null;
                        break;
                    }
                }
            }
            return true;
        }


    public void setRelationship(ShapeObject relationship) {
        /*  Relationships do not get added to the object list until they are assigned 2 objects
            This takes the new relationship object from ERDRAW
         */
        this.rCurr = (Relationship) relationship;
    }
}
