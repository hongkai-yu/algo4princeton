/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 7 Nov 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private int[][] colorMatrix;
    private double[][] energyMatrix;

    private int width; // active width, beyond this are abandoned
    private int height; // active height

    private boolean isTransposed;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {

        if (picture == null) {
            throw new IllegalArgumentException();
        }

        this.height = picture.height();
        this.width = picture.width();

        colorMatrix = new int[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                colorMatrix[col][row] = picture.getRGB(col, row);
            }
        }

        energyMatrix = new double[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                energyMatrix[col][row] = energyHelper(col, row);
            }
        }

        isTransposed = false;

    }

    private void requireTransposed() {
        if (!isTransposed) {
            transpose();
        }
    }

    private void requireOriginal() {
        if (isTransposed) {
            transpose();
        }
    }

    private void transpose() {
        int[][] transposedColorMatrix = new int[height][width];
        double[][] transposedEnergyMatrix = new double[height][width];

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                transposedColorMatrix[row][col] = colorMatrix[col][row];
                transposedEnergyMatrix[row][col] = energyMatrix[col][row];
            }
        }

        colorMatrix = transposedColorMatrix;
        energyMatrix = transposedEnergyMatrix;

        int temp = width;
        width = height;
        height = temp;

        isTransposed = !isTransposed;
    }

    // current picture, original
    public Picture picture() {

        if (isTransposed) {
            Picture picture = new Picture(height, width);

            for (int col = 0; col < width; col++) {
                for (int row = 0; row < height; row++) {
                    picture.setRGB(row, col, colorMatrix[col][row]);
                }
            }
            return picture;
        }
        else {
            Picture picture = new Picture(width, height);

            for (int col = 0; col < width; col++) {
                for (int row = 0; row < height; row++) {
                    picture.setRGB(col, row, colorMatrix[col][row]);
                }
            }
            return picture;
        }
    }

    // width of current picture, original
    public int width() {
        // width of the original picture
        if (isTransposed) {
            return height;
        }
        else {
            return width;
        }
    }

    // height of current picture, original
    public int height() {
        // height of the original picture
        if (isTransposed) {
            return width;
        }
        else {
            return height;
        }
    }

    // energy of pixel at column x and row y, original
    public double energy(int x, int y) {
        // height of the original picture
        if (isTransposed) {
            return energyHelper(y, x);
        }
        else {
            return energyHelper(x, y);
        }
    }

    private double energyHelper(int x, int y) {
        if (!inPicture(x, y)) {
            throw new IllegalArgumentException();
        }

        if (onBorder(x, y)) {
            return 1000;
        }

        int left = colorMatrix[x - 1][y];
        int right = colorMatrix[x + 1][y];
        int up = colorMatrix[x][y - 1];
        int down = colorMatrix[x][y + 1];

        return Math.sqrt(squaredDeltaColor(left, right) +
                                 squaredDeltaColor(up, down));
    }

    private double squaredDeltaColor(int color1, int color2) {

        int red1 = color1 >> 16 & 0xFF;
        int red2 = color2 >> 16 & 0xFF;
        int green1 = color1 >> 8 & 0xFF;
        int green2 = color2 >> 8 & 0xFF;
        int blue1 = color1 & 0xFF;
        int blue2 = color2 & 0xFF;

        return Math.pow(red1 - red2, 2) +
                Math.pow(green1 - green2, 2) +
                Math.pow(blue1 - blue2, 2);
    }

    private boolean inPicture(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private boolean onBorder(int x, int y) {
        return x == 0 || x == width - 1 || y == 0 || y == height - 1;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        requireOriginal();
        return findHorizontalSeamHelper();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        requireTransposed();
        return findHorizontalSeamHelper();
    }

    private int[] findHorizontalSeamHelper() {
        // dynamic programming
        double[][] loss = new double[width][height]; // minimize this
        int[][] policy = new int[width][height];

        // last column
        for (int row = 0; row < height; row++) {
            loss[width - 1][row] = energyMatrix[width - 1][row];
        }

        // backward induction
        for (int col = width - 2; col >= 0; col--) {
            for (int row = 0; row < height; row++) {
                int optimalAction = 0;
                double optimalFutureLoss = Double.POSITIVE_INFINITY;

                for (int action : actions(row)) {
                    double futureLoss = loss[col + 1][row + action];
                    if (futureLoss < optimalFutureLoss) {
                        optimalAction = action;
                        optimalFutureLoss = futureLoss;
                    }
                }

                policy[col][row] = optimalAction;
                loss[col][row] = energyMatrix[col][row] + optimalFutureLoss;
            }
        }

        // find best
        int optimalFirstRow = 0;
        double optimalFirstLoss = Double.POSITIVE_INFINITY;
        for (int row = 0; row < height; row++) {
            if (loss[0][row] < optimalFirstLoss) {
                optimalFirstLoss = loss[0][row];
                optimalFirstRow = row;
            }
        }

        int[] optimalRows = new int[width];
        optimalRows[0] = optimalFirstRow;

        for (int col = 0; col < width - 1; col++) {
            optimalRows[col + 1] = optimalRows[col] + policy[col][optimalRows[col]];
        }

        return optimalRows;
    }

    private int[] actions(int row) {

        // three possible actions
        final int UP = -1;
        final int FLAT = 0;
        final int DOWN = 1;

        if (row == 0) {
            if (height == 1) {
                return new int[] { FLAT };
            }
            else {
                return new int[] { FLAT, DOWN };
            }
        }
        else if (row == height - 1) {
            return new int[] { FLAT, UP };
        }
        else {
            return new int[] { FLAT, UP, DOWN };
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        requireOriginal();
        removeHorizontalSeamHelper(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        requireTransposed();
        removeHorizontalSeamHelper(seam);
    }

    private void removeHorizontalSeamHelper(int[] seam) {

        validateSeam(seam);

        // update the color matrix
        for (int col = 0; col < width; col++) {
            for (int row = seam[col]; row < height - 1; row++) {
                // the final one would be "abandoned"
                colorMatrix[col][row] = colorMatrix[col][row + 1];
            }
        }

        // update the energy matrix
        for (int col = 0; col < width; col++) {
            updateNeighbors(col, seam[col]);
            for (int row = seam[col] + 1; row < height - 1; row++) {
                // the final one would be "abandoned"
                energyMatrix[col][row] = energyMatrix[col][row + 1];
            }
        }

        height--; // abandon the last one
    }

    private void validateSeam(int[] seam) {
        if (seam == null || seam.length != width) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < seam.length; i++) {
            int s = seam[i];
            if (s < 0 || s >= height) {
                throw new IllegalArgumentException();
            }
            if (i > 0 && Math.abs(s - seam[i - 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void updateNeighbors(int x, int y) {
        updateEnergy(x, y);
        updateEnergy(x - 1, y);
        updateEnergy(x + 1, y);
        updateEnergy(x, y - 1);
        updateEnergy(x, y + 1);
    }

    private void updateEnergy(int x, int y) {
        if (inPicture(x, y)) {
            energyMatrix[x][y] = energyHelper(x, y);
        }
    }


    //  unit testing (optional)
    public static void main(String[] args) {
        // nothing
    }

}
