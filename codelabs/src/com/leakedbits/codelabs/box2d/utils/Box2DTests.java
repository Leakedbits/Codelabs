package com.leakedbits.codelabs.box2d.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.leakedbits.codelabs.box2d.BouncingBallTest;
import com.leakedbits.codelabs.box2d.DragAndDropTest;
import com.leakedbits.codelabs.box2d.ImpulsesTest;
import com.leakedbits.codelabs.box2d.SpawnBodiesTest;
import com.leakedbits.codelabs.box2d.SpritesTest;
import com.leakedbits.codelabs.utils.Test;

public class Box2DTests {

	@SuppressWarnings("unchecked")
	public static final List<Class<? extends Test>> tests = new ArrayList<Class<? extends Test>>(
			Arrays.asList(
					BouncingBallTest.class,
					SpawnBodiesTest.class,
					DragAndDropTest.class,
					ImpulsesTest.class,
					SpritesTest.class
			)
	);

}
