package com.leakedbits.codelabs;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.leakedbits.codelabs.box2d.utils.Box2DTests;
import com.leakedbits.codelabs.utils.Test;
import com.leakedbits.codelabs.utils.TestUtils;

public class MainMenu implements Screen {

	private Skin skin;
	private Stage stage;
	private Table table;
	private TextureAtlas atlas;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		stage = new Stage();
		stage.setViewport(Codelabs.TARGET_WIDTH, Codelabs.TARGET_WIDTH * (Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));

		Gdx.input.setInputProcessor(stage);

		atlas = new TextureAtlas("data/ui/atlas.pack");
		skin = new Skin(Gdx.files.internal("data/ui/menu_skin.json"), atlas);

		table = new Table(skin);
		table.setFillParent(true);

		List<TextButton> textButtons = new ArrayList<TextButton>();

		for (String name : TestUtils.getNames(Box2DTests.tests, false)) {
			Test test = TestUtils.instantiateTest(Box2DTests.tests, name);
			textButtons.add(createTextButton(test.getName(), skin, "blue", test));
		}

		addTestButtons(textButtons);
		
		stage.addActor(table);

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
		skin.dispose();
		atlas.dispose();
		stage.dispose();
	}

	private void addTestButtons(List<TextButton> textButtons) {
		for (TextButton textButton : textButtons) {
			table.add(textButton).fill().spaceBottom(10);
			table.row();
		}
		
	}
	
	private TextButton createTextButton(String text, Skin skin,
			String styleName, final Screen screen) {
		TextButton textButton = new TextButton(text, skin, styleName);
		textButton.pad(10);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(screen);
			}
		});

		return textButton;
	}
}
