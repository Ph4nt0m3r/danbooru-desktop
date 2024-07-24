import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class DisplayImageFrame extends JFrame{
    String path;
    BufferedImage originalImage;
    BufferedImage processedImage;
    JImage imageComponent;

    public DisplayImageFrame(String path){
        try {
            this.path = path;
            this.originalImage = ImageIO.read(new File(path));
            this.setTitle(path.substring(path.lastIndexOf("\\") + 1));

            setFrame();
            setImage(originalImage);

            //screen center
            this.setLocationRelativeTo(null);
            this.setVisible(true);

            //this.addComponentListener(new ResizeListener(this));
        } catch (IOException e){
            System.out.println(e);
        }
    }

    public void setFrame(){
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.getContentPane().setLayout(new GridLayout(1, 1));
    }

    public void setImage(BufferedImage image){
        processedImage = image;
        Dimension Screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (image.getWidth()>=Screen.width || image.getHeight()>=Screen.height){

            //get min value of transformation
            AffineTransform at = new AffineTransform();;
            double scaling = Math.min( (double) Screen.height / image.getHeight(), (double) Screen.width / image.getWidth()) / 2;
            at.scale(scaling, scaling);

            //after scaling image template
            processedImage = new BufferedImage((int) (image.getWidth() * scaling),
                    (int) (image.getHeight() * scaling),
                    BufferedImage.TYPE_INT_ARGB);

            //scaling and saving
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            processedImage = scaleOp.filter(image, processedImage);
        }
        imageComponent = new JImage(path, processedImage);

        this.add(imageComponent);
        //this.setSize(processedImage.getWidth(), processedImage.getHeight()+this.getInsets().top);
        this.pack();
        //this.repaint();
    }


}
