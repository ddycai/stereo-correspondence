package cs4487;

import org.jgrapht.graph.DefaultWeightedEdge;

@SuppressWarnings("serial")
public class CakeEdge extends DefaultWeightedEdge {
  public static final boolean HORIZONTAL = true;
  public static final boolean VERTICAL = false;
  private boolean edgeType;

  public boolean getEdgeType() {
    return edgeType;
  }

  public CakeEdge setEdgeType(boolean edgeType) {
    this.edgeType = edgeType;
    return this;
  }
}
