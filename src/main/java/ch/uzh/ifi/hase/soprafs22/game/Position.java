package ch.uzh.ifi.hase.soprafs22.game;

import java.io.Serializable;

public class Position implements Serializable {
    private final int x;
    private final int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

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
        super.equals(o);
        // Compare the data members and return accordingly
        return x == c.x && y == c.y;
    }

    @Override
    public int hashCode() {
        return 17 + x + 31*y;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", x, y);
    }
}
