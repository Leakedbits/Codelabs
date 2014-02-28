package com.leakedbits.codelabs.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.leakedbits.codelabs.box2d.utils.Box2DFactory;
import com.leakedbits.codelabs.utils.Sample;

public class SpawnBodiesSample extends Sample {

	/* Max number of balls to be spawned */
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
	 * Main constructor used to update sample name.
	 */
	public SpawnBodiesSample() {
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

		/* Create the walls */
		Box2DFactory.createWalls(world, camera.viewportWidth,
				camera.viewportHeight, 1);
	}

	@Override
	public void dispose() {
		debugRenderer.dispose();
		world.dispose();
	}

	/*
	 * Input events handling. Here will process all touch events to spawn and
	 * drag bodies.
	 */

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		/* Checks whether the max amount of balls were spawned */
		if (spawnedBalls < MAX_SPAWNED_BALLS) {
			spawnedBalls++;

			/* Translate camera point to world point */
			Vector3 unprojectedVector = new Vector3();
			camera.unproject(unprojectedVector.set(screenX, screenY, 0));

			/* Create a new ball */
			Shape shape = Box2DFactory.createCircleShape(1);
			FixtureDef fixtureDef = Box2DFactory.createFixture(shape, 2.5f,
					0.25f, 0.75f, false);
			Box2DFactory.createBody(world, BodyType.DynamicBody, fixtureDef,
					new Vector2(unprojectedVector.x, unprojectedVector.y));
		}

		return true;
	}

}
