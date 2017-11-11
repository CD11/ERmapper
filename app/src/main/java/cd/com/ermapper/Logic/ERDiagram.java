package cd.com.ermapper.Logic;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;

import cd.com.ermapper.shapes.Attribute;
import cd.com.ermapper.relations.AttributeSet;
import cd.com.ermapper.shapes.Entity;
import cd.com.ermapper.shapes.Relationship;
import cd.com.ermapper.shapes.ShapeObject;


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
    private ArrayList<Relationship> relationships;


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
        relationships = new ArrayList<>();
        name = in.readString();
        entityObjs = in.createTypedArrayList(Entity.CREATOR);
        relationships = in.createTypedArrayList(Relationship.CREATOR);

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
            if(o.getClass() == Relationship.class && !r.contains(o)){
                r.add((Relationship) o);
            }
        }
        return r;
    }



    public ArrayList<Entity> getEntityObj() {
        ArrayList<Entity> es = new ArrayList<>();
        Log.d("Ternary", String.valueOf(getRelationships().size()));
        for(Relationship r: relationships){
             /* If Relationship is ternary
                1. replace the relationship between the 3 entities with a new entity E and
                create relationships E -> E1, E->E2, E->E3
                2. Give E a temporary primary key
                3. add any attributes of R to E
            */
            if(r.isTernary()){
                Entity newE = new Entity("Temp");

                Relationship EA = new Relationship("Temp", newE, (Entity)r.getObjs().get(0));
                Relationship EB = new Relationship("Temp", newE, (Entity)r.getObjs().get(1));
                Relationship EC = new Relationship("Temp", newE, (Entity)r.getObjs().get(2));
                Attribute temp = new Attribute("temp");
                Attribute temp2 = new Attribute("temp2");
                temp.setPrimary();
                newE.addAttribute(temp2);
                newE.addAttribute(temp);
                newE.getAttr().addAll(((Entity)r.getObjs().get(0)).partAttrs());
                newE.getAttr().addAll(((Entity)r.getObjs().get(1)).partAttrs());
                newE.getAttr().addAll(((Entity)r.getObjs().get(2)).partAttrs());
                newE.getAttr().addAll(r.getAttrs());
                es.add(newE);
                addObject(EA);
                addObject(EB);
                addObject(EC);
                deleteO(r);
            }
        }
        for(Relationship r: getRelationships()){
            // If Relationship is binary add both entity objects
            if(r.isBinary()){
                es.add(((Entity)r.getObj1()));
                es.add(((Entity)r.getObj1()));
            }
        }


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
        parcel.writeTypedList(getRelationships());
    }


    public void removeO(ShapeObject curr) {
        objects.remove(curr);
    }
}
