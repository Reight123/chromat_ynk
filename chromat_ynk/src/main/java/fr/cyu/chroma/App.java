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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class App extends Application {

    private final ObservableList<ChoiceBox<String>> choiceBoxes = FXCollections.observableArrayList();
    private final ObservableList<TextField> valueFields = FXCollections.observableArrayList();
    private final ObservableList<Button> addButtons = FXCollections.observableArrayList();
    private final ObservableList<Button> deleteButtons = FXCollections.observableArrayList();
    private final ObservableList<String> choices = FXCollections.observableArrayList("FWD", "BWD", "TURN", "MOV", "POS", "HIDE", "SHOW", "PRESS", "COLOR", "THICK", "LOOKAT", "CURSOR", "SELECT", "REMOVE", "IF", "FOR", "WHILE", "MIMIC", "MIRROR", "NUM", "STR", "BOOL", "DEL", "FinBlock");
    private int drawingWindowWidth = 500;
    private int drawingWindowHeight = 500;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Menu");
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        //Canvas canvas = new Canvas(300, 250);
        //GraphicsContext gc = canvas.getGraphicsContext2D();

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(40));
        vbox.setSpacing(10);
        vbox.getStylesheets().add(("/style.css"));

        //vbox.getChildren().add(canvas);


        /*
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
        */
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);


        Scene scene = new Scene(scrollPane, screenBounds.getWidth(), screenBounds.getHeight());

        addButtons(vbox);

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

        primaryStage.setScene(scene);
        primaryStage.show();
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
        selectFileButton.setOnAction(event -> {
            File file = selectFile();
            executeFile(file);
        });

        Button selectThisFileButton = new Button("Exécuter ce fichier");
        selectThisFileButton.getStyleClass().add("button");
        selectThisFileButton.setOnAction(event -> {
            saveToFile(".currentFile");
            File file = new File("./storage/.currentFile.txt");
            executeFile(file);
        });

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(writeButton, selectFileButton, selectThisFileButton);
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
            File file = new File("./storage/" + fileName + ".txt");
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

    private File selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier");
        String directory = "storage/";
        File repertoireInitial = new File(directory);
        fileChooser.setInitialDirectory(repertoireInitial);
        File selectedFile = fileChooser.showOpenDialog(null);
        return selectedFile;
    }

    private void executeFile(File selectedFile){
        if (selectedFile == null) {
            selectedFile = selectFile();
        }

        String fileContent = getFileContent(selectedFile);
        String javaCode = "";

        if(!fileContent.isEmpty()){

            try {
                Interpreter interpreter = new Interpreter(this.drawingWindowWidth, this.drawingWindowHeight);
                javaCode = interpreter.decode(fileContent);
                File templateFile = new File("../plotter/src/main/template/templateMain.java");
                String temp = getFileContent(templateFile);
                String[] template = temp.split("//insertion area do not delete//");

                if (template.length == 2 && !temp.isEmpty()) {
                    javaCode = template[0] + javaCode + template[1];
                    writeJavaFile(javaCode);
                }

                if(!javaCode.isEmpty()){
                    run();
                }

            } catch (Exception e){
                System.out.println("Error while compiling commands to java: " + e.getMessage());
                // TODO tell user that operation failed
            }
        }else{
            // TODO tell user that operation failed
        }

    }



    private String getFileContent(File selectedFile) {
        String fileContent = "";
        if (selectedFile != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line).append("\n");
                }
                fileContent = content.toString();
            } catch (IOException e) {
                System.out.println("Error while reading file: " + e.getMessage());
            }
            return fileContent;
        }else{
            System.out.println("Error while reading file: " + "null File");
            return "";
        }
    }


    private void writeJavaFile(String fileContent){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("../plotter/src/main/java/fr/cyu/chroma/Main.java"))) {
            writer.write(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static void run(){
        try{
            String pathPlotter = "../plotter/pom.xml";
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd.exe", "/c", "mvn", "-f", pathPlotter, "clean", "javafx:run").start();
            }
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                new ProcessBuilder("mvn", "-f", pathPlotter, "clean", "javafx:run").start();
            }
            // TODO get the output of commands and check if it does not send back errors, and display them if needed

        }catch (Exception e){
            System.out.println("Error while executing file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
