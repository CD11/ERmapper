package cd.com.ermapper.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
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

        // draw a solid line
        if(this.primary){
            EditText e = this.getEditId();
            canvas.drawLine(e.getX(),e.getY() +e.getHeight()+5, e.getX() + e.getWidth(), e.getY()+e.getHeight()+5, paint);
        }

        // raw a dash effect
        if(this.foreign){
            Paint f = new Paint();
            EditText e = this.getEditId();
            Path pp = new Path();
            f.setColor(Color.BLACK);
            f.setStyle(Paint.Style.STROKE);
            f.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
            pp.setLastPoint(e.getX(),e.getY() +e.getHeight()+5 );
            pp.lineTo(e.getX() + e.getWidth(), e.getY()+e.getHeight()+5);
            canvas.drawPath(pp, f);
        }
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
    public void removeObj(ShapeObject curr, RelativeLayout textLayer) {
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

    public void setPrimary(boolean b) {
        primary = b;
    }
    public boolean isPrimary(){
        return primary;
    }
    public void setForeign(Boolean b) {
        foreign = b;
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
