import java.io.File;
import java.io.IOException;


//it loads pictures and makes stuff with them
public class Data {
    private static String[] PicturesPath; //stores path of original pictures
    private static String[] PicturesHash; //stores path of images hash

    private static String path = null;
    private static String cachePath = null;

    public static void loadImages(String path){
        try{
        Data.setPath(path);
        File[] files = new File(path).listFiles();

        //get amount of images
        int n = 0;
        assert files != null;
        for (File file : files) {
            if (isImage(file)) {
                n++;
            }
        }

        PicturesPath = new String[n];
        n=0;

        //add images to PicturesPath
        for (File file: files) {
            if (isImage(file)) {
                PicturesPath[n] = file.getAbsolutePath();
                n++;
            }
        }

        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void loadImages(){
        loadImages(path);
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

    public static void setPath(String path) throws IOException {
        File[] files = new File(path).listFiles();

        if (files == null){
            throw new IOException("Invalid directory path");
        }

        if (files.length == 0){
            throw new IOException("The chosen directory has no files or directories");
        }

        boolean hasImages = false;
        for (File file : files) {
            if (isImage(file)) {
                hasImages = true;
                break;
            }
        }

        if (!hasImages){
            throw new IOException("The chosen directory has no images");
        }

        Data.path = path;
        Data.cachePath = path + "\\cache.txt";

    }

    public static String getPath(){
        return path;
    }

    public static String getCachePath(){
        return cachePath;
    }

}
