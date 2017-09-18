package cd.com.ermapper;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


import org.w3c.dom.Attr;

import java.util.ArrayList;

import static android.R.attr.button;
import static android.R.attr.y;


public class DrawObjects extends View {

    ERDiagram d;
    float startX, startY, endX, endY;
    ArrayList<Entity> entity;
    ArrayList<Attribute> attributes;
    ArrayList<ShapeObject> objects;
    ShapeObject curr;

    public DrawObjects(Context context, ERDiagram diagram){
        super(context);
        d = diagram;
        entity = diagram.getEntities();
        attributes = diagram.getAttributes();
        objects = diagram.getObjects();

        for(ShapeObject o: objects){
            Log.d("DrawingER", "Object: " + String.valueOf(o.getClass()));
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        d.update(entity);
        d.updateA(attributes);



        for(ShapeObject e: objects) {
            Coordinates c = e.getCoordinates();
            Log.d("DrawingER", "Searching " + e.getClass());

            if(e.getClass() == Entity.class) {
                canvas.drawRect(c.x, c.y, c.width, c.height, paint);
                Log.d("DrawingER", "Entity");
            }
            else if(e.getClass() == Attribute.class){
                canvas.drawOval(c.x, c.y, c.width, c.height, paint);
                Log.d("DrawingER", "Attribute");
            }
            else if(e.getClass() == Relationship.class){
                Relationship r  = (Relationship) e;
                float startX = r.getObj1().getCoordinates().x;
                float startY = r.getObj1().getCoordinates().y;
                float endX = r.getObj2().getCoordinates().x;
                float endY = r.getObj2().getCoordinates().y;

                canvas.drawLine(startX, startY, endX, endY, paint);
                Log.d("DrawingER", "Relationship");
            }



        }

        for(ShapeObject e: objects) {
            e.getNameEdit().bringToFront();
        }

    }




}

