import java.util.HashMap;

public class TextRegionExtract {
    Segmentation segmented;
    // Image image;

    Component[] components;
    float noiseMarginHigh;
    float noiseMarginLow;

    public TextRegionExtract(Segmentation segmentation) {
        segmented = segmentation;
        // image = segmentation.getSegmentedImage();
        components = segmentation.getComponents();
        noiseMarginHigh = (float) 0.1;
        noiseMarginLow = (float) 0.1;
    }

    public void removeTinyComponents() {
        float maxRepeatedArea = 0;
        float maxArea = 0;
        float minArea = 999999;

        float[] componentsArea = new float[components.length];
        HashMap<Float, Integer> areaCount = new HashMap<Float, Integer>();

        // Populating the area array
        for (int i = 0; i < components.length; i++) {
            componentsArea[i] = components[i].getArea();

            //
            if (areaCount.get(componentsArea[i]) == null) {
                areaCount.put(componentsArea[i], 1);
            }
            // else if(areaCount.get(componentsArea[i] > )){

            // }
            else {
                areaCount.put(componentsArea[i], areaCount.get(componentsArea[i]) + 1);
            }
        }

        // SORT

        int count = 0;
        Component[] cleanComponents = new Component[components.length];
        for (int i = 0; i < components.length; i++) {
            // System.out.println("d" + componentsArea[i]);
            if (componentsArea[i] >= (1 + noiseMarginHigh) * maxRepeatedArea
                    || componentsArea[i] <= (1 - noiseMarginLow) *
                            maxRepeatedArea)
            // if (componentsArea[i] != maxRepeatedArea)

            {

                cleanComponents[count] = components[i];
                count++;
            }
        }
        System.out.println("cscdsc" + maxRepeatedArea);
        // segmented.setComponentsArray(cleanComponents);
        // areaCount.forEach((key, value) -> {

        // System.out.println(key + " " + value);
        // });

        segmented.drawRectangles(cleanComponents);

    }
}