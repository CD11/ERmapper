package cd.com.ermapper.relations;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import cd.com.ermapper.shapes.Attribute;
import cd.com.ermapper.shapes.Entity;

/**
 * Created by CD on 11/21/2017.
 */

public class EntitySet implements Parcelable {
    /* This class holds a unique set of Entity objects
     */
    private ArrayList<Entity> elements;

    public EntitySet() {
        elements = new ArrayList<Entity>();
    }

    public EntitySet(Entity a) {
        elements = new ArrayList<Entity>();
        this.add(a);
    }

    public EntitySet(EntitySet anEntitySet) {
        elements = new ArrayList<Entity>();
        this.addAll(anEntitySet);
    }

    protected EntitySet(Parcel in) {
        elements = new ArrayList<>();
        elements = in.createTypedArrayList(Entity.CREATOR);

    }


    public static final Creator<EntitySet> CREATOR = new Creator<EntitySet>() {
        @Override
        public EntitySet createFromParcel(Parcel in) {
            return new EntitySet(in);
        }

        @Override
        public EntitySet[] newArray(int size) {
            return new EntitySet[size];
        }
    };

    public ArrayList<Entity> getElements() {return elements;}

    public void add(Entity anEntity) {
        //Add anEntity  without duplication
        if(anEntity == null) return;
        for(Entity a : elements) if(a.equals(anEntity)) return; //don't add duplicates
        elements.add(anEntity);
    }
    public void addAll(EntitySet anEntitySet) {
        for(Entity a : anEntitySet.elements)
            if(!this.contains(a) && a.getName() != "-1")
                this.add(a);
    }
    public void addAll(ArrayList<Entity> anEntitySet) {
        for(Entity a : anEntitySet)
            if(!this.contains(a) && a.getName() != "-1")
                this.add(a);
    }
    public void remove(Entity anEntity) {
        //remove all equal to anEntity
        EntitySet itemsToRemove = new EntitySet();
        for(Entity a : elements) if(a.equals(anEntity)) itemsToRemove.add(a);
        elements.removeAll(itemsToRemove.elements);
    }

    public EntitySet copy(){
        EntitySet theCopy = new EntitySet();
        theCopy.addAll(this);
        return theCopy;
    }

    public boolean isEmpty() {return elements.isEmpty(); }
    public int size() {return elements.size();} //answer number of entities in this set
    public void clear(){ elements.clear(); }
    public boolean contains(Entity anEntity){
        //answer whether this contains an Attribute equal to anAttribute
        for(Entity a : elements)
            if(a.equals(anEntity)) return true;
        return false;
    }

    public boolean containsAll(EntitySet anEntitySet){
        //answer whether this contains all entites in anEntitySet
        if(anEntitySet.isEmpty()) return true;

        for(Entity a : anEntitySet.elements)
            if(!this.contains(a)) return false;

        return true;
    }

    public boolean equals(EntitySet anEntitySet){
        //two Entity sets are equal if the are mutually subsets of each others
        if(anEntitySet == null) return false;
        if(!anEntitySet.containsAll(this)) return false;
        return this.containsAll(anEntitySet);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(elements);
    }
}
