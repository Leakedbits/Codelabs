package com.leakedbits.codelabs.box2d;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.leakedbits.codelabs.box2d.controllers.BuoyancyController;
import com.leakedbits.codelabs.box2d.utils.PolygonProperties;
import com.leakedbits.codelabs.box2d.utils.PolygonUtils;
import com.leakedbits.codelabs.utils.Sample;

public class BuoyancySample extends Sample implements ContactListener {
	
	/* Define a Body to check collisions */
	private Body water, box;

	/* Use Box2DDebugRenderer, which is a model renderer for debug purposes */
	private Box2DDebugRenderer debugRenderer;

	/* As always, we need a camera to be able to see the objects */
	private OrthographicCamera camera;

	/* Define a world to hold all bodies and simulate reactions between them */
	private World world;

	private BuoyancyController buoyancyController;
	
	private List<Tuple<Fixture, Fixture>> wetBodies;

	public BuoyancySample() {
		name = "Buoyancy";
	}
	
	@Override
	public void render(float delta) {
		/* Clear screen with a black background */
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		/* Render all graphics before do physics step */
		debugRenderer.render(world, camera.combined);

		/* Step the simulation with a fixed time step of 1/60 of a second */
		world.step(1 / 60f, 6, 2);
		// buoyancyController.step(1 / 60f);

		applyBuoyancy();
	}
	
	@Override
	public void show() {
		/*
		 * This line is found in every sample but is not necessary for the
		 * sample functionality. calls Sample.show() method. That method set the
		 * sample to receive all touch and key input events. Also prevents the
		 * app from be closed whenever the user press back button and instead
		 * returns to main menu.
		 */
		super.show();

		/*
		 * Create world with a common gravity vector (9.81 m/s2 downwards force)
		 * and tell world that we want objects to sleep. This last value
		 * conserves CPU usage.
		 */
		world = new World(new Vector2(0, -9.81f), true);

		/* Create renderer */
		debugRenderer = new Box2DDebugRenderer();

		/*
		 * Define camera viewport. Box2D uses meters internally so the camera
		 * must be defined also in meters. We set a desired width and adjust
		 * height to different resolutions.
		 */
		camera = new OrthographicCamera(20,
				20 * (Gdx.graphics.getHeight() / (float) Gdx.graphics
						.getWidth()));

		/*
		 * Next line must remain commented because we do this in its parent (See
		 * Sample class). In case you are not using Sample class, uncomment this
		 * line to set input processor to handle events.
		 */
		// Gdx.input.setInputProcessor(this);

		wetBodies = new ArrayList<Tuple<Fixture, Fixture>>();

		/* Create all bodies */
		box = createBox(1f);
		box.setTransform(new Vector2(0, 0), 0.3f);
		createWalls();
		water = createWater();

//		buoyancyController = new BuoyancyController(camera, world);
//		buoyancyController.fluidAngularDrag = 0;
//		buoyancyController.fluidDensity = 1;
//		buoyancyController.fluidLinearDrag = 1f;
//		buoyancyController.fluidVelocity = new Vector2(2f, 0);
//		buoyancyController.sensor = water;

		world.setContactListener(this);
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		debugRenderer.dispose();
		world.dispose();
	}

	/**
	 * Create a box and add it to the world.
	 */
	private Body createBox(float size) {
		/*
		 * Box body definition. Represents a single point in the world. This
		 * body will be static because will be used to store objects inside and
		 * shouldn't interact with the environment
		 */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(0, 2);
		// bodyDef.angularVelocity = 0;
//		bodyDef.fixedRotation = true;

		/* Shape definition (the actual shape of the body) */
		PolygonShape boxShape = new PolygonShape();

		/*
		 * We use setAsBox to define a 2 meters wide 2 meters tall box. We have
		 * to specify half-width and half-height to the method.
		 */
		boxShape.setAsBox(size, size);

		/*
		 * Fixture definition. Let us define properties of a body like the
		 * shape, the density of the body, its friction or its restitution (how
		 * 'bouncy' a fixture is) in a physics scene.
		 */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = boxShape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0.0f;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		boxShape.dispose();

		return body;
	}

	/**
	 * Creates a ball and add it to the world.
	 */
	private Body createBall() {

		/*
		 * Ball body definition. Represents a single point in the world. This
		 * body will be dynamic because the ball must interact with the
		 * environment and will be set 6 meters right and 5 meters up from
		 * viewport center.
		 */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(0, 0);

		/* Shape definition (the actual shape of the body) */
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(1);

		/*
		 * Fixture definition. Let us define properties of a body like the
		 * shape, the density of the body, its friction or its restitution (how
		 * 'bouncy' a fixture is) in a physics scene.
		 */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = ballShape;
		fixtureDef.density = 2.5f;
		fixtureDef.friction = 0.25f;
		fixtureDef.restitution = 0.75f;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		ballShape.dispose();

		return body;
	}

	/**
	 * Creates ceiling, ground and walls and add them to the world.
	 */
	private Body createWalls() {

		/* Walls body definition */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(0, 0f);

		/* Shape definition */
		ChainShape wallsShape = new ChainShape();
		wallsShape.createChain(new Vector2[] { new Vector2(-9, -5),
				new Vector2(9, -5), new Vector2(9, 5), new Vector2(-9, 5),
				new Vector2(-9, -3), new Vector2(-9, -5) });

		/* Fixture definition */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = wallsShape;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0f;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		wallsShape.dispose();

		return body;
	}

	private Body createWater() {
		/* Walls body definition */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set(0, -2.5f);

		/* Shape definition (the actual shape of the body) */
		PolygonShape waterShape = new PolygonShape();

		/*
		 * We use setAsBox to define a 2 meters wide 2 meters tall box. We have
		 * to specify half-width and half-height to the method.
		 */
		waterShape.setAsBox(9, 2.5f);

		/* Fixture definition */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = waterShape;
		fixtureDef.friction = 2.000000029802322e-01f;
	    fixtureDef.restitution = 0.000000000000000e+00f;
	    fixtureDef.density = 2.000000000000000e+00f;
		fixtureDef.isSensor = true;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		waterShape.dispose();

		return body;
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		if (fixtureA.isSensor()
				&& fixtureB.getBody().getType() == BodyType.DynamicBody) {
			wetBodies.add(new Tuple<Fixture, Fixture>(fixtureA, fixtureB));
			// buoyancyController.addBody(fixtureB.getBody());
		} else if (fixtureB.isSensor()
				&& fixtureA.getBody().getType() == BodyType.DynamicBody) {
			wetBodies.add(new Tuple<Fixture, Fixture>(fixtureB, fixtureA));
			// buoyancyController.addBody(fixtureA.getBody());
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		if (fixtureA.isSensor()
				&& fixtureB.getBody().getType() == BodyType.DynamicBody) {
			wetBodies.remove(new Tuple<Fixture, Fixture>(fixtureA, fixtureB));
			// buoyancyController.removeBody(fixtureB.getBody());
		} else if (fixtureB.isSensor()
				&& fixtureA.getBody().getType() == BodyType.DynamicBody) {

			if (fixtureA.getBody().getWorldCenter().y > -1) {
				wetBodies
						.remove(new Tuple<Fixture, Fixture>(fixtureB, fixtureA));
				// buoyancyController.removeBody(fixtureA.getBody());
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

	public void applyBuoyancy() {
		for (Tuple<Fixture, Fixture> tuple : wetBodies) {
			// fixtureA is the fluid
			Fixture fixtureA = tuple.x;
			Fixture fixtureB = tuple.y;

			float density = fixtureA.getDensity();

			
			Vector2 aux;
			
			PolygonShape polyA = (PolygonShape) fixtureA.getShape();
			
			List<Vector2> subjectPolygon = new ArrayList<Vector2>();
			for (int i = 0; i < polyA.getVertexCount(); i++) {
				aux = new Vector2(0, 0);
				polyA.getVertex(i, aux);
				Vector2 world = fixtureA.getBody().getWorldPoint(aux);
				subjectPolygon.add(new Vector2(world.x, world.y)); /* IMPORTANTE!!!!!!!!!!! Crear nuevos vectores */
			}
			
			PolygonShape polyB = (PolygonShape) fixtureB.getShape();
			List<Vector2> clipPolygon = new ArrayList<Vector2>();
			for (int i = 0; i < polyB.getVertexCount(); i++) {
				aux = new Vector2(0, 0);
				polyB.getVertex(i, aux);
				Vector2 world = fixtureB.getBody().getWorldPoint(aux);
				clipPolygon.add(new Vector2(world.x, world.y));
			}
			
			List<Vector2> intersectionPoints = PolygonUtils.clipFixtures(subjectPolygon, clipPolygon);
			if (intersectionPoints != null && !intersectionPoints.isEmpty()) {
				// find centroid
				
				PolygonProperties polygonProperties = PolygonUtils.computeCentroid(intersectionPoints);
				float area = polygonProperties.getArea();
				Vector2 centroid = polygonProperties.getCentroid();
				Gdx.app.log("CENTROID", centroid.x + ", " + centroid.y);
				float displacedMass = fixtureA.getDensity() * area;
				Vector2 buoyancyForce = new Vector2(-world.getGravity().x
						* displacedMass, -world.getGravity().y * displacedMass);
				fixtureB.getBody().applyForce(buoyancyForce, centroid, true);

				// apply drag separately for each polygon edge
				float dragMod = 0.25f;//adjust as desired
                float liftMod = 0.25f;//adjust as desired
                float maxDrag = 2000;//adjust as desired
                float maxLift = 500;//adjust as desired
				for (int i = 0; i < intersectionPoints.size(); i++) {
					// the end points and mid-point of this edge
					Vector2 v0 = intersectionPoints.get(i);
					Vector2 v1 = intersectionPoints.get((i + 1)
							% intersectionPoints.size());
					Vector2 midPoint = v0.add(v1).scl(0.5f);

					// find relative velocity between object and fluid at edge
					// midpoint
					Vector2 velDir = fixtureB
							.getBody()
							.getLinearVelocityFromWorldPoint(midPoint)
							.sub(fixtureA.getBody()
									.getLinearVelocityFromWorldPoint(midPoint));
					float vel = velDir.nor().len();

					Vector2 edge = v1.sub(v0);
					float edgeLength = edge.nor().len();
					Vector2 normal = new Vector2(-1 * edge.y, -1 * edge.x); // gets
																			// perpendicular
																			// vector

					float dragDot = normal.crs(velDir);
					if (dragDot < 0)
						continue; // normal points backwards - this is not a
									// leading edge

					float dragMag = dragDot * dragMod * edgeLength * density * vel * vel;
					dragMag = Math.min(dragMag, maxDrag);
					Vector2 dragForce = velDir.scl(-dragMag);
					fixtureB.getBody().applyForce(dragForce, midPoint, true);

					// apply lift
					float liftDot = edge.crs(velDir);
					float liftMag =  dragDot * liftDot * liftMod * edgeLength * density * vel * vel;
					liftMag = Math.min(maxLift, liftMag);
					Vector2 liftDir = new Vector2(1 * velDir.y, 1 * velDir.x); // gets
																				// perpendicular
																				// vector
					Vector2 liftForce = liftDir.scl(liftMag);
					fixtureB.getBody().applyForce(liftForce, midPoint, true);
				}
			} else {
			}
		}
	}
	
}
