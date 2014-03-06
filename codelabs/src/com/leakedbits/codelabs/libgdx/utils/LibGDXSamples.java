package com.leakedbits.codelabs.libgdx.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.leakedbits.codelabs.libgdx.ParticlesSample;
import com.leakedbits.codelabs.utils.Sample;

public class LibGDXSamples {

	@SuppressWarnings("unchecked")
	public static final List<Class<? extends Sample>> SAMPLES = new ArrayList<Class<? extends Sample>>(
			Arrays.asList(
					ParticlesSample.class
			)
	);

}
