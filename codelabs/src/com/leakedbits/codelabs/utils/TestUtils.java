package com.leakedbits.codelabs.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestUtils {
	public static List<String> getNames(List<Class<? extends Test>> tests,
			boolean sorted) {
		List<String> names = new ArrayList<String>(tests.size());

		for (Class<? extends Test> testClass : tests) {
			names.add(testClass.getSimpleName());
		}

		if (sorted) {
			Collections.sort(names);
		}

		return names;
	}

	public static Test instantiateTest(List<Class<? extends Test>> tests,
			String testName) {
		try {
			return forName(tests, testName).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Class<? extends Test> forName(
			List<Class<? extends Test>> tests, String testName) {

		Class<? extends Test> requestedClass = null;

		for (Class<? extends Test> testClass : tests) {
			if (testClass.getSimpleName().equals(testName)) {
				requestedClass = testClass;
			}
		}

		return requestedClass;
	}

}
