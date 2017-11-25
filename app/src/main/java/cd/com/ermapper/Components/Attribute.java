package cd.com.ermapper.Components;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.support.annotation.DrawableRes;
import android.text.TextPaint;
import android.util.Log;
import android.widget.EditText;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

import cd.com.ermapper.Logic.ERDraw;
import cd.com.ermapper.R;

/**
 * Created by cd on 2015-11-04.
 */
public class Attribute extends ShapeObject {
    //This class represents a functional dependency attribute
    //Attribute equality is based on equality of the name string
     private boolean primary;
    private boolean foreign;
    private ArrayList<Attribute> values;   // this is an arraylist and not an attribute set because it becomes circluar if it is an attributeset


    public Attribute(EditText eName, String name, float x, float y){
        super(eName,name, x,y);
        primary = false;
        values = new ArrayList<>();
        setCoordinateX(x);
        setCoordinateY(y);
    }

    public Attribute(String anAttributeName)
    {
     super(anAttributeName);
        primary = false;
        values = new ArrayList<>();
    }
    public Attribute(EditText editId, Attribute o) {
        super(o.getEditId(),o.getName(), o.getCoordinates().getX(),o.getCoordinates().getY());
        primary = false;
        values = new ArrayList<>();
        setCoordinateX(o.getCoordinates().getX());
        setCoordinateY(o.getCoordinates().getY());
    }


    public Attribute(Parcel in) {
        super(in);
        primary = in.readByte() != 0;
        values = in.createTypedArrayList(Attribute.CREATOR);
    }

    public static final Creator<Attribute> CREATOR = new Creator<Attribute>() {
        @Override
        public Attribute createFromParcel(Parcel in) {
            return new Attribute(in);
        }

        @Override
        public Attribute[] newArray(int size) {
            return new Attribute[size];
        }
    };


    @Override
    public ArrayList<ShapeObject> getallobjects() {
        ArrayList<ShapeObject>s = new ArrayList<>();
        s.add(this);
        for(Attribute a: this.getValuesSet().getElements())
            s.add(a);

        return s;
    }

    // Draw the lines to each Entity in the relationship
    public void drawLines(Canvas canvas, Paint paint){
        Coordinates c =  this.getCoordinates();
        // Draw a line to each attribute
        for(Attribute o: this.getValuesSet().getElements()) {
            canvas.drawLine(c.centerX(), c.centerY(), o.getCoordinates().centerX(), o.getCoordinates().centerY(), paint);
        }
    }
    // Draw the shape to each Entity in the relationship
    public void drawShape(Canvas canvas, Paint paint){
        Coordinates c =  this.getCoordinates();
        // Draw a line to each attribute
        for(Attribute o: this.getValuesSet().getElements()) {
             canvas.drawOval(o.getCoordinates().getX(), o.getCoordinates().getY(),o.getCoordinates().getWidth(), o.getCoordinates().getHeight(),paint );
        }
        // draw
        canvas.drawOval(c.getX(),c.getY(),c.getWidth(),c.getHeight(),paint);
    }


    @Override
    public boolean containsObj(ShapeObject curr) {
        for(Attribute a: values)
            if(a.equals(curr)) return true;
        return false;

    }

    @Override
    public void remove() {
        values.clear();
    }

    @Override
    public void removeObj(ShapeObject curr) {
        if(values.contains(curr)) values.remove(curr);
    }
    // Getters and Setters
    public String toString(){ return this.getName()+" ";}
    public void setCoordinateX(float coordinateX) {
        this.getCoordinates().setX(coordinateX);
        float w = coordinateX + getEditId().getWidth()+ 80;
        if(getEditId().getWidth() == 0)
            w += 100;
        this.getCoordinates().setWidth(w);
    }
    public void setCoordinateY(float coordinateY) {
        this.getCoordinates().setY(coordinateY);
        this.getCoordinates().setHeight(coordinateY + 150);

    }
    /*
        Checks to see what if object is primary or not and sets it to the opposing,
        updates the edit text to allow and for the text to be underlined
     */
    public void setPrimary() {
        if (primary == false){
            primary = true;
            if(getEditId() != null)
             getEditId().getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }else {
            if(getEditId() != null)
            getEditId().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            primary = false;
        }
    }
    public void setPrimary(boolean b) {
        primary = b;
        if(getEditId() != null) {
            if (b == true)
                getEditId().getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            else
                getEditId().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
    }
    public boolean isPrimary(){
        return primary;
    }
    public void setForeign(Boolean b) {
        foreign = b;
        Paint dash = new Paint();
        dash.setColor(Color.BLACK);
        if(getEditId()!= null) {
            if (b == true) {
                getEditId().setBackground(Drawable.createFromPath("drawable/dash_line.xml"));
            }
            else{
                dash.setStyle(Paint.Style.FILL);
            }


        }
    }
    public boolean isForeign() {return foreign;}
    public void addAttribute(Attribute curr) {
        values.add(curr);
    }
    public ArrayList<Attribute> getValues() {
        return values;
    }
    public AttributeSet getValuesSet() {
        AttributeSet as = new AttributeSet();
        for(Attribute a: values){
            as.add(a);
        }
        return as;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getName());
        parcel.writeByte((byte) (primary ? 1 : 0));
        parcel.writeTypedList(values);
    }



    public void shapeToXML(XmlSerializer serializer) throws IOException {
        try {
            serializer.startTag("","Attribute");
            serializer.attribute("", "name", this.getName());
            serializer.attribute("", "coordinates", this.getCoordinates().toString());
            if (this.isPrimary())
                serializer.attribute("", "primary", "true");
            if (this.isForeign())
                serializer.attribute("", "foriegn", "true");
            if (!this.getValues().isEmpty()) {
                serializer.startTag(" ", "multiAttribute");
                for (Attribute subA : this.getValues()) {
                    subA.shapeToXML(serializer);
                }
                serializer.endTag("", "multiAttribyte");

            }
            serializer.endTag("","Attribute");
        } catch (IOException exception) {
            Log.d("Attribute XML Exception", String.valueOf(exception));
            throw exception;
        }
    }



}
