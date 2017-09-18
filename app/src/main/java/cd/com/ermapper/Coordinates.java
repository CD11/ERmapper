package cd.com.ermapper;

/**
 * Created by CD on 9/15/2017.
 */

public class Coordinates {
    float x;
    float y;
    float width;
    float height;

    Coordinates(float i, float i1, float i2, float i3) {
        this.x = i;
        this.y = i1;
        this.width =  i + i2;
        this.height =  i1 + i3;
    }

    public boolean contains(float x, float y) {
        return this.x < x && this.width > x && this.y < y && this.height > y;
    }

}
