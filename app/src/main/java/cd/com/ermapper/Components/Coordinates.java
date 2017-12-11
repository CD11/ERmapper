package cd.com.ermapper.Components;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CD on 9/15/2017.
 */

public class Coordinates implements Parcelable {
    /* This Class represents where on the canvas an object is located
     */

    // local Variables
    float x;
    float y;
    float width;
    float height;

    //Constructors

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

    // check if x and y are contained in teh objects coordinates
    public boolean contains(float x, float y) {
        return this.x+5 < x && this.width+5 > x && this.y+5 < y && this.height+5 > y;
    }

    // Get center on X axis
    public float centerX() {
        float c;
        c = this.x + ((this.width - this.x)/2);
        return c;
    }
    // Get center on Y axis
    public float centerY() {
        float c;
        c = this.y + ((this.height - this.y)/2);
        return c;
    }

    // Setters and getters
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
    public void setX(float x) {
        this.x = x;
    }
    public void setWidth(float width) {
        this.width = width;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void setHeight(float height) {
        this.height = height;
    }
    public String toString(){
        String s ="";
        s += this.getX() +","+this.getY() +","+this.getWidth() +","+this.getHeight();
        return s;
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
