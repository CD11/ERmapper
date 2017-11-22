package cd.com.ermapper.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.util.Log;
import android.widget.EditText;


import java.util.ArrayList;

import cd.com.ermapper.relations.AttributeSet;

/**
 * Created by me on 9/6/2017.
 */



public class Entity extends ShapeObject {
    /*
       This class represents an Entity in an ER diagram, it is represented by a square
       An Entity contains the name and attributes for a relation / relational table.
     */
    public static final float offset  =  80;
    private AttributeSet attr;
    private ArrayList<Entity> weak;
    private boolean isWeak;


    // Constructors
    public Entity(EditText eName, String name, float x, float y) {
        super(eName, name, x, y);
        attr = new AttributeSet();
        weak = new ArrayList<>();
        isWeak =false;
        setCoordinateX(x);
        setCoordinateY(y);
    }

    public Entity(String name) {
        super(null, name, 0, 0);
        attr = new AttributeSet();
        weak = new ArrayList<>();
    }

    protected Entity(Parcel in) {
        super(in);
        attr = new AttributeSet();
        weak = new ArrayList<>();
        attr = in.readTypedObject(AttributeSet.CREATOR);
        isWeak = in.readByte() != 0;
        weak = in.createTypedArrayList(Entity.CREATOR);

    }

    public static final Creator<Entity> CREATOR = new Creator<Entity>() {
        @Override
        public Entity createFromParcel(Parcel in) {
            return new Entity(in);
        }

        @Override
        public Entity[] newArray(int size) {
            return new Entity[size];
        }
    };
    // Draw the line to each Entity in the relationship
    public void drawLines(Canvas canvas, Paint paint){
        Coordinates c =  this.getCoordinates();// Center of the entity
        // Draw a line to each attribute
        for(Attribute o: this.attr.getElements()){
            Attribute a = o;
            canvas.drawLine(c.centerX(), c.centerY(), a.getCoordinates().centerX(), a.getCoordinates().centerY(), paint);
        }

    }

    // Draw the Shapes to each Entity in the relationship
    public void drawShape(Canvas canvas, Paint paint){
        Path p = new Path();
        Coordinates c =  this.getCoordinates();
        // Draw a line to each attribute
        for(Attribute o: this.attr.getElements()){
            o.drawShape(canvas, paint);
        }
        if(this.isWeak()){
            // draw weak entity
            canvas.drawRect(c.getX()-15,c.getY()-15,c.getWidth()+15,c.getHeight()+15,paint );
        }

        // draw
        canvas.drawRect(c.getX(),c.getY(),c.getWidth(),c.getHeight(),paint);


    }

    @Override
    public ArrayList<ShapeObject> getallobjects() {
        ArrayList<ShapeObject>s = new ArrayList<>();
        s.add(this);
        for(Attribute a: this.getAttr().getElements())
            s.addAll(a.getallobjects());
        s.addAll(this.getWeak());
        return s;
    }

    /////////////////////  Setters and getters
    public AttributeSet getAttr() {
            return attr;
        }
    public ArrayList<Entity> getWeak() {
            return  weak;
        }
    public boolean isWeak() {
        return isWeak;
    }
    public String toString(){
        return this.getName() + " " + this.getAttr().toString();
    }

    /*
        Fuctions: SetCoordinateX() and SetCoordinateY()
        Takes the mouse position and updates the current X and Y postions
        - the editText view has an inital width of 0 until it gets added to the Diagram view,
            therefore therefore  to try to maintain a consistent width when the object is created we
            give it a temporary width of 100
     */
    public void setCoordinateX(float coordinateX) {
        this.getCoordinates().x = coordinateX;
        this.getCoordinates().width = coordinateX + getEditId().getWidth() + offset;
        if(getEditId().getWidth() ==0 )
            this.getCoordinates().width = coordinateX + 100+ offset;

    }
    public void setCoordinateY(float coordinateY) {
        this.getCoordinates().y = coordinateY;
        this.getCoordinates().height = coordinateY + getEditId().getWidth()+offset;
        if(getEditId().getWidth() ==0 )
            this.getCoordinates().height = coordinateY + 100+ offset;

    }

    //  Add an attribute to the entity
    public void addAttribute(Attribute a) {
        this.attr.add(a);
        Log.d("edit", String.valueOf(a.getEditId())+" "  + String.valueOf(a.getEditId().getOnFocusChangeListener()));
    }

    /*
        If an entity is Strong, then it has weak entities that rely on it,
        the Relying entities get added to a list
    */
    public void addEntity(ShapeObject curr1) {
        if(!weak.contains(curr1))
            this.weak.add((Entity) curr1);
    }

    /* Function: setWeak()
       Purpose:  When an Entity is set to weak,  it cannot be identified  by itself, and its primary
                key is made up of its own primary key along with a foriegn key to its Strong Entity
                -> set any primary keys to part of the foreign key so that the system can draw them
                    different than primary keys
                -> check all relationships to Find all strong Entities,
                    a. add to each entites weak list
                    // to avoid duplication we will not store the primary keys of the strong relationships
                    in the attribute set of the weak entities;
     */
    public void setWeak(ArrayList<Relationship> relationships) {
        if(isWeak == false){
            isWeak = true;
            for(Attribute a:getAttr().getElements()) {
                if (a.isPrimary()) {
                    a.setForeign(true);
                    a.setPrimary(false);
                }
            }
        }else{
            isWeak = false;

        }

        // Search for strong entities and weak to strong list
        for(Relationship r: relationships) {
            if (r.getObj2().getClass() == Entity.class && r.getObj1().getClass() == Entity.class) {
                if (r.getObj1() == this) { // obj1 is weak, Obj2 is strong
                    if(!((Entity) (r.getObj2())).getWeak().contains(this))
                        ((Entity) (r.getObj2())).addEntity(this);
                    break;
                } else if (r.getObj2() == this) { // obj2 is weak, obj1 is strong
                    if(!((Entity)(r.getObj1())).getWeak().contains(this))
                        ((Entity) (r.getObj1())).addEntity(this);
                    break;
                }
            }
        }

    }




    // This function takes an the AttributesSet of an Entity
    // and sets any primary keys to foreign keys
    // This is used to convert N-ary relationships to Binary relationships
    public AttributeSet foreignAttrs() {
        AttributeSet newA = new AttributeSet();
        for(Attribute a: attr.getElements()){
            if( a.isPrimary()){
                a.setPrimary(false);
                a.setForeign(true);
            }
            newA.add(a);
        }
        return newA;
    }


    @Override
    public void remove() {
        this.attr.getElements().clear();
        this.weak.clear();
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeTypedObject(attr, i);
        parcel.writeByte((byte) (isWeak() ? 1 : 0));
        parcel.writeTypedList(weak);
    }


}
