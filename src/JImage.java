import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class JImage extends Component {
    private BufferedImage image;
    private String path;



    public JImage(String path, BufferedImage image, boolean mouseActions){
        this.path = path;
        this.image = image;
        this.setSize(image.getWidth(), image.getHeight());
        this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        this.repaint();

        this.setVisible(true);

        if (mouseActions){
            setMouseActions();
        }
    }

    public JImage(String path, BufferedImage image){
        this(path, image, false);
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(image, 0, 0, null);
        super.paint(g);
    }

    protected void setMouseActions(){
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                new DisplayImageFrame(path);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }
        });
    }

    public String getPath(){
        return path;
    }

    public BufferedImage getImage(){
        return image;
    }
}
