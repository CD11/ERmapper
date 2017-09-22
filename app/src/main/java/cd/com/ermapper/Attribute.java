package cd.com.ermapper;

import android.util.Log;
import android.widget.EditText;

/**
 * Created by cd on 2015-11-04.
 */
public class Attribute extends ShapeObject  {
    //This class represents a functional dependency attribute
    //Attribute equality is based on equality of the name string


    public Attribute(EditText eName, float x, float y, float w, float h){
        super(eName, x,y,w,h);
        this.name = String.valueOf(eName.getText());
        Log.d("CreatedObject", this.name);
    }

    public Attribute(String anAttributeName) {
     super(anAttributeName);
    }

    public boolean equals(Attribute a){
        if(a == null) return false;
        return a.name.equals(name);
    }

    public String toString(){ return name;}


    public int compareTo(Object arg) {
        if(!(arg instanceof Attribute)) return -1;
        Attribute a = (Attribute) arg;
        return name.compareTo(a.name);
    }


}
