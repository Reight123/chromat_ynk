package fr.cyu.chroma;

import javafx.animation.*;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class App extends Application {

    private final ObservableList<ChoiceBox<String>> choiceBoxes = FXCollections.observableArrayList();
    private final ObservableList<TextField> valueFields = FXCollections.observableArrayList();
    private final ObservableList<String> choices = FXCollections.observableArrayList("FWD", "BWD", "TURN", "MOV", "POS", "HIDE", "SHOW", "PRESS", "COLOR", "THICK", "LOOKAT", "CURSOR", "SELECT", "REMOVE", "IF", "FOR", "WHILE", "MIMIC", "MIRROR", "NUM", "STR", "BOOL", "DEL", "FinBlock");


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Menu");
        Rectangle2D
                screenBounds = Screen.getPrimary().getVisualBounds();
        Canvas canvas = new Canvas(300, 250);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(40));
        vbox.setSpacing(10);
        vbox.getChildren().add(canvas);
        Dessin dessin = new Dessin(gc);
        Button forwardButton = new Button("Forward");
        forwardButton.setOnAction(e -> instruction(gc,1,dessin));
        Button backwardButton = new Button("Backward");
        backwardButton.setOnAction(e -> instruction(gc,2,dessin));
        Button colorButton = new Button("Color");
        colorButton.setOnAction(e -> instruction(gc,3,dessin));
        vbox.getChildren().add(forwardButton);
        vbox.getChildren().add(backwardButton);
        vbox.getChildren().add(colorButton);

        Scene scene = new Scene(vbox, screenBounds.getWidth(), screenBounds.getHeight());

        addButtons(vbox);
        addBlock(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();
        //draw(gc);
    }

    private void instruction(GraphicsContext gc, int methode,Dessin dessin){
        switch (methode){
            case 1:
                dessin.forward();
                break;
            case 2:
                dessin.backward();
                break;
            case 3:
                dessin.addCouleur();
        }
    }
    private void draw(GraphicsContext gc) {
        Dessin dessin = new Dessin(gc);
        System.out.println(dessin.getPositionCursor());
        dessin.forward();
        System.out.println(dessin.getPositionCursor());
        dessin.backward();
        System.out.println(dessin.getPositionCursor());
        System.out.println(dessin.getOnAction());

    }

        private void addBlock(VBox vbox) {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setItems(choices);
        choiceBoxes.add(choiceBox);

        TextField valueField = new TextField();
        valueFields.add(valueField);

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(choiceBox, new Label("Valeur:"), valueField);
        vbox.getChildren().add(hbox);
    }

    private void suprBlock(VBox vbox) {
        if (choiceBoxes.size() > 1) {
            choiceBoxes.remove(choiceBoxes.size() - 1);
            valueFields.remove(valueFields.size() - 1);

            vbox.getChildren().remove(vbox.getChildren().size() - 1);
        }
    }

    private void addButtons(VBox vbox) {
        Button addButton = new Button("+");
        addButton.setOnAction(event -> addBlock(vbox));
        Button suprButton = new Button("-");
        suprButton.setOnAction(event -> suprBlock(vbox));
        Button writeButton = new Button("Enregistrer dans un fichier");
        writeButton.setOnAction(event -> writeCommand());
        Button selectFileButton = new Button("Sélectionner un fichier à exécuter");
        selectFileButton.setOnAction(event -> selectFile());

        vbox.getChildren().addAll(addButton, suprButton, writeButton, selectFileButton);
    }

    private void writeCommand() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Nom du fichier");
            VBox vbox = new VBox();
            vbox.setPadding(new Insets(10));
            vbox.setSpacing(5);
            TextField fileNameField = new TextField();
            fileNameField.setPromptText("Entrez le nom du fichier");
            Button confirmButton = new Button("Confirmer");
            confirmButton.setOnAction(event -> {
                String fileName = fileNameField.getText();
                if (!fileName.isEmpty()) {
                    saveToFile(fileName);
                    stage.close();
                }
            });
            vbox.getChildren().addAll(fileNameField, confirmButton);
            Scene scene = new Scene(vbox, 400, 125);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Error saving file name: " + e.getMessage());
        }
    }

    private void saveToFile(String fileName) {
        try {
            File file = new File(fileName + ".txt");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (int i = 0; i < choiceBoxes.size(); i++) {
                String selectedOption = choiceBoxes.get(i).getValue();
                String enteredValue = valueFields.get(i).getText();
                if (selectedOption != null) {
                    if (selectedOption.equals("FOR") || selectedOption.equals("IF") || selectedOption.equals("WHILE") || selectedOption.equals("MIMIC") || selectedOption.equals("MIRROR")) {
                        bufferedWriter.write(selectedOption + " " + enteredValue + "\n");
                        bufferedWriter.write("{\n");
                    } else if (selectedOption.equals("FinBlock")) {
                        bufferedWriter.write("}\n");
                    } else if (!enteredValue.isEmpty()) {
                        bufferedWriter.write(selectedOption + " " + enteredValue + "\n");
                    }
                }
            }

            bufferedWriter.close();
            System.out.println("Commands written to file successfully.");
        } catch (IOException e) {
            System.out.println("Error writing commands to file: " + e.getMessage());
        }
    }

    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Interpréteur.méthode(selectedFile);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
