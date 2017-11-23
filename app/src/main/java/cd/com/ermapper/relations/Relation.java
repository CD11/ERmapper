package cd.com.ermapper.relations;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.w3c.dom.Attr;

import cd.com.ermapper.shapes.Attribute;
import cd.com.ermapper.shapes.Entity;

/**
 * Created by ldnel_000 on 2015-11-04.
 */
public class Relation implements Parcelable{

	/*
	 * This class represents a database relation, or relational table
	 */


    private AttributeSet attributes = null; //attributes or columns of the table
    private AttributeSet primaryKey = null; //designated primary key
    private String name;

    // constructors
    public Relation(AttributeSet theAttributes, AttributeSet key, String name) {
        this.name = name;
        Log.d("name", String.valueOf(theAttributes.containsAll(key)));
        if (theAttributes == null)
            throw new NullPointerException(name + " attribute set is null");
        if (theAttributes.isEmpty())
            throw new NullPointerException(name + " attribute set is empty");
        if (key != null && !theAttributes.containsAll(key))
            throw new NullPointerException(name +(" ERROR: PRIMARY KEY MUST BE A SUBSET OF THE ATTRIBUTES"));

        attributes = new AttributeSet();
        attributes.addAll(theAttributes);
        if(key != null){
            primaryKey = new AttributeSet();
            primaryKey.addAll(key);
        }
    }

    public Relation(Entity obj1, Entity obj2) {
        AttributeSet temp = new AttributeSet();
        temp.addAll(obj1.foreignAttrs());
        temp.addAll(obj2.foreignAttrs());
        attributes.addAll(temp);
        for(Attribute a: temp.getElements()){
            if(a.isForeign()||a.isPrimary()){
                primaryKey.add(a);
            }
        }
    }


    public Relation(FunctionalDependency FD) {

			/* create a relation out of the functional dependency FD
			 * The left hand side becomes the primary key
			 */

        if (FD == null) {
            System.out.println("ERROR: Cannot create table out of null dependency");
            throw new NullPointerException("FD ERROR: Cannot create table out of null dependency");
        }
        if (FD.getLHS().isEmpty() || FD.getRHS().isEmpty()) {
            System.out.println("ERROR: Cannot create table out of empty dependency");
            throw new NullPointerException("FD ERROR: Cannot create table out of empty dependency");
        }

        primaryKey = new AttributeSet();
        primaryKey.addAll(FD.getLHS());
        attributes = new AttributeSet();
        attributes.addAll(FD.getLHS());
        attributes.addAll(FD.getRHS());
        this.name =  FD.getName();
    }

    protected Relation(Parcel in) {
        attributes  = new AttributeSet();
        primaryKey = new AttributeSet();
        name  = in.readString();

        primaryKey = in.readTypedObject(AttributeSet.CREATOR);
        attributes = in.readTypedObject(AttributeSet.CREATOR);


        if(primaryKey.isEmpty() || primaryKey == null ){
            throw new NullPointerException(name + "Primary key set is empty");
        }
        if(attributes.isEmpty() ||attributes == null){
            throw new NullPointerException(name + "attributes  set is empty");
        }

    }

    public static final Creator<Relation> CREATOR = new Creator<Relation>() {
        @Override
        public Relation createFromParcel(Parcel in) {
            return new Relation(in);
        }

        @Override
        public Relation[] newArray(int size) {
            return new Relation[size];
        }
    };


    public AttributeSet getAttributes() {
        return attributes;
    }
    public AttributeSet getPrimaryKey() {
        return primaryKey;
    }
    public String getName(){return  name;}
    public boolean containsAll(Relation r) {
        return attributes.containsAll(r.attributes);
    }

    public String toString() {

        String returnString = this.name + ": [";
        for (Attribute a : primaryKey.getElements()) {
            if (primaryKey.contains(a)) returnString = returnString + a + ",";
        }
        returnString = returnString.substring(0, returnString.length() - 1);  //strip off last ","
        returnString = returnString + " | ";

        for (Attribute a : attributes.getElements()) {
            if (!primaryKey.contains(a)) returnString = returnString + a + ",";
        }
        returnString = returnString.substring(0, returnString.length() - 1);  //strip off last ","
        returnString = returnString + "]  ";

        return returnString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeTypedObject(primaryKey, i);
        parcel.writeTypedObject(attributes, i);
    }
}



