package com.leakedbits.codelabs.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.leakedbits.codelabs.utils.Sample;

public class ParticlesSample extends Sample {

	private SpriteBatch batch;

	private ParticleEffect particle;

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
	public void render(float delta) {
		/* Clear screen with a black background */
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		particle.draw(batch, delta);
		batch.end();
	}

	@Override
	public void show() {
		super.show();

		batch = new SpriteBatch();

		/*
		 * Create the particle effect loading the internal file from assets
		 * folder
		 */
		particle = new ParticleEffect();
		particle.load(Gdx.files.internal("data/particles/fire.p"),
				Gdx.files.internal("data/images/particles"));
		particle.setPosition(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() / 2);
		particle.start();
	}

	@Override
	public void dispose() {
		batch.dispose();
		particle.dispose();
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		particle.setPosition(screenX, Gdx.graphics.getHeight() - screenY);

		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		particle.setPosition(screenX, Gdx.graphics.getHeight() - screenY);
		particle.reset();

		return true;
	}

}
