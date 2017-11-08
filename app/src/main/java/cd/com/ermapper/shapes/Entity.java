package cd.com.ermapper.shapes;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Parcel;
import android.util.Log;
import android.widget.EditText;


import java.util.ArrayList;

import cd.com.ermapper.relations.AttributeSet;
import cd.com.ermapper.relations.Relation;


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


    public Entity(EditText eName, String name, float x, float y) {
        super(eName, name, x, y);
        attr = new AttributeSet();
        weak = new ArrayList<>();
        isWeak =false;
        setCoordinateX(x);
        setCoordinateY(y);
        moveName();
    }


    protected Entity(Parcel in) {
        super(in);
        attr = new AttributeSet();
        weak = new ArrayList<>();
        attr = in.readTypedObject(AttributeSet.CREATOR);
        isWeak = in.readByte() != 0;
        weak = in.createTypedArrayList(Entity.CREATOR);
        Log.d("Parcel Enitity", toString());

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


    // add Attributes to the entity
    public void addAttribute(Attribute a) {
        this.attr.add(a);
    }


    public String toString(){
        return this.getName() + " " + this.getAttr().toString();
    }
    public AttributeSet getAttr() {
        return attr;
    }

    public Relation toRelation(){
        AttributeSet primary = new AttributeSet();
        AttributeSet attributes = new AttributeSet();
        /* Search through entity attributes,  if the attribute is part a primary adds it to the
            primary attribute set, else adds to the atttribute attribute set.
            - > allows ensures there is no duplication.
        */
        for(Attribute a: attr.getElements()) {
            if (a.isPrimary()) {
                primary.add(a);
            }
            attributes.add(a);
        }

        Relation r = new Relation(attributes, primary, this.getName());
        return r;
    }

    public void setCoordinateX(float coordinateX) {
        this.getCoordinates().setX(coordinateX);
        this.getCoordinates().setWidth(coordinateX + getEditId().getWidth() + offset);
        if(getEditId().getWidth() ==0 )
            this.getCoordinates().setWidth(coordinateX + 100+ offset);

    }

    public void setCoordinateY(float coordinateY) {
        this.getCoordinates().setY(coordinateY);
        this.getCoordinates().setHeight(coordinateY + getEditId().getWidth()+offset);
        if(getEditId().getWidth() ==0 )
            this.getCoordinates().setHeight(coordinateY + 100+ offset);

    }

    public boolean isWeak() {
        return isWeak; // if weak is empty then it is not weak
    }
    public void setWeak(ArrayList<Relationship> relationships) {
        if(isWeak == false){
            isWeak = true;
            for(Attribute a:getAttr().getElements()) {
                if (a.isPrimary()) {
                    a.getEditId().getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                }
            }
        }else{
            isWeak = false;

        }

        for(Relationship r: relationships) {
            if (r.getObj2().getClass() == Entity.class && r.getObj1().getClass() == Entity.class) {
                if (r.getObj1() == this) {
                    if(!((Entity) (r.getObj2())).getWeak().contains(this))
                        ((Entity) (r.getObj2())).addEntity(this);

                    break;
                } else if (r.getObj2() == this) {
                    if(!((Entity)(r.getObj1())).getWeak().contains(this))
                        ((Entity) (r.getObj1())).addEntity(this);
                    break;
                }
            }
        }

    }

    public ArrayList<Entity> getWeak() {
        return  weak;
    }

    public void addEntity(ShapeObject curr1) {
        if(!weak.contains(curr1))
            this.weak.add((Entity) curr1);
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeTypedObject(attr, i);
        parcel.writeByte((byte) (isWeak() ? 1 : 0));
        parcel.writeTypedList(weak);
    }


}
