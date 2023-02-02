public class test {
    public static void main(String args[]) {
        myTest();
    }

    public static void myTest() {
        Component[] c = new Component[10000];

        Image srcimg = ImageUtility.readImage("RES_a(5)_rec_.png");

        // ## image setup
        float k = 0.25f;
        int w = 50;
        Binarization sauv = new Sauvola(k, w);
        sauv.setImage(srcimg);
        sauv.binarize();

        // System.out.println(srcimg.sizeY);
        DisplayImage.display(sauv.getBinarizedImage());

        Segmentation segmentation = new Segmentation(sauv.getBinarizedImage());
        segmentation.segment();
        segmentation.drawRectangles(segmentation.getComponents());

        for (int i = 0; i < segmentation.getComponentsCount(); i++) {
            c[i] = segmentation.getComponents()[i];
            // c[i].setImage(sauv.getBinarizedImage());
            // Component[] ac = new Component[1];
            // ac[0] = c[i];
            // segmentation.drawRectangles(ac);
            // DisplayImage.display(c[i].getImage());
            System.out.println("Area " + c[i].getArea());
            System.out.println("Count of Black Pixels " + c[i].getCountOfBlackPixels());
            System.out.println("Area Density " + c[i].getAreaDensityScore());
            System.out.println("Page Score " + c[i].getPageSizeScore(srcimg.sizeX, srcimg.sizeY));
            System.out.println("Aspect Ratio Score " + c[i].getAspectRatioScore());
            System.out.println("-----------------------------------------");

        }

        // Image img = ImageUtility.addComponentsOnImage(c, 11,
        // srcimg.getWidth(), srcimg.getHeight());
        // DisplayImage.display(img);
    }
}
