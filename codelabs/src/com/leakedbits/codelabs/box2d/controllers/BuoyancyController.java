package com.leakedbits.codelabs.box2d.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.leakedbits.codelabs.box2d.utils.PolygonProperties;
import com.leakedbits.codelabs.box2d.utils.PolygonUtils;

public class BuoyancyController {

	private Fixture fluidSensor;
	private List<Vector2> fluidVertices;
	private Set<Fixture> fixtures;
	private World world;

	public boolean isFluidFixed = true;
	public float fluidDrag = 0;
	public float fluidLift = 0;
	public float maxFluidDrag = 0;
	public float maxFluidLift = 0;

	public BuoyancyController(World world, Fixture fluidSensor) {
		this.world = world;
		this.fluidSensor = fluidSensor;
		fluidVertices = getFixtureVertices(fluidSensor);

		fixtures = new HashSet<Fixture>();
	}

	public void step() {
		for (Fixture fixture : fixtures) {
			if (fixture.getBody().isAwake()) {

				/* Create clipPolygon */
				List<Vector2> clipPolygon = getFixtureVertices(fixture);

				/* Create subjectPolygon */
				List<Vector2> subjectPolygon;
				if (isFluidFixed) {
					subjectPolygon = fluidVertices;
				} else {
					subjectPolygon = getFixtureVertices(fluidSensor);
				}

				/* Get intersection polygon */
				List<Vector2> clippedPolygon = PolygonUtils.clipPolygons(
						subjectPolygon, clipPolygon);

				if (!clippedPolygon.isEmpty()) {
					applyForces(fixture, clippedPolygon.toArray(new Vector2[0]));
				}
			}
		}
	}

	private void applyForces(Fixture fixture, Vector2[] clippedPolygon) {
		PolygonProperties polygonProperties = PolygonUtils
				.computePolygonProperties(clippedPolygon);

		/* Get fixture body */
		Body body = fixture.getBody();

		/* Apply buoyancy force */
		float displacedMass = fluidSensor.getDensity()
				* polygonProperties.getArea();
		Vector2 gravity = world.getGravity();
		Vector2 buoyancyForce = new Vector2(-gravity.x * displacedMass,
				-gravity.y * displacedMass);
		body.applyForce(buoyancyForce, polygonProperties.getCentroid(), true);

		/* Apply drag and lift forces */
		for (int i = 0; i < clippedPolygon.length - 1; i++) {

			/* Apply drag force */
			
			/* End points and mid point of the edge */
			Vector2 firstPoint = clippedPolygon[i];
			Vector2 secondPoint = clippedPolygon[i + 1];
			Vector2 midPoint = secondPoint.sub(firstPoint);

			/*
			 * Find relative velocity between the object and the fluid at edge
			 * mid point.
			 */
			Vector2 velocityDirection = body.getLinearVelocityFromWorldPoint(midPoint);
			float velocity = velocityDirection.cpy().nor().len();
			
			float edgeLength = midPoint.cpy().nor().len();
			Vector2 normal = new Vector2(-midPoint.y, -midPoint.x);
			
			float dragDot = normal.dot(velocityDirection);
			if (dragDot < 0) {
				continue;
			}
			
			float drag = dragDot * fluidDrag * edgeLength * fluidSensor.getDensity()
					* velocity * velocity;
			drag = Math.min(drag, maxFluidDrag);
			Vector2 dragForce = velocityDirection.cpy().scl(-drag);
			body.applyForce(dragForce, midPoint, true);
			
			/* Apply lift force */
			float liftDot = midPoint.dot(velocityDirection);
			float lift = dragDot * liftDot * fluidLift * edgeLength
					* fluidSensor.getDensity() * velocity * velocity;
			lift = Math.min(maxFluidLift, lift);
			Vector2 liftDirection = new Vector2(velocityDirection.y, velocityDirection.x);
			Vector2 liftForce = liftDirection.scl(lift);
			body.applyForce(liftForce, midPoint, true);
		}
	}

	public void addBody(Fixture fixture) {
		try {
			PolygonShape polygon = (PolygonShape) fixture.getShape();
			if (polygon.getVertexCount() > 2) {
				fixtures.add(fixture);
			}
		} catch (ClassCastException e) {
			Gdx.app.debug("BuoyancyController",
					"Fixture shape is not an instance of PolygonShape.");
		}
	}

	public void removeBody(Fixture fixture) {
		fixtures.remove(fixture);
	}

	private List<Vector2> getFixtureVertices(Fixture fixture) {
		PolygonShape polygon = (PolygonShape) fixture.getShape();
		int verticesCount = polygon.getVertexCount();

		List<Vector2> vertices = new ArrayList<Vector2>(verticesCount);
		for (int i = 0; i < verticesCount; i++) {
			Vector2 vertex = new Vector2();
			polygon.getVertex(i, vertex);
			vertices.add(new Vector2(fixture.getBody().getWorldPoint(vertex)));
		}

		return vertices;
	}
}
