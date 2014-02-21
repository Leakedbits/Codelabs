package com.leakedbits.codelabs.box2d.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class PolygonUtils {

	/* A close to zero float epsilon value */
	public static final float EPSILON = 1.1920928955078125E-7f;

	public static boolean isPointInsideEdge(Vector2 point,
			Vector2 edgeStartPoint, Vector2 edgeEndPoint) {
		return (edgeEndPoint.x - edgeStartPoint.x)
				* (point.y - edgeStartPoint.y) > (edgeEndPoint.y - edgeStartPoint.y)
				* (point.x - edgeStartPoint.x);
	}

	public static Vector2 getEdgesIntersection(Vector2 firstEdgeStartPoint,
			Vector2 firstEdgeEndPoint, Vector2 secondEdgeStartPoint,
			Vector2 secondEdgeEndPoint) {
		Vector2 firstDirectionPoint = new Vector2(firstEdgeEndPoint.x
				- firstEdgeStartPoint.x, firstEdgeEndPoint.y
				- firstEdgeStartPoint.y);
		Vector2 secondDirectionPoint = new Vector2(secondEdgeEndPoint.x
				- secondEdgeStartPoint.x, secondEdgeEndPoint.y
				- secondEdgeStartPoint.y);

		/* Dot product of first point with perpendicular vector of second point */
		float dotPerpFirstEdge = firstEdgeStartPoint.x * firstEdgeEndPoint.y
				- firstEdgeStartPoint.y * firstEdgeEndPoint.x;
		float dotPerpSecondEdge = secondEdgeStartPoint.x * secondEdgeEndPoint.y
				- secondEdgeStartPoint.y * secondEdgeEndPoint.x;
		float inversedDotPerpDirection = 1 / (firstDirectionPoint.x
				* secondEdgeEndPoint.y - firstDirectionPoint.x
				* secondEdgeEndPoint.y);

		return new Vector2(
				(dotPerpFirstEdge * secondDirectionPoint.x - dotPerpSecondEdge
						* secondDirectionPoint.x)
						* inversedDotPerpDirection, (dotPerpFirstEdge
						* secondDirectionPoint.y - dotPerpSecondEdge
						* secondDirectionPoint.y)
						* inversedDotPerpDirection);
	}

	public static List<Vector2> clipFixtures(List<Vector2> subjectPolygon,
			List<Vector2> clipPolygon) {
		List<Vector2> clippedPolygonVertices = new ArrayList<Vector2>(subjectPolygon);

		Vector2 clipEdgeStartPoint = clipPolygon.get(clipPolygon.size() - 1);

		for (Vector2 clipEdgeEndPoint : clipPolygon) {
			Gdx.app.log("Buoyancy", clippedPolygonVertices.size() + " clipped polygon vertices");
			if (clippedPolygonVertices.isEmpty()) {
				Gdx.app.log("Buoyancy", "Ooops, no vertices intersect");
				break;
			}

			List<Vector2> inputList = new ArrayList<Vector2>(
					clippedPolygonVertices);
			clippedPolygonVertices.clear();

			Vector2 testEdgeStartPoint = inputList.get(inputList.size() - 1);
			for (Vector2 testEdgeEndPoint : inputList) {
				if (isPointInsideEdge(testEdgeEndPoint, clipEdgeStartPoint,
						clipEdgeEndPoint)) {
					if (!isPointInsideEdge(testEdgeStartPoint,
							clipEdgeStartPoint, clipEdgeEndPoint)) {
						Gdx.app.log("Buoyancy", "Adding because second if");
						clippedPolygonVertices.add(getEdgesIntersection(
								clipEdgeStartPoint, clipEdgeEndPoint,
								testEdgeStartPoint, testEdgeEndPoint));
					}

					clippedPolygonVertices.add(testEdgeEndPoint);
					Gdx.app.log("Buoyancy", "Adding because first if");
				} else if (isPointInsideEdge(testEdgeStartPoint,
						clipEdgeStartPoint, clipEdgeEndPoint)) {
					Gdx.app.log("Buoyancy", "Adding because third if");
					clippedPolygonVertices.add(getEdgesIntersection(
							clipEdgeStartPoint, clipEdgeEndPoint,
							testEdgeStartPoint, testEdgeEndPoint));
				}

				testEdgeStartPoint = testEdgeEndPoint;
			}

			clipEdgeStartPoint = clipEdgeEndPoint;
		}

		return clippedPolygonVertices;
	}

	public static PolygonProperties computeCentroid(
			List<Vector2> polygonVertices) {
		PolygonProperties polygonProperties = null;

		int count = polygonVertices.size();

		if (count >= 3) {
			Vector2 centroid = new Vector2(0, 0);
			float area = 0;

			/*
			 * Create a new vector to represent the reference point for forming
			 * triangles.
			 */
			Vector2 refPoint = new Vector2(0, 0);

			float threeInverse = 1 / 3f;

			for (int i = 0; i < count; i++) {
				/*
				 * Use refPoint, polygonVertex and thirdTriangleVertex as
				 * vertices of a triangle.
				 */
				Vector2 polygonVertex = polygonVertices.get(i);
				Vector2 thirdTriangleVertex = i + 1 < count ? polygonVertices
						.get(i + 1) : polygonVertices.get(0);

				Vector2 firstDirectionVector = polygonVertex.sub(refPoint);
				Vector2 secondDirectionVector = thirdTriangleVertex
						.sub(refPoint);

				float triangleArea = firstDirectionVector
						.crs(secondDirectionVector) / 2;
				area += triangleArea;

				/* Area weighted centroid */
				centroid.add(refPoint.add(polygonVertex)
						.add(thirdTriangleVertex)
						.scl(triangleArea * threeInverse));
			}

			if (area > EPSILON) {
				centroid.scl(1 / area);
			} else {
				area = 0;
			}

			polygonProperties = new PolygonProperties(centroid, area);
		}

		return polygonProperties;
	}

}
