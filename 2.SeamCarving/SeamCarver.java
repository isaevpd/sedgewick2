import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import java.awt.Color;

public class SeamCarver {
    private int[][] pictureMatrix;
    private double[][] energyMatrix;
    private int[] verticalSeam;
    private int[] horizontalSeam;
    private boolean isTransposed;
    private int height;
    private int width;
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        this.height = picture.height();
        this.width = picture.width();
        this.isTransposed = false;
        this.pictureMatrix = constructPictureMatrix(picture);

        this.energyMatrix = new double[this.height()][this.width()];

        // initialize energy and cumulated matrices
        for (int i = 0; i < height(); i++)
            for (int j = 0; j < width(); j++) {
                this.energyMatrix[i][j] = this.helperEnergy(i, j);
            }

        this.getBothSeams();

    }
    /* HELPER FUNCTIONS */
    private double getDeltaX(int x, int y) {
        Color topPixel = new Color(this.pictureMatrix[x - 1][y]);
        Color bottomPixel = new Color(this.pictureMatrix[x + 1][y]);
        // colors for pixel above
        int topRed = topPixel.getRed();
        int topGreen = topPixel.getGreen();
        int topBlue = topPixel.getBlue();
        // colors for pixel below
        int bottomRed = bottomPixel.getRed();
        int bottomGreen = bottomPixel.getGreen();
        int bottomBlue = bottomPixel.getBlue();
        // return sum of difference squared
        return Math.pow((bottomRed - topRed), 2) + Math.pow((bottomGreen - topGreen), 2) + Math.pow((bottomBlue - topBlue), 2);
    }

    private double getDeltaY(int x, int y) {
        Color leftPixel = new Color(this.pictureMatrix[x][y - 1]);
        Color rightPixel = new Color(this.pictureMatrix[x][y + 1]);
        // colors for pixel above
        int leftRed = leftPixel.getRed();
        int leftGreen = leftPixel.getGreen();
        int leftBlue = leftPixel.getBlue();
        // colors for pixel below
        int rightRed = rightPixel.getRed();
        int rightGreen = rightPixel.getGreen();
        int rightBlue = rightPixel.getBlue();
        // return sum of difference squared
        return Math.pow((rightRed - leftRed), 2) + Math.pow((rightGreen - leftGreen), 2) + Math.pow((rightBlue - leftBlue), 2);
    }

    // method to build a picture from 2d Color array
    private Picture constructPicture(int[][] matrix) {
        Picture picture = new Picture(width, height);
        for (int j = 0; j < width; j++)
            for (int i = 0; i < height; i++) {
                Color temp = new Color(matrix[i][j]);
                picture.set(j, i, temp);
            }
        return picture;
    }
    // method to initialize pictureMatrix in the constructor
    private int[][] constructPictureMatrix(Picture picture) {
        int[][] colorMatrix = new int[this.height][this.width];
        for (int j = 0; j < this.width; j++)
            for (int i = 0; i < this.height; i++) 
                colorMatrix[i][j] = picture.get(j, i).getRGB();

        return colorMatrix;
    }

    private void printEnergyMatrix() {
        StdOut.println("Energy Matrix is:");
        for (double[]row:this.energyMatrix) {
            // StdOut.println(i + "th row is: ");
            for (double num:row)
                StdOut.print(String.format("%.2f", num) + " ");
            StdOut.println();
        }
    }

    // energy of pixel at row x and column y                           
    private double helperEnergy(int x, int y) {
        int energyHeight = this.energyMatrix.length;
        int energyWidth = this.energyMatrix[0].length;
        if (x < 0 || y < 0) throw new IndexOutOfBoundsException();
        if (x > energyHeight - 1 || y > energyWidth - 1) throw new IndexOutOfBoundsException();
        if (x == energyHeight - 1 || y == energyWidth - 1) return 1000;
        if (x == 0 || y == 0) return 1000;
        double deltaX = getDeltaX(x, y);
        double deltaY = getDeltaY(x, y);
        return Math.sqrt(deltaX + deltaY);
    }
    
    private void transpose() {
        this.energyMatrix = helperTranspose(energyMatrix);
        this.pictureMatrix = helperTranspose(pictureMatrix);
        this.isTransposed = !this.isTransposed;
        // this.printEnergyMatrix();
    }   
    private int[][] helperTranspose(int[][] intMatrix) {
        int[][] transposedMatrix = new int[intMatrix[0].length][intMatrix.length];
        for (int i = 0; i < intMatrix.length; i++)
            for (int j = 0; j < intMatrix[0].length; j++)
                transposedMatrix[j][i] = intMatrix[i][j];
        return transposedMatrix;
    }

    private double[][] helperTranspose(double[][] doubleMatrix) {
        double[][] transposedMatrix = new double[doubleMatrix[0].length][doubleMatrix.length];
        for (int i = 0; i < doubleMatrix.length; i++)
            for (int j = 0; j < doubleMatrix[0].length; j++)
                transposedMatrix[j][i] = doubleMatrix[i][j];
        return transposedMatrix;
    }

    // fills parent value for row x column y
    private void getBestParent(int x, int y, double[][] cumulatedMatrix, int[][] parentMatrix) {
        double midChild = this.energyMatrix[x + 1][y];  // middle child

        double currentCumulatedValue = cumulatedMatrix[x][y];
        double sumBelow = midChild + currentCumulatedValue;
        if (sumBelow < cumulatedMatrix[x + 1][y]) {
            cumulatedMatrix[x + 1][y] = sumBelow;
            parentMatrix[x + 1][y] = y;
        }

        if (y != 0) {
            double leftChild = this.energyMatrix[x + 1][y - 1]; // left child
            double sumLeftBelow = leftChild + currentCumulatedValue;
            if (sumLeftBelow < cumulatedMatrix[x + 1][y - 1]) {
                cumulatedMatrix[x + 1][y - 1] = sumLeftBelow;
                parentMatrix[x + 1][y - 1] = y;
            }
        }

        if (y != energyMatrix[0].length - 1) {
            double rightChild = this.energyMatrix[x + 1][y + 1]; // right child
            double sumRightBelow = rightChild + currentCumulatedValue;
            if (sumRightBelow < cumulatedMatrix[x + 1][y + 1]) {
                cumulatedMatrix[x + 1][y + 1] = sumRightBelow;
                parentMatrix[x + 1][y + 1] = y;
            }
        }
    }

    private int[] getBestSeam() {
        int[] seam;
        int energyHeight = energyMatrix.length;
        int energyWidth = energyMatrix[0].length;
        // System.out.println(energyHeight + " " + energyWidth);
        double[][] cumulatedMatrix = new double[energyHeight][energyWidth];
        int[][] parentMatrix = new int[energyHeight][energyWidth];

        for (int i = 0; i < energyHeight; i++)
            for (int j = 0; j < energyWidth; j++)
                cumulatedMatrix[i][j] = Double.POSITIVE_INFINITY;

        for (int j = 0; j < energyWidth; j++)
            cumulatedMatrix[0][j] = 1000;

        for (int i = 0; i < energyHeight - 1; i++)
            for (int j = 0; j < energyWidth; j++) {
                this.getBestParent(i, j, cumulatedMatrix, parentMatrix);
            }
        // System.out.println("Were fien!!");
        seam = computeSeam(cumulatedMatrix, parentMatrix);
        return seam;
    }

    private int[] computeSeam(double[][] cumulatedMatrix, int[][] parentMatrix) {
        // iterate over last row in cumulatedMatrix to get min parent
        int[] result = new int[cumulatedMatrix.length];
        double min = Double.POSITIVE_INFINITY;
        int parent = -1;
        for (int j = 0; j < cumulatedMatrix[0].length; j++) {

            if (cumulatedMatrix[cumulatedMatrix.length - 1][j] < min) {
                min = cumulatedMatrix[cumulatedMatrix.length - 1][j];
                parent = j;
            }
        }

        result[cumulatedMatrix.length - 1] = parent;
        // go up and save the path
        for (int i = cumulatedMatrix.length - 1; i > 0; i--) {
            parent = parentMatrix[i][parent];
            result[i - 1] = parent;
        }
        return result;     
    }

    private void getBothSeams() {
        if (this.isTransposed) {
            this.horizontalSeam = getBestSeam();
            this.transpose();
            this.verticalSeam = getBestSeam();
        }
        else {
            this.verticalSeam = getBestSeam();
            this.transpose();
            this.horizontalSeam = getBestSeam();
        }
    }

    private int[] removeElement(int[] array, int index) {
        int[] result = new int[array.length - 1];
        int indexFill = 0;
        for (int i = 0; i < array.length; i++) {
            if (i != index) {
                result[indexFill++] = array[i];
            }
        }
        return result;
    }

    private double[] removeElement(double[] array, int index) {
        double[] result = new double[array.length - 1];
        int indexFill = 0;
        for (int i = 0; i < array.length; i++) {
            if (i != index) {
                result[indexFill++] = array[i];
            }
        }
        return result;
    }

    private int[][] removeSeamHelper(int[][] matrix, int[] seam) {
        int[] newRow;
        int x = matrix.length;
        int y = matrix[0].length;
        int[][] newMatrix = new int[x][y - 1];
        for (int i = 0; i < x; i++) {
            newRow = removeElement(matrix[i], seam[i]);
            newMatrix[i] = newRow;
        }
        return newMatrix;
    }

    private double[][] removeSeamHelper(double[][] matrix, int[] seam) {
        double[] newRow;
        int x = matrix.length;
        int y = matrix[0].length;
        double[][] newMatrix = new double[x][y - 1];
        for (int i = 0; i < x; i++) {
            newRow = removeElement(matrix[i], seam[i]);
            newMatrix[i] = newRow;
        }
        return newMatrix;
    }

    private void removeSeamWrapper(int[] seam) {
        this.pictureMatrix = this.removeSeamHelper(this.pictureMatrix, seam);
        this.energyMatrix = this.removeSeamHelper(this.energyMatrix, seam);
        int x = this.energyMatrix.length;
        int y = this.energyMatrix[0].length;
        int j;
        for (int i = 1; i < x - 1; i++) {
            j = seam[i];
            if (j < y - 1)
                this.energyMatrix[i][j] = this.helperEnergy(i, j);
            if (j > 1)
                this.energyMatrix[i][j - 1] = this.helperEnergy(i, j - 1);
        }
        this.getBothSeams();
    }


    /* ACTUAL API FUNCTIONS */

    // current picture             
    public Picture picture() {
        if (!isTransposed)
            return constructPicture(pictureMatrix);
        transpose();
        return constructPicture(pictureMatrix);
    }
    // width of current picture                          
    public int width() {
        return this.width;
    }
    // height of current picture                          
    public int height() {
        return this.height;
    }
    // energy of pixel at column x and row y
    public  double energy(int x, int y) {
        if (x < 0 || y < 0) throw new IndexOutOfBoundsException();
        if (x > width() - 1 || y > height() - 1) throw new IndexOutOfBoundsException();
        if (x == width() - 1 || y == height() - 1) return 1000;
        if (x == 0 || y == 0) return 1000;
        int temp = x;
        x = y;
        y = temp;
        if (this.isTransposed)
            return helperEnergy(y, x);
        else
            return helperEnergy(x, y);
    }
    // sequence of indices for horizontal seam          
    public int[] findHorizontalSeam() {
        return this.horizontalSeam.clone();
    }     
    // sequence of indices for vertical seam   
    public int[] findVerticalSeam() {
        return this.verticalSeam.clone();
    }
    // remove horizontal seam from current picture              
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null)
            throw new NullPointerException();
        if (this.height() < 2)
            throw new IllegalArgumentException();
        if (seam.length != this.width() || (seam[0] < 0 || seam[0] > this.height() - 1))
            throw new IllegalArgumentException();
        for (int i = 1; i < seam.length; i++) {
            if ((Math.abs(seam[i] - seam[i - 1]) > 1) || (seam[i] > this.height() - 1 || seam[i] < 0))
                throw new IllegalArgumentException();
        }

        if (isTransposed)
            removeSeamWrapper(seam);
        else {
            this.transpose();
            removeSeamWrapper(seam);
        }
        this.height--;
    }
    // remove vertical seam from current picture 
    public void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new NullPointerException();
        if (this.width() < 2)
            throw new IllegalArgumentException();
        if (seam.length != this.height() || (seam[0] < 0 || seam[0] > this.width() - 1))
            throw new IllegalArgumentException();
        for (int i = 1; i < seam.length; i++) {
            if ((Math.abs(seam[i] - seam[i - 1]) > 1) || (seam[i] > this.width() - 1 || seam[i] < 0))
                throw new IllegalArgumentException();
        }
        if (!isTransposed)
            removeSeamWrapper(seam);
        else {
            this.transpose();
            removeSeamWrapper(seam);
        }
        this.width--;
    }
    // main method for testing
    public static void main(String[] args) {

    }     
}