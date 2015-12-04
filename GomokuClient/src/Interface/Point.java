package Interface;

import java.lang.Math;

import java.util.ArrayList;

/**
 * Source From : https://code.google.com/p/gomoku/
 * The Point class represents points on the grid where game pieces may appear.
 */

public class Point {

    /**
     * Constants in pixels for determining and locating valid Points on the grid.
     */
    public static final int[] PIXEL_VALUES = getPixelValues();
    public static final int NORTH = 0;
    public static final int NORTHEAST = 45;
    public static final int EAST = 90;
    public static final int SOUTHEAST = 135;
    public static final int SOUTH = 180;
    public static final int SOUTHWEST = 225;
    public static final int WEST = 270;
    public static final int NORTHWEST = 315;
    public static final int[] DIRECTIONS = {NORTH, NORTHEAST, EAST, SOUTHEAST,
                                            SOUTH, SOUTHWEST, WEST, NORTHWEST};

    /**
     * Instance variables that represent a Cartesian (pixel) point on the grid.
     */
    private int x;
    private int y;

    /**
     * Class constructor initalizes this to (0, 0), which is off the grid.
     */
    public Point() {
        x = 0;
        y = 0;
    }

    /**
     * Class constructor that sets x and y to given parameters.
     *
     * @param  newX distance on horizontal x-axis
     * @param  newY distance on vertical y-axis
     */
    public Point(int newX, int newY) {
        x = newX;
        y = newY;
    }

    /**
     * Generate an array of valid pixel values for Points on the grid.
     * 
     * @return  an array of valid pixel values for Points on the grid
     */
    private static int[] getPixelValues() {
        int[] pixelValues = new int[Display.GRID_ROWS];
        for (int i = 0; i < Display.GRID_ROWS; i++) {
            pixelValues[i] = Display.MARGIN * (i+1);
        }
        return pixelValues;
    }

    /**
     * Accessor method for x.
     *
     * @return x
     */
    public int getX() { return x; }

    /**
     * Accessor method for y.
     *
     * @return y
     */
    public int getY() { return y; }

    /**
     * Mutator method for x.
     *
     * @param  newX distance on horizontal x-axis
     */
    public void setX(int newX) { x = newX; }

    /**
     * Mutator method for y.
     *
     * @param  y distance on vertical y-axis
     */
    public void setY(int newY) { y = newY; }

    /**
     * Mutator method for both x and y.
     *
     * @param  newX distance on horizontal x-axis
     * @param  newY distance on vertical y-axis
     */
    public void setAll(int newX, int newY) {
        x = newX;
        y = newY;
    }

    /**
     * Format Point as String in Cartesian format.
     *
     * @return  a String
     */
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Compare two Point objects by content.
     *
     * @param  obj generic Object that should be a Point
     * @return     true if this is equal to obj
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) {
            return false;
        }
        Point pointObj = (Point) obj;
        if (this.getX() == pointObj.getX() && this.getY() == pointObj.getY()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get closest Point value given a pixel distance.
     *
     * @param  px the pixel distance of either x or y
     * @return    valid Point value closest to pixel
     */
    private static int roundToPointValue(int px) {
        int min = 1000;  // cannot be a possible min value
        int val = 0;
        for (int value : Point.PIXEL_VALUES) {
            int diff = Math.abs(px-value);
            if (diff < min) {
                min = diff;
                val = value;
            }
        }
        return val;
    }

    /**
     * Create a Point object that is closest to given pixel distances.
     *
     * For example: 25px x 25px is (1, 1) Point on the grid.
     *
     * @param  x distance on horizontal x-axis
     * @param  y distance on vertical y-axis
     * @return   the closest Point object
     */
    public static Point convertFromPixel(int x, int y) {
        int newX = Point.roundToPointValue(x);
        int newY = Point.roundToPointValue(y);
        return new Point(newX, newY);
    }

    /**
    * Get adjacent Point in the given direction.
    *
    * @param  dir the direction in which to look for a Point
    * @return     the adjacent Point or null if out of bounds of the grid
    */
    public Point getAdjacent(int dir) {
        int newX = 0;
        int newY = 0;

        if (dir == NORTH) {
            newX = this.getX();
            newY = this.getY() - Display.MARGIN;
        } else if (dir == NORTHEAST) {
            newX = this.getX() + Display.MARGIN;
            newY = this.getY() - Display.MARGIN;
        } else if (dir == EAST) {
            newX = this.getX() + Display.MARGIN;
            newY = this.getY();
        } else if (dir == SOUTHEAST) {
            newX = this.getX() + Display.MARGIN;
            newY = this.getY() + Display.MARGIN;
        } else if (dir == SOUTH) {
            newX = this.getX();
            newY = this.getY() + Display.MARGIN;
        } else if (dir == SOUTHWEST) {
            newX = this.getX() - Display.MARGIN;
            newY = this.getY() + Display.MARGIN;
        } else if (dir == WEST) {
            newX = this.getX() - Display.MARGIN;
            newY = this.getY();
        } else if (dir == NORTHWEST) {
            newX = this.getX() - Display.MARGIN;
            newY = this.getY() - Display.MARGIN;
        }

        if (newX < Display.MARGIN || newX > Display.END_MARGIN ||
                newY < Display.MARGIN || newY > Display.END_MARGIN) {
            return null;
        }

        return new Point(newX, newY);
    }

    /**
     * Add all Points adjacent to a specified location in clockwise direction
     * to a list.
     *
     * @return  a list of all adjacent Points including null if not valid
     */
    public ArrayList<Point> getAdjacentAll () {
        ArrayList<Point> list = new ArrayList<Point>();
        for (int dir : Point.DIRECTIONS) {
            Point adjPoint = this.getAdjacent(dir);
            list.add(adjPoint);
        }
        return list;
    }
}