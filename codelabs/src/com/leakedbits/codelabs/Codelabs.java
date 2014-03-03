package com.leakedbits.codelabs;

import com.badlogic.gdx.Game;

public class Codelabs extends Game {

	public static final String TITLE = "Codelabs";
	public static final String VERSION = "v0.6.0";
	
	public static final float TARGET_WIDTH = 800;
			
	@Override
	public void create() {
		setScreen(new SplashScreen());
	}
	
}
