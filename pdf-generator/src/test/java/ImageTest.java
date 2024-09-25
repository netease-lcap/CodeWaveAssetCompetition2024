import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageTest {

    public static void main(String[] args) throws IOException {

//        FileInputStream inputStream = new FileInputStream("pdf-generator/src/main/resources/netease_logo2.png");
//
//        BASE64Encoder encoder = new BASE64Encoder();
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//        int read;
//        while ((read = inputStream.read(buffer)) != -1) {
//            bos.write(buffer, 0, read);
//        }
//
//        String s = encoder.encodeBuffer(bos.toByteArray());
//        System.out.println(s);


        String s = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAUwAAAAoCAYAAACVfMOnAA";
        System.out.println(s.substring(s.indexOf("base64,")+7));
    }
}
