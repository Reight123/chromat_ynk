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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Modality;
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
    private boolean notEmpty = false;

    /**
     * Sets up the drawing page with the specified primaryStage.
     *
     * @param primaryStage The primary stage for the application.
     */
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

        Pane pane = new Pane();
        pane.getChildren().addAll(canvas,currentPointer.getCursor());
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(pane);
        borderPane.setBottom(vbox);

        Scene scene = new Scene(borderPane, 800, 870);

        updatePointer = commands(gc);

        if(updatePointer.isIs_shown() == true) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(3000), currentPointer.getCursor());
            transition.setFromX(0);
            transition.setFromY(0);
            transition.setToX(updatePointer.getPos_x() - 400);
            transition.setToY(updatePointer.getPos_y() - 400);
            transition.play();
        }
        else{
            pane.getChildren().remove(currentPointer.getCursor());
        }
        primaryStage.setScene(scene);
        primaryStage.show();

        //commands(gc);

        addButtons(vbox);
        // TODO add a button to begin the drawing, and another to delete the drawing and draw it again
    }

    /**
     * Fills the canvas with the specified color using the provided GraphicsContext.
     *
     * @param gc    The GraphicsContext object used for drawing
     * @param color The color
     */
    private void fillCanvas(GraphicsContext gc, Color color) {
        gc.setFill(color);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Adds buttons for editing the drawing page.
     * Contains a button for changing the color to white and cycling through other colors,
     * A save button for saving the drawing
     * A re-draw button for redrawing the canvas.
     *
     * @param vbox The VBox to which the buttons will be added.
     */
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


    /**
     * The function that actually draw. Contains declarations for all usages
     * Commands wrote by the user will be written inside
     *
     * @param gc The GraphicsContext object used for drawing.
     * @return The last used Pointer, to know its position.
     */
    private Pointer commands(GraphicsContext gc) {
        List<Pointer> liste = new ArrayList<>(); // necessary objects to use methods such as mimic and mirror
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
        double speedSlider = 100;
        temp.add(tempPointer);
        target.add(targetPointer);
        mirrorList.add(tempMirrorPointer);
        mirrorList.add(symmetryPointer);
        oldmirrorList.add(new ArrayList<>(mirrorList));

        // the commnds written by the user and translated by the interpreter will be written here

        //insertion area do not delete//

        if (currentPointer != null) {
            return currentPointer;
        } else {
            return new Pointer(gc);
        } // if the user don't select a pointer, return a new one to not crash the program
    }

    /**
     * Saves the current drawing as an image file.
     * If the drawing is not empty, it creates a snapshot of the canvas and saves it as a PNG file named "Votre_dessin.png"
     * in the "../drawing" directory relative to the current working directory.
     * If the drawing is empty, it displays a popup window indicating that the drawing is empty.
     */
    private void saveDrawing() { /*save the drawing*/
        if(this.notEmpty) {
            WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, writableImage);

            File file = new File("./../drawing/Votre_dessin.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showEmptyDrawingPopup();
        }
    }

    /**
     * Displays a popup window indicating that the drawing is empty.
     * The popup window is set to always stay on top of other windows, and it blocks interactions with other windows until closed.
     *
     */
    private void showEmptyDrawingPopup() {
        Stage popupStage = new Stage();
        popupStage.setTitle("Dessin Vide");
        popupStage.setAlwaysOnTop(true);

        Pane emptyPane = new Pane();
        Scene popupScene = new Scene(emptyPane, 200, 100);

        popupStage.setScene(popupScene);
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block interactions with other windows until this is closed
        popupStage.show();
    }

    public static void main(String[] args) { //launch the program
        launch();
    }
}
