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
    private final ObservableList<String> choices = FXCollections.observableArrayList("FWD", "BWD", "TURN", "MOV", "POS", "HIDE", "SHOW", "PRESS", "COLOR", "THICK", "LOOKAT", "CURSOR", "SELECT", "REMOVE", "IF", "FOR", "WHILE", "MIMIC", "MIRROR", "NUM", "STR", "BOOL", "DEL", "FinBlock");
    private int drawingWindowWidth = 500;
    private int drawingWindowHeight = 500;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Menu");
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(40));
        vbox.setSpacing(10);

        Scene scene = new Scene(vbox, screenBounds.getWidth(), screenBounds.getHeight());

        addButtons(vbox);
        addBlock(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();
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
        selectFileButton.setOnAction(event -> executeFile());

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
            System.out.println("Error while writing commands to file: " + e.getMessage());
        }
    }

    private void executeFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier");
        File selectedFile = fileChooser.showOpenDialog(null);

        String fileContent = getFileContent(selectedFile);
        String javaCode = "";
        if(!fileContent.isEmpty()){
            try {
                Interpreter interpreter = new Interpreter(this.drawingWindowWidth, this.drawingWindowHeight);
                javaCode = interpreter.decode(fileContent);
                String fileStart = "package fr.cyu.chroma;\n\npublic class Main {\n\tpublic static void main(String[] args) {\n\n";
                javaCode = fileStart + javaCode + "\n\t}\n}";
                writeJavaFile(javaCode);
            } catch (Exception e){
                System.out.println("Error while compiling commands to java: " + e.getMessage());
            }

            if(!javaCode.isEmpty()){
               compile();
               run();
            }else{
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


    private static void writeJavaFile(String fileContent){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("srcPlotter/main/java/fr/cyu/chroma/Main.java"))) {
            writer.write(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void compile(){
        try{
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                String path1 = "target/classes/fr/cyu/chroma";
                String path2 = "srcPlotter/main/java/fr/cyu/chroma/*.java";
                new ProcessBuilder("cmd.exe", "/c", "javac", "-d", path1, path2).start();
            }
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                String path1 = "target/classes/fr/cyu/chroma";
                String path2 = "srcPlotter/main/java/fr/cyu/chroma/*.java";
                new ProcessBuilder("bash", "-c", "javac", "-d", path1, path2).start();
            }

        }catch (Exception e){
            System.out.println("Error while compiling file: " + e.getMessage());
        }
    }

    private static void run(){
        try{
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                String path1 = "target/classes/fr/cyu/chroma";
                new ProcessBuilder("cmd.exe", "/c", "java", "-cp", path1, "Main").start();
            }
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                String path1 = "target/classes/fr/cyu/chroma";
                new ProcessBuilder("echo", "Main").start();
                new ProcessBuilder("java", "-cp", "target/classes/fr/cyu/chroma", "Main").start();
                //Process process = Runtime.getRuntime().exec("java -cp target/classes/fr/cyu/chroma Main");
                //process.waitFor();
                //System.out.println("Le programme a terminé avec le code : " + process.exitValue());
            }

        }catch (Exception e){
            System.out.println("Error while executing file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        //launch(args);
        compile();
        run();
    }
}
