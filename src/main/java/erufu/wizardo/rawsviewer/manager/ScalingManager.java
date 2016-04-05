package erufu.wizardo.rawsviewer.manager;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;

public class ScalingManager {

    private double scaleFactor = 1;

    private final FileManager fileManager;

    private static final double MINIMUM_SCALE = 0.05;
    private static final double MAXIMUM_SCALE = 10;
    private static final double DEFAULT_SCALE = 1;

    private final ObjectProperty<ImageView> viewPort = new SimpleObjectProperty<>();

    public ImageView getViewPort() {
        return viewPort.get();
    }

    public void setViewPort(ImageView value) {
        viewPort.set(value);
    }

    public ObjectProperty viewPortProperty() {
        return viewPort;
    }

    private final ReadOnlyStringWrapper imageResolution = new ReadOnlyStringWrapper(BLANK_VALUE);
    private static final String BLANK_VALUE = " - ";

    public ReadOnlyStringProperty imageResolutionProperty() {
        return imageResolution.getReadOnlyProperty();
    }

    public String getImageResolution() {
        return imageResolution.get();
    }

    private final ReadOnlyStringWrapper scaleStatus = new ReadOnlyStringWrapper(BLANK_VALUE);

    public ReadOnlyStringProperty scaleStatusProperty() {
        return scaleStatus.getReadOnlyProperty();
    }

    public String getScaleStatus() {
        return scaleStatus.get();
    }

    public ScalingManager(FileManager fileManager) {
        this.fileManager = fileManager;
        viewPort.addListener(observable -> {
            updateStatusText();
            if (viewPort.isNotNull().get()) {
                viewPort.get().imageProperty().addListener(value -> updateStatusText());
            }
        });

    }

    public void scaleDown() {
        final double newScaleFactor = scaleFactor * 0.90;

        if (newScaleFactor > MINIMUM_SCALE) {
            scaleFactor = newScaleFactor;
        } else {
            scaleFactor = MINIMUM_SCALE;
        }
        updateViewportScale();
        updateStatusText();
    }

    public void scaleUp() {
        final double newScaleFactor = scaleFactor * 1.1;

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
        if (viewPort.isNotNull().get() && viewPort.get().getImage() != null) {
            viewPort.get().getTransforms().clear();
            viewPort.get().getTransforms().add(new Scale(scaleFactor, scaleFactor, 0, 0));
        }
    }

    private void updateStatusText() {
        final double scalePercents = scaleFactor * 100;
        final StringBuffer zoom = new StringBuffer();
        if (fileManager.isFileLoaded()) {
            zoom.append(String.format("Zoom: %.2f%% ", scalePercents));
        } else {
            zoom.append(BLANK_VALUE);
        }
        scaleStatus.set(zoom.toString());

        if (viewPort.isNotNull().get() && viewPort.get().getImage() != null) {
            final StringBuffer imageResolutionText = new StringBuffer();
            imageResolutionText.append("Resolution: ");
            imageResolutionText.append(String.format("%.0f", viewPort.get().getImage().getWidth()));
            imageResolutionText.append(":").append(String.format("%.0f", viewPort.get().getImage().getHeight()));

            imageResolutionText.append(" Zoom: ");
            imageResolutionText.append(String.format("%.2f", viewPort.get().boundsInParentProperty().get().getWidth()));
            imageResolutionText.append(":").append(String.format("%.2f", viewPort.get().boundsInParentProperty().get().getHeight()));
            imageResolutionText.append("");

            imageResolution.set(imageResolutionText.toString());
            System.out.println(imageResolution.get());
        }
    }
}
