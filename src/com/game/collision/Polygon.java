package com.game.collision;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.game.utilities.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A centered-on-zero collection of points. The order of these points is *very*
 * important as each line of the polygon is drawn between consecutive points in
 * the list, and the first and final point. To draw a square, use something
 * like: Polygon example = new Polygon(Arrays.asList( new Vector(1.0, 1.0), new
 * Vector(-1.0, 1.0), new Vector(-1.0, -1.0), new Vector(1.0, -1.0) ));
 *
 * @author mxb1143
 */
public class Polygon {

	protected List<Vector> points;

	public Polygon(List<Vector> points) {
		this.points = points;
	}

	/**
	 * A polygon is centered on (0, 0). This will translate it to world space, which
	 * is useful for collision checks. See {@link com.game.collision.WorldPolygon}
	 *
	 * @param offsetCentreFromZero The coordinates to which the centre of the
	 *                                polygon will be translated.
	 * @param rotation                The rotation through which the polygon will be
	 *                                rotated, in degrees.
	 * @return The translated polygon, now a WorldPolygon.
	 */
	public WorldPolygon translateToWorldSpace(Vector offsetCentreFromZero, double rotation) {
		return new WorldPolygon(this.points.stream().map(point -> point.rotateClockwiseAroundOrigin(rotation))
				.map(point -> point.add(offsetCentreFromZero)).collect(Collectors.toList()));
	}

	/**
	 * A polygon is stored as a list of points. This will convert these to a list of
	 * lines.
	 *
	 * @return A representation of this polygon, as a list of lines.
	 */
	public List<Line> toLines() {
		Vector previousPoint = this.points.get(0);
		ArrayList<Line> lines = new ArrayList<>();
		for (int i = 1; i < this.points.size(); i++) {
			lines.add(new Line(previousPoint, this.points.get(i)));
			previousPoint = this.points.get(i);
		}
		lines.add(new Line(this.points.get(this.points.size() - 1), this.points.get(0)));
		return lines;
	}

	/**
	 * Draw this polygon with colour `color`.
	 */
	public void draw(GraphicsContext gc, Color color) {
		gc.save();
		gc.setLineWidth(1.0);
		gc.setStroke(color);
		toLines().forEach(line -> gc.strokeLine(line.startOfSegment.x(), line.startOfSegment.y(),
				line.endOfSegment.x(), line.endOfSegment.y()));
		gc.restore();
	}

}
