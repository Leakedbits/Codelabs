package com.leakedbits.codelabs.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.leakedbits.codelabs.utils.Sample;

public class BouncingBallSample extends Sample {

	/* Use Box2DDebugRenderer, which is a model renderer for debug purposes */
	private Box2DDebugRenderer debugRenderer;

	/* As always, we need a camera to be able to see the objects */
	private OrthographicCamera camera;

	/* Define a world to hold all bodies and simulate reactions between them */
	private World world;

	/**
	 * Main constructor used to update test name.
	 */
	public BouncingBallSample() {
		name = "Bouncing ball";
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
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		/*
		 * This line is found in every sample but is not necessary for the sample
		 * functionality. calls Sample.show() method. That method set the sample to
		 * receive all touch and key input events. Also prevents the app from be
		 * closed whenever the user press back button and instead returns to
		 * main menu.
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

		/* Create all bodies */
		createBall();
		createRamp();
		createWalls();
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
		bodyDef.position.set(6, 5);

		/* Shape definition (the actual shape of the body) */
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(0.15f);

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
	 * Creates a ramp and add it to the world.
	 */
	private Body createRamp() {

		/*
		 * Ramp body definition. The ramp will be static because it doesn't move
		 * are doesn't need be affected by other objects.
		 */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(6.5f, 0f);

		/*
		 * Shape definition. In this case we use a ChainShape that is defined by
		 * an array of vectors. Our chain start 2.5 meters up and 2.5 meters
		 * left from the body center (6.5 meters right).
		 */
		ChainShape rampShape = new ChainShape();
		rampShape.createChain(new Vector2[] { new Vector2(-2.5f, -1),
				new Vector2(2.5f, 1) });

		/*
		 * Fixture definition. As this object is static, we don't need to define
		 * a density.
		 */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = rampShape;
		fixtureDef.friction = 0.3f;
		fixtureDef.restitution = 0f;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		rampShape.dispose();

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
				new Vector2(-9, -5) });

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

}
