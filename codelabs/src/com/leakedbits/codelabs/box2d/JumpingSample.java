package com.leakedbits.codelabs.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.leakedbits.codelabs.box2d.utils.Box2DFactory;
import com.leakedbits.codelabs.utils.Sample;

public class JumpingSample extends Sample implements ContactListener {

	/* Use Box2DDebugRenderer, which is a model renderer for debug purposes */
	private Box2DDebugRenderer debugRenderer;

	/* As always, we need a camera to be able to see the objects */
	private OrthographicCamera camera;

	/* Define a world to hold all bodies and simulate reactions between them */
	private World world;

	/* Define a body to later apply impulses to it */
	private Body player;

	/*
	 * Boolean variables to know if the player is jumping or if he/she can
	 * double jump.
	 */
	private boolean isPlayerGrounded;
	private boolean hasDoubleJump;

	/**
	 * Main constructor used to update sample name.
	 */
	public JumpingSample() {
		name = "Jump && Double jump";
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

		/* Create the player */
		Shape shape = Box2DFactory.createBoxShape(0.35f, 1, new Vector2(0, 0),
				0);
		FixtureDef fixtureDef = Box2DFactory.createFixture(shape, 1, 0, 0,
				false);
		player = Box2DFactory.createBody(world, BodyType.DynamicBody,
				fixtureDef, new Vector2(0, 0));

		/*
		 * Create foot sensor. This sensor will be used to detect when the
		 * player is standing on something.
		 */
		shape = Box2DFactory.createBoxShape(0.1f, 0.1f, new Vector2(0, -1), 0);
		fixtureDef = Box2DFactory.createFixture(shape, 0, 0, 0, true);

		/*
		 * Set user data to the player. In this case we are using an integer.
		 * This will help us to identify the fixture during collision handling.
		 */
		player.createFixture(fixtureDef).setUserData(new Integer(3));

		/*
		 * Fix the rotation of player's body. We don't want our player to fall
		 * every time he/she touch an object :)
		 */
		player.setFixedRotation(true);

		/* Create the walls */
		Box2DFactory.createWalls(world, camera.viewportWidth,
				camera.viewportHeight, 1);

		world.setContactListener(this);
	}

	@Override
	public void dispose() {
		debugRenderer.dispose();
		world.dispose();
	}

	@Override
	public void beginContact(Contact contact) {
		/*
		 * For each fixture contacting (A and B) check if it has user data
		 * associated. In that case and only if it is the number 3 that we
		 * previously associated to our player, change isPlayerGrounded and
		 * hasDobleJump variables to true.
		 */
		Object userData = contact.getFixtureA().getUserData();
		if (userData != null && (Integer) userData == 3) {
			isPlayerGrounded = true;
			hasDoubleJump = true;
		} else {
			userData = contact.getFixtureB().getUserData();
			if (userData != null && (Integer) userData == 3) {
				isPlayerGrounded = true;
				hasDoubleJump = true;
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		/*
		 * For each fixture contacting (A and B) check if it has user data
		 * associated. In that case and only if it is the number 3 that we
		 * previously associated to our player, change isPlayerGrounded false to
		 * indicate that our player is jumping.
		 */
		Object userData = contact.getFixtureA().getUserData();
		if (userData != null && (Integer) userData == 3) {
			isPlayerGrounded = false;
			hasDoubleJump = false;
		} else {
			userData = contact.getFixtureB().getUserData();
			if (userData != null && (Integer) userData == 3) {
				isPlayerGrounded = false;
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

	/*
	 * Input events handling. Here will stop our timer, calculate the impulse
	 * and apply it to the body.
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		/*
		 * Firstly we check if the player is standing over something or if
		 * he/she can double jump.
		 */
		if (isPlayerGrounded || hasDoubleJump) {

			/*
			 * If a double jump was use it, remove this hability until the
			 * player is over something again.
			 */
			if (!isPlayerGrounded && hasDoubleJump) {
				hasDoubleJump = false;
			}

			/*
			 * Finally, apply a vertical linear impulse relative to the player
			 * mass.
			 */
			float impulse = player.getMass() * 5;
			player.applyLinearImpulse(new Vector2(0, impulse),
					player.getWorldCenter(), true);
		}

		return true;
	}

}
