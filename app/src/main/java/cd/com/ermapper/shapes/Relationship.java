package cd.com.ermapper.shapes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Parcel;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.net.CookieHandler;
import java.util.ArrayList;

import cd.com.ermapper.R;
import cd.com.ermapper.relations.AttributeSet;

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
    ArrayList<ShapeObject> objs;
    ShapeObject obj1;
    ShapeObject obj2;

    private EditText left = null;
    private EditText right = null;

    AttributeSet attrs;
    Coordinates c;


    public Relationship() {
        super(null,"relationship", 0, 0, 0,0);
        objs = new ArrayList<>();
        attrs = new AttributeSet();
    }


    public Relationship(Parcel in) {
        super(in.readString());
        obj1 = in.readParcelable(ShapeObject.class.getClassLoader());
        obj2 = in.readParcelable(ShapeObject.class.getClassLoader());
        c = in.readParcelable(Coordinates.class.getClassLoader());
        attrs = in.readTypedObject(AttributeSet.CREATOR);
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

    // Draw the lines to each Entity in the relationship
    public Path drawLines(){
        Path p = new Path();
        Coordinates c =  this.getCoordinates();
        float x = c.centerX();
        float y = c.centerY();
        p.setLastPoint(x,y);

        // Draw a line to each entity
        for(ShapeObject o:objs){
            p.lineTo(x,y);
            p.lineTo(o.getCoordinates().centerX(), o.getCoordinates().centerY());

        }

        return p;

    }
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

    public boolean contains(float v, float v1) {
        float x =this.getCoordinates().centerX();
        float y =this.getCoordinates().centerY();
        float w = 50;
        float h = 50;
        Coordinates c = new Coordinates(x-w, y-h, x+w, y+h);


        return c.contains(v,v1);
    }

    public ShapeObject getObj1() {
        return obj1;
    }
    public ShapeObject getObj2() {
        return obj2;
    }

    public void addObj(ShapeObject obj){
        if(!objs.contains(obj)) // Check for duplicates
          objs.add(obj);


    }
    public void setObj1(ShapeObject obj1) {
        this.obj1 = obj1;
        this.setCoordinateX(obj1.getCoordinates().centerX());
        this.setCoordinateY(obj1.getCoordinates().centerY());
    }

    public void setObj2(ShapeObject obj2) {
        this.obj2 = obj2;
        this.setCoordinateW(obj2.getCoordinates().centerX());
        this.setCoordinateH(obj2.getCoordinates().centerY());
    }

    // Checks for valid objts and updates name coordinates
    public void update(){

        if(obj1 != null)
            this.setObj1(obj1);
        if(obj2 != null)
            this.setObj2(obj2);
        if(this.getEditId() != null) {
            this.getEditId().setX(this.getCoordinates().centerX());
            this.getEditId().setY(this.getCoordinates().centerY());
        }

    }
    //  For cardinality purposes
    // Initiates Edit texts  for name and cardinality and positions them in their proper place
    public void setTexts(Context c){
        // Give name edit
        EditText e = new EditText(c);
        e.setBackgroundColor(Color.BLACK);
        this.setEditText(e);
        // Give cardinality
        left = new EditText(c);
        right = new EditText(c);
        left.setText("0");
        right.setText("0");
        movecardinaity();
    }
    public void movecardinaity() {
        if (getEditId() == null) {
            return;
        }
        float w = getEditId().getWidth();
        float h = getEditId().getHeight();
        if(w == 0 || h == 0){
            w = 100;
            h = 100;
        }
        left.setX(this.getCoordinates().getX() - 150);
        left.setY(this.getObj1().getCoordinates().centerY()-55);
        left.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        right.setX(this.getCoordinates().getWidth()+150);
        right.setY(this.getObj2().getCoordinates().centerY()-55);
        right.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
    }
    public EditText getleft(){
        return left;
    }
    public EditText getRight(){
        return right;
    }

    /* checks that the connection is between two entities objects.
        returns true if yes
        returns false if no, and hides the name object.
     */
    public boolean isRelationship() {
        boolean result = false;
            try {
                if (obj1.getClass() == Entity.class && obj2.getClass() == Entity.class)
                    result = true;
            }catch (NullPointerException e){
                result = false;
            }
        return  result;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(obj1, flags);
        dest.writeParcelable(obj2, flags);
        dest.writeParcelable(c, flags);
        dest.writeTypedObject(attrs,flags);
    }

    public void addAttribute(Attribute curr1) {
        attrs.add(curr1);
    }
}
