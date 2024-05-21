package fr.cyu.chroma;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Main extends Application {
    private Canvas canvas;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Votre dessin");
        primaryStage.setX(1050);
        primaryStage.setY(125);

        primaryStage.setAlwaysOnTop(true);
        primaryStage.setResizable(false);

        primaryStage.xProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.equals(1050.0)) {
                primaryStage.setX(1050);
            }
        });
        primaryStage.yProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.equals(125.0)) {
                primaryStage.setY(125);
            }
        });

        canvas = new Canvas(800, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();
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

    private void addButtons(VBox vbox) {
        Button nextButton = new Button("Etape suivante");
        nextButton.getStyleClass().add("buttondraw");

        Button saveDrawingButton = new Button("Enregistrer ce dessin");
        saveDrawingButton.getStyleClass().add("buttondraw");
        saveDrawingButton.setOnAction(event -> saveDrawing());

        HBox buttonBox = new HBox(10);
        buttonBox.getStyleClass().add("boxdraw");
        buttonBox.getChildren().addAll(nextButton, saveDrawingButton);
        vbox.getChildren().add(buttonBox);
    }


    private Pointer commands(GraphicsContext gc) {
        List<Pointer> liste = new ArrayList<>();
        List<List<Pointer>> oldliste = new ArrayList<>();
        List<Pointer> temp = new ArrayList<>();
        List<Pointer> target = new ArrayList<>();
        Pointer tempPointer = null;
        Pointer targetPointer = null;
        Pointer targetStart;
        int k=0;
        temp.add(tempPointer);
        target.add(targetPointer);


         Pointer currentPointer;
Pointer c1 = new Pointer(gc);
		int c1Index = 0;
		 currentPointer = c1 ; 
		 currentPointer.pos( 300,300 ); 
Pointer c2 = new Pointer(gc);
		int c2Index = 0;
		 currentPointer = c2 ; 
targetStart = c1;
		k++;
		oldliste.add(new ArrayList<>(liste));
		tempPointer = new Pointer(gc);
		tempPointer.pos(currentPointer.getPos_x(),currentPointer.getPos_y());
		temp.add(tempPointer);
		targetPointer = new Pointer(gc);
		targetPointer.pos(targetStart.getPos_x(),targetStart.getPos_y());
		target.add(targetPointer);liste.add(temp.get(k));
		liste.add(target.get(k));
		for(; c1Index <2;c1Index++){
			currentPointer = liste.get(liste.size()-1 -c1Index);
		
		
		 currentPointer.fwd( 50 ); 
		  }
		

		c1Index = 0;
		liste=oldliste.get(oldliste.size() - 1);

		oldliste.remove(oldliste.size() - 1);



        return  currentPointer;
    }

    private void saveDrawing() {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);

        File file = new File("./../drawing/Votre_dessin.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
