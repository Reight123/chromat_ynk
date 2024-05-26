package fr.cyu.chroma;

import javafx.application.Application;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static javafx.collections.FXCollections.observableArrayList;

public class Main extends Application {
    private Canvas canvas;
    private GraphicsContext gc;
    @Override
    public void start(Stage primaryStage) { /*setup the drawing page*/
        primaryStage.setTitle("Votre dessin");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        primaryStage.setX(1050);
        primaryStage.setY(125);

        primaryStage.setAlwaysOnTop(true);
        primaryStage.setResizable(false);

        canvas = new Canvas(800, 800);
        gc = canvas.getGraphicsContext2D();
        fillCanvas(gc, Color.WHITE);
        Pointer currentPointer = new Pointer(gc);
        Pointer updatePointer = new Pointer(gc);

        VBox vbox = new VBox();
        vbox.getStylesheets().add(("/style.css"));
        vbox.getChildren().add(canvas);

        Scene scene = new Scene(vbox, 800, 870);

        updatePointer = commands(gc);
        System.out.println(currentPointer);

        //Scene scene = new Scene(root, 800, 850);
        TranslateTransition transition = new TranslateTransition(Duration.millis(3000), currentPointer.getCursor());
        transition.setFromX(0);
        transition.setFromY(0);
        transition.setToX(updatePointer.getPos_x()-400);
        transition.setToY(updatePointer.getPos_y()-400);
        transition.play();
        primaryStage.setScene(scene);
        primaryStage.show();

        commands(gc);

        addButtons(vbox);
        // TODO add a button to begin the drawing, and another to delete the drawing and draw it again
    }

    private void fillCanvas(GraphicsContext gc, Color color) {
        gc.setFill(color);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    private void addButtons(VBox vbox) { /*button for editing drawing page*/
        Button nextButton = new Button("Etape suivante");
        nextButton.getStyleClass().add("buttondraw");

        Button changeColorButton = new Button("Blanc");
        changeColorButton.getStyleClass().add("buttondraw");
        AtomicReference<Color> color = new AtomicReference<>(Color.WHITE);
        changeColorButton.setOnAction(e -> {
            switch (changeColorButton.getText()) {
                case "Blanc":
                    color.set(Color.RED);
                    changeColorButton.setText("Rouge");
                    break;
                case "Rouge":
                    color.set(Color.BLUE);
                    changeColorButton.setText("Bleu");
                    break;
                case "Bleu":
                    color.set(Color.VIOLET);
                    changeColorButton.setText("Violet");
                    break;
                case "Violet":
                    color.set(Color.YELLOW);
                    changeColorButton.setText("Jaune");
                    break;
                case "Jaune":
                    color.set(Color.GREEN);
                    changeColorButton.setText("Vert");
                    break;
                case "Vert":
                    color.set(Color.ORANGE);
                    changeColorButton.setText("Orange");
                    break;
                case "Orange":
                    color.set(Color.BROWN);
                    changeColorButton.setText("Marron");
                    break;
                case "Marron":
                    color.set(Color.BLACK);
                    changeColorButton.setText("Noir");
                    break;
                case "Noir":
                    color.set(Color.WHITE);
                    changeColorButton.setText("Blanc");
                    break;
            }
            fillCanvas(gc, color.get());
            commands(gc);
        });

        Button saveDrawingButton = new Button("Enregistrer ce dessin");
        saveDrawingButton.getStyleClass().add("buttondraw");
        saveDrawingButton.setOnAction(event -> saveDrawing());

        Label reDraw = new Label("\u25B6");
        reDraw.getStyleClass().add("sbuttondraw");
        reDraw.setOnMouseClicked(event -> {
            fillCanvas(gc, color.get());
            commands(gc);
        });

        HBox buttonBox = new HBox(10);
        buttonBox.getStyleClass().add("boxdraw");
        buttonBox.getChildren().addAll(nextButton, saveDrawingButton, changeColorButton,reDraw);
        vbox.getChildren().add(buttonBox);
    }


    private Pointer commands(GraphicsContext gc) {
        List<Pointer> liste = new ArrayList<>();
        List<Pointer> mirrorList = new ArrayList<>();
        List<List<Pointer>> oldliste = new ArrayList<>();
        List<List<Pointer>> oldmirrorList = new ArrayList<>();
        List<Pointer> temp = new ArrayList<>();
        List<Pointer> target = new ArrayList<>();
        List<Integer> oldIndex = new ArrayList<>();
        Pointer tempPointer = null;
        Pointer targetPointer = null;
        Pointer tempMirrorPointer = null;
        Pointer symmetryPointer = null;
        Pointer targetStart;
        int k=0,indexMirror=0,orientation=1;
        temp.add(tempPointer);
        target.add(targetPointer);
        mirrorList.add(tempMirrorPointer);
        mirrorList.add(symmetryPointer);
        oldmirrorList.add(new ArrayList<>(mirrorList));


         Pointer currentPointer;
		Pointer c1 = new Pointer(gc);
		int c1Index = 0;
		c1.setSpeed(100.0);
		 currentPointer = c1 ; 
		tempMirrorPointer = new Pointer(gc);
		symmetryPointer = new Pointer(gc);
		tempMirrorPointer.pos(currentPointer.getPos_x(),currentPointer.getPos_y());
		tempMirrorPointer.setDirection(currentPointer.getDirection());
		symmetryPointer.pos(tempMirrorPointer.getPos_x(),tempMirrorPointer.getPos_y());
		symmetryPointer.setDirection(tempMirrorPointer.getDirection());
		symmetryPointer.getPosMirror(10,10);
		mirrorList.set(0,tempMirrorPointer);
		mirrorList.set(1,symmetryPointer);
		oldmirrorList.add(new ArrayList<>(mirrorList));
		for(indexMirror = 0;  indexMirror <2; indexMirror++){
			oldIndex.add(indexMirror);
		orientation = 1;
		currentPointer = mirrorList.get(indexMirror);
		
		
		 currentPointer.fwd( 100 ); 
		 currentPointer.turnLeft( 90 *orientation); 
		 currentPointer.fwd( 100 ); 
		
		
		
		indexMirror = oldIndex.get(oldIndex.size()-1);
		oldIndex.remove(oldIndex.size()-1);
		}
		oldmirrorList.remove(oldmirrorList.size() - 1);
		mirrorList = oldmirrorList.get(oldmirrorList.size() - 1);


        if (currentPointer != null) {
            return currentPointer;
        } else {
            return new Pointer(gc);
        } // if the user don't select a pointer, return a new one to not crash the program
    }

    private void saveDrawing() { /*save the drawing*/
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);

        File file = new File("./../drawing/Votre_dessin.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { //launch the program
        launch();
    }
}
