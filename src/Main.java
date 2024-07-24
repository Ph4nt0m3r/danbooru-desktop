import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String path = "C:\\Users\\Lolof\\Desktop\\this";
        Data.loadImages(path);

        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }

}