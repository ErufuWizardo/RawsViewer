package erufu.wizardo.rawsviewer.manager;

import java.io.InputStream;
import javafx.scene.image.Image;

public interface ImageFactory {

    public Image getImage(InputStream inputStream);
    
}
