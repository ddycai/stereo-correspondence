package cs4487;
import java.util.List;

import org.jgrapht.*;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class Test {

	public static void main(String[] args) {		
		WeightedGraph<Integer, DefaultWeightedEdge> g = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		// Add vertices.
		for (int i = 0; i < 5; i++) {
			g.addVertex(i);
		}
		// Add edges.
		DefaultWeightedEdge e = null;
		e = g.addEdge(0, 1);
		g.setEdgeWeight(e, 2);
				
		e = g.addEdge(0, 2);
		g.setEdgeWeight(e, 3);
				
		e = g.addEdge(1, 4);
		g.setEdgeWeight(e, 22.2);
				
		e = g.addEdge(2, 4);
		g.setEdgeWeight(e, 7.7);
	
		List<DefaultWeightedEdge> shortest_path = DijkstraShortestPath.findPathBetween(g, 0, 4);
        System.out.println(shortest_path);
	}
	
}
