package cs4487;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.MinSourceSinkCut;

import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class StereoGrphCut extends AbstractStereoAlgorithm {

  private static final int DISPARITY_LIMIT = 1;
  private static final int SPATIAL_CONSISTENTY_WEIGHT = 1;
  private int sink, source;
  private static int[] dx = { 0, -1, 0, 1 };
  private static int[] dy = { -1, 0, 1, 0 };

  public StereoGrphCut(StereoImage stereo) {
    super(stereo);
    source = (DISPARITY_LIMIT + 1) * img.height * img.width;
    sink = source + 1;
    System.out.println("Source: " + source);
    System.out.println("Sink: " + sink);
  }

  public int[][] execute() {
    System.out.println("Constructing graph...");
    Grph graph = constructGraph();
    System.out.println("Done graph construction...");
    System.out.println("Executing max flow...");

    // MaxFlowAlgorithmResult result = graph.computeMaxFlow(source, sink,
    // graph.getEdgeWidthProperty());
    System.out.println("Done max flow...");
    // double flow = result.getFlow();
    // System.out.println("Maximum flow value: " + flow);
    int[][] disps = null;
    return disps;
  }

  private Grph constructGraph() {
    Grph graph = new InMemoryGrph();

    // Add the source and the sink.
    graph.addVertex(source);
    graph.addVertex(sink);

    int e;
    // Add all the nodes.
    for (int d = 0; d <= DISPARITY_LIMIT; d++) {
      for (int y = 0; y < img.height; y++) {
        for (int x = 0; x < img.width; x++) {
          graph.addVertex(id(d, x, y));
          // We add a vertical edge to the terminal node.
          if (d == DISPARITY_LIMIT) {
            // System.out.format("Adding edge from (%d, %d, %d) -> %d\n", d, x,
            // y, sink);
            e = graph.addDirectedSimpleEdge(id(d, x, y), sink);
            graph.getEdgeWidthProperty().setValue(e, INFINITY);
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
                e = graph.addDirectedSimpleEdge(u, v);
                graph.getEdgeWidthProperty().setValue(e, SPATIAL_CONSISTENTY_WEIGHT);
              }
            }
          }

          // Add the vertical edges.
          v = (d == 0) ? source : id(d - 1, x, y);
          if (x + d < img.width) {
            long w = (long) Math.abs(rI(x, y) - lI(x + d, y));
            e = graph.addDirectedSimpleEdge(v, u);
            graph.getEdgeWidthProperty().setValue(e, w);
            // graph.addEdge(v, u).setEdgeType(CakeEdge.VERTICAL);
            // eTS = graph.addEdge(u, v).setEdgeType(CakeEdge.VERTICAL);
            // graph.setEdgeWeight(eST, w);
            // graph.setEdgeWeight(eTS, INFINITY);
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

    System.out.println("Processing edges...");
    int[][] result = new int[img.height][img.height];
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
      int d1 = getD(u);
      int x2 = getX(v);
      int y2 = getY(v);
      int d2 = getD(v);
      // Let's do a sanity check.
      if (Math.abs(d1 - d2) == 1 && x1 == x2 && y1 == y2) {
        result[y1][x1] = d2;
      } else {
        throw new IllegalArgumentException("Oh no! We're insane!");
      }
    }
    return result;
  }

  /**
   * Returns the id of the node at layer d and coordinates (x, y).
   */
  public int id(int d, int x, int y) {
    return d * img.height * img.width + y * img.width + x;
  }

  public boolean isValidNode(int d, int x, int y) {
    return (0 <= x && x < img.width && 0 <= y && y < img.height);
  }

  /**
   * Check if the given node id is a valid node.
   */
  private boolean isValidNode(int id) {
    return 0 <= id && id <= sink;
  }

  private int getX(int id) {
    if (!isValidNode(id)) {
      throw new IllegalArgumentException("Given node is not valid!");
    }
    return (id % (img.height * img.width)) % img.height;
  }

  private int getY(int id) {
    if (!isValidNode(id)) {
      throw new IllegalArgumentException("Given node is not valid!");
    }
    return (id % (img.height * img.width)) / img.height;
  }

  private int getD(int id) {
    if (!isValidNode(id)) {
      throw new IllegalArgumentException("Given node is not valid!");
    }
    if (id == source) {
      return -1;
    }
    return id / (img.height * img.width);
  }

  public static void main(String[] args) throws IOException {
    StereoImage stereo = new StereoImage(new File("res/Tsukuba_L.png"), new File("res/Tsukuba_R.png"));
    System.out.println("Width: " + stereo.width);
    System.out.println("Height: " + stereo.height);
    StereoGrphCut algorithm = new StereoGrphCut(stereo);
    BufferedImage result = ImageUtil.convertRGBToImage(algorithm.execute());
    ImageUtil.showInJFrame("Stereo Graph Cut", result);
  }
}