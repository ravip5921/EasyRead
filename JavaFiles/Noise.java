import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
// import Image.TYPE;
import java.io.File; // Import the File class
import java.io.IOException; // Import the IOException class to handle errors
import java.io.FileWriter;
import java.io.BufferedWriter;

public class Noise {
    Image image;
    int k;
    int quanta;
    String filename;
    HashMap<Integer, Integer> areaCount;

    private void initiallize(Image _img) {
        image = new Image(_img);
    }

    public Noise() {

    }

    public Noise(Image _img) {
        initiallize(_img);
        k = 0;
    }

    public Noise(Image _img, int _k) {
        initiallize(_img);
        k = _k;
        quanta = 0;
    }

    public Noise(Image _img, String _filename) {
        initiallize(_img);
        filename = _filename;
        k = 0;
    }

    public Image reducedNoiseImage() {
        kFillReduce();
        return image;
    }

    public void kFillReduce() {
        boolean onFillOccured = false;
        boolean offFillOccured = false;
        // do {
        for (int i = 0; i < (image.getWidth() - k); i = i + k) {
            for (int j = 0; j < (image.getHeight() - k); j = j + k) {
                Image temp = getWindow(image, i, j);

                if ((getTotalOnPixels(temp) - getCornerOnPixels(temp)) >= (((k - 2) * (k - 2)) / 2)) {
                    if (offFill(temp)) {
                        offFillOccured = true;
                        setWindow(temp, i, j, false);
                    } else {
                        offFillOccured = false;
                        setWindow(temp, i, j, true);
                    }
                } else {
                    if (onFill(temp)) {
                        onFillOccured = true;
                        setWindow(temp, i, j, true);
                    } else {
                        onFillOccured = false;
                        setWindow(temp, i, j, false);
                    }
                }
            }
            // System.out.println(i);
        }
        // } while (onFillOccured || offFillOccured);
    }

    public boolean onFill(Image img) {

        int n = getTotalOnPixels(img);
        int r = getCornerOnPixels(img);
        int c = getConnectedComponents(img);

        if (c == 1 && (n > ((3 * k) - 4) || ((n == ((3 * k) - 4) && (r == 2))))) {
            return true;
        }
        return false;
    }

    public boolean offFill(Image img) {
        int n = (k * k) - getTotalOnPixels(img);
        int r = (4 * (k - 1)) - getCornerOnPixels(img);
        int c = getConnectedComponents(invertImage(img));

        if (c == 1 && (n > ((3 * k) - 4) || ((n == ((3 * k) - 4) && (r == 2))))) {
            return true;
        }
        return false;
    }

    // area analysis

    public void getTinyComponents(Component[] comps, int componentsCount) {
        int countOfDistinctAreas = 0;
        int[] countOfBlackPixels = new int[componentsCount];
        areaCount = new HashMap<Integer, Integer>();

        for (int i = 0; i < componentsCount; i++) {
            // countOfBlackPixels[i] = (((int) (comps[i].getCountOfBlackPixels() / quanta))
            // * quanta) + 1;

            countOfBlackPixels[i] = comps[i].getCountOfBlackPixels();

            if (areaCount.get(countOfBlackPixels[i]) == null) {
                areaCount.put(countOfBlackPixels[i], 1);
                countOfDistinctAreas++;
            }
            // else if(areaCount.get(countOfBlackPixels[i] > )){

            // }
            else {
                areaCount.put(countOfBlackPixels[i], areaCount.get(countOfBlackPixels[i]) + 1);
            }
        }
        ArrayList<Integer> sortedArea = new ArrayList<Integer>(countOfDistinctAreas);
        ArrayList<Integer> sortedCount = new ArrayList<Integer>(countOfDistinctAreas);

        areaCount.forEach((key, value) -> {
            sortedArea.add(key);
            // sortedCount.add(value);
        });

        Collections.sort(sortedArea);
        for (int i = 0; i < countOfDistinctAreas; i++) {
            sortedCount.add(areaCount.get(sortedArea.get(i)));
        }

        System.out.print(sortedArea);
        System.out.print(sortedCount);

        writeToFile(areaCount, filename);
        // areaCount.forEach((key, value) -> {
        // System.out.println(key + " count " + value);
        // });

        // for (int i = 0; i < componentsCount; i++) {
        // System.out.println(comps[i].getCountOfBlackPixels() + " " +
        // countOfBlackPixels[i]);
        // }

    }

    public Component[] removeTinyComponents(Component[] comps, int componentsCount,
            int lowerAreaThreshold, int upperAreaThreshold) {
        // (((int) (comps[i].getCountOfBlackPixels() / quanta)) * quanta)

        Component[] newComps = new Component[componentsCount];
        int count = 0;

        for (int i = 0; i < componentsCount; i++) {
            // if ((((int) (comps[i].getCountOfBlackPixels() / quanta)) * quanta) >
            // areaThreshold)
            if (comps[i].getCountOfBlackPixels() > lowerAreaThreshold
                    && comps[i].getCountOfBlackPixels() < upperAreaThreshold) {
                newComps[count] = comps[i];
                count++;
            }
        }
        return newComps;
        // segment.setComponentsArray(newComps);
    }

    public boolean[] getTextComponents(Component[] comps, int componentsCount, int imgSizeX, int imgSizeY) {
        boolean[] isTextComponent = new boolean[componentsCount];
        for (int i = 0; i < componentsCount; i++) {
            if (comps[i].getPageSizeScore(imgSizeX, imgSizeY) < 0.003) { // or 50
                isTextComponent[i] = false;

            } else if (comps[i].getPageSizeScore(imgSizeX, imgSizeY) > 0.2) {
                isTextComponent[i] = false;
            } else {
                if (comps[i].getAreaDensityScore() < 0.15) {
                    isTextComponent[i] = false;
                } else

                    isTextComponent[i] = true;

            }
        }
        return isTextComponent;
    }

    public boolean[] getTextComponents2(Component[] comps, int componentsCount, int imgSizeX, int imgSizeY, int avg) {
        boolean[] isTextComponent = new boolean[componentsCount];
        for (int i = 0; i < componentsCount; i++) {

            if (comps[i].getPageSizeScore(imgSizeX, imgSizeY) < 0.002) { // or 50
                isTextComponent[i] = false;

                // if (comps[i].getPageSizeScore(imgSizeX, imgSizeY) < 2 * (float) avg &&
                // comps[i].getPageSizeScore(
                // imgSizeX, imgSizeY) > 0.65 * (float) avg) {

                if (comps[i].getArea() < 2 * (float) avg && comps[i].getArea() > 0.65 * (float) avg) {
                    float areaDensityScore = comps[i].getAreaDensityScore();
                    float aspectRatioScore = comps[i].getAspectRatioScore();
                    float pageSizeScore = comps[i].getPageSizeScore(imgSizeX, imgSizeY);

                    // componentsScore[i] = aspectRatioScore;

                    if (aspectRatioScore < 0.667f) {
                        isTextComponent[i] = true;
                    }

                    if (areaDensityScore > 0.6f) {
                        isTextComponent[i] = true;
                    }
                    // componentsScore[i] = 0;
                }

            } else if (comps[i].getPageSizeScore(imgSizeX, imgSizeY) > 0.25) {
                isTextComponent[i] = false;
            } else {
                if (comps[i].getAreaDensityScore() < 0.15) {
                    isTextComponent[i] = false;
                } else
                    isTextComponent[i] = true;
            }
        }
        return isTextComponent;
    }

    public float[] getComponentsPageSizeScore(Component[] comps, int componentsCount, int imgSizeX, int imgSizeY) {
        float[] componentsScore = new float[componentsCount];
        for (int i = 0; i < componentsCount; i++) {

            {
                float pageSizeScore = comps[i].getPageSizeScore(imgSizeX, imgSizeY);
                componentsScore[i] = pageSizeScore;
            }

        }
        return componentsScore;
    }

    public Component[] removeComponentsBasedOnScore(Component[] comps, float[] componentsScore, float scoreThreshold) {
        Component[] newComps = new Component[componentsScore.length];
        int count = 0;
        for (int i = 0; i < componentsScore.length; i++) {
            if (componentsScore[i] < scoreThreshold) {
                newComps[count] = comps[i];
                count++;
            }
        }
        return newComps;
    }

    public Component[] removeNonTextComponents(Component[] comps, boolean[] textComponent) {
        Component[] newComps = new Component[textComponent.length];
        int count = 0;
        for (int i = 0; i < textComponent.length; i++) {
            if (textComponent[i]) {
                newComps[count] = comps[i];
                count++;
            }
        }
        return newComps;
    }

    //////////////////// Utility
    private Image getWindow(Image img, int i, int j) {
        Image temp = new Image(k, k, Image.TYPE.BIN);
        for (int x = 0; x < k; x++) {
            for (int y = 0; y < k; y++) {
                temp.pixel[x][y] = img.pixel[i + x][j + y];
            }
        }
        return temp;
    }

    // Modify the main image
    private void setWindow(Image img, int i, int j, boolean onFill) {
        // Image temp = new Image(img.getWidth(), img.getHeight(), Image.TYPE.BIN);
        // temp = invertImage(img);

        // Image temp = invertImage(img);
        for (int x = 1; x < k - 1; x++) {
            for (int y = 1; y < k - 1; y++) {
                // if (img.pixel[i][j] == 0) {
                // image.pixel[i + x][j + y] = 1;
                // } else {
                // image.pixel[i + x][j + y] = 0;
                // }
                if (onFill) {
                    image.pixel[x + i][y + j] = 1;
                } else {
                    image.pixel[x + i][y + j] = 0;
                }
            }
        }
    }

    private int getCornerOnPixels(Image img) {
        int count = 0;
        for (int x = 0; x < k; x++) {
            for (int y = 0; y < k; y++) {
                if (x == 0 || x == k - 1 || y == 0 || y == k - 1) {
                    if (img.pixel[x][y] == 1) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private int getTotalOnPixels(Image img) {
        int count = 0;
        for (int x = 0; x < k; x++) {
            for (int y = 0; y < k; y++) {
                if (img.pixel[x][y] == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    private int getConnectedComponents(Image img) {
        Segmentation segmentation = new Segmentation(img);
        segmentation.segment();
        return segmentation.getComponentsCount();
    }

    private Image invertImage(Image _img) {
        Image img = new Image(_img.getWidth(), _img.getHeight(), Image.TYPE.BIN);
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                if (img.pixel[i][j] == 0) {
                    img.pixel[i][j] = 1;
                } else {
                    img.pixel[i][j] = 0;
                }
            }
        }
        return img;
    }

    // Area analysis
    private void writeToFile(HashMap<Integer, Integer> areaCount, String filename) {
        try {
            File myObj = new File(filename);
            if (myObj.createNewFile()) {
                // System.out.println("File created: " + myObj.getName());
            } else {
                // System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(myWriter);
            bw.write(Integer.toString(areaCount.size()));
            // bw.newLine();
            bw.write(",");
            areaCount.forEach((key, value) -> {
                try {

                    bw.write(key + " " + value);
                    // bw.newLine();
                    bw.write(",");

                    // System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            });
            bw.close();
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

// aspect ratio
// minumum score
// % of black pixels
// % according to page size