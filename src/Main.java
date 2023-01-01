import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//TODO: обновление фрейма при изменении его размеров, теги, оценки, нахождение похожих
public class Main {
    final static File directory = new File("C:\\Users\\Lolof\\Desktop\\this");
    static int imageColumns = 4;
    static int scrollWidth=20;

    public static void main(String[] args) throws IOException {
        Picture.load();
        //Picture.findSimilar();
        ImageFrame.setFrame();

    }
}