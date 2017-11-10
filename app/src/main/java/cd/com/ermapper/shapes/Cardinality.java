package cd.com.ermapper.shapes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by CD on 11/9/2017.
 */

public class Cardinality {

    EditText num;
    ShapeObject o;

    private ArrayList<Relationship> relationships;
    public Cardinality(Context c, ShapeObject o){
      num = new EditText(c);
      num.setText("0");
      this.o = o;

    }


    public EditText getNum() {
        return num;
    }

    public ShapeObject getO() {
        return o;
    }
}
