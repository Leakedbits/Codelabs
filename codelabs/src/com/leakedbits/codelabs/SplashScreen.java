package com.leakedbits.codelabs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class SplashScreen implements Screen {

	private Image splashImage;
	private Stage stage;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();

		stage.draw();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		stage = new Stage();

		/* Load splash image */
		splashImage = new Image(new Texture(
				Gdx.files.internal("data/images/codelabs_splash.png")));

		/* Set the splash image in the center of the screen */
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();

		splashImage.setPosition((width - splashImage.getWidth()) / 2,
				(height - splashImage.getHeight()) / 2);

		/* Fade in the image and then swing it down */
		splashImage.getColor().a = 0f;
		splashImage.addAction(Actions.sequence(Actions.fadeIn(1.25f), Actions
				.delay(2, Actions.moveBy(0,
						-(height - splashImage.getHeight() / 2), 1.25f,
						Interpolation.swingIn)), Actions.run(new Runnable() {
			@Override
			public void run() {

				/* Show main menu after swing out */
				((Game) Gdx.app.getApplicationListener())
						.setScreen(new MainMenu());
			}
		})));

		stage.addActor(splashImage);
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
		stage.dispose();
	}

}