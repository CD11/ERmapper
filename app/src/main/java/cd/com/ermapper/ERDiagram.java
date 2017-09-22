package cd.com.ermapper;

import android.graphics.drawable.shapes.Shape;
import android.util.Log;

import org.w3c.dom.Attr;

import java.io.Serializable;
import java.util.ArrayList;

import static android.R.attr.type;


/**
 * Created by CD on 9/7/2017.
 */

public class ERDiagram extends Exception implements Serializable {
    private String name;
    private ArrayList<ShapeObject> objects;

    public ERDiagram(){
        this.name = "erDiagram";
        this.objects = new ArrayList<ShapeObject>();
        Log.d("CreatedER", name);
    }

    public ERDiagram(String name){
        this.name = name;
        this.objects = new ArrayList<ShapeObject>();
        Log.d("CreatedER", this.name);
    }




    public void addEntity(Entity entity) {
        this.objects.add(entity);
    }


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
                };
                // check relationships
                for(Relationship r: this.getRelationships()){
                    if(r.getObj1()==((Entity)i) || r.getObj2()==((Entity)i)){
                        this.getRelationships().remove(r);
                    }
                }
                break;
            case"Attribute":

                for(Entity e:this.getEntities()){
                    if(e.getAttr().getElements().contains(i)){
                        e.getAttr().getElements().remove((Attribute) i);
                    }

                };
                // check relationships
                for(Relationship r: this.getRelationships()){
                    if(r.getObj1()==((Attribute)i) || r.getObj2()==((Attribute)i)){
                        this.getRelationships().remove(r);
                    }
                }
                break;

            case"Relationship":
                // check relationships
                for(Relationship r: this.getRelationships()){
                    if(r.getObj1()==((Attribute)i) || r.getObj2()==((Attribute)i)){
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

    public void deleteR(Relationship rCurr) {
        objects.remove(rCurr);
        for(ShapeObject o: objects){
            if(o.relationships.contains(rCurr)){
                o.relationships.remove(rCurr);
            }
        }

    }


    public ArrayList<Entity> getEntities() {
        ArrayList<Entity> e = new ArrayList<>();

        for(ShapeObject o : objects){
            if(o.getClass() == Entity.class){
                e.add((Entity)o);
            }
        }

        return e;
    }

    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> a = new ArrayList<>();

        for(ShapeObject o : objects){
            if(o.getClass() == Attribute.class){
                a.add((Attribute) o);
            }
        }

        return a;
    }
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


}
