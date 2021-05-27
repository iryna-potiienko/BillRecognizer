package ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vision.ImageParser;
import vision.dto.Item;
import vision.exception.ChainNotDefinedException;
import vision.exception.ChainNotSupportedException;
import vision.exception.FailedToExtractImageTextException;
import vision.exception.FailedToInitException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageController implements Initializable {

    private ImageParser imageParser;

    private final AtomicInteger atomicInteger = new AtomicInteger();

    @FXML
    private TextArea textArea;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageParser = new ImageParser();
    }

    @FXML
    protected void locateFile() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File file = chooser.showOpenDialog(new Stage());

        if (file != null && file.exists()) {

            List<Item> items;

            try {
                items = imageParser.extractTextFromPhoto(file);
            } catch (FailedToInitException e) {
                textArea.setText("Failed to init");
                return;
            } catch (FailedToExtractImageTextException | ChainNotDefinedException | ChainNotSupportedException e) {
                textArea.setText(e.getMessage());
                return;
            }

            String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(items);

            textArea.setText(result);
        }
    }

    @FXML
    protected void loadToExcel() throws Exception {
        if (textArea.getText().isEmpty()) {
            return;
        }

        String text = textArea.getText();

        Item[] items = objectMapper.readValue(text, Item[].class);

        String[] COLUMNs = {"Name", "Price"};

        Workbook workbook = new XSSFWorkbook();

        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Customers");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.BLUE.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        for (int col = 0; col < COLUMNs.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(COLUMNs[col]);
            cell.setCellStyle(headerCellStyle);
        }

        CellStyle ageCellStyle = workbook.createCellStyle();
        ageCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));

        int rowIdx = 1;
        for (Item item : items) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(item.getName());
            row.createCell(1).setCellValue(item.getPrice());
        }

        FileOutputStream fileOut = new FileOutputStream("items-" + atomicInteger.getAndIncrement() + ".xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    @FXML
    protected void clear() {
        textArea.clear();
    }
}
