package SkewCorrection;

public class Skew {

    private int[][] image;
    private double dMax;
    private double skewAngle;
    private int[][] votingMat;

    // Acute Angle is maximum skew angle
    private double AA = 90;
    private int SIZE_X;
    private int SIZE_Y;

    // Vars to define matrix
    private int MAT_SIZE_X;
    private int MAT_SIZE_Y;
    private double DEG_TO_RAD = Math.PI / 180;
    private double RAD_TO_DEG = 180 / Math.PI;

    public Skew() {
        SIZE_X = 10;
        SIZE_Y = 10;
        image = new int[SIZE_X][SIZE_Y];
        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                image[i][j] = 0;

            }
        }
        // image[0][0] = 1;
        // image[1][0] = 1;
        // image[2][0] = 1;
        // image[3][3] = 1;
        // image[2][2] = 1;
        // // image[2][3] = 1;
        // image[3][0] = 1;
        // image[4][1] = 1;
        // image[5][2] = 1;
        // image[6][3] = 1;
        // // image[5][3] = 1;
        // // image[5][4] = 1;
        // image[8][6] = 1;
        // image[9][6] = 1;
        // image[8][5] = 1;
        // image[4][4] = 1;
        // image[5][5] = 1;
        // image[6][6] = 1;
        // image[7][7] = 1;
        // image[9][8] = 1;
        // image[9][9] = 1;
        // image[8][8] = 1;
        for (int i = 0; i < 10; i++) {
            image[0][i] = 1;

        }

        // Calculating maximum distance between 2 points in the image
        dMax = Math.sqrt(((SIZE_X - 1) * (SIZE_X - 1)) + ((SIZE_Y - 1) * (SIZE_Y - 1)));

        // Initializing the voting matrix
        prepareVotingMat();
    }

    public Skew(int[][] image) {
        SIZE_X = image.length;
        SIZE_Y = image[0].length;
        // Calculating maximum distance between 2 points in the image
        dMax = Math.sqrt(((SIZE_X - 1) * (SIZE_X - 1)) + ((SIZE_Y - 1) * (SIZE_Y - 1)));

        // Initializing the voting matrix
        prepareVotingMat();
    }

    public void printImg() {
        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                System.out.print(image[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void prepareVotingMat() {

        double d = 0;
        double theta = 0;

        MAT_SIZE_X = (int) (dMax / 0.1);
        MAT_SIZE_Y = (int) (AA / 0.2);
        votingMat = new int[MAT_SIZE_X][MAT_SIZE_Y];
        for (d = 0; d < dMax; d = d + 0.1) {
            for (theta = 0; theta < AA; theta = theta + 0.2) {
                votingMat[(int) (d * 10)][(int) (theta * 5)] = 0;
                // System.out.print(votingMat[(int) (d * 10)][(int) (theta * 5)] + " ");

            }
            // System.out.println();
        }

        // for (int i = (int) (d * 10); d < dMax; d = d + 0.1) {
        // for (int j = (int) (theta * 5); theta < AA; theta = theta + 0.2) {
        // votingMat[i][j] = 0;
        // System.out.print(votingMat[i][j] + " ");
        // }
        // System.out.println(i + "Sdfsf");
        // }
        // System.out.println(d + " " + theta);
    }

    public void conductElection() {
        double d = 0;
        double theta = 0;
        for (int i = 0; i < SIZE_X - 1; i++) {
            for (int j = SIZE_Y - 1; j >= 0; j--) {
                // for (int j = 0; j < SIZE_Y; j++) {
                if (image[i][j] != 0) {
                    for (theta = 0; theta < AA; theta = theta + 0.2) {
                        d = j * Math.sin(theta * DEG_TO_RAD) + i * Math.cos(theta * DEG_TO_RAD);
                        if (d < 0) {
                            System.out.println(Math.sin(theta) + " " + theta + " " + i + " " + j);
                        } else {
                            votingMat[(int) (d * 10)][(int) (theta * 5)]++;
                        }
                        // d is -ve so out of bounds error. Need to correct
                        // votingMat[(int) (d * 10)][(int) (theta * 5)]++;
                        // votingMat[0][0]++;
                    }
                }
            }
        }
    }

    public void getSkewAngle() {
        conductElection();
        double d = 0;
        double theta = 0;
        int voteMax = 0;
        for (d = 0; d < dMax; d = d + 0.1) {
            for (theta = 0; theta < AA; theta = theta + 0.2) {
                if (votingMat[(int) (d * 10)][(int) (theta * 5)] >= voteMax) {
                    voteMax = votingMat[(int) (d * 10)][(int) (theta * 5)];
                    skewAngle = theta;
                }
            }
        }
        System.out.println(skewAngle);
    }
}