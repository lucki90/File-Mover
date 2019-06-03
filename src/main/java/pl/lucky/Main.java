package pl.lucky;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private String stackTrace = "";


    private EventHandler<ActionEvent> takeFileNameAndMoveFiles(TextArea textArea, String from, String to, String fileExtension, String sourceXlsx) {
        List<String> fileNames = new ArrayList<>();
        try {
            System.out.println("AAAAAAAAAAAAAAAAAAAA");
            readFilesToMoveFromXlsx(fileNames, fileExtension, sourceXlsx);
        } catch (IOException | InvalidFormatException e) {
            System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBB");
            e.printStackTrace();
        }
        moveFiles(textArea,fileNames, from, to);
        return null;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Przenieś pliki");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label sourceFile = new Label("Excel z nazwami plików: ");
        GridPane.setConstraints(sourceFile, 0, 0);

        TextField sourceFileInput = new TextField();
        sourceFileInput.setPromptText("Sciezka");
        GridPane.setConstraints(sourceFileInput, 1, 0);

        Button sourceFileBrowser = new Button();
        sourceFileBrowser.setText("Szukaj pliku");
        sourceFileBrowser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(primaryStage);

                if(selectedFile == null){
                    sourceFileInput.setText("Nie wybrano pliku...");
                }else{
                    sourceFileInput.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        GridPane.setConstraints(sourceFileBrowser,2,0);

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Label from = new Label("From");
        GridPane.setConstraints(from, 0, 1);

        TextField fromInput = new TextField();
        fromInput.setPromptText("Sciezka");
        GridPane.setConstraints(fromInput, 1, 1);

        Button fromDirectoryBrowser = new Button();
        fromDirectoryBrowser.setText("Szukaj katalogu");
        fromDirectoryBrowser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory =
                        directoryChooser.showDialog(primaryStage);

                if(selectedDirectory == null){
                    fromInput.setText("Nie wybrano katalogu");
                }else{
                    fromInput.setText(selectedDirectory.getAbsolutePath());
                }
            }
        });
        GridPane.setConstraints(fromDirectoryBrowser,2,1);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Label to = new Label("To");
        GridPane.setConstraints(to, 0, 2);

        TextField toInput = new TextField();
        toInput.setPromptText("Sciezka");
        GridPane.setConstraints(toInput, 1, 2);

        Button toDirectoryBrowser = new Button();
        toDirectoryBrowser.setText("Szukaj katalogu");
        toDirectoryBrowser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory =
                        directoryChooser.showDialog(primaryStage);

                if(selectedDirectory == null){
                    toInput.setText("Nie wybrano katalogu");
                }else{
                    toInput.setText(selectedDirectory.getAbsolutePath());
                }
            }
        });
        GridPane.setConstraints(toDirectoryBrowser, 2, 2);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Label fileExtension = new Label("Rozszerzenie pliku");
        GridPane.setConstraints(fileExtension, 0, 3);

        TextField fileExtensionInput = new TextField();
        fileExtensionInput.setPromptText("np: .pdf");
        GridPane.setConstraints(fileExtensionInput, 1, 3);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        TextArea textArea = new TextArea();
        textArea.setText(stackTrace);
        grid.add(textArea,0,6,3,1);

        Button button = new Button("Przenies");
        button.setOnAction(e -> takeFileNameAndMoveFiles(textArea, fromInput.getText(), toInput.getText(),fileExtensionInput.getText(), sourceFileInput.getText()));
        GridPane.setConstraints(button, 1, 4);



        grid.getChildren().addAll(sourceFile,sourceFileInput,sourceFileBrowser,
                from,fromInput,fromDirectoryBrowser,
                to, toInput, toDirectoryBrowser,
                fileExtension, fileExtensionInput,
                button);

        Scene scene = new Scene(grid, 400, 300);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main() {
        launch();
    }

    private void moveFiles(TextArea textArea, List<String> fileNames, String from, String to) {
        String stackTrace = "";
        if (!from.endsWith("\\")){
            from+="\\";
        }
        if (!to.endsWith("\\")){
            to+="\\";
        }
        for (String fileName : fileNames) {
            Path beginPath = Paths.get(from + fileName);
            Path finalPath = Paths.get(to + fileName);

            try {
                finalPath = Files.move(beginPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
                stackTrace+= "OK - przeniesiono plik: " + fileName + " \n";
            } catch (IOException e) {
                stackTrace+="UWAGA!! PLIK : " + fileName + " nie istnieje w katalogu: " + from + " \n";
            }
        }
        textArea.setText(stackTrace);
    }

    private static void readFilesToMoveFromXlsx(List<String> fileNames, String fileExtension, String sourceXlsx) throws IOException, InvalidFormatException {
        InputStream inp = new FileInputStream(sourceXlsx);
        int ctr = 0;
        Workbook wb = WorkbookFactory.create(inp);
        Sheet sheet = wb.getSheetAt(0);
        Row row = null;
        Cell cell = null;
        boolean isNull = false;
        do {
            try {
                row = sheet.getRow(ctr);
                cell = row.getCell(0);
                fileNames.add(cell.toString() + fileExtension);
                ctr++;
            } catch (Exception e) {
                isNull = true;
            }

        } while (isNull != true);
        inp.close();
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}