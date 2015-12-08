package cs4487;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.MinSourceSinkCut;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class StereoGraphCut extends AbstractStereoAlgorithm {

  protected static final int DISPARITY_LIMIT = 5;
  private static final int SPATIAL_CONSISTENTY_WEIGHT = 1;
  private int sink, source;
  private static int[] dx = { 0, -1, 0, 1 };
  private static int[] dy = { -1, 0, 1, 0 };
  private int sum, n;

  public StereoGraphCut(StereoImage stereo) {
    super(stereo);
    source = (DISPARITY_LIMIT + 1) * img.height * img.width;
    sink = source + 1;
    System.out.println("Source: " + source);
    System.out.println("Sink: " + sink);
    sum = n = 0;
  }

  public int[][] execute() {
    System.out.println("Constructing graph...");
    DirectedGraph<Integer, CakeEdge> graph = constructGraph();
    System.out.println("Done graph construction...");
    int[][] result = computeDisparities(graph);
    System.out.println(Arrays.deepToString(result));

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

  private DirectedGraph<Integer, CakeEdge> constructGraph() {
    SimpleDirectedWeightedGraph<Integer, CakeEdge> graph = new SimpleDirectedWeightedGraph<>(CakeEdge.class);

    // Add the source and the sink.
    graph.addVertex(source);
    graph.addVertex(sink);

    CakeEdge e;
    // Add all the nodes.
    for (int d = 0; d <= DISPARITY_LIMIT; d++) {
      for (int y = 0; y < img.height; y++) {
        for (int x = 0; x < img.width; x++) {
          graph.addVertex(id(d, x, y));
          // We add a vertical edge to the terminal node.
          if (d == DISPARITY_LIMIT) {
            // System.out.format("Adding edge from (%d, %d, %d) -> %d\n", d, x,
            // y, sink);
            e = graph.addEdge(id(d, x, y), sink);
            e.setEdgeType(CakeEdge.HORIZONTAL);
            graph.setEdgeWeight(e, INFINITY);
          }
        }
      }
    }

    // Add all the vertical and horizontal edges.
    for (int d = 0; d <= DISPARITY_LIMIT; d++) {
      System.out.format("Constructing layer %d...\n", d);
      for (int y = 0; y < img.height; y++) {
        for (int x = 0; x < img.width; x++) {
          int u = id(d, x, y), v;

          // Add the horizontal edges.
          for (int i = 0; i < 4; i++) {
            if (isValidNode(d, x + dx[i], y + dy[i])) {
              v = id(d, x + dx[i], y + dy[i]);
              if (u < v) {
                e = graph.addEdge(u, v);
                e.setEdgeType(CakeEdge.HORIZONTAL);
                graph.setEdgeWeight(e, SPATIAL_CONSISTENTY_WEIGHT);
              }
            }
          }

          // Add the vertical edges.
          v = (d == 0) ? source : id(d - 1, x, y);
          if (x + d < img.width) {
            double w = Math.abs(rI(x, y) - lI(x + d, y));
            e = graph.addEdge(v, u).setEdgeType(CakeEdge.VERTICAL);
            graph.setEdgeWeight(e, w);
          }
        }
      }
    }
    return graph;
  }

  /**
   * Compute the disparities by computing the min cut and then extracting the
   * results of the disparities.
   */
  private int[][] computeDisparities(DirectedGraph<Integer, CakeEdge> graph) {
    MinSourceSinkCut<Integer, CakeEdge> minCut = new MinSourceSinkCut<>(graph);

    System.out.println("Computing minimum cut...");
    minCut.computeMinCut(source, sink);

    System.out.println("Done computing minimum cut...");
    Set<CakeEdge> edges = minCut.getCutEdges();
    System.out.println(edges.size());

    System.out.println("Processing edges...");
    int[][] result = new int[img.height][img.width];
    for (CakeEdge e : edges) {
      // We don't care about horizontal edges.
      if (e.getEdgeType() == CakeEdge.HORIZONTAL) {
        continue;
      }
      int u = graph.getEdgeSource(e);
      int v = graph.getEdgeTarget(e);
      // We don't care about edges to the sink.
      if (v == sink) {
        continue;
      }

      int x1 = getX(u);
      int y1 = getY(u);
      int d1 = u == source ? 0 : getD(u);
      int x2 = getX(v);
      int y2 = getY(v);
      int d2 = v == source ? 0 : getD(v);
      // Let's do a sanity check.
      if (u == source || Math.abs(d1 - d2) == 1 && x1 == x2 && y1 == y2) {
        result[y2][x2] = d1;
        sum += d1;
        n++;
      } else {
        throw new IllegalArgumentException(
            String.format("Oh no! We're insane! (%d, %d, %d, %d) (%d, %d, %d, %d)", u, x1, y1, d1, v, x2, y2, d2));
      }
    }
    return result;
  }

  /**
   * Returns the id of the node at layer d and coordinates (x, y).
   */
  protected int id(int d, int x, int y) {
    return d * img.height * img.width + y * img.width + x;
  }

  protected boolean isValidNode(int d, int x, int y) {
    return (0 <= x && x < img.width && 0 <= y && y < img.height);
  }

  /**
   * Check if the given node id is a valid node.
   */
  protected boolean isValidNode(int id) {
    return 0 <= id && id <= sink;
  }

  protected int getX(int id) {
    if (!isValidNode(id)) {
      throw new IllegalArgumentException("Given node is not valid!");
    }
    return (id % (img.height * img.width)) % img.width;
  }

  protected int getY(int id) {
    if (!isValidNode(id)) {
      throw new IllegalArgumentException("Given node is not valid!");
    }
    return (id % (img.height * img.width)) / img.width;
  }

  protected int getD(int id) {
    if (!isValidNode(id)) {
      throw new IllegalArgumentException("Given node is not valid!");
    }
    if (id == source) {
      return -1;
    }
    return id / (img.height * img.width);
  }

  public static void main(String[] args) throws IOException {
    StereoImage stereo = new StereoImage(new File("res/Tsukuba_L_small.png"), new File("res/Tsukuba_R_small.png"));
    System.out.println("Width: " + stereo.width);
    System.out.println("Height: " + stereo.height);
    StereoGraphCut algorithm = new StereoGraphCut(stereo);
    BufferedImage result = ImageUtil.convertRGBToImage(algorithm.execute());
    ImageUtil.showInJFrame("Stereo Graph Cut", result);
  }
}