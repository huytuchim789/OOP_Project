/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import model.Constraint;
import model.Model;
import model.Point;

/**
 *
 * @author JUAN DAVID
 */
public class GraphCanvas extends Canvas{
    
    public Model model;
    

    public GraphCanvas() {
        super();
    }
    
    
    public void loadModel(Model model){
        this.model=model;
    }
    
    
    public void paintAxisWithNegatives(Graphics g){
        int w=this.getWidth();
            int h=this.getHeight();
          g.setColor(Color.black);
            g.setFont(new Font("Tahoma",Font.BOLD,11));
            g.drawLine(10, h/2, w-10, h/2);
            g.drawString("X", w-10 + 2, h/2 + 4);
            g.drawLine(w/2, 10, w/2, h-10);
            g.drawString("Y", w/2 - 2, 10 - 1);
            int originX=w/2;
            int originY=h/2;
            int size=h-20;
            for (int i = -10; i <= 10; i++) {
            g.drawLine(originX+i*(size/20), originY-2, originX+i*(size/20), originY+2);
        }
            for (int i = -10; i <= 10; i++) {
            g.drawLine(originX-2,originY-i*(size/20), originX+2, originY-i*(size/20));
        }
    }
    
    public void paintAxisWithoutNegatives(Graphics g){
         int w=this.getWidth();
            int h=this.getHeight();
                    g.setColor(Color.black);
            g.setFont(new Font("Tahoma",Font.BOLD,11));
            g.drawLine(10, 10, 10, h-10);
            g.drawLine(10, h-10, w-10, h-10);
            g.drawString("Y", 10-2, 10-1);
            g.drawString("X", w-10 + 2, h-10 + 4);
            int axisSize=h-20;
            int originX=10;
            int originY=h-10;
            for (int i = 1; i <= 10; i++) {
            g.drawLine(8, originY-i*(axisSize/10), 12,originY- i*(axisSize/10));
            }
            
            for (int i = 1; i <= 10; i++) {
            g.drawLine(originX+i*(axisSize/10), h-8, originX+i*(axisSize/10),h-12);
            }
    }
    
    public void paintNumbersWithNegative(Graphics g, double maxX, double maxY){
         int w=this.getWidth();
            int h=this.getHeight();
        int axisSize=h-20;
            
            g.setFont(new Font("Tahoma",Font.PLAIN,9));
        for(int i=0; i<=20; i++){
            if(i!=10){
                if(i%2==0){
                g.drawString(String.format("%.2f", -maxX + i*(maxX/10)), 5+i*(axisSize/20)-1, h/2 -5);
            }else{
                 g.drawString(String.format("%.2f", -maxX + i*(maxX/10)), 5+i*(axisSize/20)-1, h/2 +12);
            }
            }
            
            
        }
        
        for(int i=0; i<=20; i++){
            if(i!=10){
               
                g.drawString(String.format("%.2f", maxY - i*(maxY/10)),w/2+5, 10+i*(axisSize/20)+3);
            
            }
            
            
        }
    }
    
    public void paintNumbersWithoutNegatives(Graphics g, double maxX, double maxY){
         int w=this.getWidth();
            int h=this.getHeight();
              int axisSize=h-20;
            int originX=10;
            int originY=h-10;
            g.setFont(new Font("Tahoma",Font.PLAIN,9));
            for (int i = 1; i <= 10; i++) {
            g.drawString(String.format("%.2f", i*(maxX/10)), originX+i*(axisSize/10)-7, originY-5);
            g.drawString(String.format("%.2f", i*(maxY/10)),  originX+5,originY-i*(axisSize/10)+3);
        }
    }
    
    public Point transformPointWithNegatives(Point p, double maxX, double maxY){
        int w=this.getWidth();
        int h=this.getHeight();
        //Transform X
        double xt=( ((w-20)/(2*maxX))*p.x  )+((w-20)/2)+10;
        double yt=(((h-20)/(-2*maxY))*p.y)+((h-20)/2)+10;
        return new Point(xt,yt);
        
    }
    
    public void paintWhiteFrame(Graphics g){
        Graphics2D g2d=(Graphics2D)g.create();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, 500, 10);
        g2d.fillRect(this.getWidth()-10, 0, 10, 500);
        g2d.fillRect(0, this.getHeight()-10, 500, 10);
        g2d.fillRect(0, 0, 10, 500);
    }
    
    public void paintRestrictionsWithNegatives(Graphics g, double maxX, double maxY){
        ArrayList<Constraint>constraints=model.constraints;
        Graphics2D g2d=(Graphics2D)g.create();
        Stroke s=new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(s);
//        AlphaComposite ac =
//  AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
//        g2d.setComposite(ac);
        int cont=0;
        for(Constraint c:constraints){
            if(c.type.equals(Constraint.LESS_OR_EQUAL) || c.type.equals(Constraint.MORE_OR_EQUAL) ){
                cont++;
            }
        }
        if(cont==0){
            cont=1;
        }
        
        Color drawColor=new Color(0,255,0,255/cont);
        
        for(Constraint c:constraints){
            ArrayList<Point>points=c.pointsOfIntersectionWithAxes();
            g2d.setColor(drawColor);
            if(points.size()>0){
                 
                if(points.size()==1){
                    if(c.yCoefficient==0){
                       Point p1=new Point(c.rightCoefficient/c.xCoefficient,maxY); 
                       Point p2=new Point(c.rightCoefficient/c.xCoefficient,-maxY); 
                       p1=this.transformPointWithNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithNegatives(p2, maxX, maxY);
                       
                       if(c.type.equals(Constraint.LESS_OR_EQUAL)){
                          int[]xs={10,(int)p1.x,(int)p2.x,10};
                          int[]ys={10,10,this.getHeight()-10,this.getHeight()-10};
                        g2d.fillPolygon(xs, ys, 4);
                       }else if(c.type.equals(Constraint.MORE_OR_EQUAL)){
                           int[]xs={(int)p1.x,this.getWidth()-10,this.getWidth()-10,(int)p2.x};
                          int[]ys={10,10,this.getHeight()-10,this.getHeight()-10};
                          g2d.fillPolygon(xs, ys,4);
                       }
                       g2d.setColor(Color.black);
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                       
                    }else{
                        try{
                         Point p1=new Point(-maxX,c.giveYof(-maxX)); 
                       Point p2=new Point(maxX,c.giveYof(maxX)); 
                       p1=this.transformPointWithNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithNegatives(p2, maxX, maxY);
                     if(c.type.equals(Constraint.LESS_OR_EQUAL)){
                         if(c.yCoefficient>0){
                             int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                          int[]ys={(int)p1.y,(int)p2.y,this.getHeight()-10,this.getHeight()-10};
                          g2d.fillPolygon(xs, ys,4);
                         }else{
                             int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                             int[]ys={(int)p1.y,(int)p2.y,10,10};
                             g2d.fillPolygon(xs, ys,4);
                         }
                          
                      }else if(c.type.equals(Constraint.MORE_OR_EQUAL)){
                          if(c.yCoefficient>0){
                              int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                             int[]ys={(int)p1.y,(int)p2.y,10,10};
                             g2d.fillPolygon(xs, ys,4);
                          }else{
                              int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                          int[]ys={(int)p1.y,(int)p2.y,this.getHeight()-10,this.getHeight()-10};
                          g2d.fillPolygon(xs, ys,4);
                          }
                            
                      }
                       g2d.setColor(Color.black);
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                        }catch(Exception e){
                            
                        }
                        
                       
                    }
                    
                }else{
                     try{
                         Point p1=new Point(-maxX,c.giveYof(-maxX)); 
                       Point p2=new Point(maxX,c.giveYof(maxX)); 
                       p1=this.transformPointWithNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithNegatives(p2, maxX, maxY);
                        if(c.type.equals(Constraint.LESS_OR_EQUAL)){
                            if(c.yCoefficient>0){
                                int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                          int[]ys={(int)p1.y,(int)p2.y,this.getHeight()-10,this.getHeight()-10};
                          g2d.fillPolygon(xs, ys,4);
                            }else{
                                 int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                             int[]ys={(int)p1.y,(int)p2.y,10,10};
                             g2d.fillPolygon(xs, ys,4);
                            }
                          
                      }else if(c.type.equals(Constraint.MORE_OR_EQUAL)){
                          if(c.yCoefficient>0){
                              int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                             int[]ys={(int)p1.y,(int)p2.y,10,10};
                             g2d.fillPolygon(xs, ys,4);
                          }else{
                                int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                          int[]ys={(int)p1.y,(int)p2.y,this.getHeight()-10,this.getHeight()-10};
                          g2d.fillPolygon(xs, ys,4);
                          }
                            
                      }
                        g2d.setColor(Color.black);
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                        }catch(Exception e){
                            
                        }
                    
                }
            }
        }
    }
    
    public Point transformPointWithoutNegatives(Point p, double maxX, double maxY){
        int w=this.getWidth();
        int h=this.getHeight();
        double xt=(((20-w)/(-maxX))*p.x)+10;
        double yt=(((h-20)/(-maxY))*p.y)+h-10;
        return new Point(xt,yt);
    }
    
    public void paintLinesWithoutNegatives(Graphics g, double maxX, double maxY){
        
         Graphics2D g2d=(Graphics2D)g.create();
        ArrayList<Constraint>constraints=model.constraints;
            Stroke s=new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(s);
        for(Constraint c:constraints){
            ArrayList<Point>points=c.pointsOfIntersectionWithAxes();
           
            if(points.size()>0){
                 
                if(points.size()==1){
                    if(c.yCoefficient==0){
                       Point p1=new Point(c.rightCoefficient/c.xCoefficient,maxY); 
                       Point p2=new Point(c.rightCoefficient/c.xCoefficient,-maxY); 
                       p1=this.transformPointWithoutNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithoutNegatives(p2, maxX, maxY);
                       
                       
                       g2d.setColor(Color.black);
                        if(c.type.equals(Constraint.EQUAL)){
                           g2d.setColor(Color.red);
                       }
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                       
                    }else{
                        try{
                         Point p1=new Point(-maxX,c.giveYof(-maxX)); 
                       Point p2=new Point(maxX,c.giveYof(maxX)); 
                       p1=this.transformPointWithoutNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithoutNegatives(p2, maxX, maxY);
                    
                       g2d.setColor(Color.black);
                        if(c.type.equals(Constraint.EQUAL)){
                           g2d.setColor(Color.red);
                       }
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                        }catch(Exception e){
                            
                        }
                        
                       
                    }
                    
                }else{
                     try{
                         Point p1=new Point(-maxX,c.giveYof(-maxX)); 
                       Point p2=new Point(maxX,c.giveYof(maxX)); 
                       p1=this.transformPointWithoutNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithoutNegatives(p2, maxX, maxY);
                        
                        g2d.setColor(Color.black);
                         if(c.type.equals(Constraint.EQUAL)){
                           g2d.setColor(Color.red);
                       }
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                        }catch(Exception e){
                            
                        }
                    
                }
            }
        }
    }
    
    public void paintFeasiblePoints(Graphics g,double maxX, double maxY){
        Graphics2D g2d=(Graphics2D)g.create();
        ArrayList<Point>points=model.giveAllFeasiblePoints();
        for (int i = 0; i < points.size(); i++) {
            if(model.allowsNegatives){
                Point p=this.transformPointWithNegatives(points.get(i), maxX, maxY);
                g2d.setColor(Color.yellow);
                g2d.fillOval((int)p.x-2, (int)p.y-2, 4, 4);
                g2d.setColor(Color.black);
                 g2d.drawOval((int)p.x-2, (int)p.y-2, 4, 4);
            }else{
                Point p=this.transformPointWithoutNegatives(points.get(i), maxX, maxY);
                 g2d.setColor(Color.yellow);
                g2d.fillOval((int)p.x-2, (int)p.y-2, 4, 4);
                g2d.setColor(Color.black);
                 g2d.drawOval((int)p.x-2, (int)p.y-2, 4, 4);
            }
            
        }
    }
    
     public void paintLinesWithNegatives(Graphics g, double maxX, double maxY){
         
          ArrayList<Constraint>constraints=model.constraints;
        Graphics2D g2d=(Graphics2D)g.create();
        Stroke s=new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(s);
         for(Constraint c:constraints){
            ArrayList<Point>points=c.pointsOfIntersectionWithAxes();
       
            if(points.size()>0){
                 
                if(points.size()==1){
                    if(c.yCoefficient==0){
                       Point p1=new Point(c.rightCoefficient/c.xCoefficient,maxY); 
                       Point p2=new Point(c.rightCoefficient/c.xCoefficient,-maxY); 
                       p1=this.transformPointWithNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithNegatives(p2, maxX, maxY);
                       
                       
                       g2d.setColor(Color.black);
                         if(c.type.equals(Constraint.EQUAL)){
                           g2d.setColor(Color.red);
                       }
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                       
                    }else{
                        try{
                         Point p1=new Point(-maxX,c.giveYof(-maxX)); 
                       Point p2=new Point(maxX,c.giveYof(maxX)); 
                       p1=this.transformPointWithNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithNegatives(p2, maxX, maxY);
                    
                       g2d.setColor(Color.black);
                        if(c.type.equals(Constraint.EQUAL)){
                           g2d.setColor(Color.red);
                       }
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                        }catch(Exception e){
                            
                        }
                        
                       
                    }
                    
                }else{
                     try{
                         Point p1=new Point(-maxX,c.giveYof(-maxX)); 
                       Point p2=new Point(maxX,c.giveYof(maxX)); 
                       p1=this.transformPointWithNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithNegatives(p2, maxX, maxY);
                      
                        g2d.setColor(Color.black);
                         if(c.type.equals(Constraint.EQUAL)){
                           g2d.setColor(Color.red);
                       }
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                        }catch(Exception e){
                            
                        }
                    
                }
            }
        }
    }
    
    public void paintRestrictionsWithoutNegatives(Graphics g,double maxX, double maxY){
        Graphics2D g2d=(Graphics2D)g.create();
        ArrayList<Constraint>constraints=model.constraints;
         Stroke s=new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
       g2d.setStroke(s);
         int cont=0;
        for(Constraint c:constraints){
            if(c.type.equals(Constraint.LESS_OR_EQUAL) || c.type.equals(Constraint.MORE_OR_EQUAL) ){
                cont++;
            }
        }
        if(cont==0){
            cont=1;
        }
        Color drawColor=new Color(0,255,0,255/cont);
        
        for(Constraint c:constraints){
            ArrayList<Point>points=c.pointsOfIntersectionWithAxes();
            g2d.setColor(drawColor);
            if(points.size()>0){
                 
                if(points.size()==1){
                    if(c.yCoefficient==0){
                       Point p1=new Point(c.rightCoefficient/c.xCoefficient,maxY); 
                       Point p2=new Point(c.rightCoefficient/c.xCoefficient,-maxY); 
                       p1=this.transformPointWithoutNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithoutNegatives(p2, maxX, maxY);
                       
                       if(c.type.equals(Constraint.LESS_OR_EQUAL)){
                          int[]xs={10,(int)p1.x,(int)p2.x,10};
                          int[]ys={10,10,this.getHeight()-10,this.getHeight()-10};
                        g2d.fillPolygon(xs, ys, 4);
                       }else if(c.type.equals(Constraint.MORE_OR_EQUAL)){
                           int[]xs={(int)p1.x,this.getWidth()-10,this.getWidth()-10,(int)p2.x};
                          int[]ys={10,10,this.getHeight()-10,this.getHeight()-10};
                          g2d.fillPolygon(xs, ys,4);
                       }
                       g2d.setColor(Color.black);
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                       
                    }else{
                        try{
                         Point p1=new Point(-maxX,c.giveYof(-maxX)); 
                       Point p2=new Point(maxX,c.giveYof(maxX)); 
                       p1=this.transformPointWithoutNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithoutNegatives(p2, maxX, maxY);
                     if(c.type.equals(Constraint.LESS_OR_EQUAL)){
                         if(c.yCoefficient>0){
                              int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                          int[]ys={(int)p1.y,(int)p2.y,this.getHeight()-10,this.getHeight()-10};
                          g2d.fillPolygon(xs, ys,4);
                         }else{
                            int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                             int[]ys={(int)p1.y,(int)p2.y,10,10};
                             g2d.fillPolygon(xs, ys,4); 
                         }
                         
                      }else if(c.type.equals(Constraint.MORE_OR_EQUAL)){
                          if(c.yCoefficient>0){
                               int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                             int[]ys={(int)p1.y,(int)p2.y,10,10};
                             g2d.fillPolygon(xs, ys,4);
                          }else{
                                   int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                          int[]ys={(int)p1.y,(int)p2.y,this.getHeight()-10,this.getHeight()-10};
                          g2d.fillPolygon(xs, ys,4);
                          }
                           
                      }
                       g2d.setColor(Color.black);
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                        }catch(Exception e){
                            
                        }
                        
                       
                    }
                    
                }else{
                     try{
                         Point p1=new Point(-maxX,c.giveYof(-maxX)); 
                       Point p2=new Point(maxX,c.giveYof(maxX)); 
                       p1=this.transformPointWithoutNegatives(p1, maxX, maxY);
                       p2=this.transformPointWithoutNegatives(p2, maxX, maxY);
                        if(c.type.equals(Constraint.LESS_OR_EQUAL)){
                            if(c.yCoefficient>0){
                              int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                          int[]ys={(int)p1.y,(int)p2.y,this.getHeight()-10,this.getHeight()-10};
                          g2d.fillPolygon(xs, ys,4);  
                            }else{
                                  int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                             int[]ys={(int)p1.y,(int)p2.y,10,10};
                             g2d.fillPolygon(xs, ys,4);
                            }
                          
                      }else if(c.type.equals(Constraint.MORE_OR_EQUAL)){
                          if(c.yCoefficient>0){
                              int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                             int[]ys={(int)p1.y,(int)p2.y,10,10};
                             g2d.fillPolygon(xs, ys,4); 
                          }else{
                                int[]xs={(int)p1.x, (int)p2.x,this.getWidth()-10,10};
                          int[]ys={(int)p1.y,(int)p2.y,this.getHeight()-10,this.getHeight()-10};
                          g2d.fillPolygon(xs, ys,4); 
                          }
                           
                      }
                        g2d.setColor(Color.black);
                       g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                        }catch(Exception e){
                            
                        }
                    
                }
            }
        }
        
    }
    
    @Override
    public void paint(Graphics g){
      
        if(model==null){
            int w=this.getWidth();
            int h=this.getHeight();
            g.setColor(Color.white);
            g.fillRect(0, 0, w, h);
        }else{
            if(model.allowsNegatives){
            int w=this.getWidth();
            int h=this.getHeight();
             g.setColor(Color.white);
            g.fillRect(0, 0, w, h);
          
            
            ArrayList<Point> points=model.giveAllFeasiblePoints();
            if(points.size()>0){
                 double maxX=Double.NEGATIVE_INFINITY;
                for (int i = 0; i < points.size(); i++) {
                    if(Math.abs(points.get(i).x)>maxX){
                        maxX=Math.abs(points.get(i).x);
                    }
                }
                
                double maxY=Double.NEGATIVE_INFINITY;
                for (int i = 0; i < points.size(); i++) {
                    if(Math.abs(points.get(i).y)>maxY){
                        maxY=Math.abs(points.get(i).y);
                    }
                }
                if(maxX==0){
                    maxX=10;
                }
                
                if(maxY==0){
                    maxY=10;
                }
                //pintar numeros
              
                this.paintRestrictionsWithNegatives(g, maxX, maxY);
                this.paintLinesWithNegatives(g, maxX, maxY);
                this.paintWhiteFrame(g);
                this.paintAxisWithNegatives(g);
                  this.paintNumbersWithNegative(g, maxX, maxY);
                  this.paintFeasiblePoints(g, maxX, maxY);
            }
           
                
                
            
        }else{
            int w=this.getWidth();
            int h=this.getHeight();
            g.setColor(Color.white);
            g.fillRect(0, 0, w, h);
            
             ArrayList<Point> points=model.giveAllFeasiblePoints();
             if(points.size()>0){
                 for (int i = 0; i < points.size(); i++) {
                     if(points.get(i).x<0 || points.get(i).y<0 ){
                         points.remove(i);
                         i--;
                     }
                     
                 }
                 
                  double maxX=Double.NEGATIVE_INFINITY;
                for (int i = 0; i < points.size(); i++) {
                    if((points.get(i).x)>maxX){
                        maxX=(points.get(i).x);
                    }
                }
                
                double maxY=Double.NEGATIVE_INFINITY;
                for (int i = 0; i < points.size(); i++) {
                    if((points.get(i).y)>maxY){
                        maxY=(points.get(i).y);
                    }
                }
                if(maxX==0){
                    maxX=10;
                }
                
                if(maxY==0){
                    maxY=10;
                }
                 
               
                 this.paintRestrictionsWithoutNegatives(g, maxX, maxY);
                 this.paintLinesWithoutNegatives(g, maxX, maxY);
                 this.paintWhiteFrame(g);
                 this.paintAxisWithoutNegatives(g);
                  this.paintNumbersWithoutNegatives(g, maxX, maxY);
                 this.paintFeasiblePoints(g, maxX, maxY);
             }
        }
        }
        
    }
}
