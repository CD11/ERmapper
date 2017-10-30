package cd.com.ermapper;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Parcel;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by cd on 2015-11-04.
 */
public class Attribute extends ShapeObject {
    //This class represents a functional dependency attribute
    //Attribute equality is based on equality of the name string
    public static final float width  =  80;
    public static final float height   = 150;
    private boolean primary;
    private ArrayList<Attribute> values;   // this is an arraylist and not an attribute set because it becomes circluar if it is an attributeset


    public Attribute(EditText eName, String name, float x, float y){
        super(eName,name, x,y);
        primary = false;
        values = new ArrayList<>();
        setCoordinateX(x);
        setCoordinateY(y);
        moveName();
    }

    public Attribute(String anAttributeName)
    {
     super(anAttributeName);
        primary = false;
        values = new ArrayList<>();
    }

    public Attribute(Parcel in) {
        super(in);
        primary = in.readByte() != 0;
        values = in.createTypedArrayList(Attribute.CREATOR);
    }

    public static final Creator<Attribute> CREATOR = new Creator<Attribute>() {
        @Override
        public Attribute createFromParcel(Parcel in) {
            return new Attribute(in);
        }

        @Override
        public Attribute[] newArray(int size) {
            return new Attribute[size];
        }
    };



    // Getters and Setters

    public String toString(){ return this.getName()+" ";}

    public void setCoordinateX(float coordinateX) {
        this.getCoordinates().x = coordinateX;
        float w = coordinateX + getEditId().getWidth()+ width;
        if(getEditId().getWidth() == 0)
            w += 100;
        this.getCoordinates().width = w;
    }

    public void setCoordinateY(float coordinateY) {
        this.getCoordinates().y = coordinateY;
        this.getCoordinates().height = coordinateY + height;

    }

    public void setPrimary() {
        if (primary == false){
            primary = true;
            getEditId().getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }else {
            getEditId().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            primary = false;
        }
    }
    public boolean isPrimary(){
        return primary;
    }



    public void addAttribute(Attribute curr) {
        values.add(curr);
    }

    public ArrayList<Attribute> getValues() {
        return values;
    }

    public FunctionalDependency toFD() {
        AttributeSet key = new AttributeSet();
        AttributeSet v = getValuesSet();
        key.add(this);
        FunctionalDependency fd = new FunctionalDependency(key, v, this.getName());
        return fd;
    }

    public AttributeSet getValuesSet() {
        AttributeSet as = new AttributeSet();
        for(Attribute a: values){
            as.add(a);
        }
        return as;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getName());
        parcel.writeByte((byte) (primary ? 1 : 0));
        parcel.writeTypedList(values);
    }



}
