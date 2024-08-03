import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicTreeUI;
import java.awt.*;
import java.awt.event.*;
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
        this.fitImages();
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

        if (Data.getPath() != null && cacheFound()){
            loadCache();
        }

        else {
            setPanels();
            this.setVisible(true);
            fitImages();
        }
    }

    private void setPanels(){
        imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(0, imageCols));
        imagePanel.setBackground(bg);

        scrollPanel = new JScrollPane(imagePanel);
        //scrollPanel.setSize(new Dimension(400, this.getHeight()));
        scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //remove hor. scroll
        scrollPanel.getVerticalScrollBar().setPreferredSize(new Dimension(scrollWidth,0));
        scrollPanel.getVerticalScrollBar().setUnitIncrement(30);
        scrollPanel.setEnabled(true);

        settingsPanel = new JPanel();
        //settingsPanel.setSize(new Dimension(settingsSize, this.getHeight()));
        settingsPanel.setPreferredSize(new Dimension(settingsSize, this.getHeight()));
        settingsPanel.setMinimumSize(new Dimension(settingsSize, this.getHeight()));
        settingsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        settingsPanel.setBackground(bg);
        settingsPanel.setEnabled(true);

        setButtons();

        SplitPanel splitPanel = new SplitPanel(JSplitPane.HORIZONTAL_SPLIT, scrollPanel, settingsPanel);
        this.add(splitPanel);
    }

    // TODO: path selector
    private void setButtons(){
        JButton resizeBtn = new JButton("resize");
        resizeBtn.setPreferredSize(new Dimension(btnSize, 30));
        resizeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fitImages();
            }
            @Override
            public void mouseEntered(MouseEvent e){
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e){
                setCursor(Cursor.getDefaultCursor());
            }
        });

        JButton reloadBtn = new JButton("reload");
        reloadBtn.setPreferredSize(new Dimension(btnSize, 30));
        reloadBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Data.getPath() == null){
                    return;
                }

                Data.loadImages();
                fitImages();
            }
            @Override
            public void mouseEntered(MouseEvent e){
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e){
                setCursor(Cursor.getDefaultCursor());
            }
        });

        JButton colsBtn = new JButton("cols");
        colsBtn.setPreferredSize(new Dimension(btnSize, 30));
        colsBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame frm = new JFrame();
                try{
                    String str = JOptionPane.showInputDialog(frm, "Enter amount of columns", "Edit columns", JOptionPane.PLAIN_MESSAGE);
                    //if cancel pressed
                    if (str == null){
                        return;
                    }

                    //if ok pressed, but str is empty
                    else if (str.equals("")) {
                        int input = JOptionPane.showConfirmDialog(new JFrame(),
                                "Enter something!",
                                "Edit columns",
                                JOptionPane.OK_CANCEL_OPTION);

                        //if pressed ok then show again
                        if (input == 0){
                            mouseClicked(e);
                        }
                        return;
                    }

                    int cols = Integer.parseInt(str);
                    //if columns are not in range [1:10]
                    if (cols > JImages.length || cols > 10) {
                        int input = JOptionPane.showConfirmDialog(new JFrame(),
                                "Number of columns should be lesser than image amount and less than 10",
                                "Edit columns",
                                JOptionPane.OK_CANCEL_OPTION);
                        //if pressed ok then show again
                        if (input == 0){
                            mouseClicked(e);
                        } else return; //else exit
                    }

                    //if columns are negative
                    else if (cols <= 0){
                        int input = JOptionPane.showConfirmDialog(new JFrame(),
                                "Number of columns should be a positive number!",
                                "Edit columns",
                                JOptionPane.OK_CANCEL_OPTION);
                        //if pressed ok then show again
                        if (input == 0){
                            mouseClicked(e);
                        } else return; //else exit
                    }

                    imageCols = cols;
                    imagePanel.setLayout(new GridLayout(0, imageCols));
                    fitImages();
                }
                catch (NumberFormatException ex){
                    int input = JOptionPane.showConfirmDialog(new JFrame(),
                            "The number of columns must be an integer!",
                            "Edit columns",
                            JOptionPane.OK_CANCEL_OPTION);
                    //if pressed ok then show again
                    if (input == 0){
                        mouseClicked(e);
                    }

                }
            }

            @Override
            public void mouseEntered(MouseEvent e){
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e){
                setCursor(Cursor.getDefaultCursor());
            }
        });

        JButton changePathBtn = new JButton("path");
        changePathBtn.setPreferredSize(new Dimension(btnSize, 30));
        changePathBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame frm = new JFrame("enter path");
                try{
                    String path = JOptionPane.showInputDialog(frm, "Enter path with images");

                    //if cancel pressed
                    if (path == null){
                        return;
                    }

                    //if ok is pressed but str is empty
                    else if (path.equals("")){
                        int input = JOptionPane.showConfirmDialog(new JFrame(),
                                "Enter something!",
                                "Change path",
                                JOptionPane.OK_CANCEL_OPTION);
                        if (input == 0){
                            mouseClicked(e);
                        }
                        return;
                    }

                    Data.setPath(path);
                }
                catch (IOException ex){
                    int input = ErrorDialog.ConfirmDialog(String.valueOf(ex), ErrorDialog.DialogType.INVALID_PATH);
                    if (input == 0){
                        mouseClicked(e);
                    }
                }

            }

           @Override
           public void mouseEntered(MouseEvent e){
               setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
           }
           @Override
           public void mouseExited(MouseEvent e){
               setCursor(Cursor.getDefaultCursor());
           }

        });

        settingsPanel.add(resizeBtn);
        settingsPanel.add(colsBtn);
        settingsPanel.add(reloadBtn);
        settingsPanel.add(changePathBtn);
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

    /**Deletes contents of imagePanel and resizes images loaded from Data to fit MainFrame's imagePanel*/
    private void fitImages() {
        if (Data.getPicturesPath() == null) {
            return;
        }

        imagePanel.removeAll();
        JImages = new JImage[Data.getPicturesPath().length];

        resizeImages();
    }

    /**Loads and resizes images from appendImagesPath to fit MainFrame's imagePanel*/
    private void fitImages(String[] appendImagesPath){

    }

    private void resizeImages(){
        int preferredSize = ((scrollPanel.getWidth() - scrollWidth) / imageCols) - spacing;
        //for every key-image entry
        for (int i = 0; i < Data.getPicturesPath().length; i++) {
            try {
                JImage resizedImage = resizeImage(Data.getPicturesPath()[i], preferredSize);
                JImages[i] = resizedImage;
                imagePanel.add(resizedImage);
            }

            catch (IOException e) {
                System.out.println(e);
            }

            this.setVisible(true); //refresh image panel
        }
    }

    private JImage resizeImage(String imagePath, int preferredSize) throws IOException{
        JImage jImage = null;

            BufferedImage image = ImageIO.read(new File(imagePath));

            //scaling coefficient for every image to fit preferredSize box
            int maxSize = Math.max(image.getWidth(), image.getHeight());
            double scaling = (double) preferredSize / maxSize;
            AffineTransform at = new AffineTransform();
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

            //return scaled image
            return new JImage(imagePath, processed, true);
        }

    protected void saveCache(){
        if (JImages == null) {
            return;
        }

        try {
            FileOutputStream fileOutput = new FileOutputStream(Data.getCachePath());
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
            FileInputStream fileInput = new FileInputStream(Data.getCachePath());
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
            boolean i = new File(Data.getCachePath()).delete();
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
        for (File file: new File(Data.getPath()).listFiles()) {
            if (file.getName().equals("cache.txt")){
                return true;
            }
        }
        return false;
    }

    private void findSimilar(){
        //Hashing.makeHash();
    }

    private void updateFiles(){
        String[] realImagesPath = Data.getPicturesPath();

        //if new files is found
        if (realImagesPath.length > JImages.length){
            int newFiles = 0;
            for (String realImagePath: realImagesPath){
                boolean origFound = false;
                for (JImage jimage: JImages) {
                    if (jimage.getPath() == realImagePath){
                        origFound = true;
                        break;
                    }
                }
                if (!origFound) {
                    newFiles++;
                }
            }

            String[] newImages = new String[newFiles];
            int k = 0;
            for (String realImagePath: realImagesPath){
                boolean origFound = false;
                for (JImage jimage: JImages) {
                    if (jimage.getPath() == realImagePath){
                        origFound = true;
                        break;
                    }
                }
                if (!origFound) {
                    newImages[k] = realImagePath;
                }
            }



        }

        //if old files are deleted
        else if (realImagesPath.length < JImages.length) {
            fitImages();
        }

    }

    private class SplitPanel extends JSplitPane {
        protected Component leftComponent = scrollPanel;
        protected Component rightComponent = settingsPanel;

        private int dividerSize = 15;

        public SplitPanel(int newOrientation, Component newLeftComponent, Component newRightComponent){
            super(newOrientation, newLeftComponent, newRightComponent);

            this.setOneTouchExpandable(true);
            this.setEnabled(true);

            SplitPanelUI ui = new SplitPanelUI();
            ui.installUI(this);
            ui.setDivider(dividerSize);
            this.setUI(ui);

            this.setResizeWeight(1); //or setDividerLocation
            //setDividerLocation(200); //in pixels
        }

    }

    private class SplitPanelUI extends BasicSplitPaneUI{

        public SplitPanelUI(){
            super();
        }

        protected void setDivider(int dividerSize) {
            divider = new Divider(this);
            divider.setDividerSize(dividerSize);
        }

        @Override
        protected void startDragging(){
        }

        @Override
        protected void dragDividerTo(int location) {
        }

        @Override
        protected void finishDraggingTo(int location) {
        }

    }

    private class Divider extends BasicSplitPaneDivider {

        public Divider(BasicSplitPaneUI ui) {
            super(ui);

            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            //TODO: выяснить почему цвет не меняется
            setBackground(Color.YELLOW);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        @Override
        protected JButton createLeftOneTouchButton() {
            JButton btn = super.createLeftOneTouchButton();
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return btn;
        }

        @Override
        protected JButton createRightOneTouchButton() {
            JButton btn = super.createRightOneTouchButton();
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return btn;
        }

    }
}

