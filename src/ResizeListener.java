import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class ResizeListener implements ComponentListener {
    int width = ImageFrame.getFrame().getWidth();

    public void componentResized(ComponentEvent e) {
        if (width != ImageFrame.getFrame().getWidth()) {
            ImageFrame.setFrame(ImageFrame.getFrame().getWidth(), ImageFrame.getFrame().getHeight());
            width=ImageFrame.getFrame().getWidth();
            System.out.println(1);
        }
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }
}
