package ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vision.ImageParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ImageController implements Initializable {

    private ImageParser imageParser;

    @FXML
    private TextArea textArea;

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
            String result = imageParser.extractTextFromPhoto(file);
            textArea.setText(result);
        }
    }

    @FXML
    protected void clear() {
        textArea.clear();
    }

}
