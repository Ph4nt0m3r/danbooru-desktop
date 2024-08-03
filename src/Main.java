import javax.swing.*;

//TODO: поставить ограничение на количество столбцов. Eсли сейчас вписать число большее,
// чем есть картинок, то они дублируются

public class Main {
    private static final String path = "C:\\Users\\Lolof\\Desktop\\this";
    private static final String cachePath = "C:\\Users\\Lolof\\Desktop\\this\\cache.txt";

    public static void main(String[] args) {
        Data.loadImages(path);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });


    }
}