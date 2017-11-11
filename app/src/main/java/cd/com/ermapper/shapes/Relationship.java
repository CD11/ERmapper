package cd.com.ermapper.shapes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Parcel;

import android.view.Window;
import android.widget.EditText;
import java.util.ArrayList;
import cd.com.ermapper.relations.AttributeSet;


/**
 * Created by CD on 9/15/2017.
 */

public class Relationship extends ShapeObject {
    /*
        This class represents a connection between two objects represented by a  diamond
        if a connection is between two entites, defined by its name as well as the two objects
        It also represents a connection between two objects with just a line
     */
    ArrayList<Entity> objs;
    ArrayList<Cardinality> conns;
    ShapeObject obj1;
    ShapeObject obj2;
    AttributeSet attrs;

    public Relationship(String name, Entity e1, Entity e2) {
        super(null,name, 0, 0, 0,0);
        objs = new ArrayList<>();
        attrs = new AttributeSet();
        conns = new ArrayList<>();
        obj1 = e1;
        obj2 = e2;
    }
    public Relationship() {
        super(null,"relationship", 0, 0, 0,0);
        objs = new ArrayList<>();
        attrs = new AttributeSet();
        conns = new ArrayList<>();
    }


    public Relationship(Parcel in) {
        super(in.readString());
        obj1 = in.readParcelable(ShapeObject.class.getClassLoader());
        obj2 = in.readParcelable(ShapeObject.class.getClassLoader());
        attrs = in.readTypedObject(AttributeSet.CREATOR);
        objs = in.createTypedArrayList(Entity.CREATOR);
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
        p.setLastPoint(x,y); // Center of the diamond

        // Draw a line to each entity
        for(Attribute o: attrs.getElements()){
            p.lineTo(x,y);
            p.lineTo(o.getCoordinates().centerX(), o.getCoordinates().centerY());
        }
        for(Cardinality co: conns){
            p.lineTo(x,y);
            p.lineTo(co.getO().getCoordinates().centerX(), co.getO().getCoordinates().centerY());
            if(x < co.getO().getCoordinates().getX()) {
                co.getNum().setX(x - 175);
            } else if(x > co.getO().getCoordinates().getX()){
                co.getNum().setX(x + 175);

            }
            co.getNum().setY(co.getO().getCoordinates().centerY());
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
        float x = this.getCoordinates().centerX();
        float y = this.getCoordinates().centerY();
        float w = 50;
        float h = 50;
        Coordinates c = new Coordinates(x - w, y - h, x + w, y + h);
        return c.contains(v, v1);
    }

    public ShapeObject getObj1() {
        return obj1;
    }
    public ShapeObject getObj2() {
        return obj2;
    }

    public void addObj(Entity obj, Context c){
        if(!objs.contains(obj)) { // Check for duplicates
            objs.add(obj);
            conns.add(new Cardinality(c, obj));
        }


    }
    public void setObj1(ShapeObject obj1, Context c) {
        this.obj1 = obj1;
        this.setCoordinateX(obj1.getCoordinates().centerX());
        this.setCoordinateY(obj1.getCoordinates().centerY());
        if(obj1.getClass() == Entity.class)
            addObj((Entity)obj1, c);

    }

    public void setObj2(ShapeObject obj2, Context c) {
        this.obj2 = obj2;
        this.setCoordinateW(obj2.getCoordinates().centerX());
        this.setCoordinateH(obj2.getCoordinates().centerY());
        if(obj2.getClass() == Entity.class)
            addObj((Entity)obj2, c);
    }

    // Checks for valid objts and updates name coordinates
    public void update(){
        if(obj1 != null)
            this.setCoordinateX(obj1.getCoordinates().centerX());
          this.setCoordinateY(obj1.getCoordinates().centerY());
        if(obj2 != null)
            this.setCoordinateW(obj2.getCoordinates().centerX());
          this.setCoordinateH(obj2.getCoordinates().centerY());
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
    }


    /* checks that the connection is it is a relationship or a connection
     */
    public boolean isRelationship() {
        boolean result = false;
            try {
                boolean e1 = obj1.getClass() == Entity.class;
                boolean e2 = obj2.getClass() == Entity.class;
                boolean r1 = obj1.getClass() == Relationship.class;
                boolean r2 = obj2.getClass() == Relationship.class;
                if (e1 && e2 || e1&&r1 || e1&&r2 || e2&&r1 || e2&&r2)
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
        dest.writeTypedObject(attrs,flags);
        dest.writeTypedList(objs);
    }

    public void addAttribute(Attribute curr1) {
        attrs.add(curr1);
    }

    // Relationship properties
    public boolean isWeak() {
        for (ShapeObject o : objs) {
            if (o.getClass() == Entity.class)
                if (((Entity) o).isWeak())
                    return true;
        }

        return false;
    }

    public boolean isBinary(){
        return  objs.size() ==2;
    }

    public boolean isTernary(){
        return  objs.size() ==3;
    }
    public boolean isNary(){
        return  objs.size() >3;
    }
    public ArrayList<Cardinality> getTextObjs() {
        return conns;
    }

    public ArrayList<Entity> getObjs() {
        return objs;
    }

    public AttributeSet getAttrs() {
        return attrs;
    }
}
