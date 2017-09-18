package cd.com.ermapper;

import android.widget.EditText;

import static cd.com.ermapper.R.id.entity;

/**
 * Created by CD on 9/15/2017.
 */

public class Relationship extends ShapeObject {

    ShapeObject obj1;
    ShapeObject obj2;


    public Relationship(EditText eName) {
        super(eName);
    }

    public ShapeObject getObj1() {
        return obj1;
    }

    public ShapeObject getObj2() {
        return obj2;
    }

    public void setObj1(ShapeObject obj1) {
        this.obj1 = obj1;
    }
}
