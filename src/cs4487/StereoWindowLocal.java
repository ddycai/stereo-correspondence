package cs4487;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Finds disparities by finding the matching window along each scan line.
 * 
 * @author duncan
 *
 */
public class StereoWindowLocal extends AbstractStereoAlgorithm {

  public StereoWindowLocal(StereoImage img) {
    super(img);
  }

  public int[][] execute(int w, int h) {
    int[][] result = new int[img.leftRGB.length][img.leftRGB[0].length];
    int sum = 0, n = 0;
    for (int y = h; y < img.height - h; y++) {
      for (int x = w; x < img.width - w; x++) {
        result[y][x] = findDisparity(x, y, w, h);
        // System.out.println(result[y][x]);
        sum += result[y][x];
        n++;
      }
    }
    System.out.println((double) sum / n);
    int max = sum * 2 / n;
    for (int y = h; y < img.height - h; y++) {
      for (int x = w; x < img.width - w; x++) {
        double percentage = Math.min((double) result[y][x] / max, 1.0);
        result[y][x] = ImageUtil.brightnessToGrayscaleRGB(percentage);
      }
    }
    return result;
  }

  /**
   * Finds the disparity at (x, y) in the left image by matching a window of
   * width w*2 + 1 and height h*2 + 1;
   */
  private int findDisparity(int x, int y, int w, int h) {
    double cost = 0;
    double best = Double.POSITIVE_INFINITY;
    int cor = w;
    // For each pixel in the left image, the corresponding pixel in the right
    // image must be to the left.
    for (int q = w; q <= x; q++) {
      cost = 0;
      for (int i = -h; i <= h; i++) {
        for (int j = -w; j <= w; j++) {
          // cost += Math.pow(lI(x + i, y + j) - rI(q + i, y + j), 2);
          cost += ImageUtil.dist(lC(x + i, y + j), rC(q + i, y + j));
        }
      }
      if (cost < best) {
        cor = q;
        best = cost;
      }
    }
    return Math.abs(cor - x);
  }

  public static void main(String[] args) throws IOException {
    StereoImage stereo = new StereoImage(new File("res/Tsukuba_L.png"), new File("res/Tsukuba_R.png"));
    System.out.println("Width: " + stereo.width);
    System.out.println("Height: " + stereo.height);
    StereoWindowLocal algorithm = new StereoWindowLocal(stereo);
    BufferedImage result = ImageUtil.convertRGBToImage(algorithm.execute(5, 5));
    ImageUtil.showInJFrame("Window-based algorithm", result);
  }
}