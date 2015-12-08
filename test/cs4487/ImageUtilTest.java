package cs4487;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ImageUtilTest {

  @Test
  public void testGreyscaleRGB() {
    // 25% gray should have hex code #3f3f3f.
    assertEquals("3f3f3f", Integer.toHexString(ImageUtil.brightnessToGrayscaleRGB(.25)));

    // 100% brightness should be white.
    assertEquals("ffffff", Integer.toHexString(ImageUtil.brightnessToGrayscaleRGB(1)));

    // 50% brightness should be #7F7F7F.
    assertEquals("7f7f7f", Integer.toHexString(ImageUtil.brightnessToGrayscaleRGB(.5)));
  }

  @Test
  public void testDist() {
    assertEquals(255 * 255, ImageUtil.dist(0x00ffff, 0xffffff));
    assertEquals(3 * 85 * 85, ImageUtil.dist(0xaaaaaa, 0xffffff));
  }
}
