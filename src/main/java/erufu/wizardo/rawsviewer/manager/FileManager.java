package erufu.wizardo.rawsviewer.manager;

import erufu.wizardo.rawsviewer.ExecutionHelper;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.image.Image;

public class FileManager {

    private final ExecutionHelper executionHelper = new ExecutionHelper();

    public static final String NO_FILE_LOADED_STRING = "No file loaded";
    private static final String BLANK_FILE_POSITION = "0/0";

    private Image currentImage;
    private final ImageFactory imageFactory;

    private File currentDirectory;
    private List<File> files = Collections.EMPTY_LIST;
    private int filePosition = 0;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", "bmp");

    private final ReadOnlyStringWrapper currentFileName = new ReadOnlyStringWrapper(NO_FILE_LOADED_STRING);

    public String getCurrentFileName() {
        return currentFileName.get();
    }

    public ReadOnlyStringProperty currentFileNameProperty() {
        return currentFileName.getReadOnlyProperty();
    }

    private final ReadOnlyStringWrapper filePositionText = new ReadOnlyStringWrapper(BLANK_FILE_POSITION);

    public String getFilePositionText() {
        return filePositionText.get();
    }

    public ReadOnlyStringProperty filePositionTextProperty() {
        return filePositionText.getReadOnlyProperty();
    }

    public FileManager(ImageFactory imageFactory) {
        this.imageFactory = imageFactory;
    }

    private void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }

    public Optional<Image> loadImage(File file) {
        if (file.isDirectory()) {
            currentDirectory = file;
            files = getFilesFromCurrentDirectory();
            filePosition = 0;
            if (files.size() > 0) {
                loadImageFromFile(files.get(0));
            }
        } else if (canBeOpened(file)) {
            currentDirectory = file.getParentFile();
            files = getFilesFromCurrentDirectory();
            filePosition = getFilePosition(file);
            loadImageFromFile(file);
        } else {
            return Optional.empty();
        }
        return Optional.ofNullable(currentImage);
    }

    public Optional<Image> forward() {
        if (files.isEmpty()) {
            filePosition = 0;
            return Optional.empty();
        }

        filePosition++;
        if (filePosition == files.size()) {
            filePosition = 0;
        }

        loadImageFromFile(files.get(filePosition));

        return Optional.ofNullable(currentImage);
    }

    public Optional<Image> backward() {
        if (files.isEmpty()) {
            filePosition = 0;
            return Optional.empty();
        }

        filePosition--;
        if (filePosition < 0) {
            filePosition = files.size() - 1;
        }

        loadImageFromFile(files.get(filePosition));

        return Optional.ofNullable(currentImage);
    }

    private void loadImageFromFile(File file) {
        executionHelper.executeWithExceptionPropagation(() -> {
            final Image image = imageFactory.getImage(new FileInputStream(file));
            setCurrentImage(image);
        });
        currentFileName.set(file.getName());
        updateProperties();
    }

    public boolean isFileLoaded() {
        return currentImage != null;
    }

    public boolean canBeOpened(File file) {
        final String filename = file.getName().toLowerCase();
        for (String extension : ALLOWED_EXTENSIONS) {
            if (filename.endsWith(extension)) {
                return true;
            }
        }

        return false;
    }

    private int getFilePosition(File file) {
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                if (file.equals(files.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    private List<File> getFilesFromCurrentDirectory() {
        return Arrays.asList(currentDirectory.listFiles()).stream().filter(element -> canBeOpened(element)).collect(Collectors.toList());
    }

    private void updateProperties() {
        final StringBuffer filePositionTextValue = new StringBuffer();
        filePositionTextValue.append("File# ");

        if (files.size() == 0) {
            filePositionTextValue.append(BLANK_FILE_POSITION);
        } else {
            filePositionTextValue.append(filePosition + 1).append("/").append(files.size() + 1);
        }

        filePositionText.set(filePositionTextValue.toString());

        // imageResolution.append(" Resized to:");
        // imageResolution.append(String.format("%.2f", viewPort.boundsInParentProperty().get().getWidth()));
        // imageResolution.append(" x ").append(String.format("%.2f", viewPort.boundsInParentProperty().get().getHeight()));
    }
    

}
