package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageRetriever {

    public static BufferedImage defaultImage = imageFromURL("http://png-2.findicons.com/files/icons/1995/web_application/48/smiley.png");
    public static BufferedImage imageFromURL(String url) {
        try {
            BufferedImage img = ImageIO.read(new URL(url));
            if (img == null) return defaultImage;
            return img;
        } catch (IOException e) {
            return defaultImage;
        }
    }
}
