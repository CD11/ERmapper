package cd.com.ermapper;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;
import android.widget.EditText;


/**
 * Created by me on 9/6/2017.
 */



public class Entity extends ShapeObject{
    /*
       This class represents an Entity in an ER diagram, it is represented by a square
       An Entity contains the name and attributes for a relation / relational table.
     */
    public static final float offset  =  150;
    private AttributeSet primary;
    private AttributeSet attr;
    private boolean weak;


    public Entity(EditText eName, String name, float x, float y) {
        super(eName, name, x, y, x+offset, y+offset);
        attr = new AttributeSet();
        primary = new AttributeSet();
        weak = false;
    }

    public Entity(String name) {
        super(name);
        attr = new AttributeSet();
        primary = new AttributeSet();
    }


    protected Entity(Parcel in) {
        super(in);
        primary = new AttributeSet();
        attr = new AttributeSet();
        primary = in.readTypedObject(AttributeSet.CREATOR);
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
        this.primary = p;
        this.attr = k;
    }

    // add Attributes to the entity
    public void addAttribute(ShapeObject curr1) {
        // set Primary attribute
        if(this.primary.isEmpty()){
            this.primary.add((Attribute) curr1);
            curr1.getEditId().getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }else {
            this.attr.add((Attribute) curr1);
        }
        Log.d("attr", this.getAttr().toString()+ " " + this.getPrimary().toString());

    }

    public String toString(){
        return this.getName() + " " + this.getAttr().toString();
    }
    public AttributeSet getAttr() {
        return attr;
    }

    public Relation toRelation(){
        Relation r = new Relation(attr, primary, this.getName());
        return r;
    }

    public void setCoordinateX(float coordinateX) {
        this.getCoordinates().x = coordinateX;
        this.getCoordinates().width = coordinateX + offset;
    }

    public void setCoordinateY(float coordinateY) {
        this.getCoordinates().y = coordinateY;
        this.getCoordinates().height = coordinateY + offset;
    }

    public AttributeSet getPrimary() {
        return primary;
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
        parcel.writeTypedObject(primary, i);
        parcel.writeTypedObject(attr, i);
    }

}
