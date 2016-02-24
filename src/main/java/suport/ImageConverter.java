package suport;

import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.util.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Obee on 23/02/16.
 */
public class ImageConverter {
    public static String getBase64(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        BufferedInputStream bis = new BufferedInputStream(url.openConnection().getInputStream());
        byte[] byteArray = IOUtils.toByteArray(bis);

        return Base64.encodeBase64String(byteArray);
    }
}
