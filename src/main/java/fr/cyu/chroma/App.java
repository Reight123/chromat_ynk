package fr.cyu.chroma;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private List<ChoiceBox<String>> choiceBoxes = new ArrayList<>();
    private List<TextField> valueFields = new ArrayList<>();
    private List<String> choices = List.of("FWD", "BWD", "TURN", "MOV", "POS", "HIDE", "SHOW", "PRESS", "COLOR", "THICK", "LOOKAT", "CURSOR", "SELECT", "REMOVE", "IF", "FOR", "WHILE", "MIMIC", "MIRROR", "NUM", "STR", "BOOL", "DEL");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Vos blocs de commande");

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(5);

        addBlock(vbox);

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addBlock(VBox vbox) {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(choices);
        choiceBoxes.add(choiceBox);

        TextField valueField = new TextField();
        valueFields.add(valueField);

        updateDisplay(vbox);

        Button addButton = new Button("+");
        addButton.setOnAction(event -> addBlock(vbox));
        Button writeButton = new Button("Write Selected Blocks to File");
        writeButton.setOnAction(event -> writeCommand());
        vbox.getChildren().addAll(addButton, writeButton);
    }

    private void updateDisplay(VBox vbox) {
        vbox.getChildren().clear(); // Effacer les éléments actuels

        for (int i = 0; i < choiceBoxes.size(); i++) {
            HBox hbox = new HBox(10);
            hbox.getChildren().addAll(choiceBoxes.get(i), new Label("Value:"), valueFields.get(i));
            vbox.getChildren().add(hbox);
        }
    }

    private void writeCommand() {
        try {
            File file = new File("CYcode.txt");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (int i = 0; i < choiceBoxes.size(); i++) {
                String selectedOption = choiceBoxes.get(i).getValue();
                String enteredValue = valueFields.get(i).getText();
                if (selectedOption != null && !enteredValue.isEmpty()) {
                    bufferedWriter.write(selectedOption + " " + enteredValue + "\n");
                }
            }

            bufferedWriter.close();
            System.out.println("Selected options written to file successfully.");
        } catch (IOException e) {
            System.out.println("Error writing selected options to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
