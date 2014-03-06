package com.leakedbits.codelabs.libgdx.actors;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleEffectActor extends Actor {
	ParticleEffect effect;

	public ParticleEffectActor(ParticleEffect effect) {
		this.effect = effect;
	}

	public void draw(SpriteBatch batch, float parentAlpha) {
		/* Define behavior when stage calls Actor.draw() */
		effect.draw(batch);
	}

	public void act(float delta) {
		super.act(delta);
		effect.setPosition(100, 100);
		effect.update(delta);
		effect.start();
	}

	public ParticleEffect getEffect() {
		return effect;
	}
}
