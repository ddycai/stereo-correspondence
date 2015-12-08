package cs4487;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class StereoGraphCutAlgorithmTest {

  @Test
  public void testId() throws IOException {
    StereoImage stereo = new StereoImage(new File("res/Tsukuba_L.png"), new File("res/Tsukuba_R.png"));
    StereoGraphCut algo = new StereoGraphCut(stereo);
    int expected = 0;
    for (int d = 0; d < StereoGraphCut.DISPARITY_LIMIT; d++) {
      for (int y = 0; y < algo.img.height; y++) {
        for (int x = 0; x < algo.img.width; x++) {
          int id = algo.id(d, x, y);
          assertEquals(expected, id);
          expected++;
        }
      }
    }
  }

  @Test
  public void testConversion() throws IOException {
    StereoImage stereo = new StereoImage(new File("res/Tsukuba_L.png"), new File("res/Tsukuba_R.png"));
    StereoGraphCut algo = new StereoGraphCut(stereo);
    for (int d = 0; d < StereoGraphCut.DISPARITY_LIMIT; d++) {
      for (int y = 0; y < algo.img.height; y++) {
        for (int x = 0; x < algo.img.width; x++) {
          int id = algo.id(d, x, y);
          int d0 = algo.getD(id);
          int x0 = algo.getX(id);
          int y0 = algo.getY(id);
          assertEquals(d, d0);
          assertEquals(x, x0);
          assertEquals(y, y0);
        }
      }
    }
  }

}
