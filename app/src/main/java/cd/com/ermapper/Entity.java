package cd.com.ermapper;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;
import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;

import static android.R.attr.centerX;
import static android.R.attr.centerY;
import static android.R.attr.width;
import static android.R.attr.x;
import static android.R.attr.y;

/**
 * Created by me on 9/6/2017.
 */



public class Entity extends ShapeObject{
   // private String name = "name";
    private AttributeSet primary;
    private AttributeSet attr;


    public Entity(EditText eName, float x, float y, float w, float h) {
        super(eName, x, y, w, h);
        this.name = eName.getText().toString();
        attr = new AttributeSet();
        primary = new AttributeSet();

        Log.d("CreatedObject", "Entity : " + String.valueOf(eName));

    }

    public boolean addAttribute(ShapeObject curr1, Relationship rCurr) {

        // set Primary attribute
        if(this.primary == null){
            primary.add((Attribute) curr1);
            curr1.setPrimary(true);
            curr1.getNameEdit().setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        }

        //Check for duplication
        if (!this.relationships.contains(rCurr) || !this.attr.contains((Attribute) curr1)) {
            curr1.relationships.add(rCurr);
            attr.add((Attribute) curr1);
            this.relationships.add(rCurr);
            return true;
        } else {
            return false;
        }
    }


    public AttributeSet getAttr() {
        return attr;
    }

    public Relation toRelation(){
        Relation r = new Relation(attr, primary);
        return r;
    }

    public AttributeSet getPrimary() {
        return primary;
    }
}
