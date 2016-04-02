package erufu.wizardo.rawsviewer;

import erufu.wizardo.rawsviewer.manager.DefaultImageFactory;
import erufu.wizardo.rawsviewer.manager.FileManager;
import erufu.wizardo.rawsviewer.manager.ScalingManager;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainWindowController implements Initializable {

    private FileManager fileManager = new FileManager(new DefaultImageFactory());

    public FileManager getFileManager() {
        return fileManager;
    }

    @FXML
    private Stage stage;

    @FXML
    private StackPane stackPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Label statusText;
    
    @FXML
    private Label imageResolutionLabel;

    @FXML
    private ImageView viewPort;

    private ScalingManager scalingManager = new ScalingManager(fileManager);

    public ScalingManager getScalingManager() {
        return scalingManager;
    }

    private final Consumer<Image> imageUpdateConsumer = (image) -> {
        viewPort.setImage(image);
        scalingManager.updateViewportScale();
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
    void openImage(ActionEvent e) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        System.out.println("Stage:" + (Stage) viewPort.getScene().getWindow());
        final File file = fileChooser.showOpenDialog((Stage) viewPort.getScene().getWindow());

        if (file != null) {
            fileManager.loadImage(file).ifPresent(imageUpdateConsumer);
        }
    }

    @FXML
    void scaleUp(ActionEvent e) {
        scalingManager.scaleUp();
    }

    @FXML
    void forward(ActionEvent e) {
        moveToNextFile();
    }

    @FXML
    void backward(ActionEvent e) {
        moveToPreviousFile();
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
                moveToPreviousFile();
                keyEvent.consume();
                break;
            case RIGHT:
                moveToNextFile();
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
        scalingManager.viewPortProperty().setValue(viewPort);
    }

    private void moveToNextFile() {
        fileManager.forward().ifPresent(imageUpdateConsumer);
    }

    private void moveToPreviousFile() {
        fileManager.backward().ifPresent(imageUpdateConsumer);
    }
}
