package cd.com.ermapper.Logic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import cd.com.ermapper.R;
import cd.com.ermapper.shapes.Attribute;
import cd.com.ermapper.shapes.Cardinality;
import cd.com.ermapper.shapes.Coordinates;
import cd.com.ermapper.shapes.Entity;
import cd.com.ermapper.shapes.Relationship;
import cd.com.ermapper.shapes.ShapeObject;

import static android.graphics.Color.WHITE;


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
    RelativeLayout textLayer;
    Paint paint;
    Context c;

    public DrawObjects(Context context, ERDiagram diagram, int state, RelativeLayout textLayer) {
        super(context);
        this.d = diagram;
        this.state = state;
        this.c = context;
        this.textLayer = textLayer;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        // If this is a new relationship, draw a line that follows the mouse
        if (state == 3 && rCurr != null) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawLine(rCurr.getCoordinates().getX(), rCurr.getCoordinates().getY(), rCurr.getCoordinates().getWidth(), rCurr.getCoordinates().getHeight(), paint);

            if (rCurr.isRelationship()) {

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(WHITE);
                canvas.drawPath(rCurr.drawDiamond(), paint);

            }
        }
        // check each object in the diagram
        for (ShapeObject e : d.getSortedObjects()) {
            Coordinates c = e.getCoordinates();

            // draw  relationships objects
            if (e.getClass() == Relationship.class) {


                if (((Relationship) e).isRelationship()) {
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawPath(((Relationship) e).drawLines(), paint);
                    paint.setColor(Color.BLACK);

                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(WHITE);
                    canvas.drawPath(((Relationship) e).drawDiamond(), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.BLACK);
                    canvas.drawPath(((Relationship) e).drawDiamond(), paint);
                    if (((Relationship) e).isWeak()) {
                        canvas.drawPath(((Relationship) e).drawOuterDiamond(), paint);
                    }

                } else {
                    canvas.drawLine(c.getX(), c.getY(), c.getWidth(), c.getHeight(), paint);
                }
            }

            // draw entities pbjects
            else if (e.getClass() == Entity.class) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(WHITE);
                canvas.drawRect(c.getX(), c.getY(), c.getWidth(), c.getHeight(), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawRect(c.getX(), c.getY(), c.getWidth(), c.getHeight(), paint);
                if (((Entity) e).isWeak()) {
                    canvas.drawRect(c.getX() - 15, c.getY() - 15, c.getWidth() + 15, c.getHeight() + 15, paint);

                }
            }

            // draw attribute objects
            else if (e.getClass() == Attribute.class) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(WHITE);
                canvas.drawOval(c.getX(), c.getY(), c.getWidth(), c.getHeight(), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawOval(c.getX(), c.getY(), c.getWidth(), c.getHeight(), paint);
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
        boolean doublepress = false;
        switch (eventaction) {
            case MotionEvent.ACTION_DOWN: {
                /* check for double click */
                duration = 0;
                clickCount++;
                /*** check for moving ****/
                // Remember where we started (for dragging)
                startX = event.getX();
                startY = event.getY();

                if (clickCount == 1) {
                    startTime = System.currentTimeMillis(); // only start if  first click
                }
                long time = System.currentTimeMillis() - startTime;
                duration = duration + time;
                // check for clicks that cannot be double clicks
                if (duration > MAX_DURATION || clickCount > 2) {
                    clickCount = 0;
                    duration = 0;
                }


                // check of an object was clicked
                for (ShapeObject i : d.getObjects()) {
                    if (i.contains(startX + 10, startY + 10)) {
                        curr = i;
                        // set x,y for a new relationship
                        if (state == 3 && rCurr != null) {
                            rCurr.setCoordinateX(startX);
                            rCurr.setCoordinateY(startY);

                        } else if (state == 5 && curr != null) {
                            textLayer.removeView(curr.getEditId());
                            if(curr.getClass() == Relationship.class){
                                for(Cardinality cc :((Relationship)curr).getTextObjs()){
                                    textLayer.removeView(cc.getNum());
                                }
                            }
                            curr.remove();
                            d.deleteO(curr);
                            curr = null;
                            invalidate();
                            state = 4; // set state to select to prevent accidental deletions
                            break;
                        }
                    }

                    // check for a double click on an object
                    if (clickCount == 2 && state != 3 && curr != null && duration <= MAX_DURATION) {
                        // if the curr object is an entity set it to weak
                        if (curr.getClass() == Entity.class) {
                            ((Entity) curr).setWeak(d.getRelationships());
                            invalidate();
                        }
                        // if the curr object is an attribute set it to primary
                        else if (curr.getClass() == Attribute.class) {
                            ((Attribute) curr).setPrimary();
                        }
                        doublepress = true;

                        clickCount = 0;
                        duration = 0;
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

                    for (Relationship r : d.getRelationships()) {
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
                if (rCurr != null && curr != null ) {
                    for (ShapeObject i : d.getObjects()) {
                        // Object clicked
                        if (i.contains(endX + 10, endY + 10) && !(i == curr)) {
                            curr1 = i;
                            // Check Entities
                            if (curr.getClass() == Entity.class && curr1.getClass() == Attribute.class) {  // if curr is an entity, add attribute curr1 to entity
                                ((Entity) curr).addAttribute((Attribute) curr1);
                            } else if (curr1.getClass() == Entity.class && curr.getClass() == Attribute.class) {// if curr1 is an entity, add attriubte curr to entity
                                ((Entity) curr1).addAttribute((Attribute) curr);
                                // Check Attribute
                            } else if (curr1.getClass() == Attribute.class && curr.getClass() == Attribute.class) { // if attribute is multivalued
                                //  check for other values already being stored.
                                if (((Attribute) curr).getValues().isEmpty() && !((Attribute) curr1).getValues().isEmpty()) {
                                    ((Attribute) curr1).addAttribute((Attribute) curr);
                                } else {
                                    ((Attribute) curr).addAttribute((Attribute) curr1);
                                }
                                //Check Relationship
                            } else if (curr1.getClass() == Attribute.class && curr.getClass() == Relationship.class) { // Add Attributes to a relationship
                                ((Relationship) curr).addAttribute((Attribute) curr1);

                            } else if (curr1.getClass() == Relationship.class && curr.getClass() == Attribute.class) { // Add Attributes to a relationship
                                ((Relationship) curr1).addAttribute((Attribute) curr);

                            } else if (curr1.getClass() == Entity.class && curr.getClass() == Relationship.class) {
                                rCurr.setObj1(curr, c);
                                ((Relationship) curr).addObj((Entity) curr1, c);
                                // adds the edit text to the layer so you can edit cardinality
                                textLayer.addView(((Relationship) curr).addObj((Entity) curr1, c));

                                break;
                            } else if (curr1.getClass() == Relationship.class && curr.getClass() == Entity.class) {
                                rCurr.setObj2(curr1, c);
                                textLayer.addView(((Relationship) curr1).addObj((Entity) curr, c));

                                break;

                            }
                            rCurr.setObj2(curr1, c);
                            rCurr.setObj1(curr, c);


                            rCurr.update();
                            if (rCurr.isRelationship()) {
                                // only add the relationship to the diagram if it is valid
                                rCurr.setTexts(c);
                                textLayer.addView(rCurr.getEditId());
                                for (Cardinality e : rCurr.getTextObjs()) {
                                    textLayer.addView(e.getNum());
                                }


                            }
                            d.addObject(rCurr);
                            break;
                        }
                    }
                    // set final position of objects (except relationships)
                    if (curr != null && state != 3) {
                        curr.setCoordinateX(endX);
                        curr.setCoordinateY(endY);
                        this.getContext();
                        curr.moveName();
                    }


                    invalidate();


                    break;
                }
                curr = null;
                curr1 = null;
                rCurr = null;
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
