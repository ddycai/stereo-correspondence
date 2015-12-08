package cs4487;

/**
 * Wrapper class for stereo algorithms that holds a stereo image and provides
 * functions for accessing the stereo image in a convenient way.
 * 
 * @author duncan
 *
 */
public abstract class AbstractStereoAlgorithm {
  protected static final long INFINITY = 1000000;
  StereoImage img;

  public AbstractStereoAlgorithm(StereoImage stereo) {
    img = stereo;
  }

  /**
   * Left intensity at (x, y).
   */
  public double lI(int x, int y) {
    return img.leftI[y][x];
  }

  /**
   * Right intensity at (x, y).
   */
  public double rI(int x, int y) {
    return img.rightI[y][x];
  }

  /**
   * Left RGB value at (x, y).
   */
  public int lC(int x, int y) {
    return img.leftRGB[y][x];
  }

  /**
   * Left RGB value at (x, y).
   */
  public int rC(int x, int y) {
    return img.rightRGB[y][x];
  }
}
