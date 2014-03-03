package com.leakedbits.codelabs.box2d.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.leakedbits.codelabs.box2d.BouncingBallSample;
import com.leakedbits.codelabs.box2d.BuoyancySample;
import com.leakedbits.codelabs.box2d.DragAndDropSample;
import com.leakedbits.codelabs.box2d.GravityAccelerometerSample;
import com.leakedbits.codelabs.box2d.ImpulsesSample;
import com.leakedbits.codelabs.box2d.CollisionsSample;
import com.leakedbits.codelabs.box2d.JumpingSample;
import com.leakedbits.codelabs.box2d.SpawnBodiesSample;
import com.leakedbits.codelabs.box2d.SpritesSample;
import com.leakedbits.codelabs.utils.Sample;

public class Box2DSamples {

	@SuppressWarnings("unchecked")
	public static final List<Class<? extends Sample>> SAMPLES = new ArrayList<Class<? extends Sample>>(
			Arrays.asList(
					BouncingBallSample.class,
					SpawnBodiesSample.class,
					DragAndDropSample.class,
					ImpulsesSample.class,
					SpritesSample.class,
					GravityAccelerometerSample.class,
					CollisionsSample.class,
					BuoyancySample.class,
					JumpingSample.class
			)
	);

}
