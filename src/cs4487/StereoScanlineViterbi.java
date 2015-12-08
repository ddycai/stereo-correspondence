package cs4487;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Compute a disparity map by using the Viterbi algorithm to find the optimal
 * disparity of each pixel per scanline.
 * 
 * @author duncan
 *
 */
public class StereoScanlineViterbi extends AbstractStereoAlgorithm {
  // Upper threshold on the disparity value (to improve performance).
  private static final double DISPARITY_LIMIT_FACTOR = .25;
  private static final double DISPARITY_TERM_WEIGHT = 3;
  private int sum, n;

  public StereoScanlineViterbi(StereoImage stereo) {
    super(stereo);
  }

  public int[][] execute() {
    sum = n = 0;
    int[][] result = new int[img.height][img.width];
    for (int y = 0; y < img.height; y++) {
      calculateDisparities(result[y], y);
    }

    // Convert to grayscale RGB values.
    int max = sum * 2 / n;
    for (int y = 0; y < img.height; y++) {
      for (int x = 0; x < img.width; x++) {
        double percentage = Math.min((double) result[y][x] / max, 1.0);
        result[y][x] = ImageUtil.brightnessToGrayscaleRGB(percentage);
      }
    }
    return result;
  }

  /**
   * Calculate disparities for one scanline using Viterbi DP algorithm.
   */
  public void calculateDisparities(int[] disparity, int y) {
    int disparityLimit = (int) (img.width * DISPARITY_LIMIT_FACTOR);

    // memo[p][dp]: stores the optimal energy for the solution ending at pixel p
    // with disparity dp.
    double[][] memo = new double[img.width][disparityLimit];
    // link[p][dp]: stores the disparity for pixel p - 1 for the optimal
    // solution ending at for pixel p with disparity dp.
    int[][] link = new int[img.width][disparityLimit];

    // Set everything to infinity.
    for (int i = 0; i < memo.length; i++) {
      for (int j = 0; j < memo[0].length; j++) {
        memo[i][j] = INFINITY;
      }
    }

    // Initialize the first pixel at each disparity.
    for (int d = 0; d < disparityLimit && d < img.width; d++) {
      memo[0][d] = Math.abs(rI(0, y) - lI(d, y));
    }

    // For each subsequent pixel, optimize the links.
    for (int p = 1; p < img.width; p++) {
      // For each disparity, get the optimal previous disparity.
      for (int dp = 0; dp < disparityLimit && p + dp < img.width; dp++) {
        for (int dq = 0; dq < disparityLimit; dq++) {
          double energy = memo[p - 1][dq] + Math.abs(rI(p, y) - lI(p + dp, y))
              + DISPARITY_TERM_WEIGHT * Math.abs(dp - dq);
          // double energy = memo[p - 1][dq] + ImageUtil.dist(rC(p, y), lC(p +
          // dp, y))
          // + DISPARITY_TERM_WEIGHT * Math.abs(dp - dq);
          if (energy < memo[p][dp]) {
            memo[p][dp] = energy;
            link[p][dp] = dq;
          }
        }
      }
    }

    // Find the optimal ending energy.
    double best = INFINITY;
    int dCurrent = 0;
    for (int i = 0; i < disparityLimit; i++) {
      if (memo[img.width - 1][i] < best) {
        best = memo[img.width - 1][i];
        dCurrent = i;
      }
    }

    // Follow the links backward to find the disparities.
    int index = img.width - 1;
    while (index >= 0) {
      disparity[index] = dCurrent;
      sum += dCurrent;
      n++;
      dCurrent = link[index][dCurrent];
      index--;
    }
  }

  public static void main(String[] args) throws IOException {
    StereoImage stereo = new StereoImage(new File("res/Tsukuba_L.png"), new File("res/Tsukuba_R.png"));
    System.out.println("Width: " + stereo.width);
    System.out.println("Height: " + stereo.height);
    StereoScanlineViterbi algorithm = new StereoScanlineViterbi(stereo);
    BufferedImage result = ImageUtil.convertRGBToImage(algorithm.execute());
    ImageUtil.showInJFrame("Scanline Viterbi algorithm", result);
  }
}
