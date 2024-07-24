import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame{
    protected JPanel imagePanel;
    protected JPanel settingsPanel;
    protected JScrollPane scrollPanel;

    private int imageCols = 3;
    private int spacing = 10;
    private int scrollWidth = 20;

    private double panelRatio = 7/10.0;
    private int settingsSize = 80;
    private int btnSize = 70;
    
    public MainFrame() {
        this.setTitle("Danbooru Desktop");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(210, 210, 230));
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);

        imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(0, imageCols));

        //scrollPanel
        scrollPanel = new JScrollPane(imagePanel);
        scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //remove hor. scroll
        scrollPanel.getVerticalScrollBar().setPreferredSize(new Dimension(scrollWidth,0));
        scrollPanel.getVerticalScrollBar().setUnitIncrement(30);

        settingsPanel = new JPanel();
        settingsPanel.setPreferredSize(new Dimension(settingsSize, this.getHeight()));
        settingsPanel.setMinimumSize(new Dimension(settingsSize, this.getHeight()));
        settingsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        //buttons
        JButton resizeBtn = new JButton("resize");
        resizeBtn.setPreferredSize(new Dimension(btnSize, 30));
        resizeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resizeImages();
            }
        });

        JButton reloadBtn = new JButton("reload");
        reloadBtn.setPreferredSize(new Dimension(btnSize, 30));
        reloadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Data.loadImages();
                resizeImages();
            }
        });

        JButton colsBtn = new JButton("cols");
        colsBtn.setPreferredSize(new Dimension(btnSize, 30));
        colsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frm = new JFrame("edit cols");
                try{
                    int cols = Integer.parseInt(JOptionPane.showInputDialog(frm, "Enter amount of columns"));

                    if (cols <= 0){
                        throw new NumberFormatException("amount of columns should be greater then 0");
                    }

                    imageCols = cols;
                    imagePanel.setLayout(new GridLayout(0, imageCols));
                    resizeImages();
                }
                catch (NumberFormatException ex){
                    System.out.println(ex);
                }
            }
        });

        settingsPanel.add(resizeBtn);
        settingsPanel.add(colsBtn);
        settingsPanel.add(reloadBtn);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPanel, settingsPanel);
        splitPane.setDividerSize(15);
        splitPane.setOneTouchExpandable(true);
        splitPane.resetToPreferredSizes();
        //splitPane.setEnabled(false);

        this.add(splitPane);

        splitPane.setResizeWeight(1);
        //splitPane.setDividerLocation(0.8);

        setImages();
    }

    private void setImages(){
        HashMap<String, BufferedImage> images = Data.getPicturesData();
        for (Map.Entry<String, BufferedImage> entry : images.entrySet()) {
            this.imagePanel.add(new JImage(entry.getKey(), entry.getValue(), true));
        }
    }

    private void resizeImages() {
        int preferredSize = ((scrollPanel.getWidth() - scrollWidth) / imageCols) - spacing;
        AffineTransform at;

        imagePanel.removeAll();

        //for every key-image entry
        for (Map.Entry<String, BufferedImage> entry : Data.getPicturesData().entrySet()) {
            String key = entry.getKey();
            BufferedImage image = entry.getValue();

            //scaling coefficient for every image to fit preferredSize box
            int maxSize = Math.max(image.getWidth(), image.getHeight());
            double scaling = (double) preferredSize / maxSize;
            at = new AffineTransform();
            at.scale(scaling, scaling);

            //scaling
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

            //after scaling image template
            BufferedImage tmp = new BufferedImage((int) (image.getWidth() * scaling),
                    (int) (image.getHeight() * scaling),
                    BufferedImage.TYPE_INT_ARGB);

            tmp = scaleOp.filter(image, tmp);

            //finalized image with spacing
            BufferedImage processed = new BufferedImage((int) (maxSize * scaling + spacing),
                    (int) (maxSize * scaling + spacing),
                    BufferedImage.TYPE_INT_ARGB);

            //indent to center image at finalize box
            int w = (processed.getWidth() - tmp.getWidth())/2;
            int h = (processed.getHeight() - tmp.getHeight())/2;

            Graphics g = processed.createGraphics();
            g.drawImage(tmp, w, h, null);
            g.dispose();

            //display scaled images
            imagePanel.add(new JImage(entry.getKey(), processed, true));
            this.setVisible(true);
        }
    }
}
