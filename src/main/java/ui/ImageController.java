package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainController {
    @FXML
    protected void locateFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File file = chooser.showOpenDialog(new Stage());

        if (file != null) {
            openFile(file);
        }
    }

    private void openFile(File file) {
        System.out.println(file);
    }
}
