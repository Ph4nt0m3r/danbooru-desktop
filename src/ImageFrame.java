import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

//whole class is the only one frame, so I made fields static
public class ImageFrame {
    static final int scrWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    static final int scrHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    static int imageColumns = Main.imageColumns;
    static int scrollWidth = Main.scrollWidth;
    static JFrame frame = new JFrame();
    static BufferedImage[] image = Picture.image;

    public static void setFrame() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //container for scrolling
        Container container = new Container();
        container.setLayout(new GridLayout(0, imageColumns));
        frame.add(container);

        //scroll
        JScrollPane scroll = new JScrollPane(container);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(scrollWidth,0));//remove hor. scroll
        frame.add(scroll);

        //////////////////
        //window size and location
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        //size of window (half of screen size)
        frame.setSize(scrWidth / 2, scrHeight / 2);
        //screen center
        frame.setLocation((scrWidth - scrWidth / 2) / 2, (scrHeight - scrHeight / 2) / 2);
        ////////////////////

        fitImages(container);
        //making scroll not crossing images;
        frame.setSize(frame.getWidth()+scrollWidth+20, frame.getHeight());

        container.setVisible(Boolean.TRUE);
        frame.setVisible(Boolean.TRUE);

        frame.addComponentListener(new ResizeListener());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void setFrame(int width, int height) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //container for scrolling
        Container container = new Container();
        container.setLayout(new GridLayout(0, imageColumns));
        frame.add(container);

        //scroll
        JScrollPane scroll = new JScrollPane(container);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(scrollWidth,0));//remove hor. scroll
        frame.add(scroll);

        //////////////////
        //window size and location
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        //size of window (half of screen size)
        frame.setSize(width, height);
        //screen center
        frame.setLocation((scrWidth - scrWidth / 2) / 2, (scrHeight - scrHeight / 2) / 2);
        ////////////////////

        fitImages(container);
        //making scroll not crossing images;
        frame.setSize(frame.getWidth()+scrollWidth+20, frame.getHeight());

        container.setVisible(Boolean.TRUE);
        frame.setVisible(Boolean.TRUE);

        frame.addComponentListener(new ResizeListener());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    //fitting image to a frame
    private static void fitImages(Container container) {
        int preferredWidth = frame.getWidth() / imageColumns;
        AffineTransform at;
        for (int i = 0; i < image.length; i++) {
            //scaling coefficient for every image to fit window width
            double scaling = (double) preferredWidth / image[i].getWidth();
            at = new AffineTransform();
            at.scale(scaling, scaling);

            //after scaling image template
            BufferedImage processed = new BufferedImage((int) (image[i].getWidth() * scaling),
                    (int) (image[i].getHeight() * scaling),
                    BufferedImage.TYPE_INT_ARGB);

            //scaling and saving
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            processed = scaleOp.filter(image[i], processed);

            //displaying scaled image
            ImageIcon icon = new ImageIcon(processed);
            JLabel label = new JLabel();
            label.setIcon(icon);
            container.add(label);
        }
    }


    public static JFrame getFrame() {
        return frame;
    }
}
