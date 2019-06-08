package pl.lucky.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javafx.scene.control.TextArea;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class FilesHandler {

    public void takeFilesNameAndMoveFiles(String sourceXlsx, String from, String to,
                                          String fileExtension, TextArea stackTraceArea) {
        List<String> fileNames = new ArrayList<>();
        try {
            getFileListFromExcel(fileNames, fileExtension, sourceXlsx);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        moveFiles(fileNames, from, to, stackTraceArea);
    }

    private void getFileListFromExcel(List<String> fileNames, String filesExtension, String excelPath) throws IOException, InvalidFormatException {
        InputStream input = new FileInputStream(excelPath);
        int rowNumber = 0;
        Workbook workbook = WorkbookFactory.create(input);
        Sheet sheet = workbook.getSheetAt(0);
        Row row;
        Cell cell;
        boolean isNull = false;
        do {
            try {
                row = sheet.getRow(rowNumber);
                cell = row.getCell(0);
                fileNames.add(cell.toString() + filesExtension);
                rowNumber++;
            } catch (Exception e) {
                isNull = true;
            }

        } while (!isNull);
        input.close();
    }

    private void moveFiles(List<String> fileNames, String sourceCatalogPath, String destinyCatalogPath, TextArea stackTraceArea) {
        StringBuilder stackTrace = new StringBuilder();
        if (!sourceCatalogPath.endsWith("\\")) {
            sourceCatalogPath += "\\";
        }
        if (!destinyCatalogPath.endsWith("\\")) {
            destinyCatalogPath += "\\";
        }
        for (String fileName : fileNames) {
            Path beginPath = Paths.get(sourceCatalogPath + fileName);
            Path finalPath = Paths.get(destinyCatalogPath + fileName);

            try {
                Files.move(beginPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
                stackTrace.append("SUCCESS - File \"")
                        .append(fileName)
                        .append("\" moved correctly.\n");
            } catch (IOException e) {
                stackTrace.append("FAILURE! - File \"")
                        .append(fileName)
                        .append("\" does not exist in the catalog \"")
                        .append(sourceCatalogPath)
                        .append("\"\n");
            }
        }
        stackTraceArea.setText(stackTrace.toString());
    }
}
