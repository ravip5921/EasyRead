import Binarization.*;
import SEG.*;

public class OcrSystem {
    public static void main(String[] args) {
        // ********************************BINARIZATION********************************

        BinarizationTest bint1 = new BinarizationTest();
        bint1.test("a.png");

        // ********************************SEGMENTATION********************************

        Segmentation seg = new Segmentation(bint1.binarizedImage.pixel);
        // Segmentation seg = new Segmentation();
        // seg.printImg();
        int index = seg.labelComponents();
        // System.out.println("hi");
        // seg.printImg();
        seg.prepareComponentList();
        seg.mergeSiblings(index);
        // seg.getRectangles();
        seg.colorComponents();
        // *******************************SKEW-CORRECTION*******************************
        // Skew skew = new Skew();
        // skew.printImg();
        // skew.getSkewAngle();

    }
}