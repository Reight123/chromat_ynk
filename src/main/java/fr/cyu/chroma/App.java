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

    private final List<ChoiceBox<String>> choiceBoxes = new ArrayList<>();
    private final List<TextField> valueFields = new ArrayList<>();
    private final List<String> choices = List.of("FWD", "BWD", "TURN", "MOV", "POS", "HIDE", "SHOW", "PRESS", "COLOR", "THICK", "LOOKAT", "CURSOR", "SELECT", "REMOVE", "IF", "FOR", "WHILE", "MIMIC", "MIRROR", "NUM", "STR", "BOOL", "DEL", "FinBoucle");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Vos blocs de commande");

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(5);

        addBlock(vbox);
        addButtons(vbox);

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
    }

    private void suprBlock(VBox vbox) {
        if (choiceBoxes.size() > 1) {
            choiceBoxes.remove(choiceBoxes.size() - 1);
            valueFields.remove(valueFields.size() - 1);

            updateDisplay(vbox);

            addButtons(vbox);
        }
    }

    private void addButtons(VBox vbox) {
        vbox.getChildren().removeIf(node -> node instanceof Button);

        Button addButton = new Button("+");
        addButton.setOnAction(event -> addBlock(vbox));
        Button suprButton = new Button("-");
        suprButton.setOnAction(event -> suprBlock(vbox));
        Button writeButton = new Button("Write Selected Blocks to File");
        writeButton.setOnAction(event -> writeCommand());

        vbox.getChildren().addAll(addButton, suprButton, writeButton);

        updateDisplay(vbox);
    }

    private void updateDisplay(VBox vbox) {
        vbox.getChildren().removeIf(node -> node instanceof HBox);

        for (int i = 0; i < choiceBoxes.size(); i++) {
            HBox hbox = new HBox(10);
            hbox.getChildren().addAll(choiceBoxes.get(i), new Label("Value:"), valueFields.get(i));
            vbox.getChildren().add(hbox);
        }
    }

    private void writeCommand() {
        try {
            File file = new File("UserCode.txt");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (int i = 0; i < choiceBoxes.size(); i++) {
                String selectedOption = choiceBoxes.get(i).getValue();
                String enteredValue = valueFields.get(i).getText();
                if (selectedOption != null) {
                    if (selectedOption.equals("FOR") || selectedOption.equals("IF") || selectedOption.equals("WHILE") ) {
                        bufferedWriter.write(selectedOption + " " + enteredValue + "\n");
                        bufferedWriter.write("{\n");
                    } else if (selectedOption.equals("FinBoucle")) {
                        bufferedWriter.write("}\n");
                    } else if (!enteredValue.isEmpty()) {
                        bufferedWriter.write(selectedOption + " " + enteredValue + "\n");
                    }
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
