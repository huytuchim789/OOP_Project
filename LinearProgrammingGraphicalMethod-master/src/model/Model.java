package model;

import java.util.ArrayList;
import model.*;
/**
 * Class representating a linear programming problem with two decision variables.
 */
public class Model {
	
    /**
     * Property of type double representing the x variable coefficient for this Model's objective function.
     */
	public double xCoefficient;
        
     /**
     * Property of type double representing the y variable coefficient for this Model's objective function.
     */
	public double yCoefficient;
        
        /**
         * Property indicating if this Model allows negative variable values.
         */
	public boolean allowsNegatives;
        
        /**
         * Represents the type of objective function of this Model, either MAXIMIZE or MINIMIZE, both class constants.
         */
	public String type;
        
        /**
         * An ArrayList containing all Constraint instances associated to this Model.
         */
	public ArrayList<Constraint> constraints;
        
        /**
         * Constant for objective function of type MAXIMIZE.
         */
	public static final String MAXIMIZE="MAXIMIZE";
        
          /**
         * Constant for objective function of type MINIMIZE.
         */
	public static final String MINIMIZE="MINIMIZE";
	
        /**
         * Creates a new Model instance with given parameters.
         * @param xCoefficient double x coefficient for this model's objective function.
         * @param yCoefficient double y coefficient for this model's objective function.
         * @param allowsNegatives boolean for negative values allowance in this Model instance.
         * @param type Objective function type for this Model instance.
         */
	public Model(double xCoefficient, double yCoefficient, boolean allowsNegatives, String type) {
		super();
		this.xCoefficient = xCoefficient;
		this.yCoefficient = yCoefficient;
		this.allowsNegatives = allowsNegatives;
		this.type = type;
		this.constraints = new ArrayList<Constraint>();
	}
	
        /**
         * Method used to add a new Constraint instance to this Model.
         * @param c Constraint to be added.
         */
	public void addConstraint(Constraint c) {
		this.constraints.add(c);
	}
	
        
        /**
         * Evaluates the objective function value of the given Point instance.
         * @param p Point to be evaluated.
         * @return A double value, the evaluation of the Point in the function of this Model.
         */
	public double evaluateObjetive(Point p) {
		return this.xCoefficient*p.x + this.yCoefficient*p.y;
	}
        
        /**
         * Gives an ArrayList containing all Points that are axis intersections for each Constraint in this model.
         * @return An ArrayList of Points.
         */
        public ArrayList<Point>giveAxisIntersections(){
            ArrayList<Point>points=new ArrayList<>();
            for (int i = 0; i < constraints.size(); i++) {
			points.addAll(constraints.get(i).pointsOfIntersectionWithAxes());
		}
            return points;
        }
	
        /**
         * Gives an ArrayList containing all Points that are feasible intersection Points for this Model.
         * @return An ArrayList of Points.
         */
	public ArrayList<Point>giveAllFeasiblePoints(){
		ArrayList<Point>points=new ArrayList<Point>();
		points.add(new Point(0,0));
		for (int i = 0; i < constraints.size(); i++) {
			points.addAll(constraints.get(i).pointsOfIntersectionWithAxes());
		}
		
		for (int i = 0; i < constraints.size(); i++) {
			for (int j = i+1; j < constraints.size(); j++) {
				Point p=constraints.get(i).intersectionWith(constraints.get(j));
				if(p!=null) {
					points.add(p);
				}else{
                                    System.out.println("Interseccion nula");
                                }
			}
		}
		
		if(!this.allowsNegatives) {
			for (int i = 0; i < points.size(); i++) {
				if(points.get(i).x<0 || points.get(i).y<0) {
					points.remove(i);
					i--;
				}
			}
		}
		
		for (int i = 0; i < points.size(); i++) {
			boolean feasible=true;
			for (int j = 0; j < constraints.size() && feasible; j++) {
				if(!constraints.get(j).isSatisfiedBy(points.get(i))) {
					feasible=false;
					System.out.println("Discarding point "+points.get(i).toString()+" because of constraint "+constraints.get(j).toString());
					points.remove(i);
					i--;
				}
			}
		}
		System.out.println("Feasible points:");
		for (int i = 0; i < points.size(); i++) {
			System.out.println(points.get(i).toString());
		}
		return points;
	}
	
	/**
         * Gives a Point representing the optimum Point found within all feasible Points for this Model.
         * @return A Point instance. Can be null.
         */
	public Point giveOptimumPoint() {
		ArrayList<Point>feasiblePoints=this.giveAllFeasiblePoints();
		Point op=null;
		if(this.type.equals(this.MAXIMIZE)) {
			System.out.println("Maximum:");
			double max=Double.NEGATIVE_INFINITY;
			for (int i = 0; i < feasiblePoints.size(); i++) {
				if(evaluateObjetive(feasiblePoints.get(i))>max) {
					max=evaluateObjetive(feasiblePoints.get(i));
					op=feasiblePoints.get(i);
				}
			}
			
		}else {
			System.out.println("Minimum:");
			double min=Double.POSITIVE_INFINITY;
			for (int i = 0; i < feasiblePoints.size(); i++) {
				if(evaluateObjetive(feasiblePoints.get(i))<min) {
					min=evaluateObjetive(feasiblePoints.get(i));
					op=feasiblePoints.get(i);
				}
			}
		}
		return op;
	}
	
	/**
         * Overriden toString().
         * @return String representation of this Model instance.
         */
	public String toString() {
		String t="";
		String separator="";
		if(this.yCoefficient>=0) {
			separator="+";
		}
		if(this.type.equals(this.MAXIMIZE)) {
			t="MAXIMIZE";
		}else {
			t="MINIMIZE";
		}
		String st=t+" "+this.xCoefficient+"X"+separator+""+this.yCoefficient+"Y SUBJECT TO:"+"\n";
		if(!this.allowsNegatives) {
			st+="X,Y>=0"+"\n";
		}
		for (int i = 0; i < constraints.size(); i++) {
			st+=constraints.get(i).toString()+"\n";
		}
		return st;
	}
	
	
	

}
