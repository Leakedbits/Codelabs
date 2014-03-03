package com.leakedbits.codelabs.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.leakedbits.codelabs.box2d.utils.Box2DFactory;
import com.leakedbits.codelabs.utils.Sample;

public class ImpulsesSample extends Sample {

	/* Use Box2DDebugRenderer, which is a model renderer for debug purposes */
	private Box2DDebugRenderer debugRenderer;

	/* Use a long to store current time and calculate user touches duration */
	private Long timer;

	/* As always, we need a camera to be able to see the objects */
	private OrthographicCamera camera;

	/* Define a world to hold all bodies and simulate reactions between them */
	private World world;
	
	/* Define a body to later apply impulses to it */
	private Body box;

	/**
	 * Main constructor used to update sample name.
	 */
	public ImpulsesSample() {
		name = "Impulses";
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

		/* Create the box */
		Shape shape = Box2DFactory.createBoxShape(1.5f, 1.5f,
				new Vector2(0, 0), 0);
		FixtureDef fixtureDef = Box2DFactory.createFixture(shape, 0.3f, 0.5f,
				0.5f, false);
		box = Box2DFactory.createBody(world, BodyType.DynamicBody, fixtureDef,
				new Vector2(0, 0));

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
	 * Input events handling. Here will start our timer, which will be used
	 * before.
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		/*
		 * Get current time in milliseconds. We will use this to calculate the
		 * time the user has touched the screen.
		 */
		this.timer = System.currentTimeMillis();

		return true;
	}

	/*
	 * Input events handling. Here will stop our timer, calculate the impulse
	 * and apply it to the body.
	 */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		/* Calculate the time the user has touched the screen */
		long touchedTime = System.currentTimeMillis() - timer;

		/* Every second touching the screen will increment by 20 the impulse */
		float impulse = Math.max(10f, touchedTime / 50);

		/*
		 * The impulse is applied to the body's center, and only on the Y axis.
		 * It will aggregate the impulse to the current Y speed, so, if the
		 * object is falling the impulse will be the difference between the
		 * object speed and the given value.
		 */
		box.applyLinearImpulse(new Vector2(0, impulse), box.getWorldCenter(),
				true);

		return true;
	}
}
