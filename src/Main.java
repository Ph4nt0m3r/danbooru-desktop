import javax.swing.*;

//TODO: поставить ограничение на количество столбцов. Eсли сейчас вписать число большее,
// чем есть картинок, то они дублируются

public class Main {
    public static String path = "C:\\Users\\Lolof\\Desktop\\this";
    public static String cachePath = "C:\\Users\\Lolof\\Desktop\\this\\cache.txt";

    public static void main(String[] args) {
        Data.loadImages();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

}