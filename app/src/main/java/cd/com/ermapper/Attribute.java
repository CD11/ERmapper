package cd.com.ermapper;

import android.util.Log;
import android.widget.EditText;

/**
 * Created by cd on 2015-11-04.
 */
public class Attribute extends ShapeObject  {
    //This class represents a functional dependency attribute
    //Attribute equality is based on equality of the name string


    public Attribute(EditText eName){
        super(eName);
        this.name = String.valueOf(eName.getText());
        this.coordinates = new Coordinates(50,50,200,150);
        
        Log.d("CreatedObject", this.name);
    }

}
