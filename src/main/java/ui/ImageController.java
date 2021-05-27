package ui;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ImageController implements Initializable {

    private ImageParser imageParser;

    @FXML
    private TextArea textArea;

    private Map<String, String> itemPerPrice;

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
            try {
                itemPerPrice = imageParser.extractTextFromPhoto(file);
            } catch (FailedToInitException e) {
                textArea.setText("Failed to init");
            } catch (FailedToExtractImageTextException | ChainNotDefinedException | ChainNotSupportedException e) {
                textArea.setText(e.getMessage());
            }

            String result = itemPerPrice.entrySet().stream()
                    .map(entry -> entry.getKey() + " = " + entry.getValue() + " ")
                    .collect(Collectors.joining("\n"));

            textArea.setText(result);
        }
    }

    @FXML
    protected void loadToExcel() throws Exception {
        if (itemPerPrice.isEmpty()) {
            return;
        }

        List<Item> items = itemPerPrice.entrySet().stream()
                .map(entry -> mapToItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

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

        long currentTimeMillis = System.currentTimeMillis();

        FileOutputStream fileOut = new FileOutputStream("items-" + currentTimeMillis + ".xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private Item mapToItem(String name, String price) {
        Item item = new Item();

        item.setName(name);
        item.setPrice(price);

        return item;
    }

    @FXML
    protected void clear() {
        textArea.clear();
    }
}
