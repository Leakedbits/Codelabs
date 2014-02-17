package com.leakedbits.codelabs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;

public class Splash implements Screen {

	private SpriteBatch spriteBatch;
	private Texture splash;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		spriteBatch.draw(splash,
				(Gdx.graphics.getWidth() - splash.getWidth()) / 2,
				(Gdx.graphics.getHeight() - splash.getHeight()) / 2);
		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		spriteBatch = new SpriteBatch();
		splash = new Texture(
				Gdx.files.internal("data/images/codelabs_splash.png"));

		Timer.schedule(new Timer.Task() {

			@Override
			public void run() {
				dispose();
				((Game) Gdx.app.getApplicationListener())
						.setScreen(new MainMenu());
			}
		}, 4);
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
		splash.dispose();
		spriteBatch.dispose();
	}

}
