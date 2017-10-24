package cd.com.ermapper;

import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Created by CD on 9/15/2017.
 */

public class Relationship extends ShapeObject {
    /*
        This class represents a connection between two objects represented by a  diamond
        if a connection is between two entites, defined by its name as well as the two objects
        It also represents a connection between two objects with just a line
     */

    ShapeObject obj1;
    ShapeObject obj2;
    Coordinates c;

    public Relationship(EditText nameEdit, String name, ShapeObject curr, ShapeObject curr1) {
        super(nameEdit,"relationship", curr.getCoordinates().x, curr.getCoordinates().y, curr1.getCoordinates().x, curr1.getCoordinates().y);
        obj1 = curr;
        obj2 = curr1;
    }
    public Relationship(String name, ShapeObject curr, ShapeObject curr1) {
        super(name);
        obj1 = curr;
        obj2 = curr1;
    }
    public Relationship(EditText et, String name) {
        super(et,"name", 0, 0, 0, 0);
    }

    public Relationship(Parcel in) {
        super(in.readString());
        obj1 = in.readParcelable(ShapeObject.class.getClassLoader());
        obj2 = in.readParcelable(ShapeObject.class.getClassLoader());
        c = in.readParcelable(Coordinates.class.getClassLoader());
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(obj1, flags);
        dest.writeParcelable(obj2, flags);
        dest.writeParcelable(c, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Relationship> CREATOR = new Creator<Relationship>() {
        @Override
        public Relationship createFromParcel(Parcel in) {
            return new Relationship(in);
        }

        @Override
        public Relationship[] newArray(int size) {
            return new Relationship[size];
        }
    };

    // This gets the path from the coordinates of the objects that will be draw the diamond to the canvas
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

    public Path drawOuterDiamond() {
            Coordinates c =  this.getCoordinates();
            float x =c.centerX();
            float y =c.centerY();
            float w = 130;
            float h = 130;

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
        Log.d("Fds", obj1.getClass().toString());
        this.obj1 = obj1;
        this.setCoordinateX(obj1.getCoordinates().x + 40);
        this.setCoordinateY(obj1.getCoordinates().y + 40);
    }

    public void setObj2(ShapeObject obj2) {
        Log.d("Fds", obj2.getClass().toString());
        this.obj2 = obj2;
        this.setCoordinateW(obj2.getCoordinates().x + 40);
        this.setCoordinateH(obj2.getCoordinates().y + 40);
    }

    public void update(){
        if(obj1 != null)
            this.setObj1(obj1);
        if(obj2 != null)
            this.setObj2(obj2);


        this.getEditId().setX(this.getCoordinates().centerX());
        this.getEditId().setY(this.getCoordinates().centerY());

    }


    /* checks that the connection is between two entities objects.
        returns true if yes
        returns false if no, and hides the name object.
     */
    public boolean isRelationship() {
        boolean result = false;
        try{
         if (obj1.getClass() == Entity.class && obj2.getClass() == Entity.class)
           result =  true;
            this.getEditId().setTextColor(BLACK);
            this.getEditId().setVisibility(View.VISIBLE);
        }catch (NullPointerException exception) {
            result =  false;
            this.getEditId().setHint("");
            this.getEditId().setTextColor(WHITE);
            this.getEditId().setX(0);
            this.getEditId().setY(0);
            this.getEditId().setVisibility(View.INVISIBLE);
        }
        return  result;
    }

}
