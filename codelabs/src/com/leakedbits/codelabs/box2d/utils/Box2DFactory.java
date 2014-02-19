package com.leakedbits.codelabs.box2d.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2DFactory {

	/**
	 * Create a Box2D box and add it to the world.
	 * 
	 * @param world
	 *            World where to add the new box
	 * @param bodyType
	 *            Type of body (Dynamic, Kinematic, Static)
	 * @param position
	 *            Position of the box
	 * @param halfWidth
	 *            Half of the width of the box
	 * @param halfHeight
	 *            Half of the height of the box
	 * @param density
	 *            Fixture density
	 * @param friction
	 *            Fixture friction
	 * @param restitution
	 *            Fixcture restitution
	 * @return A new Box2D body with box shape
	 */
	public static Body createBox(World world, BodyType bodyType,
			Vector2 position, float halfWidth, float halfHeight, float density,
			float friction, float restitution) {

		/* Box body definition. Represents a single point in the world */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;
		bodyDef.position.set(position);

		/* Shape definition (the actual shape of the body) */
		PolygonShape boxShape = new PolygonShape();

		/*
		 * Use setAsBox to define the box shape. We have to specify half-width
		 * and half-height to the method.
		 */
		boxShape.setAsBox(halfWidth, halfHeight);

		/*
		 * Fixture definition. Define properties of a body like the shape, the
		 * density of the body, its friction or its restitution (how 'bouncy' a
		 * fixture is) in a physics scene.
		 */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = boxShape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		boxShape.dispose();

		return body;
	}

	/**
	 * Create a Box2D circle and add it to the world.
	 * 
	 * @param world
	 *            World where to add the new circle
	 * @param bodyType
	 *            Type of body (Dynamic, Kinematic, Static)
	 * @param position
	 *            Position of the circle
	 * @param radius
	 *            Radius of the circle
	 * @param density
	 *            Fixture density
	 * @param friction
	 *            Fixture friction
	 * @param restitution
	 *            Fixture restitution
	 * @return A new Box2D body with circle shape
	 */
	public static Body createCircle(World world, BodyType bodyType,
			Vector2 position, float radius, float density, float friction,
			float restitution) {

		/* Circle body definition. Represents a single point in the world */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;
		bodyDef.position.set(position);

		/* Shape definition (the actual shape of the body) */
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(radius);

		/*
		 * Fixture definition. Define properties of a body like the shape, the
		 * density of the body, its friction or its restitution (how 'bouncy' a
		 * fixture is) in a physics scene.
		 */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = 2.5f;
		fixtureDef.friction = 0.25f;
		fixtureDef.restitution = 0.75f;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		circleShape.dispose();

		return body;
	}

	/**
	 * Create a Box2D triangle and add it to the world.
	 * 
	 * @param world
	 *            World where to add the new triangle
	 * @param bodyType
	 *            Type of body (Dynamic, Kinematic, Static)
	 * @param position
	 *            Position of the triangle
	 * @param halfBase
	 *            Half of the base of the triangle
	 * @param halfHeight
	 *            Half of the height of the triangle
	 * @param density
	 *            Fixture density
	 * @param friction
	 *            Fixture friction
	 * @param restitution
	 *            Fixcture restitution
	 * @return A new Box2D body with triangle shape
	 */
	public static Body createTriangle(World world, BodyType bodyType,
			Vector2 position, float halfBase, float halfHeight, float density,
			float friction, float restitution) {

		/* Box body definition. Represents a single point in the world */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;
		bodyDef.position.set(position);

		/* Shape definition (the actual shape of the body) */
		PolygonShape triangleShape = new PolygonShape();
		triangleShape
				.set(new Vector2[] { new Vector2(-halfBase, -halfHeight),
						new Vector2(0, halfHeight),
						new Vector2(halfBase, -halfHeight) });

		/*
		 * Fixture definition. Define properties of a body like the shape, the
		 * density of the body, its friction or its restitution (how 'bouncy' a
		 * fixture is) in a physics scene.
		 */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = triangleShape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		triangleShape.dispose();

		return body;
	}

	/**
	 * Create walls around the viewport to keep objects inside.
	 * 
	 * @param world
	 *            World where to add the new walls
	 * @param viewportWidth
	 *            Viewport width
	 * @param viewportHeight
	 *            Viewport height
	 * @param offsetX
	 *            Horizontal distance from edge to wall position
	 * @param offsetY
	 *            Vertical distance from edge to wall position
	 * @param density
	 *            Fixture density
	 * @return A new Box2D body
	 */
	public static Body createWalls(World world, float viewportWidth,
			float viewportHeight, float offsetX, float offsetY, float density) {

		float halfWidth = viewportWidth / 2 - offsetX;
		float halfHeight = viewportHeight / 2 - offsetY;

		Vector2[] vertices = new Vector2[] {
				new Vector2(-halfWidth, -halfHeight),
				new Vector2(halfWidth, -halfHeight),
				new Vector2(halfWidth, halfHeight),
				new Vector2(-halfWidth, halfHeight),
				new Vector2(-halfWidth, -halfHeight) };

		return createChain(world, BodyType.StaticBody, new Vector2(0, 0), vertices, density, 0.5f, 0f);
	}

	public static Body createChain(World world, BodyType bodyType,
			Vector2 position, Vector2[] vertices, float density,
			float friction, float restitution) {

		/* Chain body definition. Represents a single point in the world */
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;
		bodyDef.position.set(position);

		/* Shape definition (the actual shape of the body) */
		ChainShape chainShape = new ChainShape();
		chainShape.createChain(vertices);

		/*
		 * Fixture definition. Define properties of a body like the shape, the
		 * density of the body, its friction or its restitution (how 'bouncy' a
		 * fixture is) in a physics scene.
		 */
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = chainShape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;

		/* Create body and fixture */
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		/* Dispose shape once the body is added to the world */
		chainShape.dispose();

		return body;
	}
}
