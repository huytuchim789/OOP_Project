package edmondskarp.Gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;

public abstract class MyShape {

    protected Point2D[] points;

    protected Point2D[] pointText;

    protected Color c;

    protected Shape shape;
    protected boolean select;

    public MyShape() {
    }

    public Shape getShape() {
        return shape;
    }

    public Point2D[] getPoints() {
        return points;
    }

    public void setPoints(Point2D a, Point2D b) {
        points[0] = a;
        points[1] = b;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean b) {
        select = b;
    }

    public void setColor(Color colorC) {
        c = colorC;
    }

    public Color getColor() {
        return c;
    }

    public abstract void draw(Graphics2D graphics2D);

    public abstract void drawText(Graphics2D graphics2D);
}
