package cd.com.ermapper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CD on 9/15/2017.
 */

public class Coordinates implements Parcelable {
    /* This Class represents where on the canvas an object is located
     */
    float x;
    float y;
    float width;
    float height;

    Coordinates(float i, float i1, float i2, float i3) {
        this.x = i;
        this.y = i1;
        this.width =   i2;
        this.height =   i3;
    }


    protected Coordinates(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
        width = in.readFloat();
        height = in.readFloat();
    }

    public static final Creator<Coordinates> CREATOR = new Creator<Coordinates>() {
        @Override
        public Coordinates createFromParcel(Parcel in) {
            return new Coordinates(in);
        }

        @Override
        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };

    public boolean contains(float x, float y) {
        return this.x < x && this.width > x && this.y < y && this.height > y;
    }

    public float centerX() {
        float c;
        c = this.x + ((this.width - this.x)/2);
        return c;
    }

    public float centerY() {
        float c;
        c = this.y + ((this.height - this.y)/2);
        return c;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(x);
        parcel.writeFloat(y);
        parcel.writeFloat(width);
        parcel.writeFloat(height);
    }
}
