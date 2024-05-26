package fr.cyu.chroma;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.shape.Circle;


public class Pointer {
    private double pos_x = 400;
    private double pos_y = 400;
    private double direction = 0.0;
    private boolean is_shown = true;
    private int opacity = 100;
    private int thick = 5;
    private int onAction = 0;
    private boolean animationEnCours = false;
    private Circle cursor;
    private int speed;

    private final GraphicsContext gc;

    private Color[] colors = {Color.BLUE, Color.RED, Color.GREEN};
    private Color currentColor = Color.BLACK; // currentColor declaration

    public Pointer(GraphicsContext gc) {
        this.gc = gc;
        this.cursor = new Circle(pos_x, pos_y, 5, currentColor);

    }
    public Circle getCursor() {
        return cursor;
    }

    public double getPos_x() {
        return this.pos_x;
    }

    public double getPos_y() {
        return this.pos_y;
    }

    public double getDirection() {
        return direction;
    }

    public boolean isIs_shown() {
        return is_shown;
    }

    public int getCursorOpacity() {
        return opacity;
    }

    public int getThick() {
        return thick;
    }

    public int getSpeed() { return speed; }



    public void setPos_x(double pos_x) {
        this.pos_x = pos_x;
    }

    public void setPos_y(double pos_y) {
        this.pos_y = pos_y;
    }

    public void setDirection(double direction) { this.direction = direction; }

    public void setOpacity(int opacity) { this.opacity = opacity; }

    public void setThickness(int thick) { this.thick = thick; }
    public void setSpeed(double speedSlider) {
        switch ((int) speedSlider) {
            case 100:
                this.speed = 0;
                break;
            case 50:
                this.speed = 10;
                break;
            case 0:
                this.speed = 40;
                break;
            default:
                System.out.println("Invalid speed value: " + speed);
                break;
        }
    }

    public String toString() {
        return  "1 (" + getPos_x() + "," + getPos_y() + ")";
    }


    /**
     * this function define how it forward of 1
     *
     * @param pointsX horizontal position
     * @param pointsY vertical position
     * @param value
     * @param direction
     */
    public void doodleTracker(double[] pointsX, double[] pointsY, double value, double direction) {
        AnimationTimer timer = new AnimationTimer() {
            int count = 0;
            double deltaX = Math.cos(direction * Math.PI / 180);
            double deltaY = Math.sin(direction * Math.PI / 180);

            @Override
            public void handle(long now) {
                pointsX[0] = pointsX[1];
                pointsY[0] = pointsY[1];
                if (value < 0) {
                    pointsX[1] -= deltaX;   //backward if value is negative
                    pointsY[1] -= deltaY;
                } else {                //forward if it is positive
                    pointsX[1] += deltaX;
                    pointsY[1] += deltaY;
                }
                gc.setStroke(currentColor); //Use of currentColor to define the color of the stroke
                gc.setLineWidth(2);
                gc.strokeLine(pointsX[0], pointsY[0], pointsX[1], pointsY[1]);

                try {
                    TimeUnit.MILLISECONDS.sleep(speed);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                count++;

                if (count >= Math.abs(value)) {
                    onAction = 0;
                    stop();
                }
            }
        };
        timer.start();
    }

    /**
     * the function that will make the cursor forward
     *
     * @param value the value the cursor forward
     */
    public void fwd(double value) {
        double x1 = this.pos_x;
        double y1 = this.pos_y;
        double[] pointsX = {x1, x1};
        double[] pointsY = {y1, y1};
        doodleTracker(pointsX,pointsY,value,direction);         //define what it does if forward of 1

        pos_x += value * Math.cos(direction * Math.PI / 180);       //horizontal part
        pos_y += value * Math.sin(direction * Math.PI / 180);       //vertical part
    }

    /**
     * this function make the cursor go backward
     * @param value value the cursor backward
     */
    public void bwd(double value){
        fwd(-1*value);
    }

    /**
     * the cursor turn in the clockwise
     * @param i value in degree it turns
     */
    public void turnRight(int i){
        direction+=i;
    }
    public void turnRight(double i){
        direction+= i;
    }

    /**
     * turn in the trigonometric sense
     * @param i value in degree it turns
     */
    public void turnLeft(int i){
        direction-=i;
    }
    public void turnLeft(double i){
        direction-= i;
    }

    /**
     * this function make the cursor move to a position without drawing
     * @param x
     * @param y
     */
    public void pos(double x, double y){
        pos_x=x;
        pos_y=y;
    }

    /**
     * make the cursor move of x in horizontal and y in vertical with drawing
     * @param x value of the horizontal forward
     * @param y value of the vertical forward
     */
    public void move(double x, double y) {
        double newXpoint = pos_x +x;
        double newYpoint = pos_y +y;
        lookat(newXpoint,newYpoint);
        double distance = Math.sqrt(Math.pow(newXpoint - pos_x, 2) + Math.pow(newYpoint - pos_y, 2));
        fwd(distance);
    }

    /**
     * change the direction to make the cursor go to th point if it forwards
     * @param x
     * @param y
     */
    public void lookat(double x, double y){
        direction = Math.toDegrees(Math.atan2(y - pos_y, x - pos_x));
    }

    /**
     * hide the cursor
     */
    public void hide(){
        is_shown = false;
    }

    /**
     * show the cursor
     */
    public void show(){
        is_shown = true;
    }

    /**
     * change the color with a rgb code
     * @param red
     * @param green
     * @param blue
     */
    public void setColor(int red, int green, int blue){
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255){
            throw new IllegalArgumentException("RGB values must be between 0 and 255.");
        }

        this.currentColor = Color.rgb(red,green,blue);

    }

    /**
     * change the color with a hexadcimal code
     * @param hexadecimal
     */
    public void setColor(String hexadecimal){
        this.currentColor = Color.web(hexadecimal);
    }

    /**
     * change the color with a rgb code in decimal
     *
     * @param red
     * @param green
     * @param blue
     */
    public void setColor(double red, double green, double blue){
        if (red < 0.0 || red > 1.0 || green < 0.0 || green > 1.0 || blue < 0.0 || blue > 1.0){
            throw new IllegalArgumentException("RGB values must be between 0.0 and 1.0");
        }

        this.currentColor = Color.color(red, green, blue);
    }

    /**
     * change the color with an instance of the Color class
     *
     * @param color
     */
    public void setColor(Color color){
        this.currentColor = color;
    }

    public double distance(Pointer other) { return Math.sqrt(Math.pow(pos_x - other.pos_x, 2) + Math.pow(pos_y - other.pos_y, 2)); }

    public double distance(double x, double y) { return Math.sqrt(Math.pow(pos_x - x, 2) + Math.pow(pos_y - y, 2)); }

    public void getPosMirror(double x, double y){
        double d = 0, angle = this.direction;
        Pointer p = new Pointer(gc);
        p.pos(x,y);
        lookat(p.pos_x,p.pos_y);
        d = distance(p);
        pos(x,y);
        this.setPos_x( pos_x + d * Math.cos(this.direction * Math.PI / 180));
        this.setPos_y( pos_y + d * Math.sin(this.direction * Math.PI / 180));
        setDirection(angle+180);
    }

    public void getPosMirror(double x1, double y1, double x2, double y2){
        Pointer p1 = new Pointer(gc);
        Pointer p2 = new Pointer(gc);
        p1.setPos_x(x1);
        p1.setPos_y(y1);
        p2.setPos_x(x2);
        p2.setPos_y(y2);

        double a = 0, b=0,c=0,alpha = 0, beta = 0, angle = this.direction;
        double x = pos_x;
        double y = pos_y;
        Pointer tempPointer = new Pointer(gc);
        tempPointer.pos(x,y);

        this.pos(p1.pos_x,p1.pos_y);
        lookat(x,y);
        double angle1 = direction;
        lookat(p2.pos_x,p2.pos_y);
        double angle2 = direction;
        double diff = angle1 - angle2;

        c = p1.distance(p2);
        b = tempPointer.distance(p1);
        a = tempPointer.distance(p2);

        alpha =  Math.toDegrees(Math.acos((b * b + c * c -a * a) / (2*(b * c))));

        if( Math.abs(diff) < 180){
            if (diff > 0)
            {
                this.turnLeft(alpha);
            }
            else this.turnRight(alpha);
        }
        else{
            if (diff > 0) this.turnRight(alpha);
            else this.turnLeft(alpha);
        }
        this.setPos_x( pos_x + b * Math.cos(this.direction * Math.PI / 180));
        this.setPos_y( pos_y + b * Math.sin(this.direction * Math.PI / 180));

        p1.lookat(p2.pos_x, p2.pos_y);
        p2.lookat(p1.pos_x, p1.pos_y);

        setDirection(angle);
        if( Math.abs(p1.direction - angle) < Math.abs(p2.direction - angle) ){
            turnRight(2*(p1.direction - angle));
        }
        else{
            turnRight(2*(p2.direction - angle));
        }

    }

    public void drawCross(double side){
        side = side/2;
        Pointer p1 = new Pointer(gc);
        Pointer p2 = new Pointer(gc);
        p1.pos(pos_x,pos_y);
        p2.pos(pos_x,pos_y);
        p1.setDirection(-135);
        p1.setDirection(45);

        p1.setPos_x( pos_x + side * Math.cos(this.direction * Math.PI / 180));
        p1.setPos_y( pos_y + side * Math.sin(this.direction * Math.PI / 180));
        p2.setPos_x( pos_x + (-side) * Math.cos(this.direction * Math.PI / 180));
        p2.setPos_y( pos_y + (-side) * Math.sin(this.direction * Math.PI / 180));

        p1.setDirection(0);
        p1.setDirection(180);
        p1.fwd(side);
        p2.fwd(side);
        p1.turnLeft(90);
        p2.turnLeft(90);
        p1.fwd(side);
        p2.fwd(side);
    }
    public void drawSquare(double side){
        double centretovertex = ( side * Math.sqrt(2) )/2;
        Pointer p1 = new Pointer(gc);
        p1.pos(pos_x,pos_y);
        p1.setDirection(135);

        p1.setPos_x( pos_x + (centretovertex) * Math.cos(p1.direction * Math.PI / 180));
        p1.setPos_y( pos_y + (centretovertex) * Math.sin(p1.direction * Math.PI / 180));
        p1.setDirection(0);
        p1.fwd(side);
        for(int i = 0 ; i < 3;i++){
            p1.turnLeft(90);
            p1.fwd(side);
        }
    }
    public void drawFillSquare(double side){
        double centretovertex = ( side * Math.sqrt(2) )/2;
        Pointer p1 = new Pointer(gc);
        p1.pos(pos_x,pos_y);
        p1.setDirection(-135);

        p1.setPos_x( pos_x + (centretovertex) * Math.cos(p1.direction * Math.PI / 180));
        p1.setPos_y( pos_y + (centretovertex) * Math.sin(p1.direction * Math.PI / 180));
        gc.setFill(currentColor);
        gc.fillRect(p1.pos_x, p1.pos_y, side, side);
    }
    public void drawRectangle(double width, double height) {
        double centretovertex = Math.sqrt(Math.pow(width / 2, 2) + Math.pow(height / 2, 2));
        Pointer p1 = new Pointer(gc);
        p1.pos(pos_x - (width/2), pos_y + (height/2) );
       // p1.setDirection(135);
       // p1.setPos_x( pos_x + (centretovertex/2) * Math.cos(p1.direction * Math.PI / 180));
        //p1.setPos_y( pos_y + (centretovertex/2) * Math.sin(p1.direction * Math.PI / 180));
       // p1.setDirection(0);
        for(int i =0 ; i < 2 ; i++){
            p1.fwd(width);
            p1.turnLeft(90);
            p1.fwd(height);
            p1.turnLeft(90);
        }
    }
    public void drawFillRectangle(double width, double height){
        double centretovertex = Math.sqrt(Math.pow(width / 2, 2) + Math.pow(height / 2, 2));
        Pointer p1 = new Pointer(gc);
        p1.pos(pos_x - (width/2), pos_y - (height/2) );
        gc.setFill(currentColor);
        gc.fillRect(p1.pos_x, p1.pos_y, width, height);
    }

    public void drawTriangle(double side){
        double centretovertex = (side * Math.sqrt(3)) / 3;
        Pointer p1 = new Pointer(gc);
        p1.pos(pos_x,pos_y - centretovertex);
        p1.turnRight(60);
        p1.fwd(side);
        p1.turnRight(120);
        p1.fwd(side);
        p1.turnRight(120);
        p1.fwd(side);
    }

    public void drawFillTriangle(double side) {
        double centretovertex = (side * Math.sqrt(3)) / 3;
        double[] xPoints = new double[3];
        double[] yPoints = new double[3];
        Pointer p1 = new Pointer(gc);
        p1.pos(pos_x,pos_y - centretovertex);

        xPoints[0] = p1.pos_x;
        yPoints[0] = p1.pos_y;
        p1.turnRight(60);
        p1.setPos_x( p1.pos_x + side * Math.cos(p1.direction * Math.PI / 180));
        p1.setPos_y( p1.pos_y + side * Math.sin(p1.direction * Math.PI / 180));

        xPoints[1] = p1.pos_x;
        yPoints[1] = p1.pos_y;

        p1.turnRight(120);
        p1.setPos_x( p1.pos_x + side * Math.cos(p1.direction * Math.PI / 180));
        p1.setPos_y( p1.pos_y + side * Math.sin(p1.direction * Math.PI / 180));

        xPoints[2] = p1.pos_x;
        yPoints[2] = p1.pos_y;

        gc.setFill(currentColor);
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    public void drawCircle(double radius) {
        Pointer p1 = new Pointer(gc);
        Pointer p2 = new Pointer(gc);
        p1.pos(pos_x,pos_y-radius);
        p2.pos(pos_x,pos_y+radius);
        double numIterations = 2*Math.PI *radius;

        for (int j = 0; j < numIterations; j++) {
            p2.bwd(1);
            p2.turnRight(360 / numIterations);
        }
    }
    public void drawFillCircle(double radius) {
        gc.setFill(currentColor);
        gc.fillOval(pos_x - radius, pos_y - radius, 2 * radius, 2 * radius);
    }

}
