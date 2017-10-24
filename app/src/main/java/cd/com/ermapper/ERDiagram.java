package cd.com.ermapper;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;

import static cd.com.ermapper.ShapeObject.*;


/**
 * Created by CD on 9/7/2017.
 */

public class ERDiagram implements Parcelable {
    /*
        This class represents an ER Diagram, containting Entities, Relations and Attributes
        The ERDiagram Entities can be broken down into relations with dunctional dependencies

     */
    private String name;
    private ArrayList<ShapeObject> objects;
    private ArrayList<Relation> relations;
    private DependencySet dependencies;



    public ERDiagram(){
        this.name = "erDiagram";
        this.objects = new ArrayList<>();
        this.relations = new ArrayList<>();
        this.dependencies = new DependencySet();
    }

    public ERDiagram(String name){
        this.name = name;
        this.objects = new ArrayList<>();
        this.relations = new ArrayList<>();
        this.dependencies = new DependencySet();
        Log.d("CreatedER", this.name);
    }


    protected ERDiagram(Parcel in) {
        objects  = new ArrayList<>();
        relations = new ArrayList<>();
        dependencies = new DependencySet();
        name = in.readString();
        relations = in.createTypedArrayList(Relation.CREATOR);
        dependencies = in.readTypedObject(DependencySet.CREATOR);

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

        for(ShapeObject o: objects){
            if(o.relationships.contains(i)){
                o.relationships.remove(i);
            }
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
            if(o.getClass() == Relationship.class){
                r.add((Relationship) o);

            }
        }

        return r;
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

    public DependencySet getDependencies() {
        return dependencies;
    }

    public ArrayList<Relation> findRelations(){
       for(Entity e: getEntities()){
           if(!e.equals(null)){
               relations.add(new Relation(e.getAttr(), e.getPrimary(), e.getName()));
           }
       }
       return relations;
    }

    public DependencySet findDependencies(){

        for( Relation r: relations){
            FunctionalDependency fd = new FunctionalDependency(r.getPrimaryKey(), r.getAttributes());
            if(fd != null || fd.isTrivial()) dependencies.add(fd);
            Log.d("FD", fd.getLHS().toString()  + " " + fd.getRHS().toString());
        }
        return dependencies;
    }

    public ArrayList<Relation> getRelations(){
        return relations;
    }

    // Prints the ER diagram Relations, with their dependencies
    public void printER(){
        for(Relation r: relations){
           Log.d("printing diagram", r.toString());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeTypedList(relations);
        parcel.writeTypedObject(dependencies, i);
//        printER();

    }


}
