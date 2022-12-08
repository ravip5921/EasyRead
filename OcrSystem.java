import Binarization.*;
import Segmentation.*;

public class OcrSystem {
    public static void main(String[] args) {
        // ********************************BINARIZATION********************************

        BinarizationTest bint1 = new BinarizationTest();
        bint1.test();

        // ********************************SEGMENTATION********************************

        Segmentation seg = new Segmentation(bint1.binarizedImage.pixel);
        // Segmentation seg = new Segmentation();
        seg.printImg();
        seg.labelComponents1();
        // System.out.println("hi");
        seg.printImg();
        seg.prepareComponentList();
        seg.mergeSiblings();
        seg.getRectangles();

        // *******************************SKEW-CORRECTION*******************************
        // Skew skew = new Skew();
        // skew.printImg();
        // skew.getSkewAngle();

    }
}