package cs4487;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class ImageUtil {

  /**
   * Convert an image into an int array of RGB values. From
   * http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
   */
  public static int[][] convertImageToRGB(BufferedImage image) {

    final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    final int width = image.getWidth();
    final int height = image.getHeight();
    final boolean hasAlphaChannel = image.getAlphaRaster() != null;

    int[][] result = new int[height][width];
    if (hasAlphaChannel) {
      final int pixelLength = 4;
      for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
        int argb = 0;
        argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
        argb += ((int) pixels[pixel + 1] & 0xff); // blue
        argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
        argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
        result[row][col] = argb;
        col++;
        if (col == width) {
          col = 0;
          row++;
        }
      }
    } else {
      final int pixelLength = 3;
      for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
        int argb = 0;
        argb += -16777216; // 255 alpha
        argb += ((int) pixels[pixel] & 0xff); // blue
        argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
        argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
        result[row][col] = argb;
        col++;
        if (col == width) {
          col = 0;
          row++;
        }
      }
    }

    return result;
  }

  public static BufferedImage convertRGBToImage(int[][] rgb) {
    BufferedImage img = new BufferedImage(rgb[0].length, rgb.length, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < rgb.length; y++) {
      for (int x = 0; x < rgb[0].length; x++) {
        img.setRGB(x, y, rgb[y][x]);
      }
    }
    return img;
  }

  public static void showInJFrame(String title, BufferedImage img) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        final JFrame frame = new JFrame();
        final ImagePanel panel = new ImagePanel(img);
        frame.add(panel);
        frame.setTitle(title);
        frame.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
      }
    });
  }

  public static double luma(int rgb) {
    int b = rgb & 0xff;
    int g = (rgb >> 8) & 0xff;
    int r = (rgb >> 16) & 0xff;
    return 0.2126 * r + 0.7152 * g + 0.0722 * b;
  }

  /**
   * Converts a percentage brightness into a grayscale RGB value. Multiplies 255
   * by the percentage (integer truncation) to get the value.
   */
  public static int brightnessToGrayscaleRGB(double percentage) {
    int hex = (int) ((percentage) * 255);
    return ((hex << 8) | hex) << 8 | hex;
  }

  public static int dist(int rgb1, int rgb2) {
    int b = (rgb1 & 0xff) - (rgb2 & 0xff);
    int g = ((rgb1 >> 8) & 0xff) - ((rgb2 >> 8) & 0xff);
    int r = ((rgb1 >> 16) & 0xff) - ((rgb2 >> 16) & 0xff);
    return r * r + g * g + b * b;
  }
}
