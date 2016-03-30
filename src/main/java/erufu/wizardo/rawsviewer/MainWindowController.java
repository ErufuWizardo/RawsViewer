package erufu.wizardo.rawsviewer;

import erufu.wizardo.rawsviewer.manager.DefaultImageFactory;
import erufu.wizardo.rawsviewer.manager.FileManager;
import erufu.wizardo.rawsviewer.manager.ScalingManager;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
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

    private final Consumer<Image> imageUpdateConsumer = (image) -> {
        viewPort.setImage(image);
        scalingManager.updateViewportScale();
        scalingManager.updateStatusText();
    };

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

            fileManager.loadImage(file).ifPresent(imageUpdateConsumer);
        }
        event.setDropCompleted(true);
        event.consume();
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
        switch (keyEvent.getCode()) {
            case ADD:
                scalingManager.scaleUp();
                keyEvent.consume();
                break;
            case SUBTRACT:
                scalingManager.scaleDown();
                keyEvent.consume();
                break;
            case LEFT:
                fileManager.backward().ifPresent(imageUpdateConsumer);
                keyEvent.consume();
                break;
            case RIGHT:
                fileManager.forward().ifPresent(imageUpdateConsumer);
                keyEvent.consume();
                break;
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
