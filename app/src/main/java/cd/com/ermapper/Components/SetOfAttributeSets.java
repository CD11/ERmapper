package cd.com.ermapper.Components;


/**
 * Created by ldnel_000 on 2015-11-04.
 */
        import android.os.Parcel;
        import android.os.Parcelable;
        import java.util.ArrayList;

public class SetOfAttributeSets implements Parcelable
{
	/*
	 * This class represents a set of attribute sets
	 * It can be used to represent a set of keys, or a set of relations
	 * Anything that requires representing sets of attribute groupings
	 *
	 * Duplicates are removed based on equality of elements
	 * Non duplication is not guaranteed if elements are modified while
	 * members of the set.
	 * Instead to modify an element it should be removed, modified,
	 * and re-inserted with the add() method which will check for duplicates.
	 *
	 */

    private ArrayList<AttributeSet> elements;

    public SetOfAttributeSets() { elements = new ArrayList<AttributeSet>(); }

    protected SetOfAttributeSets(Parcel in) {
        elements = in.createTypedArrayList(AttributeSet.CREATOR);
    }

    public static final Creator<SetOfAttributeSets> CREATOR = new Creator<SetOfAttributeSets>() {
        @Override
        public SetOfAttributeSets createFromParcel(Parcel in) {
            return new SetOfAttributeSets(in);
        }

        @Override
        public SetOfAttributeSets[] newArray(int size) {
            return new SetOfAttributeSets[size];
        }
    };

    public AttributeSet get(int i) {return elements.get(i); }

    public void add(AttributeSet anAttriubteSet) {
        //add an Attribute Set without duplications
        if(anAttriubteSet == null) return;

        for(AttributeSet s : elements) if(s.equals(anAttriubteSet)) return; //don't add duplicates

        elements.add(anAttriubteSet);
    }
    public void addAll(SetOfAttributeSets aSetOfAttributeSets) {
        //add  without duplications
        if(aSetOfAttributeSets == null) return;

        for(AttributeSet s : aSetOfAttributeSets.elements)
            this.add(s);
    }

    public void remove(AttributeSet anAttributeSet) {
        //remove all items equal to anFD

        if(anAttributeSet == null) return;

        SetOfAttributeSets itemsToRemove = new SetOfAttributeSets();

        for(AttributeSet s : elements) if(s.equals(anAttributeSet)) itemsToRemove.add(s);

        elements.removeAll(itemsToRemove.elements);
    }
    public SetOfAttributeSets copy(){
        SetOfAttributeSets theCopy = new SetOfAttributeSets();
        theCopy.addAll(this);
        return theCopy;
    }
    public ArrayList<AttributeSet> getElements() {return elements; }
    public boolean isEmpty() {return elements.isEmpty(); }
    public void clear() {elements.clear(); }

    public boolean contains(AttributeSet anAttributeSet){
        //answer whether this contains an Attribute equal to anAttribute
        for(AttributeSet s : elements)
            if(s.equals(anAttributeSet)) return true;

        return false;
    }

    public boolean containsAll(SetOfAttributeSets aSet){
        //answer whether this contains all attributes in anAttributeSet
        if(aSet.isEmpty()) return true;

        for(AttributeSet s : aSet.elements)
            if(!this.contains(s)) return false;

        return true;
    }

    public boolean equals(SetOfAttributeSets aSet){
        //two attribute sets are equal if the are mutually subsets of each others
        if(!aSet.containsAll(this)) return false;
        return this.containsAll(aSet);

    }


    public String toString(){
        String returnString = "";
        for(int i=0; i<elements.size(); i++){
            returnString = returnString + "{" + elements.get(i) + "} ";
        }
        return returnString;

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