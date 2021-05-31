package model;

import java.util.ArrayList;

/**
 * Class representation for a two variable constraint in a linear programming problem.
 */
public class Constraint {

    /**
     * This property identifies a Constraint instance with and Integer.
     */
	public int id;
        
        /**
         * Represents the coefficient associated with the variable X of the constraint.
         */
	public double xCoefficient;
        
         /**
         * Represents the coefficient associated with the variable Y of the constraint.
         */
	public double yCoefficient;
        
        /**
         * Represent the coefficient associated with the constraint value.
         */
	public double rightCoefficient;
        
        /**
         * Indicates the type of the constraint, either less or equal,more or equal or equal, this constraints types are defined as class constants.
         */
	public String type;
        
        /**
         * Constant associated with less or equal constraints.
         */
	public static final String LESS_OR_EQUAL="<=";
        
         /**
         * Constant associated with more or equal constraints.
         */
	public static final String MORE_OR_EQUAL=">=";
        
         /**
         * Constant associated with equal constraints.
         */
	public static final String EQUAL="=";
        
        /**
         * Property associated with double tolerance for constraint evaluation.
         */
	public static final double DELTA=0.000001;
	
        /**
         * Creates a new instance of Constraint.
         * @param id The int identifier for this Constraint.
         * @param xCoefficient X Coefficient value.
         * @param yCoefficient Y Coefficient value.
         * @param rightCoefficient Constraint Coefficient value.
         * @param type Type of Constraint.
         */
	public Constraint(int id,double xCoefficient, double yCoefficient, double rightCoefficient, String type) {
		super();
		this.id=id;
		if((xCoefficient==0 && yCoefficient==0) ) {
			throw new IllegalArgumentException("Inconsistent constraint specifications at constraint "+id+".");
		}
		
		this.xCoefficient = xCoefficient;
		this.yCoefficient = yCoefficient;
		this.rightCoefficient = rightCoefficient;
		this.type = type;
	}
	
        /**
         * Gives the value of evaluating the parameter Point with this constraint coefficients.
         * @param p A Point instance.
         * @return double value, indicating the linear combination of the Point coordinates with this constraint coefficients.
         */
	public double evaluate(Point p) {
            
              return (p.x*xCoefficient)+(p.y*yCoefficient);
            
	}
	
        /**
         * 
         * @param x The X coordinate to be evaluated in this Constraint.
         * @return The value of this Constraint in the form of y=f(x).
         * @throws Exception Exception thrown when the y value of the Constraint is 0.
         */
        public double giveYof(double x)throws Exception{
            if(this.yCoefficient==0){
		throw new Exception("Cannot calculate y value of a function with Y coefficient 0.");
            }else{
              return (this.rightCoefficient-(this.xCoefficient*x))/this.yCoefficient;
            }
            
        }
	
        /**
         * Indicates if this Constraint instance is satisfied by the Point parameter.
         * @param p Point to be evaluated.
         * @return A boolean value, true if the Point satisfies this Constraint, false otherwise.
         */
	public boolean isSatisfiedBy(Point p) {
		double evaluation=evaluate(p);
		if(this.type.equals(LESS_OR_EQUAL)) {
			return (evaluation<=this.rightCoefficient+DELTA);
		}else if(this.type.equals(MORE_OR_EQUAL)) {
			return (evaluation>=this.rightCoefficient-DELTA);
		}else {
			return (evaluation>=this.rightCoefficient-DELTA && evaluation<=this.rightCoefficient+DELTA);
		}
	}
	
        /**
         * Returns an ArrayList containing all intersections of this Constraint with the coordinate axes.
         * @return ArrayList of type Point.
         */
	public ArrayList<Point> pointsOfIntersectionWithAxes(){
		ArrayList<Point>cuts=new ArrayList<Point>();
		double c1=this.rightCoefficient/this.yCoefficient;
               
		if(!Double.isInfinite(c1) && !Double.isNaN(c1)){
			cuts.add(new Point(0,c1));
		}
		double c2=this.rightCoefficient/this.xCoefficient;
              
		if(!Double.isInfinite(c2) && !Double.isNaN(c2)){
			cuts.add(new Point(c2,0));
		}
		
		return cuts;
	}
	
        /**
         * Gives the intersection of this Constraint wtih the given parameter Constraint.
         * @param c Another Constraint instance.
         * @return A Point, representing the intersection between both Constraints. Can be null.
         */
	public Point intersectionWith(Constraint c) {
		double topPart=(this.rightCoefficient*c.yCoefficient) - (c.rightCoefficient*this.yCoefficient);
		double bottomPart=(this.xCoefficient*c.yCoefficient)-(c.xCoefficient*this.yCoefficient);
		double x=topPart/bottomPart;
		double y;
                if(this.yCoefficient!=0){
                    y=(this.rightCoefficient-(this.xCoefficient*x))/this.yCoefficient;
                }else{
                    y=(c.rightCoefficient-(c.xCoefficient*x))/c.yCoefficient;
                }
//                System.out.println("TOP:"+topPart);
//                 System.out.println("BOTTOM:"+bottomPart);
		if(!Double.isNaN(x)  && !Double.isNaN(y)) {
			return new Point(x,y);
		}else {
			return null;
		}
		
		
	}
	
        /**
         * Overriden toString() method.
         * @return String representation of this Constraint.
         */
public String toString() {
	String separator="";
	if(this.yCoefficient>=0) {
		separator="+";
	}
	return this.xCoefficient+"X"+separator+this.yCoefficient+"Y"+this.type+this.rightCoefficient;
}
	
	
}
