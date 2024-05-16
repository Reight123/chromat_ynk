package fr.cyu.chroma;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Pointer {
    private double pos_x = 10;
    private double pos_y = 23;
    private int direction = 30;
    private boolean is_shown = false;
    private int opacity = 100;
    private int thick = 5;



    private GraphicsContext gc;

    private Color[] colors = {Color.BLUE, Color.RED, Color.GREEN};

    public Pointer(GraphicsContext gc) {
        this.gc = gc;
    }


    public double getPos_x() {
        return this.pos_x;
    }

    public double getPos_y() {
        return this.pos_y;
    }

    public int getDirection() {
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

    public void setIs_shown(boolean is_shown) {
        this.is_shown = is_shown;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public void setThick(int thick) {
        this.thick = thick;
    }

    public String toString() {
        return  "1 (" + getPos_x() + "," + getPos_y() + ")";
    }

    public Color setColor(int c) {
        return colors[c];
    }


    public void forward(int value) {

        System.out.println("Demarrage");
        //this.gc.setStroke(Color.BLUE);
        //this.gc.setLineWidth(2);
        //int value = 100;
        //direction = cursor.getDirection();
        //int direction = 2;*
        double x1 = this.pos_x;
        double y1 = this.pos_y;
        double[] pointsX = {x1, x1};
        double[] pointsY = {y1, y1};
        AnimationTimer timer = new AnimationTimer() {
            int count = 0;

            public void handle(long l) {
                if (value < 0) {
                    // gc.setStroke(Color.BLUE);
                    pointsX[1] -= Math.cos(direction * Math.PI / 180);
                    pointsY[1] -= Math.sin(direction * Math.PI / 180);
                }
                else {
                    pointsX[1] += Math.cos(direction * Math.PI / 180);
                    pointsY[1] += Math.sin(direction * Math.PI / 180);
                }
                gc.setLineWidth(2);
                gc.strokeLine(pointsX[0], pointsY[0], pointsX[1], pointsY[1]);
                //System.out.println(pointsX[1] + "," + pointsY[1]+ ",(" + pointsX[0] + "," + pointsY[0]);

                try {
                    TimeUnit.MILLISECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                count++;

                if (count >= Math.abs(value)) {
                    stop();
                    System.out.println("Forward fini");

                }
            }
        };
        timer.start();
            pos_x+=value*Math.cos(direction*Math.PI/180);
            pos_y+=value*Math.sin(direction*Math.PI/180);

    }
    public void backward(int value){
        forward(-1*value);
    }

    public void turnRight(int i){
        direction-=i;
    }

    public void turnLeft(int i){
        direction+=i;
    }

    public void position(double x, double y){
        pos_x=x;
        pos_y=y;
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

}
