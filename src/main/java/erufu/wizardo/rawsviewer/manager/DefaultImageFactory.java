package erufu.wizardo.rawsviewer.manager;

import java.io.InputStream;
import javafx.scene.image.Image;

public class DefaultImageFactory implements ImageFactory {

    @Override
    public Image getImage(InputStream inputStream) {
        return new Image(inputStream);
    }

}
