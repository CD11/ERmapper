package cd.com.ermapper.shapes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.shapes.Shape;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.util.ArrayList;

import static android.graphics.Color.BLACK;

/**
 * Created by CD on 11/9/2017.
 */

public class Cardinality implements Parcelable {

    private EditText num;
    private ShapeObject object;

    public Cardinality(Context c, ShapeObject o){
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

    public EditText getNum() {
        return num;
    }

    public ShapeObject getO() {
        return object;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(object, i);
    }
}
