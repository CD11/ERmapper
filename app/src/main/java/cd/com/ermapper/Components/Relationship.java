package cd.com.ermapper.Components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

import static android.graphics.Color.BLACK;


/**
 * Created by CD on 9/15/2017.
 */

public class Relationship extends ShapeObject {
    /*
        This class represents a connection between two or more Entity objects represented by a diamond
        - > a relationship can have an attribute.
     */


    //Todo Fix issue with Attributes when the are part of a relationship. They are not being deleted correctly

    // Local variables
    private EntitySet objs;
    private ArrayList<Cardinality> conns;
    private ShapeObject obj1;
    private ShapeObject obj2;
    private AttributeSet attrs;

    // costructors

    public Relationship(String name, Entity e1, Entity e2) {
        super(null,name, 0, 0, 0,0);
        objs = new EntitySet();
        attrs = new AttributeSet();
        conns = new ArrayList<>();
        obj1 = e1;
        obj2 = e2;
        addObj(e1,null);
        addObj(e2,null);
    }
    public Relationship() {
        super(null,"relationship", 0, 0, 0,0);
        objs = new EntitySet();
        attrs = new AttributeSet();
        conns = new ArrayList<>();
    }


    public Relationship(Parcel in) {
        super(in.readString());
        obj1 = in.readParcelable(ShapeObject.class.getClassLoader());
        obj2 = in.readParcelable(ShapeObject.class.getClassLoader());
        attrs = in.readTypedObject(AttributeSet.CREATOR);
        objs = in.readTypedObject(EntitySet.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void isValid() {
        try {
            for (Entity e : this.getObjs().getElements()) {
                e.isValid();
            }
        }catch (NullPointerException e){
            throw e;
        }

    }

    public static final Creator<Relationship> CREATOR = new Creator<Relationship>() {
        @Override
        public Relationship createFromParcel(Parcel in) {
            return new Relationship(in);
        }

        @Override
        public Relationship[] newArray(int size) {
            return new Relationship[size];
        }
    };

    // Draw the lines to each Entity in the relationship
    public void drawLines(Canvas canvas, Paint paint){
        Path p = new Path();
        Coordinates c =  this.getCoordinates();
        float x = c.centerX(); // center of diamond
        float y = c.centerY();  // center of diamond

        for(Attribute a: this.getAttrs().getElements()){
            canvas.drawLine(x,y,a.getCoordinates().centerX(), a.getCoordinates().centerY(),paint);

        }
        //
        if(this.getObjs().isEmpty()){
            // then it must be an attribute
            canvas.drawLine(this.getObj1().getCoordinates().centerX(), this.getObj1().getCoordinates().centerY(),this.getObj2().getCoordinates().centerX(), this.getObj2().getCoordinates().centerY(),paint);

        }else { // both objects must be entities for it to be weak
            for (Entity o : getObjs().getElements()) {
                // Draws 2 lines to show total participation of weak entities
                for (Attribute a : o.getAttr().getElements()) {
                    o.drawLines(canvas,paint);
                    canvas.drawLine(o.getCoordinates().centerX(), o.getCoordinates().centerY(), a.getCoordinates().centerX(), a.getCoordinates().centerY(), paint);
                }

                if (o.isWeak()) {
                    // Calculates a second line at an offset of 20.
                    float offset = 20;
                    float x2 = o.getCoordinates().centerX();
                    float y2 = o.getCoordinates().centerY();
                    float L = (float) Math.sqrt((x-x2)*(x-x2)+(y-y2)*(y-y2));

                    float x1p = x + offset * (y2-y) / L;
                    float x2p = x2 + offset * (y2-y) / L;
                    float y1p = y + offset * (x-x2) / L;
                    float y2p = y2 + offset * (x-x2) / L;
                    canvas.drawLine(x, y, o.getCoordinates().centerX(), o.getCoordinates().centerY() , paint);
                    canvas.drawLine(x1p, y1p,x2p,y2p, paint);
                } else {  /// only draw 1 line
                    canvas.drawLine(x, y, o.getCoordinates().centerX(), o.getCoordinates().centerY(), paint);
                }
            }
            // add the cardinality to the correct position
            for(Cardinality co: conns){
                co.getNum().setX(((x + co.getO().getCoordinates().centerX())/2));
                co.getNum().setY(((y + co.getO().getCoordinates().centerY())/2));
            }
        }
    }

    // This gets the path from the coordinates of the objects that will be draw the diamond to the canvas
    public void drawShape(Canvas canvas, Paint paint){
       Coordinates c =  this.getCoordinates();
       float x =c.centerX();
       float y =c.centerY();
       float w = 100;
       float h = 100;

        for(Attribute a: this.attrs.getElements()){
            a.drawShape(canvas, paint);
        }

        if(this.getObj1().getClass()==Entity.class && this.getObj2().getClass()==Entity.class){
        Path p = new Path();
        p.setLastPoint(x-w, y);
        p.lineTo(x,y+h);
        p.lineTo(x+w,y );
        p.lineTo(x,y-h);
        p.lineTo(x-w, y);
        canvas.drawPath(p, paint);


        for(Entity e:objs.getElements()) {
            e.drawShape(canvas, paint);
            if (e.isWeak()) {
                drawOuterDiamond(canvas, paint);
            }
        }
        }else{
            obj1.drawShape(canvas, paint);
            obj2.drawShape(canvas, paint);
        }
    }

    public void drawOuterDiamond(Canvas canvas, Paint paint) {
            Coordinates c =  this.getCoordinates();

            float x =c.centerX();
            float y =c.centerY();
            float w = 115;
            float h = 115;

            Path p = new Path();
            p.setLastPoint(x-w, y);
            p.lineTo(x,y+h);
            p.lineTo(x+w,y );
            p.lineTo(x,y-h);
            p.lineTo(x-w, y);
        canvas.drawPath(p, paint);
    }
    // Checks for valid objts and updates name coordinates
    public void update(){
        if(obj1 != null)
            this.setCoordinateX(obj1.getCoordinates().centerX());
        this.setCoordinateY(obj1.getCoordinates().centerY());
        if(obj2 != null)
            this.setCoordinateW(obj2.getCoordinates().centerX());
        this.setCoordinateH(obj2.getCoordinates().centerY());
        if(this.getEditId() != null) {
            moveName();
        }
    }

    public boolean contains(float v, float v1) {

        float x = this.getCoordinates().centerX();
        float y = this.getCoordinates().centerY();
        float w = 50;
        float h = 50;
        Coordinates c = new Coordinates(x - w, y - h, x + w, y + h);
        return c.contains(v, v1);
    }

    @Override
    public void remove() {
        this.attrs.clear();
        this.conns = null;
        this.getObjs().clear();
    }
    @Override
    public void removeObj(ShapeObject curr, RelativeLayout textLayer) {
        for(Attribute a: this.getAttrs().getElements()){
            if(a.containsObj(curr)){
                a.removeObj(curr, textLayer);
                break;
            }
            if(a.equals(curr)){
                this.attrs.remove(a);
                break;
            }

        }
        for(Entity e: objs.getElements()) {
            if (e.containsObj(curr)){
                e.removeObj(curr,textLayer);
                break;
            }
            if(e.equals(curr)) {
                this.getObjs().remove((Entity) curr);
                break;
            }
        }
        for(Cardinality c : conns){
            if(c.getO().equals(curr))
                textLayer.removeView(c.getNum());
        }
    }

    @Override
    public ArrayList<ShapeObject> getallobjects() {
        ArrayList<ShapeObject> s = new ArrayList<>();
        for(Attribute a: this.attrs.getElements()) {
            s.addAll(a.getallobjects());
            s.add(a);

        }
        if(this.getObjs().isEmpty()){
            s.add(obj1);
            s.addAll(obj1.getallobjects());
            s.add(obj2);
            s.addAll(obj2.getallobjects());
        }else {
            for (Entity e : this.getObjs().getElements()) {
                s.addAll(e.getallobjects());
                s.add(e);
            }
        }
        return s;
    }

    // Checks if the relationship contains a certain object
    @Override
    public boolean containsObj(ShapeObject curr) {
        if(obj1.equals(curr)) return true;
        if(obj2.equals(curr)) return true;
        for(Entity e :this.objs.getElements()){
            if(e.containsObj(curr)) return true;
        }
        return false;

    }

    /* When an object in a relationship is removed
            - if it is binary, the remaining object needs to get added back into the general list
                and the relationship itself needs to be deleted
            - if it is n-ary,  the single object needs to be deleted, but the relationship is still valid.

     */


    //moves name
    public void moveName() {
        this.getEditId().setX(this.getCoordinates().centerX()-60);
        this.getEditId().setY(this.getCoordinates().centerY()-60);
    }


    // Getters and settrs
    public ShapeObject getObj1() {
        return obj1;
    }
    public ShapeObject getObj2() {
        return obj2;
    }
    // Sets obj1 and updates coordinates
    public void setObj1(ShapeObject obj1, Context c) {
        this.obj1 = obj1;
        this.setCoordinateX(obj1.getCoordinates().centerX());
        this.setCoordinateY(obj1.getCoordinates().centerY());
        if(obj1.getClass() == Entity.class)
            addObj((Entity)obj1, c);
    }
    // sets obj2 and updates coordinates 2
    public void setObj2(ShapeObject obj2, Context c) {
        this.obj2 = obj2;
        this.setCoordinateW(obj2.getCoordinates().centerX());
        this.setCoordinateH(obj2.getCoordinates().centerY());
        if(obj2.getClass() == Entity.class)
            addObj((Entity)obj2, c);
    }

    public EntitySet getObjs() { return objs;}


    // Is the relationship is between two objects, then add objects to entity set
    public EditText addObj(Entity obj, Context c){
        if(obj == null )
            return null;
        // Create a cardinality for the object
        Cardinality cd = null;
        if(!objs.contains(obj)) { // Check for duplicates
           // if obj is Weak add it to strong entities

            cd = new Cardinality(c, obj);
            conns.add(cd);
            objs.add(obj);
        }

        return cd.getNum();
    }
    public void addAttribute(Attribute curr) {
        if(!this.attrs.contains(curr))
            attrs.add(curr);
    }
    public AttributeSet getAttrs() { return attrs;  }

    //  For cardinality purposes
    // gets all cardniality objects for the relationship
    public ArrayList<Cardinality> getTextObjs() {return conns;}
    // Initiates Edit texts  for name and cardinality and positions them in their proper place
    public void setTexts(Context c){
        // Give name edit
        final EditText e = new EditText(c);
        e.setImeOptions(EditorInfo.IME_ACTION_DONE);
        e.setSingleLine();
        e.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        e.setTextColor(BLACK);
        e.setHint("name");

        e.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if(!view.hasFocus()){
                    setName(String.valueOf(e.getText()));
                }
            }
        });

        this.setEditText(e);
        moveName();
    }

    /* checks that the connection is it is a relationship or a connection
     */
    // Relationship properties
    // isRelationship = between 2 entities
    public boolean isRelationship() {
        boolean result = false;
            try {
                boolean e1 = obj1.getClass() == Entity.class;
                boolean e2 = obj2.getClass() == Entity.class;
                boolean r1 = obj1.getClass() == Relationship.class;
                boolean r2 = obj2.getClass() == Relationship.class;
                if (e1 && e2 || e1&&r1 || e1&&r2 || e2&&r1 || e2&&r2)
                    result = true;
            }catch (NullPointerException e){
                result = false;
            }
        return  result;
    }
    public boolean isWeak() {
        boolean result = false;
        for (Entity o : objs.getElements()) {
            if (o.isWeak()) {
                result = true;
                break;
            }
        }

        return result;
    }
    public boolean isBinary(){
        return  objs.size() <= 2;
    }
    public boolean isTernary(){
        return  objs.size() ==3;
    }
    public boolean isNary(){
        return  objs.size() >3;
    }

    /*  Function: isOneToOne()
        Purpose:
          Checks if a relationhips is 1:1
     */
    public boolean isOneToOne() {
                if(obj1.getEditId() == null || obj2.getEditId() ==null )
                    return false;
                if(obj1.getEditId().getText().equals("1") && obj2.getEditId().getText().equals("1") )
                    return true;
        return false;
    }
    /*  Function : isOneToN
        Purpose:
        Checks if a relationship is 1:N
       todo: -- > Need to implement something to define what N is.
    */
    public boolean isOneToN() {
        if(obj1.getEditId() == null || obj2.getEditId() ==null )
            return false;
        if(obj1.getEditId().getText().equals("1") && !obj2.getEditId().getText().equals("N"))
            return true;
        if(obj2.getEditId().getText().equals("1") && !obj1.getEditId().getText().equals("N"))
            return true;
        return false;
    }

    /*  Funciton : isMtoN()
        Purpose  :
        Checks if a relationship is M:N
        todo:--> need to implement something to define what N:M is
     */
    public boolean isMToN() {
        if(obj1.getEditId() == null || obj2.getEditId() ==null )
            return false;
        if(obj1.getEditId().getText().equals("M") && !obj2.getEditId().getText().equals("N"))
            return true;
        if(obj2.getEditId().getText().equals("N") && !obj1.getEditId().getText().equals("M"))
            return true;
        return false;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(obj1, flags);
        dest.writeParcelable(obj2, flags);
        dest.writeTypedObject(attrs,flags);
        dest.writeTypedObject(objs,flags);
    }

    public void shapeToXML( XmlSerializer serializer) throws IOException {
        try {
            serializer.startTag(null, "Relationship");
            for (Entity e : this.getObjs().getElements()) {
               // serializer.startTag("", "Entity");
                e.shapeToXML(serializer);
              //  serializer.endTag("", "Entity");
            }

            if (!this.getAttrs().isEmpty()) {
               // serializer.startTag("", "Attributes");
                for (Attribute a : this.getAttrs().getElements()) {
                    a.shapeToXML(serializer);
                }
               // serializer.endTag(null, "Attributes");

            }
            serializer.endTag(null, "Relationship");
        }catch (IOException exception){
            Log.d("R XML Exception", String.valueOf(exception));
            throw exception;
        }
    }

    public EntitySet getStrong() {
        EntitySet strong = new EntitySet();
        for(Entity e: this.getObjs().getElements()){
            if(!e.isWeak()){
                strong.add(e);
            }
        }
        return strong;
    }


    // this returns all attributes in the entire relationship
    public AttributeSet getAllAttr() {
        AttributeSet attrs = new AttributeSet();
        for(Entity e : this.getObjs().getElements()){
            attrs.addAll(e.getAttr());
        }
        // add any attrs in the relationship
        attrs.addAll(this.attrs);
        return  attrs;
    }

    public boolean validForeignKey(String name){
        for(Entity e: this.getStrong().getElements()){
            if(e.getPrimary().hasName(name)){
                return true;
            }
        }
        return false;
    }

}
