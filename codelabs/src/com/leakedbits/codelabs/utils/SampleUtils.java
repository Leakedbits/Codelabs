package com.leakedbits.codelabs.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SampleUtils {
	public static List<String> getNames(List<Class<? extends Sample>> samples,
			boolean sorted) {
		List<String> names = new ArrayList<String>(samples.size());

		for (Class<? extends Sample> sample : samples) {
			names.add(sample.getSimpleName());
		}

		if (sorted) {
			Collections.sort(names);
		}

		return names;
	}

	public static Sample instantiateSample(List<Class<? extends Sample>> samples,
			String sampleName) {
		try {
			return forName(samples, sampleName).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Class<? extends Sample> forName(
			List<Class<? extends Sample>> samples, String sampleName) {

		Class<? extends Sample> requestedClass = null;

		for (Class<? extends Sample> sample : samples) {
			if (sample.getSimpleName().equals(sampleName)) {
				requestedClass = sample;
			}
		}

		return requestedClass;
	}

}
