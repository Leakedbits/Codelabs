package com.leakedbits.codelabs.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.leakedbits.codelabs.utils.Sample;

public class DragAndDropSample extends Sample {

	/* Use Box2DDebugRenderer, which is a model renderer for debug purposes */
	private Box2DDebugRenderer debugRenderer;

	/* As always, we need a camera to be able to see the objects */
	private OrthographicCamera camera;

	/* Define a world to hold all bodies and simulate reactions between them */
	private World world;

	/*
	 * Used to define a mouse joint for a body. This point will track a
	 * specified world point.
	 */
	private MouseJoint mouseJoint;
	private MouseJointDef mouseJointDef;

	/* Store the position of the last touch or mouse click */
	private Vector3 touchPosition;

	/**
	 * Main constructor used to update sample name.
	 */
	public DragAndDropSample() {
		name = "Drag and drop";
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

		/*
		 * Next line must remain commented because we do this in its parent (See
		 * Sample class). In case you are not using Sample class, uncomment this
		 * line to set input processor to handle events.
		 */
		//Gdx.input.setInputProcessor(this);

		/*
		 * Instantiate the vector that will be used to store click/touch
		 * positions.
		 */
		touchPosition = new Vector3();

		/* Create all bodies */
		createBall();
		Body walls = createWalls();

		/* Define the mouse joint. We use walls as the first body of the joint */
		createMouseJointDefinition(walls);
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

	/**
	 * Creates the MouseJoint definition.
	 * 
	 * @param body
	 *            First body of the joint (i.e. ground, walls, etc.)
	 */
	private void createMouseJointDefinition(Body body) {
		mouseJointDef = new MouseJointDef();
		mouseJointDef.bodyA = body;
		mouseJointDef.collideConnected = true;
		mouseJointDef.maxForce = 500;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		super.touchDown(screenX, screenY, pointer, button);
		
		/*
		 * Define a new QueryCallback. This callback will be used in
		 * world.QueryAABB method.
		 */
		QueryCallback queryCallback = new QueryCallback() {

			@Override
			public boolean reportFixture(Fixture fixture) {
				boolean testResult;

				/*
				 * If the hit point is inside the fixture of the body, create a
				 * new MouseJoint.
				 */
				if (testResult = fixture.testPoint(touchPosition.x,
						touchPosition.y)) {
					mouseJointDef.bodyB = fixture.getBody();
					mouseJointDef.target.set(touchPosition.x, touchPosition.y);
					mouseJoint = (MouseJoint) world.createJoint(mouseJointDef);
				}

				return testResult;
			}
		};

		/* Translate camera point to world point */
		camera.unproject(touchPosition.set(screenX, screenY, 0));

		/*
		 * Query the world for all fixtures that potentially overlap the touched
		 * point.
		 */
		world.QueryAABB(queryCallback, touchPosition.x, touchPosition.y,
				touchPosition.x, touchPosition.y);

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		super.touchUp(screenX, screenY, pointer, button);
		
		/* Whether the input was processed */
		boolean processed = false;

		/* If a MouseJoint is defined, destroy it */
		if (mouseJoint != null) {
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
			processed = true;
		}

		return processed;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		super.touchDragged(screenX, screenY, pointer);

		/* Whether the input was processed */
		boolean processed = false;

		/*
		 * If a MouseJoint is defined, update its target with current position.
		 */
		if (mouseJoint != null) {

			/* Translate camera point to world point */
			camera.unproject(touchPosition.set(screenX, screenY, 0));
			mouseJoint.setTarget(new Vector2(touchPosition.x, touchPosition.y));
		}

		return processed;
	}
	
}
