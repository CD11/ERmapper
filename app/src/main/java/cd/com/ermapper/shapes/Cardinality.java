package cd.com.ermapper.shapes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by CD on 11/9/2017.
 */

public class Cardinality implements Parcelable {

    EditText num;
    ShapeObject o;

    public Cardinality(Context c, ShapeObject o){
      num = new EditText(c);
      num.setText("1");
      this.o = o;

    }


    protected Cardinality(Parcel in) {
        o = in.readParcelable(ShapeObject.class.getClassLoader());
    }

    public static final Creator<Cardinality> CREATOR = new Creator<Cardinality>() {
        @Override
        public Cardinality createFromParcel(Parcel in) {
            return new Cardinality(in);
        }

        @Override
        public Cardinality[] newArray(int size) {
            return new Cardinality[size];
        }
    };

    public EditText getNum() {
        return num;
    }

    public ShapeObject getO() {
        return o;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(o, i);
    }
}
