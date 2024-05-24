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

    private final GraphicsContext gc;

    private Color[] colors = {Color.BLUE, Color.RED, Color.GREEN};

    public Pointer(GraphicsContext gc) {
        this.gc = gc;
        this.cursor = new Circle(pos_x, pos_y, 5, Color.BLACK);

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

    public void setPos_x(double pos_x) {
        this.pos_x = pos_x;
    }

    public void setPos_y(double pos_y) {
        this.pos_y = pos_y;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }


    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public void setThickness(int thick) {
        this.thick = thick;
    }

    public String toString() {
        return  "1 (" + getPos_x() + "," + getPos_y() + ")";
    }

    //public Color setColor(int c) {return colors[c];}

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

                gc.setLineWidth(2);
                gc.strokeLine(pointsX[0], pointsY[0], pointsX[1], pointsY[1]);

                try {
                    TimeUnit.MILLISECONDS.sleep(0);
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
        System.out.println("Position initiale : (" + pos_x + ", " + pos_y + ")");
        double x1 = this.pos_x;
        double y1 = this.pos_y;
        double[] pointsX = {x1, x1};
        double[] pointsY = {y1, y1};
        doodleTracker(pointsX,pointsY,value,direction);         //define what it does if forward of 1

        pos_x += value * Math.cos(direction * Math.PI / 180);       //horizontal part
        pos_y += value * Math.sin(direction * Math.PI / 180);       //vertical part
        System.out.println("Position finale : (" + pos_x + ", " + pos_y + ")");
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
        direction-=i;
    }
    public void turnRight(double i){
        direction-= (int) i;
    }

    /**
     * turn in the trigonometric sense
     * @param i value in degree it turns
     */
    public void turnLeft(int i){
        direction+=i;
    }
    public void turnLeft(double i){
        direction+= (int) i;
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

    }

    /**
     * change the color with a hexadcimal code
     * @param hexadecimal
     */
    public void setColor(String hexadecimal){}


/*
    public void lookat(Pointer pointer){}

    public int lookat(double x, double y){
        int teta_initial=direction;
        direction= (int) Math.atan(y/x);
        return teta_initial;
    }
    public int lookat(int x, int y){
        int teta_initial=direction;
        direction= (int) Math.atan(y/x);
        return teta_initial;
    }
    public void addCouleur(){
        int c;
        System.out.println("0) Bleu, 1) Rouge  2) Vert");
        Scanner lectureClavier = new Scanner(System.in);
        System.out.println("Entrer une couleur: ");
        c = lectureClavier.nextInt();
        Color color = setColor(c);
        gc.setStroke(color);
    }
     public void move(int x, int y){
        //System.out.println(pos_x + ",z" + pos_y);
        System.out.println("direction : " + direction);
        int teta_initial=direction;
        direction = (int) Math.toDegrees(Math.atan2(y - pos_y, x - pos_x));
        System.out.println("directionS : " + direction);
        System.out.println("distance : " + Math.abs(Math.sqrt(y*y+x*x)));
        fwd(Math.abs(Math.sqrt(y*y+x*x)));
        direction=teta_initial;
        System.out.println("direction : " + direction);
        //System.out.println(pos_x + "," + pos_y);
      }
    public void move(int x, int y){
        int teta_initial=direction;
        direction=(int) Math.atan(y/x);
        fwd(Math.sqrt(y*y+x*x));
        direction=teta_initial;
    }
    public void move(double x, double y){
        int teta_initial=lookat(x,y);
        fwd(Math.sqrt(y*y+x*x));
        direction=teta_initial;
    }
    public void doodleTracker(double[] pointsX, double[] pointsY, int value, double direction) {

            AnimationTimer timer = new AnimationTimer() {
                int count = 0;
                @Override
                public void handle(long now) {
                    if (value < 0) {
                        pointsX[1] -= Math.cos(direction * Math.PI / 180);
                        pointsY[1] -= Math.sin(direction * Math.PI / 180);
                    } else {
                        pointsX[1] += Math.cos(direction * Math.PI / 180);
                        pointsY[1] += Math.sin(direction * Math.PI / 180);
                    }
                    gc.clearRect(cursor.getCenterX() - cursor.getRadius(),
                            cursor.getCenterY() - cursor.getRadius(),
                            cursor.getRadius() * 2, cursor.getRadius() * 2);
                   // gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                    gc.setLineWidth(2);
                    gc.strokeLine(pointsX[0], pointsY[0], pointsX[1], pointsY[1]);
                    cursor.setCenterX(pointsX[1]);
                    cursor.setCenterY(pointsY[1]);

                    gc.strokeOval(cursor.getCenterX() - cursor.getRadius(),
                            cursor.getCenterY() - cursor.getRadius(),
                            cursor.getRadius() * 2, cursor.getRadius() * 2);


                    try {
                        TimeUnit.MILLISECONDS.sleep(0);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    count++;

                    if (count >= Math.abs(value)) {
                        onAction = 0;
                        stop();
                       // System.out.println("FIN fwdI");
                    }
                }
            };
            timer.start();
    }

    public void fwd(int value) {
        System.out.println("Position initiale : (" + pos_x + ", " + pos_y + ")");
        double x1 = this.pos_x;
        double y1 = this.pos_y;
        double[] pointsX = {x1, x1};
        double[] pointsY = {y1, y1};
        doodleTracker(pointsX, pointsY, value, direction);

        pos_x += value * Math.cos(direction * Math.PI / 180);
        pos_y += value * Math.sin(direction * Math.PI / 180);

           TranslateTransition transition = new TranslateTransition(Duration.millis(5000), cursor);
           transition.setNode(cursor);
           transition.setToX(pos_x - x1);
           transition.setToY(pos_y - y1);
           transition.play();

        System.out.println("Position finale : (" + pos_x + ", " + pos_y + ")");
}
   public void move(int x, int y) {
        double newXpoint = pos_x +x;
        double newYpoint = pos_y +y;
        lookat(newXpoint,newYpoint);
        int distance = (int) Math.sqrt(Math.pow(newXpoint - pos_x, 2) + Math.pow(newYpoint - pos_y, 2));
        fwd(distance);
    }
*/
}
