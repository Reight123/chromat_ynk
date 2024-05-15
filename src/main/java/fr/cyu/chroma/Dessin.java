package fr.cyu.chroma;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Dessin extends Canvas {
    //private Color couleur = Color.green;

    /*public Dessin(){
        //setCursor(new Cursor(Cursor.CROSS));
        setCursor(new javafx.scene.Cursor(javafx.scene.Cursor.CROSSHAIR));
    }*/
   /* private int[] XPoints = {10,2};
    private int[] YPoints = {3,1};

    public forward(Cursor cursor){
     cursor += YPoints[1];
    }*/
 /*  public Dessin(GraphicsContext gc) {
       super(gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
   }*/

  /*  public void paint(Cursor cursor){
        Cursor newPositionCursor = forward(cursor);
        double [] XPoints = {cursor.getPos_x(),newPositionCursor.getPos_x()};
        double [] YPoints = {cursor.getPos_y(),newPositionCursor.getPos_y()};
        gc.setFill(Color.BLACK); // Définir la couleur de remplissage
        gc.fillPolygon(XPoints,YPoints,2);
    }*/
    private Cursor cursor;
    private GraphicsContext gc;
    private int onAction = 0;

    public Dessin(GraphicsContext gc){
        cursor = new Cursor(1); // Création du curseur
        this.gc = gc;
    }

    public void forward(){
        System.out.println("Demarrage");
        setOnAction(1);
        //this.gc.setStroke(Color.BLUE);
        //this.gc.setLineWidth(2);
        int value = 100;
        //direction = cursor.getDirection();
        int direction = 2;
        int x1 = cursor.getPos_x();
        int y1 = cursor.getPos_y();
        int[] pointsX = {x1,x1};
        int[] pointsY = {y1,y1};
        AnimationTimer timer = new AnimationTimer() {
            int count = 0;

            @Override
            public void handle(long l) {
                switch (direction){
                    case 1:
                        pointsX[1] = pointsX[1] + 1;
                        break;
                    case 2:
                        pointsY[1] = pointsY[1] + 1;
                        break;
                    case 3:
                        pointsX[1] = pointsX[1] - 1;
                        break;
                    case 4:
                        pointsY[1] = pointsY[1] - 1;
                        break;
                    default:
                        System.out.println("Direction non définie");
                        break;
                }
               // gc.setStroke(Color.BLUE);
                gc.setLineWidth(2);
                gc.strokeLine(pointsX[0], pointsY[0], pointsX[1], pointsY[1]);

                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                count++;

                if (count >= value) {
                    stop();
                    l = 1;
                    System.out.println("Forward fini");

                }
            }
        };
        timer.start();
        switch (direction){
            case 1:
                cursor.setPos_x(cursor.getPos_x() + value);
                break;
            case 2:
                cursor.setPos_y(cursor.getPos_y() + value);
                break;
            case 3:
                cursor.setPos_x(cursor.getPos_x() - value);
                break;
            case 4:
                cursor.setPos_y(cursor.getPos_y() - value);
                break;
            default:
                System.out.println("Direction non défini");
        }
    }

    public void backward(){
        int value = 150;
        //direction = cursor.getDirection();
        int direction = 3;
        int x1 = cursor.getPos_x();
        int y1 = cursor.getPos_y();
        int[] pointsX = {x1,x1};
        int[] pointsY = {y1,y1};

        AnimationTimer timer = new AnimationTimer() {
            int count = 0;

            @Override
            public void handle(long l2) {
               // gc.setStroke(Color.GREEN);
                gc.setLineWidth(3);
                switch (direction){
                    case 1:
                        pointsX[1] = pointsX[1] - 1;
                        break;
                    case 2:
                        pointsY[1] = pointsY[1] - 1;
                        break;
                    case 3:
                        pointsX[1] = pointsX[1] + 1;
                        break;
                    case 4:
                        pointsY[1] = pointsY[1] + 1;
                        break;
                    default:
                        System.out.println("Direction non définie");
                        break;
                }

                gc.strokeLine(pointsX[0], pointsY[0], pointsX[1], pointsY[1]);

                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                count++;

                if (count >= value) {
                    stop();
                    System.out.println("Backward fini");

                }
            }
        };
        timer.start();
        switch (direction){
            case 1:
                cursor.setPos_x(cursor.getPos_x() - value);
                break;
            case 2:
                cursor.setPos_y(cursor.getPos_y() - value);
                break;
            case 3:
                cursor.setPos_x(cursor.getPos_x() + value);
                break;
            case 4:
                cursor.setPos_y(cursor.getPos_y() + value);
                break;
            default:
                System.out.println("Direction non défini");
        }
    }

    public void addCouleur(){
        int c;
        System.out.println("1) Bleu, 2) Rouge  3) Vert");
        Scanner lectureClavier = new Scanner(System.in);
        System.out.println("Entrer une couleur: ");
        c = lectureClavier.nextInt();
        Color color = cursor.setColor(c);
        gc.setStroke(color);
    }
    public int getOnAction(){
        return this.onAction;
    }

    public void setOnAction(int onAction) {
        this.onAction = onAction;
    }

    public void drawCursor(GraphicsContext gc) {
        Image image = new Image(new File("src/main/img/curseur.png").toURI().toString());
        double width = 30; // Largeur souhaitée de l'image
        double height = 30; // Hauteur souhaitée de l'image
        gc.drawImage(image, cursor.getPos_x() - (width / 2), cursor.getPos_y() - (height / 2), width, height);         // gc.clearRect(0, 0, getWidth(), getHeight()); // Effacer le canvas
       // gc.setFill(Color.BLACK);
      //  gc.fillRect(cursor.getPos_x() - 2, cursor.getPos_y() - 2, 5, 5); // Dessiner le curseur comme un petit carré noir
    }
    public Cursor getPositionCursor() {
        return cursor;
    }

}
