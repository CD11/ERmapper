package cd.com.ermapper;



import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


/**
 * Created by ldnel_000 on 2015-11-04.
 */
public class FunctionalDependency implements Parcelable{

    //This class represents a functional dependency lhs -> rhs

    private AttributeSet lhs; //left hand side
    private AttributeSet rhs; //right hand side

    public FunctionalDependency(AttributeSet aLHS, AttributeSet aRHS) {

        if (aLHS == null || aRHS == null) {
            System.out.println("ERROR: NULL ATTRITBUTE SET");
        } else if (aLHS.isEmpty() || aRHS.isEmpty()){
            System.out.println("ERROR: EMPTY ATTRITBUTE SET");
        }else {
            lhs = new AttributeSet();
            rhs = new AttributeSet();
            lhs.addAll(aLHS);
            rhs.addAll(aRHS);
        }
    }


    protected FunctionalDependency(Parcel in) {
        lhs = new AttributeSet();
        rhs = new AttributeSet();
        lhs = in.readTypedObject(AttributeSet.CREATOR);
        rhs = in.readTypedObject(AttributeSet.CREATOR);
    }


    public static final Creator<FunctionalDependency> CREATOR = new Creator<FunctionalDependency>() {
        @Override
        public FunctionalDependency createFromParcel(Parcel in) {
            return new FunctionalDependency(in);
        }

        @Override
        public FunctionalDependency[] newArray(int size) {
            return new FunctionalDependency[size];
        }
    };

    public AttributeSet getLHS(){return lhs;}
    public AttributeSet getRHS(){return rhs;}

    public boolean equals(FunctionalDependency anFD){
		/*]
		 * Perform an equality check based on whether both the left hand side and right hand side
		 * sets are equal. That is this.lhs equals anFD.lhs and this.rhs equals anFD.rhs.
		 * This is not an equality check based on whether one FD can be inferred
		 * from another based in Armstrong's Axioms. Rather it is a simple equality check
		 * based on whether the left hand sides and right hand sides of this and anFD are
		 * equal as sets.
		 */

        if(!lhs.equals(anFD.lhs)) return false;
        return rhs.equals(anFD.rhs);
    }

    public boolean isTrivial(){
        //Answer whether this functional dependency is trivial.
        //A dependency X->Y is trivial is Y is a subset of X

        if(rhs.isEmpty()) return true;
        return lhs.containsAll(rhs);
    }

    public String toString()
    {
        return lhs.toString() + " -> " + rhs.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedObject(this.lhs, i);
        parcel.writeTypedObject(this.rhs, i);
    }
}
