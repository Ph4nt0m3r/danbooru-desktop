import javax.swing.*;
import java.io.File;

public class ErrorDialog{

    public static int ConfirmDialog(String error, DialogType dt){
        JFrame jframe = new JFrame();

        int input=-1;

        if (dt.equals(DialogType.CORRUPTED_CACHE)){
            input = JOptionPane.showConfirmDialog(jframe,
                    error + "\nWould you like to reset cached data?",
                    "Cache error",
                    JOptionPane.YES_NO_OPTION);

        }
        else if (dt.equals(DialogType.INVALID_PATH)){
            input = JOptionPane.showConfirmDialog(jframe,
                    error + "\nPlease enter the valid path to images' directory",
                    "Invalid images' path",
                    JOptionPane.OK_CANCEL_OPTION);
        }

        return input;
    }


    public enum DialogType {
        CORRUPTED_CACHE,
        INVALID_PATH

    }
}
