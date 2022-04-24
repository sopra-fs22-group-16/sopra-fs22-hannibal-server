package ch.uzh.ifi.hase.soprafs22.game;

import java.awt.*;

public class Position {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // We need to define equals to compare them. Existing libraries have all this, and more.
    // Point https://docs.oracle.com/javase/7/docs/api/java/awt/Point.html seems to do exactly what we want, and we don't have to test it...
    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Position or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Position)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Position c = (Position) o;

        // Compare the data members and return accordingly
        return x == c.x && y == c.y;
    }
    @Override
    public String toString() {
        return String.format("(%s, %s)", x, y);
    }
}
