package fr.cyu.chroma;

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
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class App extends Application {

    private final ObservableList<ChoiceBox<String>> choiceBoxes = FXCollections.observableArrayList();
    private final ObservableList<TextField> valueFields = FXCollections.observableArrayList();
    private final ObservableList<Button> addButtons = FXCollections.observableArrayList();
    private final ObservableList<Button> deleteButtons = FXCollections.observableArrayList();
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

        Pointer pointer = new Pointer(gc);
        Button forwardButton = new Button("Forward");
        forwardButton.setOnAction(e -> instruction(gc,1, pointer));
        Button backwardButton = new Button("Backward");
        backwardButton.setOnAction(e -> instruction(gc,2, pointer));
        Button colorButton = new Button("Color");
        colorButton.setOnAction(e -> instruction(gc,3, pointer));
        vbox.getChildren().add(forwardButton);
        vbox.getChildren().add(backwardButton);
        vbox.getChildren().add(colorButton);

        Scene scene = new Scene(vbox, screenBounds.getWidth(), screenBounds.getHeight());
        scene.getStylesheets().add(("/style.css"));

        addButtons(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


   private void instruction(GraphicsContext gc, int methode, Pointer pointer){
        switch (methode){
            case 1:
                System.out.println(pointer);
                pointer.forward(10);

                break;
            case 2:
                pointer.backward(10);
                break;
            case 3:
                pointer.addCouleur();
        }
    }

    private void addBlock(VBox vbox) {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getStyleClass().add("choice");
        choiceBox.setItems(choices);
        choiceBoxes.add(choiceBox);

        TextField valueField = new TextField();
        valueField.getStyleClass().add("value");
        valueFields.add(valueField);

        Button addButton = new Button("+");
        addButton.getStyleClass().add("buttonmodif");
        addButton.setOnAction(event -> addNewBlockAfter(vbox, choiceBox));

        Button deleteButton = new Button("-");
        deleteButton.getStyleClass().add("buttonmodif");
        deleteButton.setOnAction(event -> deleteBlock(vbox, choiceBox));

        HBox hbox = new HBox(10);
        hbox.getStyleClass().add("block-container");
        hbox.getChildren().addAll(choiceBox, valueField, addButton, deleteButton);
        vbox.getChildren().add(hbox);

        addButtons.add(addButton);
        deleteButtons.add(deleteButton);
    }

    private void addNewBlockAfter(VBox vbox, ChoiceBox<String> previousChoiceBox) {
        int index = choiceBoxes.indexOf(previousChoiceBox) + 1;
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getStyleClass().add("choice");
        choiceBox.setItems(choices);
        choiceBoxes.add(index, choiceBox);

        TextField valueField = new TextField();
        valueField.getStyleClass().add("value");
        valueFields.add(index, valueField);

        Button addButton = new Button("+");
        addButton.getStyleClass().add("buttonmodif");
        addButton.setOnAction(event -> addNewBlockAfter(vbox, choiceBox));

        Button deleteButton = new Button("-");
        deleteButton.getStyleClass().add("buttonmodif");
        deleteButton.setOnAction(event -> deleteBlock(vbox, choiceBox));

        HBox hbox = new HBox(10);
        hbox.getStyleClass().add("block-container");
        hbox.getChildren().addAll(choiceBox, valueField, addButton, deleteButton);
        vbox.getChildren().add(index + 1, hbox);


        addButtons.add(addButton);
        deleteButtons.add(deleteButton);
    }

    private void deleteBlock(VBox vbox, ChoiceBox<String> choiceBox) {
        if (choiceBoxes.size() > 1) {
            int index = choiceBoxes.indexOf(  choiceBox);
            choiceBoxes.remove(index);
            valueFields.remove(index);
            vbox.getChildren().remove(index + 1);

            addButtons.remove(index);
            deleteButtons.remove(index);
        }
    }

    private void addButtons(VBox vbox) {
        Button writeButton = new Button("Enregistrer dans un fichier");
        writeButton.getStyleClass().add("button");
        writeButton.setOnAction(event -> writeCommand());

        Button selectFileButton = new Button("Sélectionner un fichier à exécuter");
        selectFileButton.getStyleClass().add("button");
        selectFileButton.setOnAction(event -> selectFile());

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(writeButton, selectFileButton);
        vbox.getChildren().add(buttonBox);
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
            scene.getStylesheets().add("/style.css");
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
