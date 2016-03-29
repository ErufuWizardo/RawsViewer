package erufu.wizardo.rawsviewer;

import erufu.wizardo.rawsviewer.manager.DefaultImageFactory;
import erufu.wizardo.rawsviewer.manager.FileManager;
import erufu.wizardo.rawsviewer.manager.ScalingManager;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainWindowController implements Initializable {

    private ScalingManager scalingManager;
    private FileManager fileManager;

    @FXML
    private Stage stage;

    @FXML
    private StackPane stackPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label statusText;

    @FXML
    private ImageView viewPort;

    @FXML
    void onDragOver(DragEvent event) {
        final Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
    }

    @FXML
    void onDragDropped(DragEvent event) {
        final Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            final int lastFileIndex = dragboard.getFiles().size() - 1;
            final File file = dragboard.getFiles().get(lastFileIndex);

            final Image image = fileManager.loadImage(file);
            viewPort.setImage(image);
        }
        event.setDropCompleted(true);
        event.consume();
        scalingManager.resetScale();
        scalingManager.updateStatusText();
    }

    @FXML
    void scaleDown(ActionEvent e) {
        scalingManager.scaleDown();
    }

    @FXML
    void scaleUp(ActionEvent e) {
        scalingManager.scaleUp();
    }

    @FXML
    void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ADD) {
            scalingManager.scaleUp();
        } else if (keyEvent.getCode() == KeyCode.SUBTRACT) {
            scalingManager.scaleDown();
        }
    }

    @FXML
    void scaleToActualSize(ActionEvent e) {
        scalingManager.resetScale();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fileManager = new FileManager(new DefaultImageFactory());
        scalingManager = ScalingManager.builder().build(statusText).build(viewPort).build(fileManager).getInstance();
        scalingManager.updateStatusText();
    }

}
