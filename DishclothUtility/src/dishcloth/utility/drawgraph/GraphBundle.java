package dishcloth.utility.drawgraph;

import dishcloth.api.util.geom.Point;
import dishcloth.api.util.memory.PointCache;

import java.awt.*;
import java.util.List;

/**
 * Created by Lassi on 21.7.2015.
 */
public class GraphBundle {

	private List<Graph> graphs;
	private float xmin, xmax, ymin, ymax;

	public GraphBundle(List<Graph> graphs) {
		this.graphs = graphs;

		if (graphs.size() == 0) return;

		xmin = graphs.get( 0 ).getCurve().getXMin();
		for (Graph graph : graphs) {
			float n = graph.getCurve().getXMin();
			if (n < xmin) xmin = n;
		}
		xmax = graphs.get( 0 ).getCurve().getXMax();
		for (Graph graph : graphs) {
			float n = graph.getCurve().getXMax();
			if (n > xmax) xmax = n;
		}
		ymin = graphs.get( 0 ).getCurve().getYMin();
		for (Graph graph : graphs) {
			float n = graph.getCurve().getYMin();
			if (n < ymin) ymin = n;
		}
		ymax = graphs.get( 0 ).getCurve().getYMax();
		for (Graph graph : graphs) {
			float n = graph.getCurve().getYMax();
			if (n > ymax) ymax = n;
		}
	}

	public void draw(Graphics g, int x0, int y0, float xScale, float yScale) {
		for (Graph graph : graphs) {
			Point min = getMin();
			graph.draw( g, x0, y0, xScale, yScale, min );

			PointCache.cachePoint( min );
		}
	}

	// TODO: Can these be done without instantiating new points?
	public Point getMin() {
		return PointCache.getPoint( xmin, ymin );
	}

	public Point getMax() {
		return PointCache.getPoint( xmax, ymax );
	}

}