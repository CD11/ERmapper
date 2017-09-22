package cd.com.ermapper;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import static android.R.attr.x;
import static android.R.attr.y;

/**
 * Created by CD on 9/15/2017.
 */

public class ShapeObject {
    String name;
    EditText eName;
    Coordinates coordinates;
    ArrayList<Relationship> relationships;
    Boolean isPrimary;

    public ShapeObject(EditText eName, float x, float y, float w, float h){
        this.name = String.valueOf(eName.getText());
        this.eName = eName;
        this.coordinates = new Coordinates(x,y,w,h);
        this.relationships = new ArrayList<>();

        Log.d("CreatedObject", "Object Created " + String.valueOf(this.eName));
    }

    public ShapeObject(String name){
        this.name = name;

    }

    public boolean equals(Attribute a){
        if(a == null) return false;
        return a.name.equals(name);
    }

    public String toString(){ return name;}


    public Coordinates getCoordinates() {
        return coordinates;
    }

    public View getNameEdit() {
        return eName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinateX(float coordinateX) {
        this.coordinates.x = coordinateX;
        this.coordinates.width = coordinateX+200;
        Log.d("DrawingER", "new coord " + String.valueOf(this.coordinates.x));
    }

    public void setCoordinateY(float coordinateY) {
        this.coordinates.y = coordinateY;
        this.coordinates.height = coordinateY + 200;
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


    public void delete() {

    }

    public void setPrimary(boolean t) {
        this.isPrimary = t;
    }
}
