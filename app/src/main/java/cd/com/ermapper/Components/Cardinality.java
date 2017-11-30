package cd.com.ermapper.Components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import static android.graphics.Color.BLACK;

/**
 * Created by CD on 11/9/2017.
 */

public class Cardinality implements Parcelable {
    /* this class represents the cardinality of an Entity obejct in a relationship
        - contains an edit text id
        - is related to an object.
     */

    // variables
    private EditText num;
    private ShapeObject object;

    // constructors
    public Cardinality(Context c, ShapeObject o){
        if(c ==  null) return;
      num = new EditText(c);
      num.setText("1");
      this.object = o;
        num.setImeOptions(EditorInfo.IME_ACTION_DONE);
        num.setSingleLine();
        num.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        num.setTextColor(BLACK);
        num.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if(!view.hasFocus()){
                    object.setName(String.valueOf(num.getText()));
                }
            }
        });


    }


    protected Cardinality(Parcel in) {
        object = in.readParcelable(ShapeObject.class.getClassLoader());
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

    // Setters and Getters
    public EditText getNum() {
        return num;
    }
    public ShapeObject getO() {
        return object;
    }
    public void setO(ShapeObject o) {
        this.object = o;
    }
    public void setNum(EditText e){this.num = e;}
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(object, i);
    }


}
