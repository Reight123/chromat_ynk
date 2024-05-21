package fr.cyu.chroma;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Votre dessin");
        primaryStage.setX(1050);
        primaryStage.setY(125);

        primaryStage.setAlwaysOnTop(true);
        primaryStage.setResizable(false);

        primaryStage.xProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.equals(100.0)) {
                primaryStage.setX(1050);
            }
        });
        primaryStage.yProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.equals(100.0)) {
                primaryStage.setY(125);
            }
        });

        Canvas canvas = new Canvas(800, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        VBox vbox = new VBox();
        vbox.getStylesheets().add(("/style.css"));
        vbox.getChildren().add(canvas);

        Scene scene = new Scene(vbox, 800, 870);

        primaryStage.setScene(scene);
        primaryStage.show();

        commands(gc);

        addButtons(vbox);
    }

    private void addButtons(VBox vbox) {

        Button nextButton = new Button("Etape suivante");
        nextButton.getStyleClass().add("buttondraw");

        Button saveDrawingButton = new Button("Enregistrer ce dessin");
        saveDrawingButton.getStyleClass().add("buttondraw");

        HBox buttonBox = new HBox(10);
        buttonBox.getStyleClass().add("boxdraw");
        buttonBox.getChildren().addAll(nextButton, saveDrawingButton);
        vbox.getChildren().add(buttonBox);
    }


    private void commands(GraphicsContext gc) {
        //insertion area do not delete//
    }

    public static void main(String[] args) {
        launch();
    }
}
