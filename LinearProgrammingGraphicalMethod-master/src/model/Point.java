package model;

/**
 * Class model for a coodinate point in 2D cartesian plane.
 */
public class Point {

	/**
         * double type X Coodinate of this point. 
         */
	public double x;
        
        	/**
         * double type Y Coodinate of this point. 
         */
	public double y;
	
	/**
         * Creates a new Point instance with the given coordinates.
         * @param x x coordinate for new Point instance.
         * @param y y coordinate for new Point instance.
         */
	public Point(double x, double y) {
		this.x=x;
		this.y=y;
	}
	
	
        /**
         * Overriden toString() method.
         * @return String representation of this Point instance.
         */
	public String toString() {
		return "( "+x+" , "+y+" )";
	}
}
