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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.leakedbits.codelabs.utils.Test;

public class ImpulsesTest extends Test {

	/*
	 * This is a static variable used to define the number of boxes inside our
	 * test
	 */
	private static final int FRAME_NUMBER = 1;

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

	/**
	 * Main constructor used to update test name.
	 */
	public ImpulsesTest() {
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
		 * height to different resolutions.
		 */
		camera = new OrthographicCamera(20,
				20 * (Gdx.graphics.getHeight() / (float) Gdx.graphics
						.getWidth()));

		/* Create all bodies */
		createFrames();
		box = createBox(new Vector2(0, 0));

		// for (Body box : createBoxes(BOX_NUMBERS)) {
		// createInnerBall(box);
		// }

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
	 * Split the screen into multiple frames
	 * @return
	 */
	private List<Body> createFrames() {
		List<Body> boxes = new ArrayList<Body>();

		Vector2 center;
		float width;
		float height;

		switch (FRAME_NUMBER) {
		case 1:
			center = new Vector2(0f, 0f);
			width = camera.viewportWidth - 1;
			height = camera.viewportHeight - 1;

			boxes.add(createBox(center, width, height));

			break;
		case 2:
			width = (camera.viewportWidth - 1) / 2;
			height = camera.viewportHeight - 1;

			center = new Vector2(-camera.viewportWidth / 4, 0);
			boxes.add(createBox(center, width, height));

			center = new Vector2(camera.viewportWidth / 4, 0);
			boxes.add(createBox(center, width, height));

			break;
		case 3:
			break;
		case 4:
			break;
		}

		return boxes;
	}

	/**
	 * Create a box and add it to the world.
	 * 
	 * @param center
	 *            Center point of the box
	 * @param width
	 *            Width of the box in meters
	 * @param height
	 *            Height of the box in meters
	 * @return
	 */
	private Body createBox(Vector2 center, float width, float height) {
		/*
		 * Box body definition. Represents a single point in the world. This
		 * body will be static because will be used to store objects inside and
		 * shouldn't interact with the environment
		 */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(center.x, center.y);

		/*
		 * Calculate the vertex from the center and the dimensions.
		 */
		Vector2 leftBottomVertex = new Vector2(center.x - width / 2, center.y
				- height / 2);
		Vector2 leftTopVertex = new Vector2(center.x - width / 2, center.y
				+ height / 2);
		Vector2 rightBottomVertex = new Vector2(center.x + width / 2, center.y
				- height / 2);
		Vector2 rightTopVertex = new Vector2(center.x + width / 2, center.y
				+ height / 2);

		/* Shape definition (the actual shape of the body) */
		ChainShape boxShape = new ChainShape();
		boxShape.createChain(new Vector2[] { leftBottomVertex, leftTopVertex,
				rightTopVertex, rightBottomVertex, leftBottomVertex });

		/*
		 * Fixture definition. Let us define properties of a body like the
		 * shape, the density of the body, its friction or its restitution (how
		 * 'bouncy' a fixture is) in a physics scene.
		 */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = boxShape;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		boxShape.dispose();

		return body;
	}

	/**
	 * Create a ball inside a given container
	 * @param container Must be a ChainShape
	 * @return
	 */
	private Body createInnerBall(Body container) {
		/*
		 * Ball body definition. Represents a single point in the world. This
		 * body will be dynamic because the ball must interact with the
		 * environment.
		 */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(container.getWorldCenter());
		Gdx.app.log("DEBUG", "Center: " + container.getWorldCenter());

		/* Shape definition (the actual shape of the body) */
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(0.30f);

		/*
		 * Fixture definition. Let us define properties of a body like the
		 * shape, the density of the body, its friction or its restitution (how
		 * 'bouncy' a fixture is) in a physics scene.
		 */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = ballShape;
		fixtureDef.density = 2.5f;
		fixtureDef.friction = 0.25f;
		fixtureDef.restitution = 0f;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		ballShape.dispose();

		return body;
	}

	private Body createBox(Vector2 center) {
		/*
		 * Ball body definition. Represents a single point in the world. This
		 * body will be dynamic because the ball must interact with the
		 * environment.
		 */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(center);

		/* Shape definition (the actual shape of the body) */
		PolygonShape squareShape = new PolygonShape();
		squareShape.set(new Vector2[] { new Vector2(-0.5f, -0.5f),
				new Vector2(-0.5f, 0.5f), new Vector2(0.5f, 0.5f),
				new Vector2(0.5f, -0.5f), new Vector2(-0.5f, -0.5f) });

		/*
		 * Fixture definition. Let us define properties of a body like the
		 * shape, the density of the body, its friction or its restitution (how
		 * 'bouncy' a fixture is) in a physics scene.
		 */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = squareShape;
		fixtureDef.density = 2.5f;
		fixtureDef.friction = 1f;
		fixtureDef.restitution = 0.0f;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		squareShape.dispose();

		return body;
	}

	/*
	 * Input events handling. Here will start our timer, which will be used
	 * before.
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		super.touchUp(screenX, screenY, pointer, button);

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

		/* Calculate the time the user has toucher the screen */
		long touchedTime = System.currentTimeMillis() - timer;

		/* Every second touching the screen will increment by 20 the impulse */
		float impulse = Math.max(10f, touchedTime / 50);

		/* Reset the timer for future uses */
		timer = 0L;

		/*
		 * The impulse is applied to the body's center, and only on the Y axis.
		 * It will aggregate the impulse to the current Y speed, so, if the
		 * object is falling the impulse will be the difference between the
		 * object speed and the given value.
		 */
		box.applyLinearImpulse(0, impulse, 0, 0, true);

		return true;
	}
}
