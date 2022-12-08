
/**
 * Write a description of class BinImage here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class BinImage {
    // instance variables - replace the example below with your own

    /**
     * Constructor for objects of class BinImage
     */
    public BinImage() {
        // initialise instance variables

    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param y a sample parameter for a method
     * @return the sum of x and y
     */
    public static void main(String args[]) {

        Segmentation seg = new Segmentation();
        seg.printImg();
        seg.labelComponents();
        seg.printImg();
        seg.prepareComponentList();
    }
}
