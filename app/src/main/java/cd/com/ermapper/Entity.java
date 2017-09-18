package cd.com.ermapper;

import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;
import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;

import static android.R.attr.centerX;
import static android.R.attr.centerY;

/**
 * Created by me on 9/6/2017.
 */



public class Entity extends ShapeObject{
   // private String name = "name";
    private Attribute primary;
    private AttributeSet attr;


    public Entity(EditText eName, Attribute primary, AttributeSet list, Coordinates rect) {
        super(eName);
        this.name = eName.getText().toString();
        this.primary = primary;
        this.attr = list;
        this.coordinates = rect;

        Log.d("CreatedObject", "Entity : " + String.valueOf(eName));

    }


    public Entity(EditText eName) {
        super(eName);
        this.name = eName.getText().toString();
        this.coordinates = new Coordinates(50,50,200,200);

        Log.d("Created Object", "Entity : " + String.valueOf(eName));
    }



}
