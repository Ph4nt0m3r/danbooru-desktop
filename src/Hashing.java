
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Hashing {
    final static int height = 16;
    final static int width = 16;
    static String dir = "C:\\Users\\Lolof\\Desktop\\this\\hash\\";



    public static void makeHash(Map<String, BufferedImage> pictureData){
        for (Map.Entry<String, BufferedImage> entry: pictureData.entrySet()){
            String fileName = new File(entry.getKey()).getName();
            fileName = fileName.substring(0, fileName.lastIndexOf(".")-1);

            BufferedImage compressed = compress(entry.getValue(), fileName);

        }
    }


    private static BufferedImage compress(BufferedImage image, String fileName) {
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D graphics2D = out.createGraphics();

        graphics2D.drawImage(image, 0, 0, width, height, null);
        //graphics2D.dispose();

        try {
            File file = new File(dir + "hash_" + fileName + ".png");
            ImageIO.write(out, "png", file);
        } catch (Exception e){
            System.out.println("IO Exception: " + e);
        }

        return out;


    }

    /*private static BufferedImage monoHash(BufferedImage image, String fileName){
        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();

        int[][] result = new int[height][width];

        System.out.println(image.getColorModel().getPixelSize());
        System.out.println(pixels.length);

        int[] bits = new int[width*height];
        int i=0;
        //get bits of mono picture
        for (int k=0; k<pixels.length; k++){
            for (int n=0; n<8; n++){
                System.out.print((pixels[k]>>n) & 1);
                bits[i]=(pixels[k]>>n) & 1;
            }
            System.out.println();
        }


        i=0;
        for (int row = 0; row<height; row++) {
            for (int col = 0; col<width; col++){
                result[row][col] = (pixels[0] >> i);
                System.out.println(result[row][col]);
                i++;
            }
        }
        System.out.println(i);

        int[] test = new int[result[0].length*result.length];
        i = 0;
        for (int row = 0; row<height; row++) {
            for (int col = 0; col<width; col++){
                test[i]=result[row][col];
                i++;
            }
        }

        BufferedImage pic = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        WritableRaster raster = (WritableRaster) pic.getData();
        raster.setPixels(0,0,width,height,bits);

        final byte[] pixels1 = ((DataBufferByte) raster.getDataBuffer()).getData();

        for (int p = 0; p<pixels1.length; p++){
            System.out.println(pixels1[p] + " | " + pixels[p]);
            if (pixels1[p] == pixels[p]) System.out.println(true);
        }

        try {
            File file = new File(dir + "hash_test_" + fileName + ".png");
            ImageIO.write(pic, "png", file);
        } catch (Exception e){
            System.out.println("IO Exception: " + e);
        }

        return pic;
    }*/
}
