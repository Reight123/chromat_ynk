package fr.cyu.chroma;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class Main extends Application{
    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Plotter");
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Canvas canvas = new Canvas(800, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(40));
        vbox.setSpacing(10);
        vbox.getChildren().add(canvas);

        Scene scene = new Scene(vbox, 800, 800);

        //addButtons(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();





        commands(gc);

        // TODO add a button to begin the drawing, and another to delete the drawing and draw it again

    }

    private void commands(GraphicsContext gc){
         Pointer currentPointer;
		 Pointer machin = new Pointer(gc); 
		 currentPointer = machin ; 
		 currentPointer.fwd( 100 ); 
		 currentPointer.turnRight( 90 ); 
		 currentPointer.bwd( 50 ); 
    }

    public static void main(String[] args) {
        launch();
    }
}


