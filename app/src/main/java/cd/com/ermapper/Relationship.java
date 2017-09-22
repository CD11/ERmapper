package cd.com.ermapper;

import android.graphics.Path;
import android.widget.EditText;

import static android.R.attr.path;


/**
 * Created by CD on 9/15/2017.
 */

public class Relationship extends ShapeObject {

    ShapeObject obj1;
    ShapeObject obj2;
    Coordinates c;



    public Relationship(EditText nameEdit, ShapeObject curr, ShapeObject curr1) {
        super(nameEdit, curr.coordinates.x, curr.coordinates.y, curr1.coordinates.x, curr1.coordinates.y);
        obj1 = curr;
        obj2 = curr1;
    }

    public Relationship(EditText et) {
        super(et, 0, 0, 0, 0);
    }

    public Path drawDiamond(){
       Coordinates c =  this.getCoordinates();
       float x =c.centerX();
       float y =c.centerY();
       float w = 100;
       float h = 100;

        Path p = new Path();
        p.setLastPoint(x-w, y);
        p.lineTo(x,y+h);
        p.lineTo(x+w,y );
        p.lineTo(x,y-h);
        p.lineTo(x-w, y);
        return p;
    }
    public ShapeObject getObj1() {
        return obj1;
    }
    public ShapeObject getObj2() {
        return obj2;
    }

    public void setObj1(ShapeObject obj1) {
        this.obj1 = obj1;
        this.setCoordinateX(obj1.getCoordinates().x + 40);
        this.setCoordinateY(obj1.getCoordinates().y + 40);
    }

    public void setObj2(ShapeObject obj2) {
        this.obj2 = obj2;
        this.setCoordinateW(obj2.getCoordinates().x + 40);
        this.setCoordinateH(obj2.getCoordinates().y + 40);
    }

    public void update(){
        if(obj1 != null)
            this.setObj1(obj1);
        if(obj2 != null)
            this.setObj2(obj2);


        eName.setX(coordinates.centerX());
        eName.setY(coordinates.centerY());

    }
}
