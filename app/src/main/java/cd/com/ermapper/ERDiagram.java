package cd.com.ermapper;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;


/**
 * Created by CD on 9/7/2017.
 */

public class ERDiagram implements Parcelable {
    /*
        This class represents an ER Diagram, containting Entities, Relations and Attributes
        The ERDiagram Entities can be broken down into relations with dunctional dependencies

     */
    private String name;
    private ArrayList<ShapeObject> objects;   // We hold a list of all objects because we do not know what objects are connect right away
    private ArrayList<Entity> entityObjs;     // upon time to normalize we can pass all entity objs with their attributes to the FDNormalization.



    public ERDiagram(){
        this.name = "erDiagram";
        this.objects = new ArrayList<>();
    }

    public ERDiagram(String name){
        this.name = name;
        this.objects = new ArrayList<>();
        Log.d("CreatedER", this.name);
    }


    protected ERDiagram(Parcel in) {
        objects = new ArrayList<>();
        entityObjs= new ArrayList<>();
        name = in.readString();
        entityObjs = in.createTypedArrayList(Entity.CREATOR);
    }

    public static final Creator<ERDiagram> CREATOR = new Creator<ERDiagram>() {
        @Override
        public ERDiagram createFromParcel(Parcel in) {
            return new ERDiagram(in);
        }

        @Override
        public ERDiagram[] newArray(int size) {
            return new ERDiagram[size];
        }
    };


    public String getName() {
        return name;
    }

    public ArrayList<ShapeObject> getObjects() {
        return objects;
    }

    public void addObject(ShapeObject obj) {
        this.objects.add(obj);
    }


    public void deleteO(ShapeObject i) {
        String s = i.getClass().toString();

        switch(s){
            case"Entity":

                for(Attribute a:((Entity)i).getAttr().getElements()){
                    ((Entity)i).getAttr().getElements().remove(a);
                }
                // check relationships
                for(Relationship r: this.getRelationships()){
                    if(r.getObj1()== i || r.getObj2()== i){
                        this.getRelationships().remove(r);
                    }
                }
                break;
            case"Attribute":

                for(Entity e:this.getEntities()){
                    if(e.getAttr().getElements().contains(i)){
                        e.getAttr().getElements().remove(i);
                    }

                }
                // check relationships
                for(Relationship r: this.getRelationships()){
                    if(r.getObj1()== i || r.getObj2()== i){
                        this.getRelationships().remove(r);
                    }
                }
                break;

            case"Relationship":
                // check relationships
                for(Relationship r: this.getRelationships()){
                    if(r.getObj1()== i || r.getObj2()== i){
                        this.getRelationships().remove(r);
                    }
                }
                break;

        }

        objects.remove(i);
    }


     // Get all entity shape objects
    public ArrayList<Entity> getEntities() {
        ArrayList<Entity> e = new ArrayList<>();

        for(ShapeObject o : objects){
            if(o.getClass() == Entity.class){
                e.add((Entity)o);
            }
        }

        for(Relationship r: getRelationships()){

        }

        return e;
    }
    // get all attribute shapeobjects
    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> a = new ArrayList<>();

        for(ShapeObject o : objects){
            if(o.getClass() == Attribute.class){
                a.add((Attribute) o);
            }
        }

        return a;
    }
    // get all relationship shapeobjects
    public ArrayList<Relationship> getRelationships() {
        ArrayList<Relationship> r = new ArrayList<>();

        for(ShapeObject o : objects){
            if(o.getClass() == Relationship.class){
                r.add((Relationship) o);

            }
        }

        return r;
    }
    public ArrayList<Entity> getEntityObj() {
        ArrayList<Entity> es = new ArrayList<>();

        for(Entity e: entityObjs) {
            if(!e.isWeak()){
                es.add(e);
            }

        }
        return es;
    }

    /* Sorts objects so that all relationship objects are first,
        this allows for them to be drawn first onto the canvas,
        allowing unwanted lines to be covered.
     */

    public ArrayList<ShapeObject> getSortedObjects() {
        ArrayList<ShapeObject> sortedObjects = new ArrayList<>();

        sortedObjects.addAll(this.getRelationships());
        sortedObjects.addAll(this.getEntities());
        sortedObjects.addAll(this.getAttributes());
        return sortedObjects;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeTypedList(getEntities());
    }


    public void removeO(ShapeObject curr) {
        objects.remove(curr);
    }
}
