package cd.com.ermapper;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

import static android.R.attr.x;
import static android.R.attr.y;
import static android.R.id.primary;

/**
 * Created by cd on 2015-11-04.
 */
public class Attribute extends ShapeObject{
    //This class represents a functional dependency attribute
    //Attribute equality is based on equality of the name string
    public static final float width  =  50;
    public static final float height   = 200;
    public boolean primary;

    public Attribute(EditText eName, String name, float x, float y){
        super(eName,name, x,y, 150, 200);
        primary = false;
    }

    public Attribute(String anAttributeName)
    {
     super(anAttributeName);
        primary = false;
    }

    public Attribute(Parcel in) {
        super(in);
        primary = in.readByte() != 0;
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

    public String toString(){ return this.getName();}

    public void setCoordinateX(float coordinateX) {
        this.getCoordinates().x = coordinateX;
        this.getCoordinates().width = coordinateX + getEditId().getWidth()+ width;
    }

    public void setCoordinateY(float coordinateY) {
        this.getCoordinates().y = coordinateY;
        this.getCoordinates().height = coordinateY + height;
    }

    public void setPrimary() {
        if (primary == false){
            primary = true;
            getEditId().getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }else
            primary = true;
    }
    public boolean isPrimary(){
        return primary;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getName());
        parcel.writeByte((byte) (primary ? 1 : 0));
    }

}
