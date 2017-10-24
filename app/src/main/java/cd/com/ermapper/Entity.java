package cd.com.ermapper;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;
import android.widget.EditText;

import static android.R.id.primary;


/**
 * Created by me on 9/6/2017.
 */



public class Entity extends ShapeObject{
    /*
       This class represents an Entity in an ER diagram, it is represented by a square
       An Entity contains the name and attributes for a relation / relational table.
     */
    public static final float offset  =  30;
    private AttributeSet attr;
    private boolean weak;


    public Entity(EditText eName, String name, float x, float y) {
        super(eName, name, x, y, 250 , 250);
        attr = new AttributeSet();
        weak = false;
        moveName();
    }


    protected Entity(Parcel in) {
        super(in);
        attr = new AttributeSet();
        attr = in.readTypedObject(AttributeSet.CREATOR);
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

    public Entity(String name, AttributeSet p, AttributeSet k) {
        super(name);
        this.attr = k;
    }

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
        for(Attribute a: attr.getElements()){
            if(a.isPrimary() && !primary.contains(a)){
                primary.add(a);
            }
            if (!attributes.contains(a))
                attributes.add(a);

        }
        Relation r = new Relation(attributes, primary, this.getName());
        return r;
    }

    public void setCoordinateX(float coordinateX) {
        this.getCoordinates().x = coordinateX;
        this.getCoordinates().width = coordinateX + getEditId().getWidth() +offset;
    }

    public void setCoordinateY(float coordinateY) {
        this.getCoordinates().y = coordinateY;
        this.getCoordinates().height = coordinateY +  getEditId().getWidth()+ offset;
    }

    public boolean isWeak() {
        return weak;
    }
    public void setWeak() {
        if(weak == false){
            weak = true;
        }else{
            weak = false;
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeTypedObject(attr, i);
    }

}
