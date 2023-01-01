import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//it loads pictures and makes stuff with them
public class Picture {
    static File directory = Main.directory;
    static BufferedImage[] image;

    public static BufferedImage[] load() throws IOException {
        File[] files = directory.listFiles();
        int amount = files.length;
        image = new BufferedImage[amount];

        for (int i=0; i<amount; i++){
            image[i]= ImageIO.read(files[i]);
        }
        return image;
    }

    //TODO: найти алгоритм для преобразования в хэш
    public static void findSimilar(){
        Map<BufferedImage, int[][]> RGBMassive= new HashMap<>();
        //making massive of pixels for every picture
        for (int i=0; i<image.length; i++) {
           int[][] pixels = new int[image[i].getWidth()][image[i].getHeight()];
            for (int h = 0; h < image[i].getHeight(); h++) {
                for (int w = 0; w < image[i].getWidth(); w++) {
                    pixels[w][h] = image[i].getRGB(w,h);
                }
            }
            RGBMassive.put(image[i], pixels);
        }

        BufferedImage smaller;
        BufferedImage larger;
        int[] goodness = new int[image.length];
        for (int i=0; i<image.length; i++) {
            if (image[i].getWidth() * image[i].getHeight() > image[i + 1].getWidth() * image[i + 1].getHeight()) {
                smaller = image[i + 1];
                larger = image[i];
            } else {
                smaller = image[i];
                larger = image[i + 1];
            }

            int vertlikelyhood = 0;
            int horlikelyhood = 0;
            int vertunlikelyhood=0;
            for (int hs = 0; hs < smaller.getHeight(); hs++) {
                for (int ws = 0; ws < smaller.getWidth(); ws++) {


                    for (int hl = 0; hl < larger.getHeight(); hl++) {
                        for (int wl = 0; wl < larger.getWidth(); wl++) {
                            if (RGBMassive.get(smaller)[ws]
                                    [hs] == RGBMassive.get(larger)[wl][hl] && horlikelyhood < 30) {
                                goodness[i]++;
                                horlikelyhood++;
                                vertunlikelyhood=0;
                            } else if (horlikelyhood == 30) {
                                horlikelyhood = 0;
                                vertlikelyhood++;
                                hl++;
                                wl=wl-30;
                            } else if (RGBMassive.get(smaller)[ws][hs] != RGBMassive.get(larger)[wl][hl]) {
                                horlikelyhood = 0;
                                vertunlikelyhood=0;
                                vertunlikelyhood++;
                            }
                            if (vertunlikelyhood !=0 && hl+3<larger.getHeight()) {hl+=3;wl=0;}
                            if (vertlikelyhood == 30) {break;}
                        }
                        if (vertlikelyhood == 30) {break;}
                    }
                    if (vertlikelyhood == 30) {break;}
                }
                if (vertlikelyhood == 30) {System.out.println("pretty similar");break;}
            }
            goodness[i] = goodness[i] / (larger.getWidth() * larger.getHeight()) * 100;
            System.out.println(goodness[i]);
        }
    }
}
