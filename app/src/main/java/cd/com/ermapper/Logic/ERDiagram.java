package cd.com.ermapper.Logic;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;

import cd.com.ermapper.Components.EntitySet;
import cd.com.ermapper.Components.Attribute;
import cd.com.ermapper.Components.Cardinality;
import cd.com.ermapper.Components.Entity;
import cd.com.ermapper.Components.Relationship;
import cd.com.ermapper.Components.ShapeObject;


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
    private EntitySet entityObjs;     // upon time to normalize we can pass all entity objs with their attributes to the FDNormalization.
    private ArrayList<Relationship> relationshipsobjs;


    public ERDiagram(String name){
        this.name = name;
        this.objects = new ArrayList<>();
        Log.d("CreatedER", this.name);
    }


    protected ERDiagram(Parcel in) {
        objects = new ArrayList<>();
        entityObjs= new EntitySet();
        relationshipsobjs = new ArrayList<>();
        name = in.readString();
        entityObjs = in.readTypedObject(EntitySet.CREATOR);
        relationshipsobjs = in.createTypedArrayList(Relationship.CREATOR);

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
        objects.remove(i);
    }

    // searches through each element for any sub attributes
    public ArrayList<ShapeObject> getDrawnObjects() {
        ArrayList s = new ArrayList<>();
        s.addAll(objects);
        for(ShapeObject o : objects){
            s.addAll(o.getallobjects());
        }
        return s;
    }
    // Get all entity shape objects
    public EntitySet getAllEntities() {
        EntitySet e = new EntitySet();
        for(ShapeObject o : objects){
            // If entity then add the entity along with any weak entites
            if(o.getClass() == Entity.class){
                e.add((Entity)o);
                e.addAll(((Entity) o).getWeak());
            }
            // If Relationship, check for any entities in the relationship
            if(o.getClass() == Relationship.class){
                for(Entity sub: ((Relationship)o).getObjs().getElements()){
                    e.add(sub);
                    e.addAll(sub.getWeak());
                }
            }
        }
        return e;
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
    // get all relationship shapeobjects
    public ArrayList<Relationship> getRelationshipsObjs() {
        return relationshipsobjs;
    }



    /* Funciton: getBinaryEntities()
       Purpose:
            -> Searches all Diagram relationships, if they are not Binary converts them to binary
            -> Also removes any  redundant entites ( weak entites are stored in 2 places
     */
    public EntitySet getBinaryEntities() {
        EntitySet es = new EntitySet();
        ArrayList<Relationship> tempR = new ArrayList<>();
        for(Relationship r: relationshipsobjs){
             /* If Relationship is ternary
                1. replace the relationship between the 3 entities with a new entity E and
                create relationships E -> E1, E->E2, E->E3
                2. Give E a temporary primary key
                3. add any attributes of R to E
            */

             /////////////////////// Converts all N-ary relationships to Binary
            if(r.isTernary()){
                Entity newE = new Entity("-1");
                Relationship EA = new Relationship("-1", newE, (Entity)r.getObjs().getElements().get(0));
                Relationship EB = new Relationship("-1", newE, (Entity)r.getObjs().getElements().get(1));
                Relationship EC = new Relationship("-1", newE, (Entity)r.getObjs().getElements().get(2));
                Attribute temp = new Attribute("-1");
                Attribute temp2 = new Attribute("-1");
                temp.setPrimary();
                newE.addAttribute(temp2);
                newE.addAttribute(temp);
                newE.getAttr().addAll(((Entity)r.getObjs().getElements().get(0)).foreignAttrs());
                newE.getAttr().addAll(((Entity)r.getObjs().getElements().get(1)).foreignAttrs());
                newE.getAttr().addAll(((Entity)r.getObjs().getElements().get(2)).foreignAttrs());
                newE.getAttr().addAll(r.getAttrs());
                es.add(newE);
                addObject(EA);
                addObject(EB);
                addObject(EC);
                tempR.add(EA);
                tempR.add(EB);
                tempR.add(EC);
                r = null;  // this relaitonship isn't needed
            }

        }
        relationshipsobjs.addAll(tempR);


        for(Relationship r: getRelationships()){
            // If Relationship is binary add both entity objects
            if(r.isBinary()){
                es.add(((Entity)r.getObj1()));
                es.add(((Entity)r.getObj2()));
            }
        }


        ////////////////////////// Step 2 in normalization
        for(Entity e: entityObjs.getElements()) {
            if(!e.isWeak()){
                es.add(e);
            }

        }
        return es;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeTypedObject(getAllEntities(),i);
        parcel.writeTypedList(getRelationships());
    }


    public ArrayList<Cardinality> getAllCardinalities() {
        ArrayList<Cardinality> c = new ArrayList<>();
        for(Relationship r: getRelationships()){
            c.addAll(r.getTextObjs());
        }
        return c;
    }
}
