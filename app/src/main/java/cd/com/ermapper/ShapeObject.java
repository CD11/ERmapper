package cd.com.ermapper;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import static java.lang.String.valueOf;


/**
 * Created by CD on 9/15/2017.
 */

public abstract class ShapeObject implements Parcelable {
    /*
        This abstract class represents objects that can be drawn to the canvas

     */
    public static final String ENTITY = "Entity";
    public static final String ATTRIBUTE = "Attribute";
    public static final String RELATIONSHIP = "Relationship";
    private String name;
    private  EditText eName;
    private Coordinates coordinates;
    ArrayList<Relationship> relationships;

    // Constructors
    public ShapeObject(EditText eName, String name, float x, float y, float w, float h){
        this.name = "Name";
        this.eName = eName;
        this.coordinates = new Coordinates(x,y,w,h);
        this.relationships = new ArrayList<>();
        moveName();
    }

    public ShapeObject(String name){
        this.name = name;
        moveName();
    }

    public ShapeObject(Parcel in) {
        this.name = in.readString();
    }

    public String toString(){ return name;}

    // getters and setters
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public EditText getEditId() {
        return eName;
    }

    public void setName(String name) {
        Log.d("Changing name", this.name+" - >" + name );
        this.name = name;

    }

    public void setCoordinateX(float coordinateX) {
        this.coordinates.x = coordinateX;
    }

    public void setCoordinateY(float coordinateY) {
        this.coordinates.y = coordinateY;
    }


    public void setCoordinateW(float coordinateW) {
        this.coordinates.width = coordinateW;
    }

    public void setCoordinateH(float coordinateH) {
        this.coordinates.height = coordinateH;
    }


    public boolean hasRelationships() {
       return this.relationships.size() >0;
    }


    public void moveName() {
        float w =  eName.getWidth()/2;
        float h = eName.getHeight()/2;
        eName.setX(this.getCoordinates().centerX()-w);
        eName.setY(this.getCoordinates().centerY()-h);
        eName.setVisibility(View.VISIBLE);

    }


    public String getName() {
        return name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);


    }
    @Override
    public int describeContents() {
        return 0;
    }



}
