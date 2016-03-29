/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package erufu.wizardo.rawsviewer.manager;

import erufu.wizardo.rawsviewer.ExecutionHelper;
import java.io.File;
import java.io.FileInputStream;
import javafx.scene.image.Image;

public class FileManager {

    private final ExecutionHelper executionHelper = new ExecutionHelper();

    private String filePath = NO_FILE_LOADED_STRING;
    public static final String NO_FILE_LOADED_STRING = "No file loaded";

    private Image currentImage;

    public String getFilePath() {
        return filePath;
    }

    private void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }

    public Image loadImage(File file) {
        executionHelper.executeWithExceptionPropagation(() -> {
            final Image image = new Image(new FileInputStream(file));
            setCurrentImage(image);
        });
        filePath = file.getAbsolutePath();
        return currentImage;
    }

    public boolean isFileLoaded() {
        final boolean isImageNotNull = currentImage != null;
        final boolean isPathNotBlank = !NO_FILE_LOADED_STRING.equals(filePath);
        return isImageNotNull && isPathNotBlank;
    }

}
