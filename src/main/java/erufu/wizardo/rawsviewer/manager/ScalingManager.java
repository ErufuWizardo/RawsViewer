/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package erufu.wizardo.rawsviewer.manager;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;

public class ScalingManager {

    private double scaleFactor = 1;

    private final ImageView viewPort;
    private final Label statusText;
    private final FileManager fileManager;

    private static final double MINIMUM_SCALE = 0.05;
    private static final double MAXIMUM_SCALE = 10;
    private static final double DEFAULT_SCALE = 1;

    public ScalingManager(ImageView viewPort, Label statusText, FileManager fileManager) {
        this.viewPort = viewPort;
        this.statusText = statusText;
        this.fileManager = fileManager;
    }

    public void scaleDown() {
        final double newScaleFactor = scaleFactor * 0.78;

        if (newScaleFactor > MINIMUM_SCALE) {
            scaleFactor = newScaleFactor;
        } else {
            scaleFactor = MINIMUM_SCALE;
        }
        updateViewportScale();
        updateStatusText();
    }

    public void scaleUp() {
        final double newScaleFactor = scaleFactor * 1.33;

        if (newScaleFactor < MAXIMUM_SCALE) {
            scaleFactor = newScaleFactor;
        } else {
            scaleFactor = MAXIMUM_SCALE;
        }
        updateViewportScale();
        updateStatusText();
    }

    public void resetScale() {
        scaleFactor = DEFAULT_SCALE;
        updateViewportScale();
        updateStatusText();
    }

    public void updateViewportScale() {
        if (viewPort.getImage() != null) {
            viewPort.getTransforms().clear();
            viewPort.getTransforms().add(new Scale(scaleFactor, scaleFactor, 0, 0));
        }
    }

    public void updateStatusText() {
        final double scalePercents = scaleFactor * 100;
        final StringBuffer status = new StringBuffer();
        if (fileManager.isFileLoaded()) {
            status.append(String.format("Current scale: %.2f%% ", scalePercents));
        }
        status.append(fileManager.getFilePath());
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

    public static ScalingManagerBuilder builder() {
        return new ScalingManagerBuilder();
    }

    public static class ScalingManagerBuilder {

        private Label statusText;
        private ImageView viewPort;
        private FileManager fileManager;

        public ScalingManagerBuilder build(ImageView viewPort) {
            this.viewPort = viewPort;
            return this;
        }

        public ScalingManagerBuilder build(Label statusText) {
            this.statusText = statusText;
            return this;
        }

        public ScalingManagerBuilder build(FileManager fileManager) {
            this.fileManager = fileManager;
            return this;
        }

        public ScalingManager getInstance() {
            return new ScalingManager(viewPort, statusText, fileManager);
        }
    }
}
