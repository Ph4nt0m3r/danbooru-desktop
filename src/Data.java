import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

//it loads pictures and makes stuff with them
public class Data {
    private static HashMap<String, BufferedImage> PicturesData = new HashMap<>(); //stores path and picture
    private static HashMap<String, String> PicturesHash = new HashMap<>();

    private static String DataPath;

    public static void loadImages(String path) {
        DataPath = path;
        File[] files = new File(path).listFiles();

        try {
            for (File file : files) {
                if (file.isFile()) {
                    PicturesData.put(file.getAbsolutePath(), ImageIO.read(file));
                }
            }

        } catch (IOException e) {
            System.out.print(e.getStackTrace());
        }
    }

    public static void loadImages(){
        loadImages(DataPath);
    }

    public static BufferedImage[] getPictures(){
        BufferedImage[] images = new BufferedImage[PicturesData.size()];

        int i=0;
        for (BufferedImage image: PicturesData.values()) {
            images[i]=image;
            i++;
        }

        return images;
    }

    public static HashMap<String, BufferedImage> getPicturesData(){
        return PicturesData;
    }
}
