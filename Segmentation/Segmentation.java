/**
 * Class for segmentation using connected component analysis
 *
 * @author Ravi Pandey
 * @version v1
 */

package Segmentation;

import javax.swing.*;

import Binarization.Image;
import Binarization.ImagePanel;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Segmentation {

    private int MAX_COMP = 50000;
    private int MAX_TWINS = 10000;
    private int IMG_X = 10;
    private int IMG_Y = 10;

    private int twinsIndex = 0;
    private int[][] image;
    private int[][] twins;
    private Component listedComp[];

    private int componentSequence[];
    private ArrayList<ArrayList<Integer>> componentSiblings = new ArrayList<ArrayList<Integer>>();

    private BufferedImage bimg;
    // private Component listedComp[] = new Component[MAX_COMP];

    /**
     * Constructor for objects of class Segmentation
     */
    public Segmentation() {
        // initialise instance variables

        image = new int[IMG_X][IMG_Y];
        twins = new int[MAX_TWINS][2];
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
        twins = new int[MAX_TWINS][2];
        listedComp = new Component[MAX_COMP];
        IMG_X = binImage.length;
        IMG_Y = binImage[0].length;
        System.out.println(IMG_X + "hi" + IMG_Y);
        MAX_COMP = IMG_X * IMG_Y;
        MAX_TWINS = (int) (MAX_COMP / 2);
        componentSequence = new int[MAX_COMP];
    }

    public void printImg() {
        for (int j = 0; j < IMG_Y; j++) {
            for (int i = 0; i < IMG_X; i++) {
                System.out.print(image[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void labelComponents1() {
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
                            // componentSequence[image[i - 1][j]] = image[i][j];
                            int temp = componentSequence[image[i - 1][j]];
                            int temp2 = image[i - 1][j];
                            boolean setSibling = true;
                            while (componentSequence[temp] != temp) {
                                temp = componentSequence[temp];
                                if (temp == temp2) {
                                    setSibling = false;
                                    break;
                                }
                            }
                            if (setSibling) {
                                componentSequence[temp] = image[i][j];
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
                    }
                }
            }
        }

        for (int i = 2; i < componentIndex; i++) {

            defineSiblings(i, componentSequence[i]);

            System.out.println(componentSequence[i]);
        }
        // Displaying the siblings list
        for (ArrayList<Integer> siblings : componentSiblings) {
            System.out.println(siblings.toString());
        }

    }

    // Adding components to siblings list
    public void defineSiblings(int componentIndex, int siblingValue) {

        for (ArrayList<Integer> siblings : componentSiblings) {
            for (Integer componentVal : siblings) {
                if (componentVal == siblingValue) {
                    siblings.add(componentIndex);
                    return;
                }
            }
        }
        ArrayList<Integer> temp = new ArrayList<Integer>();
        temp.add(componentIndex);
        componentSiblings.add(temp);
    }

    public void mergeSiblings() {
        for (ArrayList<Integer> siblings : componentSiblings) {

            int parent = siblings.get(0);
            // System.out.println("hi" + parent);

            for (int i = 1; i < siblings.size(); i++) {
                // System.out.println("hi" + i);
                listedComp[parent].mergeComp(listedComp[siblings.get(i)]);
                listedComp[siblings.get(i)] = null;
            }

        }
    }

    public void labelComponents() {
        int componentIndex = 1;

        // Twins indicate different components with different index that are a single
        // component
        // int twins[][] = new int[10][2];
        // int twinsIndex = 0;
        for (int i = 0; i < IMG_X; i++) {
            for (int j = 0; j < IMG_Y; j++) {
                if (image[i][j] == 1) {
                    image[i][j] = componentIndex + 1;
                    componentIndex++;
                }

                // Checking to see if upper neighbour is a part of previously labelled component
                if (((i > 0 && image[i - 1][j] != 0)) && image[i][j] == componentIndex) {

                    // Decrement index count to previous value as no new component detected.
                    componentIndex--;

                    // Make current pixel a part of neighbour component
                    image[i][j] = image[i - 1][j];

                    // Checking if precceding pixels are part of the component or not
                    if (j > 0 && image[i][j - 1] != 0) {
                        image[i][j - 1] = image[i][j];
                        if ((j - 1 > 0 && image[i][j - 2] != 0) && (image[i][j - 1] != image[i][j - 2])) {
                            twins[twinsIndex][0] = image[i][j];
                            twins[twinsIndex][1] = image[i][j - 2];
                            twinsIndex++;
                        }
                    }

                    // System.out.println("Neighbour!!" + i + j);
                    continue;
                }
                // Checking to see if left beighbour is a part of previously labelled component
                if ((j > 0 && image[i][j - 1] != 0) && image[i][j] == componentIndex) {

                    // Decrement index count to previous value as no new component detected.
                    componentIndex--;

                    // Make current pixel a part of neighbour component
                    image[i][j] = image[i][j - 1];
                    // System.out.println("Neighbour!!" + i + j);
                    continue;
                }

            }
        }

        // print twins array
        for (int i = 0; i < twinsIndex; i++) {
            System.out.println(twins[i][0] + " " + twins[i][1]);
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
                        listedComp[image[i][j]].setValues(j, i);
                    }
                    // For parts of components already initiallized, setValues is only called
                    else {

                        listedComp[image[i][j]].setValues(j, i);
                    }

                }
            }
        }
        // Twins i.e. co-ordinates of component parts of a single component having
        // different labels are merged as one big rectangle

        // colorComponents();
        // mergeTwins();
        // Bounding rectangle for each labelled component
        // getRectangles();
    }

    public void mergeTwins() {
        // Iterates in twins array and merges two rectangles into one (done by mergeComp
        // method) and rearranges values in listedComp array
        for (int i = 0; i < twinsIndex; i++) {
            listedComp[twins[i][0]].mergeComp(listedComp[twins[i][1]]);
            listedComp[twins[i][1]] = null;

        }
    }

    public void getRectangles() {
        for (int i = 0; i < listedComp.length - 1; i++) {
            if (listedComp[i] != null) {
                listedComp[i].showValues(i);
            }
        }
    }

    public void colorComponents() {
        ImagePanel imgP = new ImagePanel();
        Image img = new Image(IMG_X, IMG_Y);
        img.pixel = image;
        JFrame window = new JFrame();
        // printImg();
        imgP.setImgComp(img);

        window.getContentPane().add(imgP);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(600, 600);
        window.setVisible(true);
    }
}