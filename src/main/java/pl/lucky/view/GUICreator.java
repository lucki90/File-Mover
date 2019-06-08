package pl.lucky.view;

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
import pl.lucky.service.FilesHandler;

import java.io.File;

public class GUICreator {
    private FilesHandler filesHandler = new FilesHandler();

    public void createGUI(Stage stage) {
        stage.setTitle("File mover");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        //First row
        Label excelFile = new Label("Excel file: ");
        GridPane.setConstraints(excelFile, 0, 0);

        TextField excelFileInput = new TextField();
        excelFileInput.setPromptText("Excel file path...");
        GridPane.setConstraints(excelFileInput, 1, 0);

        String fileStatement = "Nie wybrano pliku...";
        Button excelFileBrowser = GUICreator.createButton(stage, excelFileInput, "Find file", fileStatement, false);
        GridPane.setConstraints(excelFileBrowser, 2, 0);
        //Second row
        Label sourceCatalog = new Label("Source catalog: ");
        GridPane.setConstraints(sourceCatalog, 0, 1);

        TextField sourceCatalogInput = new TextField();
        sourceCatalogInput.setPromptText("Catalog path...");
        GridPane.setConstraints(sourceCatalogInput, 1, 1);

        String catalogStatement = "Nie wybrano katalogu";
        Button sourceCatalogBrowser = GUICreator.createButton(stage, sourceCatalogInput, "Find catalog", catalogStatement, true);
        GridPane.setConstraints(sourceCatalogBrowser, 2, 1);

        //Third row
        Label destinyCatalog = new Label("Destiny catalog: ");
        GridPane.setConstraints(destinyCatalog, 0, 2);

        TextField destinyCatalogInput = new TextField();
        destinyCatalogInput.setPromptText("Catalog path...");
        GridPane.setConstraints(destinyCatalogInput, 1, 2);

        Button destinyCatalogBrowser = GUICreator.createButton(stage, destinyCatalogInput, "Find catalog", catalogStatement, true);
        GridPane.setConstraints(destinyCatalogBrowser, 2, 2);

        //Forth row
        Label filesExtension = new Label("Files extension: ");
        GridPane.setConstraints(filesExtension, 0, 3);

        TextField filesExtensionInput = new TextField();
        filesExtensionInput.setPromptText("example: .pdf");
        GridPane.setConstraints(filesExtensionInput, 1, 3);

        //Sixth row
        TextArea stackTraceArea = new TextArea();
        grid.add(stackTraceArea, 0, 5, 3, 1);

        //Fifth row
        Button launchButton = new Button("Move files!");
        launchButton.setOnAction(e -> filesHandler.takeFilesNameAndMoveFiles(
                excelFileInput.getText(), sourceCatalogInput.getText(),
                destinyCatalogInput.getText(), filesExtensionInput.getText(), stackTraceArea));
        GridPane.setConstraints(launchButton, 1, 4);

        grid.getChildren().addAll(excelFile, excelFileInput, excelFileBrowser,
                sourceCatalog, sourceCatalogInput, sourceCatalogBrowser,
                destinyCatalog, destinyCatalogInput, destinyCatalogBrowser,
                filesExtension, filesExtensionInput,
                launchButton);

        Scene scene = new Scene(grid, 400, 300);

        stage.setScene(scene);
        stage.show();

    }

    private static Button createButton(Stage stage, TextField inputText, String buttonName, String messageContent, boolean isCatalog) {
        Button browserButton = new Button();
        browserButton.setText(buttonName);
        browserButton.setOnAction(new EventHandler<ActionEvent>() {
            File chooser;

            @Override
            public void handle(ActionEvent event) {
                if (isCatalog) {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    chooser = directoryChooser.showDialog(stage);
                } else {
                    FileChooser fileChooser = new FileChooser();
                    chooser = fileChooser.showOpenDialog(stage);
                }

                if (chooser == null) {
                    inputText.setText(messageContent);
                } else {
                    inputText.setText(chooser.getAbsolutePath());
                }
            }
        });
        return browserButton;
    }
}
