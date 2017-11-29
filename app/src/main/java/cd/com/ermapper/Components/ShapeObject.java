package cd.com.ermapper.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by CD on 9/15/2017.
 */

public abstract class ShapeObject implements Parcelable {
    /*
        This abstract class represents objects that can be drawn to the canvas

     */
    private String name;
    private  EditText eName;
    private Coordinates coordinates;

    // Constructors
    public ShapeObject(EditText eName, String name, float x, float y, float w, float h){
        this.name = name;
        this.eName = eName;
        this.coordinates = new Coordinates(x,y,w,h);

    }
    public ShapeObject(EditText eName, String name, float x, float y){
        this.name = name;
        this.eName = eName;
        this.coordinates = new Coordinates(x,y,0,0);

    }

    public ShapeObject(String name){
        this.name = name;

    }

    public ShapeObject(Parcel in) {
        this.name = in.readString();
    }

    public String toString(){ return name;}

    // getters and setters
    public Coordinates getCoordinates() {
        return coordinates;
    }
    public EditText getEditId() {return eName; }
    public void setName(String name) {this.name = name;}
    public String getName() {
        return name;
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
    public boolean contains(float v, float v1) {
        return  this.getCoordinates().contains(v,v1);
    }
    public void setEditText(EditText e){
        eName = e;
    }

    // Updates the edit text name object to the middle of the shape object
    public void moveName() {
        float w = getEditId().getWidth();
        float h = getEditId().getHeight();
        if(w == 0 || h == 0){
            w = 100;
            h = 100;
        }
        eName.setX(this.getCoordinates().centerX()- w/2);
        eName.setY(this.getCoordinates().centerY()-h/2);
    }

    public abstract void remove();
    public abstract void drawLines(Canvas c, Paint p);
    public abstract void drawShape(Canvas c, Paint p);
    public abstract ArrayList<ShapeObject> getallobjects();
    public abstract boolean containsObj(ShapeObject curr);
    public abstract void removeObj(ShapeObject curr, RelativeLayout textLayer);
    public abstract void shapeToXML(XmlSerializer serializer) throws IOException;
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
