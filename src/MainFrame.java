import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;


public class MainFrame extends JFrame{
    protected JPanel imagePanel;
    protected JPanel settingsPanel;
    protected JScrollPane scrollPanel;

    private int imageCols = 4;
    private int spacing = 10;
    private int scrollWidth = 20;

    private int settingsSize = 80;
    private int btnSize = 70;

    private JImage[] JImages;

    private Color bg = new Color(230, 220, 220);


    public MainFrame(int w, int h) {
        this.setSize(w, h);
        this.setTitle("Danbooru Desktop");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveCache();
                super.windowClosing(e);
            }
        });

        setPanels();

        //setImages();
        this.setVisible(true);
        this.resizeImages();
    }

    public MainFrame(){
        this.setTitle("Danbooru Desktop");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveCache();
                super.windowClosing(e);
            }
        });

        if (cacheFound()){
            loadCache();
        }

        else {
            setPanels();
            this.setVisible(true);
            resizeImages();
        }
    }

    private void setPanels(){
        imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(0, imageCols));
        imagePanel.setBackground(bg);

        //scrollPanel
        scrollPanel = new JScrollPane(imagePanel);
        scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //remove hor. scroll
        scrollPanel.getVerticalScrollBar().setPreferredSize(new Dimension(scrollWidth,0));
        scrollPanel.getVerticalScrollBar().setUnitIncrement(30);

        settingsPanel = new JPanel();
        settingsPanel.setPreferredSize(new Dimension(settingsSize, this.getHeight()));
        settingsPanel.setMinimumSize(new Dimension(settingsSize, this.getHeight()));
        settingsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        settingsPanel.setBackground(bg);

        setButtons();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPanel, settingsPanel);
        splitPane.setDividerSize(15);
        splitPane.setOneTouchExpandable(true);
        splitPane.resetToPreferredSizes();
        //splitPane.setEnabled(false);

        this.add(splitPane);

        splitPane.setResizeWeight(1);
        //splitPane.setDividerLocation(0.8);
    }

    private void setButtons(){
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
    }

    ///мб этот метод вообще не нужен??
    private void setImages(){
        for (String path: Data.getPicturesPath()) {
            try {
                File file = new File(path);
                BufferedImage image = ImageIO.read(file);
                this.imagePanel.add(new JImage(path, image, true));

            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    private void resizeImages() {
        if (Data.getPicturesPath() == null){
            return;
        }

        int preferredSize = ((scrollPanel.getWidth() - scrollWidth) / imageCols) - spacing;
        AffineTransform at;

        imagePanel.removeAll();
        JImages = new JImage[Data.getPicturesPath().length];

        //for every key-image entry
        for (int i=0; i < Data.getPicturesPath().length; i++) {
            try {
                BufferedImage image = ImageIO.read(new File(Data.getPicturesPath()[i]));

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
                int w = (processed.getWidth() - tmp.getWidth()) / 2;
                int h = (processed.getHeight() - tmp.getHeight()) / 2;

                Graphics g = processed.createGraphics();
                g.drawImage(tmp, w, h, null);
                g.dispose();

                //display scaled images
                JImage jimage = new JImage(Data.getPicturesPath()[i], processed, true);
                JImages[i] = jimage;
                imagePanel.add(jimage);


                this.setVisible(true); //refresh image panel
            }
            catch (IOException e){
                System.out.println(e);
            }
        }
    }

    protected void saveCache(){
        if (JImages == null) {
            return;
        }

        try {
            FileOutputStream fileOutput = new FileOutputStream(Main.cachePath);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);

            objectOutput.writeInt(this.getWidth());
            objectOutput.writeInt(this.getHeight());
            objectOutput.writeInt(JImages.length);

            for (JImage jimage : JImages){
                byte[] pathBytes = jimage.getPath().getBytes(StandardCharsets.UTF_8);
                objectOutput.writeInt(pathBytes.length);
                objectOutput.write(pathBytes);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(jimage.getImage(), "png", baos);
                byte[] imageBytes = baos.toByteArray();
                objectOutput.writeInt(imageBytes.length);
                objectOutput.write(imageBytes);
            }

            objectOutput.close();
            fileOutput.close();

        } catch (IOException e) {
            System.out.println(e);
        }

    }

    protected void loadCache(){
        System.out.println("loading cached data");
        try{
            FileInputStream fileInput = new FileInputStream(Main.cachePath);
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            int w = objectInput.readInt();
            int h = objectInput.readInt();
            int imageAmount = objectInput.readInt();

            this.setSize(w, h);
            this.setLocationRelativeTo(null);
            setPanels();

            JImages = new JImage[imageAmount];

            readCachedImages(fileInput, objectInput);

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**read images from cache*/
    private void readCachedImages(FileInputStream fileInput, ObjectInputStream objectInput) throws IOException {
        int imageAmount = JImages.length;

        int corruptedImages = 0;
        for (int i=0; i < imageAmount; i++){
            int length = objectInput.readInt();

            byte[] pathBytes = new byte[length];
            objectInput.readFully(pathBytes);
            String path = new String(pathBytes, StandardCharsets.UTF_8);

            System.out.println(path);

            length = objectInput.readInt();
            byte[] imageBytes = new byte[length];
            objectInput.readFully(imageBytes);
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bais);

            if (image == null){
                corruptedImages++;
                continue;
            }

            JImage jimage = new JImage(path, image, true);
            JImages[i] = jimage;
            imagePanel.add(jimage);
        }

        objectInput.close();
        fileInput.close();

        if (corruptedImages!=0){
            corruptedCacheInform(corruptedImages);
        }
    }

    private void corruptedCacheInform(int corruptedFiles){
        int input = ErrorDialog.ConfirmDialog("Cache contains " + corruptedFiles + " corrupted images.",
                ErrorDialog.DialogType.CORRUPTED_CACHE);

        if (input == 0){
            boolean i = new File(Main.cachePath).delete();
            System.out.println(i);
            //TODO: reload
        }
        else {
            JImage[] intactImages = new JImage[JImages.length-corruptedFiles];

            int k=0;
            for (int i=0; i<JImages.length; i++) {
                if (JImages[i] != null){
                    intactImages[k] = JImages[i];
                    k++;
                }
                JImages = new JImage[intactImages.length];
                JImages = intactImages;
            }
        }
    }

    private boolean cacheFound(){
        for (File file: new File(Main.path).listFiles()) {
            if (file.getName().equals("cache.txt")){
                return true;
            }
        }
        return false;
    }

}
