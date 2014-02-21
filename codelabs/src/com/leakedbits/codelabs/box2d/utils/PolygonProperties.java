package com.leakedbits.codelabs.box2d.utils;

import com.badlogic.gdx.math.Vector2;

public class PolygonProperties {

	private Vector2 centroid;
	
	private float area;
	
	public PolygonProperties(Vector2 centroid, float area) {
		this.centroid = centroid;
		this.area = area;
	}

	public Vector2 getCentroid() {
		return centroid;
	}

	public void setCentroid(Vector2 centroid) {
		this.centroid = centroid;
	}

	public float getArea() {
		return area;
	}

	public void setArea(float area) {
		this.area = area;
	}
	
}
