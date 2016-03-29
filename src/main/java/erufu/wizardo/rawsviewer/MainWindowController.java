package erufu.wizardo.rawsviewer;

import java.io.File;
import java.io.FileInputStream;
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
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class MainWindowController implements Initializable {

    private final ExecutionHelper executionHelper = new ExecutionHelper();
    private static final double MINIMUM_SCALE = 0.05;
    private static final double MAXIMUM_SCALE = 10;
    private static final double DEFAULT_SCALE = 1;
    private String filePath = NO_FILE_LOADED_STRING;
    private static final String NO_FILE_LOADED_STRING = "No file loaded";

    private double scaleFactor = 1;
    
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

            executionHelper.executeWithExceptionPropagation(() -> {
                final Image image = new Image(new FileInputStream(file));
                viewPort.setImage(image);
            });
            filePath = file.getAbsolutePath();
        }
        event.setDropCompleted(true);
        event.consume();
        resetScale();
        updateStatusText();
    }

    @FXML
    void scaleDown(ActionEvent e) {
        scaleDown();
    }

    @FXML
    void scaleUp(ActionEvent e) {
        scaleUp();
    }

    private void resetScale() {
        scaleFactor = DEFAULT_SCALE;
        updateViewportScale();
        updateStatusText();
    }

    private void updateViewportScale() {
        if (viewPort.getImage() != null) {
            viewPort.getTransforms().clear();
            viewPort.getTransforms().add(new Scale(scaleFactor, scaleFactor, 0, 0));
        }
    }

    private void updateStatusText() {
        final double scalePercents = scaleFactor * 100;
        final StringBuffer status = new StringBuffer();
        if (!NO_FILE_LOADED_STRING.equals(filePath)) {
            status.append(String.format("Current scale: %.2f%% ", scalePercents));
        }
        status.append(filePath);
        if (viewPort.imageProperty().isNotNull().get()) {
            status.append(" Original size:");
            status.append(String.format("%.0f", viewPort.getImage().getWidth()));
            status.append(" x ").append(String.format("%.0f", viewPort.getImage().getHeight()));

            status.append(" Resized to:");
            status.append(String.format("%.2f", viewPort.boundsInParentProperty().get().getWidth()));
            status.append(" x ").append(String.format("%.2f", viewPort.boundsInParentProperty().get().getHeight()));
        }
        statusText.setText(status.toString());
    }

    @FXML
    void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ADD) {
            scaleUp();
        } else if (keyEvent.getCode() == KeyCode.SUBTRACT) {
            scaleDown();
        }
    }

    @FXML
    void scaleToActualSize(ActionEvent e) {
        resetScale();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateStatusText();
        scrollPane.widthProperty().addListener((observable, oldValue, newValue) -> updateStatusText());
    }

    private void scaleDown() {
        final double newScaleFactor = scaleFactor * 0.78;

        if (newScaleFactor > MINIMUM_SCALE) {
            scaleFactor = newScaleFactor;
        } else {
            scaleFactor = MINIMUM_SCALE;
        }
        updateViewportScale();
        updateStatusText();
    }

    private void scaleUp() {
        final double newScaleFactor = scaleFactor * 1.33;

        if (newScaleFactor < MAXIMUM_SCALE) {
            scaleFactor = newScaleFactor;
        } else {
            scaleFactor = MAXIMUM_SCALE;
        }
        updateViewportScale();
        updateStatusText();
    }
}
