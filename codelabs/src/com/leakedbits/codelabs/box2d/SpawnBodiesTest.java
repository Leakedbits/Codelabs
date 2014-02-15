package com.leakedbits.codelabs.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.leakedbits.codelabs.Codelabs;
import com.leakedbits.codelabs.utils.Test;

public class SpawnBodiesTest extends Test {

	/*
	 * As we are using input events in this test, we need to translate
	 * coordinates from pixels to meters, so we use this variable that specifies
	 * that 40 pixels are 1 meter.
	 */
	private static final float PIXELS_TO_METERS = 40;

	private static final int MAX_SPAWNED_BALLS = 20;

	/* Use Box2DDebugRenderer, which is a model renderer for debug purposes */
	private Box2DDebugRenderer debugRenderer;

	/* As always, we need a camera to be able to see the objects */
	private OrthographicCamera camera;

	/* Define a world to hold all bodies and simulate reactions between them */
	private World world;

	/* Counter to know how many ball have been spawned */
	private int spawnedBalls;

	/**
	 * Main constructor used to update test name.
	 */
	public SpawnBodiesTest() {
		name = "Spawn bodies on touch";
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
		 * This line is found in every test but is not necessary for the sample
		 * functionality. calls Test.show() method. That method set the test to
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
		 * height to different resolutions. In this text we are using
		 * PIXELS_TO_METERS to calculate the viewport with but will not be used
		 * in other samples to make the code more readable and easier to follow.
		 */
		float widthMeters = Codelabs.TARGET_WIDTH / PIXELS_TO_METERS;
		camera = new OrthographicCamera(widthMeters, widthMeters
				* (Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));

		/*
		 * Next line must remain commented because we do this in its parent (See
		 * Test class). In case you are not using Test class, uncomment this
		 * line to set input processor to handle events.
		 */
		//Gdx.input.setInputProcessor(this);

		/* Create walls */
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
	private void createBall(float x, float y) {

		/*
		 * Ball body definition. Represents a single point in the world. This
		 * body will be dynamic because the ball must interact with the
		 * environment and will be set 6 meters right and 5 meters up from
		 * viewport center.
		 */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);

		/* Shape definition (the actual shape of the body) */
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(1f);

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
		world.createBody(bodyDef).createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		ballShape.dispose();
	}

	/**
	 * Creates ceiling, ground and walls and add them to the world.
	 */
	private void createWalls() {

		/* Walls body definition */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(0, 0);

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

		/* Body creation */
		world.createBody(bodyDef).createFixture(fixtureDef);

		wallsShape.dispose();
	}

	/*
	 * Input events handling. Here will process all touch events to spawn and
	 * drag bodies.
	 */

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		super.touchUp(screenX, screenY, pointer, button);
		
		/* Checks whether the max amount of balls were spawned */
		if (spawnedBalls < MAX_SPAWNED_BALLS) {
			spawnedBalls++;

			/* Translate camera point to world point */
			Vector3 unprojectedVector = new Vector3();
			camera.unproject(unprojectedVector.set(screenX, screenY, 0));

			createBall(unprojectedVector.x, unprojectedVector.y);
		}

		return true;
	}


}
