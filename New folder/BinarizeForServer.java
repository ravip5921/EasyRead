public class BinarizeForServer {
    // public static void main(String args[]) {
    // binarizeAndSave(args[0], args[1], args[2]);
    // }

    public static void main(String args[]) {

        // for (int i = 1; i < 19; i++) {
        // binarizeAndSave("a (" + i + ").jpg", "RES_" + "a(" + i + ")", "resa(" + i +
        // ").txt");

        // }
        // String id = args[0];
        String id = "14";
        // binarizeAndSave("a (" + id + ").jpg", "RES_" + "a(" + id + ")",
        // "resa(" + id + ").txt");
        binarizeAndSave("t20.jpg", "RES_" + "t20", "rest20.txt");
    }

    public static void test() {
        binarizeAndSave("05_in.png", "temp05_in.jpg", "temp.txt");
    }

    public static void binarizeAndSave(String imagepath, String savepath1, String savepath2) {
        Image srcimg = ImageUtility.readImage(imagepath);
        System.out.println("Read image");
        // ## image setup
        float k = 0.25f;
        int w = 50;
        Binarization sauv = new Sauvola(k, w);
        sauv.setImage(srcimg);
        sauv.binarize();

        int windowSize = 3;
        // Image erodedImage = ImageUtility.erode(sauv.getBinarizedImage(), windowSize);

        // Image dilatedImage = ImageUtility.dilate(erodedImage, windowSize);

        // DisplayImage.display(sauv.getBinarizedImage(),
        // sauv.getBinarizedImage().getWidth(),
        // sauv.getBinarizedImage().getHeight());
        ImageUtility.writeImage(sauv.getBinarizedImage(), savepath1 + "_bin_" + ".png");
        System.out.println("Binarized");

        System.out.println("Segementation started");
        Segmentation segmentation = new Segmentation(sauv.getBinarizedImage());
        segmentation.segment();

        // segmentation.drawRectangles(segmentation.getComponents());
        segmentation.saveComponentImage(segmentation.getComponents(), savepath1 + "_rec_" + ".png");
        // System.out.print("");
        segmentation.printImg();
        System.out.println("Segementation  done");

        // ########################## SCORE NOISE ###################
        Noise scoreBasedNoise = new Noise();
        Component[] noiseFilledComponents = segmentation.getComponents();
        int noiseFilledComponentsCount = segmentation.getComponentsCount();
        boolean isNoisy[] = scoreBasedNoise.getTextComponents(noiseFilledComponents,
                noiseFilledComponentsCount, srcimg.sizeX, srcimg.sizeY);
        Component[] textComponents = scoreBasedNoise.removeNonTextComponents(segmentation.getComponents(),
                isNoisy);
        // segmentation.setComponentsArray(textComponents);

        // Image img = ImageUtility.addComponentsOnImage(segmentation.getComponents(),
        // segmentation.getComponentsCount(),
        // srcimg.getWidth(), srcimg.getHeight());
        // DisplayImage.display(img);
        // ImageUtility.writeImage(img, savepath1 + "_denoised_" + ".png");

        int sumArea = 0;
        int sumPageSizeScore = 0;
        for (int i = 0; i < isNoisy.length; i++) {
            if (isNoisy[i]) {
                sumArea += noiseFilledComponents[i].getArea();
                sumPageSizeScore += noiseFilledComponents[i].getPageSizeScore(srcimg.sizeX, srcimg.sizeY);
            }
        }
        int avgArea = sumArea / isNoisy.length;
        int avgPageSizeScore = sumPageSizeScore / isNoisy.length;

        boolean isNoisy2[] = scoreBasedNoise.getTextComponents2(noiseFilledComponents,
                noiseFilledComponentsCount, srcimg.sizeX, srcimg.sizeY, avgArea);
        Component[] textComponents2 = scoreBasedNoise.removeNonTextComponents(segmentation.getComponents(),
                isNoisy2);

        segmentation.setComponentsArray(textComponents2);

        Image img = ImageUtility.addComponentsOnImage(segmentation.getComponents(),
                segmentation.getComponentsCount(),
                srcimg.getWidth(), srcimg.getHeight());
        // DisplayImage.display(img);
        ImageUtility.writeImage(img, savepath1 + "_denoised_" + ".png");

        // // ######## PAGE SIZE SCORE #######
        // float pageSizeScore[] =
        // scoreBasedNoise.getComponentsPageSizeScore(segmentation.getComponents(),
        // segmentation.getComponentsCount(), srcimg.sizeX, srcimg.sizeY);

        // float sum = 0;
        // float max = 0;
        // float min = 100;
        // for (float f : pageSizeScore) {
        // sum += f;
        // if (f > max)
        // max = f;
        // if (f < min)
        // min = f;
        // }

        // float avg = sum / segmentation.getComponentsCount();

        // // Component[] finalComp =
        // //
        // scoreBasedNoise.removeComponentsBasedOnScore2(segmentation.getComponents(),
        // // segmentation.getComponentsCount(), isNoisy,
        // // pageSizeScore, 0.7f, avg);

        // // segmentation.setComponentsArray(finalComp);

        // // Image img =
        // ImageUtility.addComponentsOnImage(segmentation.getComponents(),
        // // segmentation.getComponentsCount(),
        // // srcimg.getWidth(), srcimg.getHeight());
        // // DisplayImage.display(img);
        // // ImageUtility.writeImage(img, "Tdenoised_pic_" + savepath1);

        // ######## PAGE SIZE SCORE END #######

        // ######################################################################3
        // salt
        // DisplayImage.display(redu);

        // save binarized image
        // ImageUtility.writeImage(otsuBound.getBinarizedImage(), savepath);
    }
}
