package fr.cyu.chroma;

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
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;


public class Main extends Application{
    @Override
    public void start(Stage stage) {

        var label = new Label("JavaFX Application");
        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    private void command(){
         Cursor currentCursor;
		 Cursor curseur = new Cursor(); 
		 currentCursor = curseur ; 
		 if( 1 == 1 ){ 
		
		 currentCursor.fwd( 10 ); 
		  }
    }

    public static void main(String[] args) {
        launch();
    }
}
