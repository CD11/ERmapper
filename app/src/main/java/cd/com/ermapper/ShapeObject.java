package cd.com.ermapper;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by CD on 9/15/2017.
 */

public class ShapeObject {
    String name;
    EditText eName;
    Coordinates coordinates;


    public ShapeObject(EditText eName){
        this.name = String.valueOf(eName.getText());
        this.eName = eName;
        this.coordinates = new Coordinates(50,50,100,80);

        Log.d("CreatedObject", "Object Created " + String.valueOf(this.eName));
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
    }

    public void setCoordinateY(float coordinateY) {
        this.coordinates.y = coordinateY;
        this.coordinates.height = coordinateY + 200;
    }

    public void update(ShapeObject curr) {
        this.name = curr.name;
        this.coordinates = curr.coordinates;
    }



}
