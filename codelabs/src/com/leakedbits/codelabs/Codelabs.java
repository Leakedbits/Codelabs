package com.leakedbits.codelabs;

import com.badlogic.gdx.Game;
import com.leakedbits.codelabs.box2d.BouncingBall;

public class Codelabs extends Game {

	public static final String TITLE = "Leakedbits Codelabs";
			
	@Override
	public void create() {
		setScreen(new BouncingBall());		
	}
	
}
