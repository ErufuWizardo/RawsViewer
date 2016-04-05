package erufu.wizardo.rawsviewer;

import erufu.wizardo.rawsviewer.manager.DefaultImageFactory;
import erufu.wizardo.rawsviewer.manager.FileManager;
import erufu.wizardo.rawsviewer.manager.ScalingManager;
import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Effect;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainWindowController implements Initializable {

    private FileManager fileManager = new FileManager(new DefaultImageFactory());

    public FileManager getFileManager() {
        return fileManager;
    }

    @FXML
    private ToggleButton darkenButton;

    @FXML
    private ImageView viewPort;

    @FXML
    private StackPane stackPane;

    @FXML
    private ScrollPane scrollPane;

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
    void darken(ActionEvent e) {
        if (darkenButton.selectedProperty().get()) {
            final Stage stage = new Stage(StageStyle.UNDECORATED);

            final Slider slider = new Slider(0d, 1d, 0.5d);
            slider.setMajorTickUnit(0.25d);
            slider.setBlockIncrement(0.1d);

            final Pane pane = new Pane();
            pane.getChildren().add(slider);
            stage.setScene(new Scene(pane));
            final Point2D buttonCoords = darkenButton.getParent().localToScreen(darkenButton.getBoundsInParent().getMinX(), darkenButton.getBoundsInParent().getMaxY());

            stage.setX(buttonCoords.getX());
            stage.setY(buttonCoords.getY());
            stage.show();

//            new Robot().mouseMove( x, y );
            stage.setX(buttonCoords.getX() - (stage.getWidth() - darkenButton.getWidth()) / 2);
            stage.setY(buttonCoords.getY());
            try {
                new Robot().mouseMove((int) (stage.getX() + stage.getWidth() / 2), (int) (stage.getY() + stage.getHeight() / 2));
            } catch (AWTException ex) {
            }

            stage.focusedProperty().addListener((observable, oldValue, newValue) -> stage.close());
            slider.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.booleanValue()) {
                    stage.close();
                }
            });
            final Blend blend = new Blend();
            blend.setMode(BlendMode.DARKEN);

            blend.opacityProperty().bind(slider.valueProperty());
            final ColorInput colorInput = new ColorInput();
            colorInput.setPaint(Color.GRAY);
            colorInput.setX(0);
            colorInput.setY(0);
            colorInput.widthProperty().bind(scrollPane.widthProperty());
            colorInput.heightProperty().bind(scrollPane.heightProperty());
            blend.setTopInput(colorInput);
            scrollPane.setEffect(blend);
        } else {
            scrollPane.setEffect(null);
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
