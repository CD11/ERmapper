package cd.com.ermapper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;



public class DrawObjects extends View {

    ERDiagram d;
    int state;
    float startX, startY, endX, endY;
    ShapeObject curr;
    ShapeObject curr1;
    Relationship rCurr;
    Paint paint;

    public DrawObjects(Context context, ERDiagram diagram, int state){
        super(context);
        d = diagram;
        this.state = state;

        for(ShapeObject o: d.getObjects()){
            Log.d("DrawingER", "Object: " + String.valueOf(o.getClass()));
        }
    }

    public void setState(int state) {
     this.state = state;
     Log.d("setState", "setting"  + Integer.toString(state));
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        paint = new Paint();

        // If this is a new relationship, draw a line that follows the mouse
        if(state == 3 && rCurr != null ) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawLine(rCurr.getCoordinates().x, rCurr.getCoordinates().y, rCurr.getCoordinates().width, rCurr.getCoordinates().height, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawPath(rCurr.drawDiamond(), paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawPath(rCurr.drawDiamond(), paint);

        }
        // check each object in teh diagram
        for(ShapeObject e: d.getSortedObjects()) {
            Coordinates c = e.getCoordinates();
            Log.d("DrawingER", "Searching " + e.getClass());

            // draw  relationships objects
            if(e.getClass() == Relationship.class){
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawLine(c.x, c.y, c.width, c.height, paint);

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                canvas.drawPath(((Relationship)e).drawDiamond(), paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawPath(((Relationship)e).drawDiamond(), paint);
                Log.d("DrawingER", "Relationship");
            }

            // draw entities pbjects
            else if(e.getClass() == Entity.class) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                canvas.drawRect(c.x, c.y, c.width, c.height, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawRect(c.x, c.y, c.width, c.height, paint);


                Log.d("DrawingER", "Entity");
            }

            // draw attribute objects
            else if(e.getClass() == Attribute.class){
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                canvas.drawOval(c.x, c.y, c.width, c.height, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawOval(c.x, c.y, c.width, c.height, paint);
                Log.d("DrawingER", "Attribute");
            }

        }
        // make sure name are visible
        for(ShapeObject e: d.getObjects()) {
            e.getNameEdit().bringToFront();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
            int eventaction = event.getAction();
            Log.d("checkState", Integer.toString(state));


            switch (eventaction) {

                case MotionEvent.ACTION_DOWN: {
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
                    if (curr != null && (state != 3) && curr.getClass() != Relationship.class) {
                        curr.setCoordinateX(endX);
                        curr.setCoordinateY(endY);
                        float w =  curr.getNameEdit().getWidth()/2;
                        float h = curr.getNameEdit().getHeight()/2;
                        curr.getNameEdit().setX(curr.getCoordinates().centerX()-w);
                        curr.getNameEdit().setY(curr.getCoordinates().centerY()-h);
                        // make sure relationship lines follow its obhects
                        if(curr.hasRelationships()){
                            for(Relationship r : curr.relationships){
                                r.update();
                            }
                        }
                        // update the relationship line coordinates to follow the mouse
                    }else if (rCurr!= null && (state == 3)){
                      rCurr.setCoordinateW(endX);
                      rCurr.setCoordinateH(endY);

                    }
                    invalidate();
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    // touch down so check if the finger is on
                    endX = event.getX();
                    endY = event.getY();

                    //  Find connecting relationship
                    if(rCurr != null && curr.getClass() != Relationship.class) {
                        for (ShapeObject i : d.getObjects()) {
                            if (i.getCoordinates().contains(endX + 10, endY + 10) && !(i == curr)) {
                                curr1 = i;
                                Log.d("TouchEvent", "Connecting" + String.valueOf(i.getClass()));
                                if (curr.getClass() == Entity.class && curr1.getClass() == Attribute.class) {
                                    boolean success = ((Entity) curr).addAttribute(curr1, rCurr);
                                    if(!success){
                                        d.deleteR(rCurr);
                                    }
                                }


                                 else if (curr1.getClass() == Entity.class && curr.getClass() == Attribute.class)
                                    ((Entity) curr1).addAttribute(curr, rCurr);

                                rCurr.setObj1(curr);
                                rCurr.setObj2(curr1);
                                float w =  rCurr.getNameEdit().getWidth()/2;
                                float h = rCurr.getNameEdit().getHeight()/2;
                                rCurr.getNameEdit().setX(rCurr.getCoordinates().centerX()-w);
                                rCurr.getNameEdit().setY(rCurr.getCoordinates().centerY()-h);
                                rCurr.getNameEdit().setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                    // set final position of objects (except relationships)
                    if (curr != null && state != 3 && curr.getClass() != Relationship.class) {
                        Log.d("TouchEvent", "contains");
                        curr.setCoordinateX(endX);
                        curr.setCoordinateY(endY);
                        float w =  curr.getNameEdit().getWidth()/2;
                        float h = curr.getNameEdit().getHeight()/2;
                        curr.getNameEdit().setX(curr.getCoordinates().centerX()-w);
                        curr.getNameEdit().setY(curr.getCoordinates().centerY()-h);

                        if(curr.hasRelationships()){
                            for(Relationship r : curr.relationships){
                                r.update();
                            }
                        }
                    }


                    invalidate();
                    curr = null;
                    curr1 = null;
                    rCurr = null;
                    break;

                }
            }
            return true;
        }

    public void setRelationship(ShapeObject relationship) {
        this.rCurr = (Relationship) relationship;
    }
}
