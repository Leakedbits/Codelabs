package com.leakedbits.codelabs.box2d.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2DFactory {

	public static Body createBody(World world, BodyType bodyType,
			FixtureDef fixtureDef, Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;
		bodyDef.position.set(position);

		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		fixtureDef.shape.dispose();

		return body;
	}

	public static Shape createBoxShape(float halfWidth, float halfHeight) {
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(halfWidth, halfHeight);

		return boxShape;
	}

	public static Shape createChainShape(Vector2[] vertices) {
		ChainShape chainShape = new ChainShape();
		chainShape.createChain(vertices);

		return chainShape;
	}

	public static Shape createCircleShape(float radius) {
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(radius);

		return circleShape;
	}

	public static Shape createPolygonShape(Vector2[] vertices) {
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.set(vertices);

		return polygonShape;
	}

	public static Shape createTriangleShape(float halfWidth, float halfHeight) {
		PolygonShape triangleShape = new PolygonShape();
		triangleShape
				.set(new Vector2[] { new Vector2(-halfWidth, -halfHeight),
						new Vector2(0, halfHeight),
						new Vector2(halfWidth, -halfHeight) });

		return triangleShape;
	}

	public static FixtureDef createFixture(Shape shape, float density,
			float friction, float restitution, boolean isSensor) {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.isSensor = isSensor;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;

		return fixtureDef;
	}

	public static Body createWalls(World world, float viewportWidth,
			float viewportHeight, float offset) {
		float halfWidth = viewportWidth / 2 - offset;
		float halfHeight = viewportHeight / 2 - offset;

		Vector2[] vertices = new Vector2[] {
				new Vector2(-halfWidth, -halfHeight),
				new Vector2(halfWidth, -halfHeight),
				new Vector2(halfWidth, halfHeight),
				new Vector2(-halfWidth, halfHeight),
				new Vector2(-halfWidth, -halfHeight) };

		Shape shape = createChainShape(vertices);
		FixtureDef fixtureDef = createFixture(shape, 1, 0.5f, 0, false);

		return createBody(world, BodyType.StaticBody, fixtureDef, new Vector2(
				0, 0));
	}

}
