package cd.com.ermapper;

import android.graphics.drawable.shapes.Shape;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;





/**
 * Created by CD on 9/7/2017.
 */

public class ERDiagram extends Exception implements Serializable {
    private String name;
    private ArrayList<Entity> entities;
    private ArrayList<Attribute> attributes;
    private ArrayList<ShapeObject> objects;

    public ERDiagram(){
        this.name = "erDiagram";
        this.entities = new ArrayList<Entity>();
        this.attributes = new ArrayList<Attribute>();
        this.objects = new ArrayList<ShapeObject>();
        Log.d("CreatedER", name);
    }

    public ERDiagram(String name){
        this.name = name;
        this.entities = new ArrayList<Entity>();
        this.attributes = new ArrayList<Attribute>();
        this.objects = new ArrayList<ShapeObject>();
        Log.d("CreatedER", this.name);
    }



    // Setters and getters
    public ArrayList<Entity> getEntities() {
        return this.entities;
    }


    public void addEntity(Entity entity) {
        this.entities.add(entity);
       Log.d("EntitySize", String.valueOf(this.entities.size()));

    }


    public String getName() {
        return name;
    }

    public void update(ArrayList<Entity> entity) {
        this.entities = entity;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void updateA(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public ArrayList<ShapeObject> getObjects() {
        return objects;
    }

    public void addObject(ShapeObject obj) {
        this.objects.add(obj);
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }
}
