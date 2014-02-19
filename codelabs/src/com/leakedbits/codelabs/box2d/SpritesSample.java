package com.leakedbits.codelabs.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.leakedbits.codelabs.box2d.utils.Box2DFactory;
import com.leakedbits.codelabs.utils.Sample;

public class SpritesSample extends Sample {

	/* Define a body to later apply impulses to it */
	private Body box;

	/* Use Box2DDebugRenderer, which is a model renderer for debug purposes */
	private Box2DDebugRenderer debugRenderer;

	/* Use a long to store current time and calculate user touches duration */
	private Long timer;

	/* As always, we need a camera to be able to see the objects */
	private OrthographicCamera camera;

	/* Define a world to hold all bodies and simulate reactions between them */
	private World world;

	/* New variables used to hold the sprite and draw it in the screen */
	private Sprite sprite;
	private SpriteBatch batch;

	/* This array will hold all the bodies of the word for rendering purposes */
	private Array<Body> worldBodies;

	/**
	 * Main constructor used to update sample name.
	 */
	public SpritesSample() {
		name = "Sprites";
	}

	@Override
	public void render(float delta) {
		/* Clear screen with a black background */
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		/* Render all graphics before do physics step */
		debugRenderer.render(world, camera.combined);

		/*
		 * Set projection matrix to camera.combined, the same way we did with
		 * the debug renderer.
		 */
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		/* Get word bodies */
		world.getBodies(worldBodies);

		/*
		 * For each body in the world we have to check if it has user data
		 * associated and if it is an Sprite. In that case, we draw it in the
		 * screen.
		 */
		for (Body body : worldBodies) {
			if (body.getUserData() instanceof Sprite) {
				Sprite sprite = (Sprite) body.getUserData();

				/*
				 * Set body position equals to box position. We also need to
				 * center it in the box (measures are relative to body center).
				 */
				Vector2 position = body.getPosition();
				sprite.setPosition(position.x - sprite.getWidth() / 2,
						position.y - sprite.getWidth() / 2);

				/* Set sprite rotation equals to body rotation */
				sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

				/* Draw the sprite on screen */
				sprite.draw(batch);
			}
		}

		batch.end();

		/* Step the simulation with a fixed time step of 1/60 of a second */
		world.step(1 / 60f, 6, 2);
	}

	@Override
	public void resize(int width, int height) {

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

		batch = new SpriteBatch();

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

		/* Create all bodies */
		/* Create the box */
		box = Box2DFactory.createBox(world, BodyType.DynamicBody, new Vector2(
				0, 0), 1.5f, 1.5f, 0.3f, 0.5f, 0.5f);

		/* Create the walls */
		Box2DFactory.createWalls(world, camera.viewportWidth,
				camera.viewportHeight, 1, 1, 1);

		/* Set box texture */
		sprite = new Sprite(new Texture("data/images/crab.png"));

		/*
		 * Set the size of the sprite. We have to remember that we are not
		 * working in pixels, but with meters. The size of the sprite will be
		 * the same as the size of the box; 2 meter wide, 2 meters tall.
		 */
		sprite.setSize(3, 3);

		/*
		 * Sets the origin in relation to the sprite's position for scaling and
		 * rotation.
		 */
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);

		/* Set sprite as a user data of the body to draw it in each render step */
		box.setUserData(sprite);

		/* Instantiate the array of bodies that will be used during render step */
		worldBodies = new Array<Body>();
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
		sprite.getTexture().dispose();
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
		super.touchUp(screenX, screenY, pointer, button);

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
