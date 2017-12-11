package cd.com.ermapper.Logic;


import android.os.Parcel;
import android.os.Parcelable;
import android.widget.RelativeLayout;

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
            }
            // If Relationship, check for any entities in the relationship
            if(o.getClass() == Relationship.class){
                for(Entity sub: ((Relationship)o).getObjs().getElements()){
                    e.add(sub);
                  //  e.addAll(sub.getWeak());
                }
            }
        }
        entityObjs = e;
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
        this.relationshipsobjs = r;
        return r;
    }
    // get all relationship shapeobjects
    public ArrayList<Relationship> getRelationshipsObjs() {
        return relationshipsobjs;
    }

    // get all relationship shapeobjects
    public void setRelationshipsObjs(ArrayList<Relationship> r) {
        this.relationshipsobjs = r;
    }


    /* Funciton: relationshipDecomposition()
       Purpose:
            -> Searches all Diagram relationships, if they are not Binary converts them to binary
            -> Also removes any  redundant entites ( weak entites are stored in 2 places
     */
    public EntitySet relationshipDecomposition() {
        EntitySet es = new EntitySet();
        ArrayList<Relationship> del = new ArrayList<>();
        ArrayList<Relationship> tempR = new ArrayList<>();
        for(Relationship r: relationshipsobjs) {
             /* If Relationship is > binary
                1. replace the relationship between entities with a new Relationship R and
                create relationships E -> E1, E->E2, E->E3
                2. Give E a temporary primary key
                3. add any attributes of R to E
            */
            if (!r.isBinary()) {
                ////////////////////// Step 1 /////////////////////////////
                Entity newE = new Entity("placeholder");
                Attribute temp = new Attribute("-1");
                temp.setPrimary(true);
                newE.addAttribute(temp);

                for (Entity e : r.getObjs().getElements()) {
                    Relationship EA = new Relationship("new"+e.getName(), newE, e);
                    newE.getAttr().addAll(e.foreignAttrs());
                    this.addObject(EA);
                    tempR.add(EA);
                    es.add(e);
                }
                es.add(newE);
                del.add(r);

            }
        }
        relationshipsobjs.addAll(tempR);

        for(Relationship r: getRelationshipsObjs()){
            // If Relationship is binary add both entity objects
            if(r.isBinary()){
                es.add(((Entity)r.getObj1()));
                es.add(((Entity)r.getObj2()));
            }
        }

        for(Entity e: entityObjs.getElements()) {
            if(!e.isWeak()){
                es.add(e);
            }

        }


        return es;
    }



    public ArrayList<Cardinality> getAllCardinalities() {
        ArrayList<Cardinality> c = new ArrayList<>();
        for(Relationship r: getRelationships()){
            c.addAll(r.getTextObjs());
        }
        return c;
    }

    public void removeO(ShapeObject curr, RelativeLayout textLayer) {
        for (ShapeObject o : this.objects) {
            if(o.equals(curr)){
                this.objects.addAll(curr.getallobjects());
                this.objects.remove(o);
                break;
            }
            if (o.containsObj(curr)) {
                this.objects.addAll(o.getallobjects());
                if (o.getClass() == Relationship.class) {
                    if (((Relationship) o).isBinary() ) {
                        if(curr.getClass() != Attribute.class) {// check that it is the entity being removed
                            for(Cardinality c: ((Relationship) o).getTextObjs()){
                                if(textLayer!=null) textLayer.removeView(c.getNum());
                                c = null;
                            }

                            this.objects.remove(o);
                            if (o.getEditId()!=null && textLayer !=null) textLayer.removeView(o.getEditId());
                       }
                    }
                }
                this.objects.remove(curr);
                o.removeObj(curr, textLayer);
                break;
            }

        }
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

    public void isValid() {
        for(ShapeObject o: this.getObjects()){
            try {
               o.isValid();
            } catch (NullPointerException e){
                throw new NullPointerException(e.getMessage());
                }
        }
    }
}
