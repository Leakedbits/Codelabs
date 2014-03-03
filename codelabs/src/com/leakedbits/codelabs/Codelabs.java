package com.leakedbits.codelabs;

import com.badlogic.gdx.Game;
import com.leakedbits.codelabs.box2d.JumpingSample;

public class Codelabs extends Game {

	public static final String TITLE = "Codelabs";
	public static final String VERSION = "v0.5.0";
	
	public static final float TARGET_WIDTH = 800;
			
	@Override
	public void create() {
//		setScreen(new SplashScreen());
		setScreen(new JumpingSample());
	}
	
}
