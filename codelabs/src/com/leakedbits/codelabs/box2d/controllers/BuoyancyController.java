package com.leakedbits.codelabs.box2d.controllers;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class BuoyancyController {

	/* A close to zero float epsilon value */
	public static final float EPSILON = 1.1920928955078125E-7f;
	
	public Body sensor;
	public OrthographicCamera camera;
	public Set<Body> bodies;
	public World world;

	public Vector2 fluidVelocity;
	public float fluidAngularDrag;
	public float fluidDensity;
	public float fluidLinearDrag;
	
	public BuoyancyController(OrthographicCamera camera, World world) {
		this.camera = camera;
		this.world = world;
		this.bodies = new HashSet<Body>();
	}

	public void step(float timeStep) {
		if (!bodies.isEmpty()) {
			Vector2 gravity = world.getGravity();

			Shape shape;
			for (Body body : bodies) {
				if (body.isAwake()) {
					float submergedArea = 0;
					float submergedMass = 0;
					Vector2 areaCenter = new Vector2(0,0);
					Vector2 massCenter = new Vector2(0,0);
					
					for (Fixture fixture : body.getFixtureList()) {
						Vector2 summergedCenter = new Vector2(0,0);
						shape = fixture.getShape();
						submergedArea += calculateSubmergedArea(body);
						
						areaCenter.x += submergedArea * summergedCenter.x;
						areaCenter.y += submergedArea * summergedCenter.y;
						
						float shapeDensity = fixture.getDensity();
						submergedMass = submergedArea * shapeDensity;
						massCenter.x +=  submergedArea * summergedCenter.x * shapeDensity;
						massCenter.y +=  submergedArea * summergedCenter.y * shapeDensity;
					}
					
					areaCenter.x/=submergedArea;
					areaCenter.y/=submergedArea;
					
					massCenter.x/=submergedMass;
					massCenter.y/=submergedMass;
					
					if(submergedArea >= EPSILON) {
						Vector3 unprojectVector = new Vector3(areaCenter.x, areaCenter.y, 0);
						camera.unproject(unprojectVector);
						
						areaCenter.x = unprojectVector.x;
						areaCenter.y = unprojectVector.y;
						
						unprojectVector = new Vector3(massCenter.x, massCenter.y, 0);
						camera.unproject(unprojectVector);
						
						massCenter.x = unprojectVector.x;
						massCenter.y = unprojectVector.y;
						
						/* Buoyancy force */
						Vector2 buoyancyForce = gravity.scl(-fluidDensity * submergedArea);
						Gdx.app.log("Buoyancy", buoyancyForce.x + ", " + buoyancyForce.y);
						body.applyForce(buoyancyForce, massCenter, true);
						
						/* Linear drag */
						Vector2 dragForce = body.getLinearVelocityFromWorldPoint(areaCenter).sub(fluidVelocity);
						dragForce = dragForce.scl(-fluidLinearDrag*submergedArea);
						body.applyForce(dragForce, areaCenter, true);
						
						/* Angular drag */
						body.applyTorque(-body.getInertia()/body.getMass()*submergedArea*body.getAngularVelocity()*fluidAngularDrag, true);
					}
				} else {
					continue;
				}
			}
		}
	}

	public void addBody(Body body) {
		bodies.add(body);
	}

	public void removeBody(Body body) {
		bodies.remove(body);
	}
	
	public float calculateSubmergedArea(Body body) {
		float area = 0;
		Fixture sensor = this.sensor.getFixtureList().get(0);
		float submergedDistance = -2 - body.getWorldCenter().y;
		
		area = Math.min(4f,submergedDistance * 2);
		Gdx.app.log("Area", area+"");
		return area;
	}
}
