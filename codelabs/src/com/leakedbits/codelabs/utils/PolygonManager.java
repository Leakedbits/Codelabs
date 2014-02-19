package com.leakedbits.codelabs.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class PolygonManager {

	/* A close to zero float epsilon value */
	public static final float EPSILON = 1.1920928955078125E-7f;

	public static boolean inside(Vector2 clippingPolygon1,
			Vector2 clippingPolygon2, Vector2 p) {
		return (clippingPolygon2.x - clippingPolygon1.x)
				* (p.y - clippingPolygon1.y) > (clippingPolygon2.y - clippingPolygon1.y)
				* (p.x - clippingPolygon1.x);
	}

	public static Vector2 intersection(Vector2 cp1, Vector2 cp2, Vector2 s,
			Vector2 e) {
		Vector2 dc = new Vector2(cp1.x - cp2.x, cp1.y - cp2.y);
		Vector2 dp = new Vector2(s.x - e.x, s.y - e.y);
		float n1 = cp1.x * cp2.y - cp1.y * cp2.x;
		float n2 = s.x * e.y - s.y * e.x;
		float n3 = 1.0f / (dc.x * dp.y - dc.y * dp.x);

		return new Vector2((n1 * dp.x - n2 * dc.x) * n3,
				(n1 * dp.y - n2 * dc.y) * n3);
	}

	// http://rosettacode.org/wiki/Sutherland-Hodgman_polygon_clipping#JavaScript
	// Note that this only works when fB is a convex polygon, but we know all
	// fixtures in Box2D are convex, so that will not be a problem
	public static boolean findIntersectionOfFixtures(Fixture fA, Fixture fB,
			List<Vector2> outputVertices) {
		// currently this only handles polygon vs polygon
		if (fA.getShape().getType() != Shape.Type.Polygon
				|| fB.getShape().getType() != Shape.Type.Polygon) {
			Gdx.app.log("POLYGON", "Bad shape");
			return false;
		}

		PolygonShape polyA = (PolygonShape) fA.getShape();
		PolygonShape polyB = (PolygonShape) fB.getShape();

		// fill subject polygon from fixtureA polygon
		Vector2 vertex;
		for (int i = 0; i < polyA.getVertexCount(); i++) {
			vertex = new Vector2(0, 0);
			polyA.getVertex(i, vertex);
			outputVertices.add(fA.getBody().getWorldPoint(vertex));
		}

		// fill clip polygon from fixtureB polygon
		List<Vector2> clipPolygon = new ArrayList<Vector2>();
		for (int i = 0; i < polyB.getVertexCount(); i++) {
			vertex = new Vector2(0, 0);
			polyB.getVertex(i, vertex);
			clipPolygon.add(fB.getBody().getWorldPoint(vertex));

		}

		Vector2 cp1 = clipPolygon.get(clipPolygon.size() - 1);
		for (int j = 0; j < clipPolygon.size(); j++) {
			Vector2 cp2 = clipPolygon.get(j);
			if (outputVertices.isEmpty()) {
				Gdx.app.log("POLYGON", "Empty output");
				return false;
			}
			Gdx.app.log("POLYGON", "Empty output");
			List<Vector2> inputList = new ArrayList<Vector2>(outputVertices);
			outputVertices.clear();
			Vector2 s = inputList.get(inputList.size() - 1); // last on the
																// input list
			for (int i = 0; i < inputList.size(); i++) {
				Vector2 e = inputList.get(i);
				if (inside(cp1, cp2, e)) {
					if (!inside(cp1, cp2, s)) {
						outputVertices.add(intersection(cp1, cp2, s, e));
					}
					outputVertices.add(e);
				} else if (inside(cp1, cp2, s)) {
					outputVertices.add(intersection(cp1, cp2, s, e));
				}
				s = e;
			}
			cp1 = cp2;
		}

		return !outputVertices.isEmpty();
	}

	public static Vector2 ComputeCentroid(List<Vector2> vs, float area) {
		int count = (int) vs.size();
		if (count >= 3) {
			Vector2 c = new Vector2(0, 0);
			area = 0.0f;

			// pRef is the reference point for forming triangles.
			// Its location doesnt change the result (except for rounding
			// error).
			Vector2 pRef = new Vector2(0.0f, 0.0f);

			float inv3 = 1.0f / 3.0f;

			for (int i = 0; i < count; ++i) {
				// Triangle vertices.
				Vector2 p1 = pRef;
				Vector2 p2 = vs.get(i);
				Vector2 p3 = i + 1 < count ? vs.get(i + 1) : vs.get(0);

				Vector2 e1 = new Vector2(p2.x - p1.x, p2.y - p1.y);
				Vector2 e2 = new Vector2(p3.x - p1.x, p3.y - p1.y);

				float D = e1.crs(e2);

				float triangleArea = 0.5f * D;
				area += triangleArea;

				// Area weighted centroid
				Vector2 increment = p1.add(p2).add(p3).scl(triangleArea * inv3);
				c.add(increment);
			}

			// Centroid
			if (area > EPSILON) {
				c.scl(1.0f / area);
			} else {
				area = 0;
			}

			return c;
		}

		return null;
	}
}
