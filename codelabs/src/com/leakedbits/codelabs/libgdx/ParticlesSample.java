package com.leakedbits.codelabs.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.leakedbits.codelabs.libgdx.actors.ParticleEffectActor;
import com.leakedbits.codelabs.utils.Sample;

public class ParticlesSample extends Sample implements ApplicationListener {

	/* Max number of balls to be spawned */
	private static final int MAX_SPAWNED_PARTICLES = 3;

	/* Stage to display the actors with scene2d */
	private Stage stage;

	/* Define the effect that will spawn the particles in the screen */
	private ParticleEffect particle;

	/* Counter to know how many particles have been spawned */
	private int spawnedParticles;

	/**
	 * Main constructor used to update sample name.
	 */
	public ParticlesSample() {
		name = "Particles";
	}

	/*
	 * Input events handling. Here will process all touch events to spawn and
	 * drag bodies.
	 */

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		/* Checks whether the max amount of balls were spawned */
		// if (spawnedBalls < MAX_SPAWNED_BALLS) {
		// spawnedBalls++;
		//
		// /* Translate camera point to world point */
		// Vector3 unprojectedVector = new Vector3();
		// camera.unproject(unprojectedVector.set(screenX, screenY, 0));
		//
		// /* Create a new ball */
		// Shape shape = Box2DFactory.createCircleShape(1);
		// FixtureDef fixtureDef = Box2DFactory.createFixture(shape, 2.5f,
		// 0.25f, 0.75f, false);
		// Box2DFactory.createBody(world, BodyType.DynamicBody, fixtureDef,
		// new Vector2(unprojectedVector.x, unprojectedVector.y));
		// }

		return true;
	}

	@Override
	public void create() {
		/* Create the stage with the desired width and height */
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				true);
		Gdx.input.setInputProcessor(stage);

		/*
		 * Create the particle effect loading the internal file from assets
		 * folder
		 */
		particle = new ParticleEffect();
		particle.load(Gdx.files.internal("effects/fire.p"),
				Gdx.files.internal("effects"));

		stage.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				ParticleEffectActor particleActor = new ParticleEffectActor(
						particle);
				stage.addActor(particleActor);
				return true;
			}
		});
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stage.getActors().clear();
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
