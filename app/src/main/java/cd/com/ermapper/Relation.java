package cd.com.ermapper;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import static android.R.attr.key;

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
        if (theAttributes == null)
            System.out.println("ERROR: Attribute set cannot be null");
        if (theAttributes.isEmpty())
            System.out.println("ERROR: EMPTY ATTRITBUTE SET");

        if (key != null && !theAttributes.containsAll(key))
            System.out.println("ERROR: PRIMARY KEY MUST BE A SUBSET OF THE ATTRIBUTES");

        attributes = new AttributeSet();
        attributes.addAll(theAttributes);
        if (!key.isEmpty() && !key.equals(null)) {
            primaryKey = new AttributeSet();
            primaryKey.addAll(key);
        }
    }

    public Relation(FunctionalDependency FD) {

			/* create a relation out of the functional dependency FD
			 * The left hand side becomes the primary key
			 */

        if (FD == null)
            System.out.println("ERROR: Cannot create table out of null dependency");
        if (FD.getLHS().isEmpty() || FD.getRHS().isEmpty())
            System.out.println("ERROR: Cannot create table out of empty dependency");
        attributes = new AttributeSet();
       // attributes.addAll(FD.getLHS());
        attributes.addAll(FD.getRHS());

        primaryKey = new AttributeSet();
        primaryKey.addAll(FD.getLHS());
    }

    protected Relation(Parcel in) {
        attributes  = new AttributeSet();
        primaryKey = new AttributeSet();
        name  = in.readString();
        primaryKey = in.readTypedObject(AttributeSet.CREATOR);
        attributes =  in.readTypedObject(AttributeSet.CREATOR);

    //    Log.d("Parcel Relation",  primaryKey.size() +" " + attributes.size());
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

    public boolean containsAll(Relation r) {
        return attributes.containsAll(r.attributes);
    }

    public String toString() {

        String returnString = this.name+": [";
        for (Attribute a : primaryKey.getElements()) {
            if (primaryKey.contains(a)) returnString = returnString + a + ",";
        }
        returnString = returnString.substring(0, returnString.length() - 1);  //strip off last ","
        returnString = returnString + " | ";

        for (Attribute a : attributes.getElements()) {
            if (!primaryKey.contains(a)) returnString = returnString + a + ",";
        }
        returnString = returnString.substring(0, returnString.length() - 1);  //strip off last ","
        returnString = returnString + "]";

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



