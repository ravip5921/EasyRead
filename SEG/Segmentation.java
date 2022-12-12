package SEG;

import javax.swing.*;

import Binarization.Image;
import Binarization.ImagePanel;

public class Segmentation {

    private int MAX_COMP = 50000;
    private int IMG_X = 10;
    private int IMG_Y = 10;

    private int[][] image;
    private Component listedComp[];

    private int componentSequence[];
    private int componentRoot[];
    private int componentTrailer[];

    /**
     * Constructor for objects of class Segmentation
     */
    public Segmentation() {
        // initialise instance variables

        image = new int[IMG_X][IMG_Y];
        listedComp = new Component[MAX_COMP];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                image[i][j] = 0;

            }
        }
        image[0][0] = 1;
        image[1][0] = 1;
        image[2][0] = 1;
        image[2][1] = 1;
        image[2][2] = 1;
        image[2][3] = 1;
        image[3][0] = 1;
        image[4][0] = 1;
        image[6][0] = 1;
        image[7][0] = 1;
        image[5][3] = 1;
        image[5][4] = 1;
        image[8][6] = 1;
        image[9][6] = 1;
        image[8][5] = 1;
        image[9][5] = 1;
        image[6][6] = 1;
        image[7][6] = 1;
        image[9][7] = 1;
        image[9][8] = 1;
        image[9][9] = 1;
        image[8][9] = 1;
        for (int i = 0; i < 3; i++) {
            for (int j = 4; j < 7; j++) {
                image[i][j] = 1;
            }
        }
    }

    public Segmentation(int[][] binImage) {
        image = binImage;
        listedComp = new Component[MAX_COMP];
        IMG_X = binImage.length;
        IMG_Y = binImage[0].length;

        MAX_COMP = IMG_X * IMG_Y;
        componentSequence = new int[MAX_COMP];
        componentRoot = new int[MAX_COMP];
        componentTrailer = new int[MAX_COMP];
    }

    public void printImg() {
        for (int j = 0; j < IMG_Y; j++) {
            for (int i = 0; i < IMG_X; i++) {
                System.out.print(image[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int getRoot(int x) {
        int temp = x;
        while (componentSequence[temp] != temp) {
            // System.out.println(temp + "ds" + componentSequence[temp]);
            temp = componentSequence[temp];
        }
        return temp;
    }

    public int labelComponents() {
        int componentIndex = 2;
        // boolean neighbourFound;
        for (int j = 0; j < IMG_Y; j++) {
            for (int i = 0; i < IMG_X; i++) {
                if (image[i][j] == 1) {
                    // neighbourFound = false;

                    // Checking upper neighbour
                    if (((j > 0 && image[i][j - 1] != 0))) {
                        image[i][j] = image[i][j - 1];
                        // neighbourFound = true;

                        // Checking left neighbour for SIBLINGS
                        if (((i > 0 && image[i - 1][j] != 0)) && image[i][j] != image[i - 1][j]) {
                            int rootX = getRoot(image[i][j]);
                            int rootY = getRoot(image[i - 1][j]);
                            if (rootX == rootY) {
                                continue;
                            }
                            componentSequence[rootY] = componentTrailer[rootX];

                            componentTrailer[rootX] = componentTrailer[rootY];

                            int temp1 = componentTrailer[rootY];

                            while (temp1 != componentSequence[rootY]) {
                                // System.out.println("Root changed.");
                                componentRoot[temp1] = rootX;

                                temp1 = componentSequence[temp1];

                            }
                        }
                    }
                    // Checking left neighbour
                    else if (((i > 0 && image[i - 1][j] != 0))) {
                        image[i][j] = image[i - 1][j];
                        // neighbourFound = true;
                    }

                    // Labelling as new component
                    else {
                        image[i][j] = componentIndex;
                        componentIndex++;
                        componentSequence[image[i][j]] = image[i][j];

                        componentRoot[image[i][j]] = image[i][j];
                        componentTrailer[image[i][j]] = image[i][j];
                    }
                }
            }
        }
        // for (int i = 2; i < componentIndex; i++) {
        // System.out.println(componentSequence[i]);
        // }
        // System.out.println("Roots:");
        // for (int i = 2; i < componentIndex; i++) {
        // System.out.println(componentRoot[i]);
        // }
        // System.out.println("Trailer Array");
        // for (int i = 2; i < componentIndex; i++) {
        // System.out.println(componentTrailer[i]);
        // }
        return componentIndex;
    }

    public void mergeSiblings(int componentIndex) {
        for (int i = 2; i < componentIndex; i++) {
            listedComp[componentRoot[i]].mergeComp(listedComp[i]);
            if (componentRoot[i] != i) {
                listedComp[i] = null;
            }
        }
    }

    public void prepareComponentList() {
        for (int i = 0; i < IMG_X; i++) {
            for (int j = 0; j < IMG_Y; j++) {
                // For every black pixel i.e part of some component
                if (image[i][j] != 0) {
                    // Initially all indices are null

                    // listedComp array has objects defined at indices corresponding to component
                    // labels other indices have no object efined i.e. are null
                    if (listedComp[image[i][j]] == null) {

                        // Defining an oject at index of component label for every new component label
                        // encountered during the pass
                        listedComp[image[i][j]] = new Component();

                        // setValues compares and sets diagonal co-ordinate values for component
                        // rectangle
                        listedComp[image[i][j]].setValues(i, j);
                    }
                    // For parts of components already initiallized, setValues is only called
                    else {

                        listedComp[image[i][j]].setValues(i, j);
                    }

                }
            }
        }

    }

    public void getRectangles() {
        int count = 0;
        for (int i = 0; i < listedComp.length - 1; i++) {
            if (listedComp[i] != null) {
                listedComp[i].showValues(i);
                count++;
            }
        }
        System.out.println("components = " + count);
    }

    public void colorComponents() {
        ImagePanel imgP = new ImagePanel();
        Image img = new Image(IMG_X, IMG_Y);
        img.pixel = image;
        JFrame window = new JFrame();
        // printImg();
        imgP.setBuffBounding(img, listedComp);

        window.getContentPane().add(imgP);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(600, 600);
        window.setVisible(true);

    }

    public void drawRectangles() {
        // ImagePanel imgP = new ImagePanel();

        // Image imgRect = new Image(IMG_X, IMG_Y);

        // imgP.setBuffBounding(imgRect, listedComp);
        // for (int i = 0; i < listedComp.length - 1; i++) {
        // if (listedComp[i] != null) {

        // for (int j = listedComp[i].getMinX(); j < listedComp[i].getMaxX(); j++) {
        // imgRect.pixel[j][listedComp[i].getMinY()] = -1;
        // imgRect.pixel[j][listedComp[i].getMaxY()] = -1;
        // }
        // }
        // }
    }
}