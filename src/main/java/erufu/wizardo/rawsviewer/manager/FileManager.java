package erufu.wizardo.rawsviewer.manager;

import erufu.wizardo.rawsviewer.ExecutionHelper;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.scene.image.Image;

public class FileManager {

    private final ExecutionHelper executionHelper = new ExecutionHelper();

    private String filePath = NO_FILE_LOADED_STRING;
    public static final String NO_FILE_LOADED_STRING = "No file loaded";

    private Image currentImage;
    private final ImageFactory imageFactory;

    private File currentDirectory;
    private List<File> files = Collections.EMPTY_LIST;
    private int filePosition = 0;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", "bmp");

    public FileManager(ImageFactory imageFactory) {
        this.imageFactory = imageFactory;
    }

    public String getFilePath() {
        return filePath;
    }

    private void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }

    public Optional<Image> loadImage(File file) {
        if (file.isDirectory()) {
            currentDirectory = file;
            files = Arrays.asList(currentDirectory.listFiles()).stream().filter(element -> canBeOpened(element)).collect(Collectors.toList());
            filePosition = 0;
            if (files.size() > 0) {
                loadImageFromFile(files.get(0));
            }
        } else if (canBeOpened(file)) {
            currentDirectory = file.getParentFile();
            files = Arrays.asList(currentDirectory.listFiles()).stream().filter(element -> canBeOpened(element)).collect(Collectors.toList());
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
        filePath = file.getAbsolutePath();
    }

    public boolean isFileLoaded() {
        final boolean isImageNotNull = currentImage != null;
        final boolean isPathNotBlank = !NO_FILE_LOADED_STRING.equals(filePath);
        return isImageNotNull && isPathNotBlank;
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

}
