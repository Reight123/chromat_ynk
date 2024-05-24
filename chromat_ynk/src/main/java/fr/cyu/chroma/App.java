package fr.cyu.chroma;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.collections.FXCollections.observableArrayList;

public class App extends Application {

    private final ObservableList<ChoiceBox<String>> choiceBoxes = observableArrayList();
    private final ObservableList<TextField> valueFields = observableArrayList();
    private final ObservableList<Button> addButtons = observableArrayList();
    private final ObservableList<Button> deleteButtons = observableArrayList();
    private final ObservableList<String> choices = observableArrayList( "BOOL", "BWD", "NUM", "STR", "CURSOR", "SELECT", "COLOR", "DEL", "ENDBLOCK", "ENDMIMIC", "ENDMIRROR", "FOR", "FWD", "HIDE", "IF", "LOOKAT", "MIMIC", "MIRROR", "MOV", "POS", "PRESS", "REMOVE", "SHOW", "THICK", "TURNL", "TURNR", "WHILE");
    private final ObservableList<String> error = observableArrayList();
    private VBox messageBox = new VBox();
    private int drawingWindowWidth = 800;
    private int drawingWindowHeight = 800;
    private double sliderValue = 50;
    private boolean errorGestion = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Menu"); //set the title of the main window
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds(); //get the dimension of the user screen

        VBox vbox = new VBox(); //content of the main window
        vbox.setPadding(new Insets(40));
        vbox.setSpacing(10);
        vbox.getStylesheets().add(("/style.css"));

        ScrollPane scrollPane = new ScrollPane(); //put the vbox with a scrollbar
        scrollPane.setContent(vbox);
        scrollPane.getStylesheets().add(("/style.css"));

        Scene scene = new Scene(scrollPane, screenBounds.getWidth(), screenBounds.getHeight()); //instance of the content

        addButtons(vbox); //add the button of the menu

        ChoiceBox<String> choiceBox = new ChoiceBox<>(); //setup the first command lign
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
        hbox.getStyleClass().add("box");
        hbox.getChildren().addAll(choiceBox, valueField, addButton, deleteButton);
        vbox.getChildren().add(hbox);

        addButtons.add(addButton);
        deleteButtons.add(deleteButton);

        messageBox = new VBox(); //set the empty error box
        messageBox.getStyleClass().add("gbox");
        Label label = new Label("Erreur :");
        messageBox.getChildren().add(label);
        messageBox.setPadding(new Insets(10));
        messageBox.setSpacing(5);
        vbox.getChildren().add(messageBox);

        primaryStage.setScene(scene); // put the content into the stage
        primaryStage.show(); //show the content
    }


    private static void updateMessageBox(ObservableList<String> items, VBox messageBox) {
        messageBox.getChildren().clear();
        Label label = new Label("Erreur :");
        messageBox.getChildren().add(label);
        for (String item : items) {
            if (item.length() > 90) {
                StringBuilder wrappedItem = new StringBuilder();
                int startIndex = 0;
                int endIndex = Math.min(90, item.length());
                while (startIndex < item.length()) {
                    wrappedItem.append(item.substring(startIndex, endIndex));
                    wrappedItem.append("\n\t");
                    startIndex = endIndex;
                    endIndex = Math.min(startIndex + 90, item.length());
                }
                label = new Label(wrappedItem.toString());
            } else {
                label = new Label(item);
            }
            label.setWrapText(true);
            messageBox.getChildren().add(label);
        }
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
        hbox.getStyleClass().add("box");
        hbox.getChildren().addAll(choiceBox, valueField, addButton, deleteButton);
        vbox.getChildren().add(index + 1, hbox);

        addButtons.add(addButton);
        deleteButtons.add(deleteButton);
    }

    private void deleteBlock(VBox vbox, ChoiceBox<String> choiceBox) {
        if (choiceBoxes.size() > 1) {
            int index = choiceBoxes.indexOf(choiceBox);
            choiceBoxes.remove(index);
            valueFields.remove(index);
            vbox.getChildren().remove(index + 1);

            addButtons.remove(index);
            deleteButtons.remove(index);
        }
    }

    private void addButtons(VBox vbox) {
        Button writeButton = new Button("Enregistrer");
        writeButton.getStyleClass().add("button");
        writeButton.setOnAction(event -> writeCommand());

        Button selectFileButton = new Button("Sélectionner un fichier");
        selectFileButton.getStyleClass().add("button");
        selectFileButton.setOnAction(event -> {
            File file = selectFile();
            executeFile(file);
        });

        Label executeFile = new Label("\u25B6");
        executeFile.getStyleClass().add("sbutton");
        executeFile.setOnMouseClicked(event -> {
            saveToFile(".currentFile");
            File file = new File("./storage/.currentFile.txt");
            executeFile(file);
        });

        Slider slider = new Slider(0, 100, 50);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(25);
        slider.setMinorTickCount(5);
        slider.setBlockIncrement(10);
        slider.getStyleClass().add("slider");
        slider.setOnMouseReleased(event -> {
            sliderValue = slider.getValue();
        });

        ToggleButton  selecterror= new ToggleButton("Gestion Erreur");
        selecterror.getStyleClass().add("button");
        selecterror.setOnAction(event -> {
            if (selecterror.isSelected()) {
                selecterror.setText("Arret si Erreur");
                errorGestion = false;
            } else {
                selecterror.setText("Erreurs ignorés");
                errorGestion = true;
            }
        });

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(writeButton, selectFileButton, slider, selecterror, executeFile);
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
                    if (selectedOption.equals("FOR") || selectedOption.equals("IF") || selectedOption.equals("WHILE") || selectedOption.equals("MIMIC")|| selectedOption.equals("MIRROR")) {
                        bufferedWriter.write(selectedOption + " " + enteredValue + "\n");
                        bufferedWriter.write("{\n");
                    } else if (selectedOption.equals("ENDBLOCK") || selectedOption.equals("ENDMIRROR") ) {
                        bufferedWriter.write("}\n");
                    } else if (selectedOption.equals("ENDMIMIC")) {
                        bufferedWriter.write("}\n");
                        bufferedWriter.write(selectedOption + " " + enteredValue + "\n");
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

    private void executeFile(File selectedFile) {
        if (selectedFile == null) {
            selectedFile = selectFile();
        }

        updateMessageBox(error, messageBox); // delete errors from previous execution

        String fileContent = getFileContent(selectedFile);
        String javaCode = "";

        if (!fileContent.isEmpty()) {
            try {
                Interpreter interpreter = new Interpreter(this.drawingWindowWidth, this.drawingWindowHeight);
                javaCode = interpreter.decode(fileContent, errorGestion);
                File templateFile = new File("../plotter/src/main/template/templateMain.java");
                String temp = getFileContent(templateFile);
                String[] template;
                if(temp.contains("//insertion area do not delete//")) {
                    template = temp.split("//insertion area do not delete//");
                } else {
                    updateMessageBox(observableArrayList("source file plotter/src/main/template/templateMain.java does not contains insertion area"), messageBox);
                    System.out.println("error while executing file : source file plotter/src/main/template/templateMain.java does not contains insertion area");
                    return;
                }


                if (template.length == 2 && !javaCode.isEmpty()) {
                    javaCode = template[0] + javaCode + template[1];
                    writeJavaFile(javaCode);
                    Thread thread = new Thread(() -> run(this, messageBox));
                    thread.start();
                } else {
                    updateMessageBox(observableArrayList("source file plotter/src/main/template/templateMain.java is missing parts"), messageBox);
                    System.out.println("source file plotter/src/main/template/templateMain.java is missing parts");
                }

            } catch (Exception e){
                updateMessageBox(observableArrayList(e.getMessage()), messageBox);
                System.out.println("error while executing : " + e.getMessage());
            }
        } else {
            updateMessageBox(observableArrayList("Selected file " + selectedFile.toString() + " is empty"), messageBox);
            System.out.println("error while executing : Selected file " + selectedFile.toString() + " is empty");
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
                updateMessageBox(observableArrayList("Error while reading file: " + e.getMessage()), messageBox);
                System.out.println("Error while reading file: " + e.getMessage());
            }
            return fileContent;
        } else {
            updateMessageBox(observableArrayList("Error while reading file: file \"null\" does not exist"), messageBox);
            System.out.println("Error while reading file: file \"null\" does not exist");
            return "";
        }
    }

    private void writeJavaFile(String fileContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("../plotter/src/main/java/fr/cyu/chroma/Main.java"))) {
            writer.write(fileContent);
        } catch (IOException e) {
            updateMessageBox(observableArrayList("Error while writing file Main.java : " + e.getMessage()), messageBox);
            System.out.println("Error while writing file Main.java : " + e.getMessage());
        }
    }


    private static void run(App appInstance, VBox messageBox) {
        try {
            String pathPlotter = "../plotter/pom.xml";
            String osName = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;
            if (osName.contains("windows")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", "mvn", "-f", pathPlotter, "clean", "javafx:run");
            } else if (osName.contains("linux") || osName.contains("mac")) {
                processBuilder = new ProcessBuilder("mvn", "-f", pathPlotter, "clean", "javafx:run");
            } else {
                Platform.runLater(() -> {
                    updateMessageBox(observableArrayList("OS " + osName + " is not supported."), messageBox);
                });
                return;
            }

            Process process = processBuilder.start();

            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String error;
            String fullError = "";
            boolean interestingPart = false;
            ObservableList<String> errorMessages = observableArrayList();
            while ((error = stdError.readLine()) != null) {
                fullError += error+"\n";
                if (error.contains("[Help 1]")) {                // when the error finishes explaining the pb, stop printing it
                    interestingPart = false;
                }
                if (interestingPart) {
                    if (error.contains("variable currentPointer might not have been initialized")) {    // as this error cannot be understood by the user, write on that can be
                        errorMessages.add("Call of a function, but no cursor was selected");
                    } else {
                        errorMessages.add(error);
                    }
                }
                if (error.contains("Failed to execute goal")) {          // when the error explains what is th pb, start printing it
                    errorMessages.add(error);
                    interestingPart = true;
                }
            }

            if (!errorMessages.isEmpty()) {
                Platform.runLater(() -> appInstance.updateMessageBox(errorMessages, messageBox));
                System.out.println(fullError);
            }

        } catch (Exception e) {
            updateMessageBox(observableArrayList("Error while executing file: " + e.getMessage()), messageBox);
            System.out.println("Error while executing file: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
