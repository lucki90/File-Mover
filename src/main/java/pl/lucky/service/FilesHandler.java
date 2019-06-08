package pl.lucky.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TextArea;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

/**
 * Supports file operations.
 */
public class FilesHandler {

    /**
     * Method that invoke method getFileListFromExcel and moveFiles.
     * @param excelPath path to a file that contains a list of files to move
     * @param sourceCatalogPath catalog from which the files will be moved
     * @param destinyCatalogPath catalog to which the files will be moved
     * @param filesExtension extension of files that will be moved
     * @param stackTraceArea field in GUI in which stacktrace will be displayed
     */
    public void takeFilesNameAndMoveFiles(final String excelPath, final String sourceCatalogPath,
                                          final String destinyCatalogPath, final String filesExtension,
                                          final TextArea stackTraceArea) {
        List<String> fileNames = new ArrayList<>();
        try {
            getFileListFromExcel(fileNames, filesExtension, excelPath);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        moveFiles(fileNames, sourceCatalogPath, destinyCatalogPath, stackTraceArea);
    }

    /**
     * Opens the excel file and takes the file names to be moved.
     * @param fileNames list of files that will be moved
     * @param filesExtension extension of files that will be moved
     * @param excelPath path to a file that contains a list of files to move
     * @throws IOException Signals that an I/O exception of some sort has occurred
     * @throws InvalidFormatException will be throw if format of excelPath will not be xlsx
     */
    private void getFileListFromExcel(final List<String> fileNames, final String filesExtension,
                                      final String excelPath) throws IOException, InvalidFormatException {
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

    /**
     * Move files from sourceCatalogPath to destinyCatalogPath and prints logs in stackTraceArea.
     * @param fileNames list of files that will be moved
     * @param sourceCatalogPath catalog from which the files will be moved
     * @param destinyCatalogPath catalog to which the files will be moved
     * @param stackTraceArea field in GUI in which stacktrace will be displayed
     */
    private void moveFiles(final List<String> fileNames, String sourceCatalogPath, String destinyCatalogPath, final TextArea stackTraceArea) {
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
