package cd.com.ermapper.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.util.Log;
import android.widget.EditText;
import android.widget.RelativeLayout;


import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by me on 9/6/2017.
 */



public class Entity extends ShapeObject {
    /*
       This class represents an Entity in an ER diagram, it is represented by a square
       An Entity contains the name and attributes for a relation / relational table.
     */
    public static final float offset  =  80;
    private AttributeSet attr;
    private boolean isWeak;


    // Constructors
    public Entity(EditText eName, String name, float x, float y) {
        super(eName, name, x, y);
        attr = new AttributeSet();
        isWeak =false;
        setCoordinateX(x);
        setCoordinateY(y);
    }

    public Entity(String name) {
        super(null, name, 0, 0);
        attr = new AttributeSet();
    }

    protected Entity(Parcel in) {
        super(in);
        attr = new AttributeSet();
        attr = in.readTypedObject(AttributeSet.CREATOR);
        isWeak = in.readByte() != 0;

    }

    public static final Creator<Entity> CREATOR = new Creator<Entity>() {
        @Override
        public Entity createFromParcel(Parcel in) {
            return new Entity(in);
        }

        @Override
        public Entity[] newArray(int size) {
            return new Entity[size];
        }
    };
    // Draw the line to each Entity in the relationship
    public void drawLines(Canvas canvas, Paint paint){
        Coordinates c =  this.getCoordinates();// Center of the entity
        // Draw a line to each attribute
        for(Attribute o: this.attr.getElements()){
            Attribute a = o;
            o.drawLines(canvas, paint);
            canvas.drawLine(c.centerX(), c.centerY(), a.getCoordinates().centerX(), a.getCoordinates().centerY(), paint);
        }

    }

    // Draw the Shapes to each Entity in the relationship
    public void drawShape(Canvas canvas, Paint paint){
        Path p = new Path();
        Coordinates c =  this.getCoordinates();
        // Draw a line to each attribute
        for(Attribute o: this.attr.getElements()){
            if(this.isWeak && o.isPrimary()){
                o.setPrimary(false);
                o.setForeign(true);
            }
            o.drawShape(canvas, paint);
        }
        if(this.isWeak()){
            // draw weak entity
            canvas.drawRect(c.getX()-15,c.getY()-15,c.getWidth()+15,c.getHeight()+15,paint );
        }

        // draw
        canvas.drawRect(c.getX(),c.getY(),c.getWidth(),c.getHeight(),paint);


    }

    @Override
    public boolean containsObj(ShapeObject curr) {
        for(Attribute a: attr.getElements())
            if(a.equals(curr)) return true;
        return false;

    }

    @Override
    public void removeObj(ShapeObject curr, RelativeLayout textLayer) {
        if(curr.getClass() == Attribute.class)
            if(attr.contains((Attribute) curr)) attr.remove((Attribute) curr);
      /*  if(curr.getClass() == Entity.class)
            if(weak.contains(curr)) weak.remove(curr);
*/
    }

    @Override
    public void remove() {
        this.attr.getElements().clear();
    }

    @Override
    public ArrayList<ShapeObject> getallobjects() {
        ArrayList<ShapeObject>s = new ArrayList<>();
       // s.add(this);
        for(Attribute a: this.getAttr().getElements()) {
            s.addAll(a.getallobjects());
            s.add(a);
        }
        //s.addAll(this.getWeak());
        return s;
    }

    /////////////////////  Setters and getters
    public AttributeSet getAttr() {
            return attr;
        }
    public boolean isWeak() {
        return isWeak;
    }
    public String toString(){
        return this.getName() + " " + this.getAttr().toString();
    }

    /*
        Fuctions: SetCoordinateX() and SetCoordinateY()
        Takes the mouse position and updates the current X and Y postions
        - the editText view has an inital width of 0 until it gets added to the Diagram view,
            therefore therefore  to try to maintain a consistent width when the object is created we
            give it a temporary width of 100
     */
    public void setCoordinateX(float coordinateX) {
        this.getCoordinates().x = coordinateX;
        this.getCoordinates().width = coordinateX + getEditId().getWidth() + offset;
        if(getEditId().getWidth() ==0 )
            this.getCoordinates().width = coordinateX + 100+ offset;

    }
    public void setCoordinateY(float coordinateY) {
        this.getCoordinates().y = coordinateY;
        this.getCoordinates().height = coordinateY + getEditId().getWidth()+offset;
        if(getEditId().getWidth() ==0 )
            this.getCoordinates().height = coordinateY + 100+ offset;

    }

    //  Add an attribute to the entity
    public void addAttribute(Attribute a) {
        if(this.isWeak && a.isPrimary()){
            a.setPrimary(false);
            a.setForeign(true);
        }
        this.attr.add(a);


    }

    /*
        If an entity is Strong, then it has weak entities that rely on it,
        the Relying entities get added to a list
    */

    /* Function: setWeak()
       Purpose:  When an Entity is set to weak,  it cannot be identified  by itself, and its primary
                key is made up of its own primary key along with a foriegn key to its Strong Entity
                -> set any primary keys to part of the foreign key so that the system can draw them
                    different than primary keys
                -> check all relationships to Find all strong Entities,
                    a. add to each entites weak list
                    // to avoid duplication we will not store the primary keys of the strong relationships
                    in the attribute set of the weak entities;
     */
    public void setWeak(Boolean weak) {
        isWeak = weak;
        for (Attribute a : getAttr().getElements()) {
            if (a.isPrimary() && isWeak == true) {
                a.setPrimary(false);
                a.setForeign(true);
            }
            else if(a.isForeign() && isWeak ==false){
                a.setPrimary(true);
                a.setForeign(false);
            }
        }

    }

    // This function takes an the AttributesSet of an Entity
    // and sets any primary keys to foreign keys
    // This is used to convert N-ary relationships to Binary relationships
    public AttributeSet foreignAttrs() {
        AttributeSet newA = new AttributeSet();
        for(Attribute a: attr.getElements()){
            if( a.isPrimary()){
                a.setPrimary(false);
                a.setForeign(true);
                newA.add(a);
            }
        }
        return newA;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeTypedObject(attr, i);
        parcel.writeByte((byte) (isWeak() ? 1 : 0));
    }

    @Override
    public void isValid() {
        if (this.getAttr() == null)
            throw new NullPointerException(this.getName() + " attribute set is null");
        if (this.getAttr().isEmpty())
            throw new NullPointerException(this.getName() + " primmary set is empty");
        if (this.getPrimary() != null && !this.getAttr().containsAll(this.getPrimary()))
            throw new NullPointerException(this.getName() +(" ERROR: PRIMARY KEY MUST BE A SUBSET OF THE ATTRIBUTES"));
    }

    @Override
    public void shapeToXML(XmlSerializer serializer) throws IOException {
        try {
            serializer.startTag(null, "Entity");
            if (!this.isWeak())
                serializer.attribute("", "Name", this.getName());
            serializer.attribute("", "coordinates", this.getCoordinates().toString());

            if(!this.getAttr().isEmpty()){
                //serializer.startTag("", "Attributes");
                for (Attribute a : this.getAttr().getElements()) {
                    a.shapeToXML(serializer);
                }
                //serializer.endTag(" ", "Attributes");
            }
            /*if(!this.getWeak().isEmpty()) {
                serializer.startTag(" ", "weakEntites");
                for (Entity subE : this.getWeak()) {
                   this.shapeToXML(serializer);
                }
                serializer.endTag("","weakEntities");
            }
            serializer.endTag(null,"Entity");
        */
        }catch (IOException exception){
            Log.d("Entity XML Exception", String.valueOf(exception));
            throw exception;
        }


    }


    public AttributeSet getPrimary() {
        AttributeSet primary = new AttributeSet();
        for(Attribute a: this.attr.getElements()){
            if(a.isPrimary()||a.isForeign()){
                primary.add(a);
            }
        }
        return primary;
    }
}
