package cs4487;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Container for stereo images. Converts left and right image into RGB int
 * arrays.
 * 
 * @author duncan
 */
public class StereoImage {

  int width, height;
  // Image RGB values.
  int[][] leftRGB, rightRGB;
  // Intensity values.
  double[][] leftI, rightI;

  public StereoImage(File left, File right) throws IOException {
    leftRGB = ImageUtil.convertImageToRGB(ImageIO.read(left));
    rightRGB = ImageUtil.convertImageToRGB(ImageIO.read(right));
    width = leftRGB[0].length;
    height = leftRGB.length;
    leftI = new double[height][width];
    rightI = new double[height][width];

    // Calculate the intensities.
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        leftI[i][j] = ImageUtil.luma(leftRGB[i][j]);
        rightI[i][j] = ImageUtil.luma(rightRGB[i][j]);
      }
    }
  }
}
