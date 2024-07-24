import java.io.File;
import java.io.IOException;


//it loads pictures and makes stuff with them
public class Data {
    private static String[] PicturesPath; //stores path of original pictures
    private static String[] PicturesHash; //stores path of images hash

    public static void loadImages(String path){
        try {
            File[] files = new File(path).listFiles();

            if (files.length == 0){
                throw new IOException("The chosen directory has no files");
            }

            int n = 0;

            for (File file : files) {
                if (isImage(file)) {
                    n++;
                }
            }

            if (n == 0){
                throw new IOException("The chosen directory has no images");
            }
            PicturesPath = new String[n];

            n=0;
            for (File file: files) {
                if (isImage(file)) {
                    PicturesPath[n] = file.getAbsolutePath();
                    n++;
                }
            }

        } catch (IOException e) {
            System.out.print(e);
        }
    }

    public static void loadImages(){
        loadImages(Main.path);
    }

    public static String[] getPicturesPath(){
        return PicturesPath;
    }

    private static boolean isImage(File file){
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return false; // empty extension
        }
        String ext = name.substring(lastIndexOf).toLowerCase();

        return switch (ext) {
            case ".png", ".jpeg", ".jpg" -> true;
            default -> false;
        };
    }


}
