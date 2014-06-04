package fr.alex.games.entities;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Enemy {
	private float radius;
	private Body body;
	private boolean dead;
	private int lifeCount;
	private int DEFAULT_LIFE_COUNT;

	public Enemy(Body b, int life) {
		body = b;
		DEFAULT_LIFE_COUNT = life;
		lifeCount = DEFAULT_LIFE_COUNT;
		computeRadius();
		CircleShape dynamicCircle = new CircleShape();
		dynamicCircle.setRadius(radius);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = dynamicCircle;
		fixtureDef.density = .8f;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = .1f;
		body.createFixture(fixtureDef);
	}

	private void computeRadius() {
		radius = 20f + (lifeCount * 8f);
	}

	public boolean hit(Lazer lazer) {
		return Intersector.distanceSegmentPoint(lazer.getQueue(), lazer.getHead(), body.getPosition()) < radius;
	}

	public void looseLife() {
		if (lifeCount > 0) {
			lifeCount--;
			computeRadius();
			((CircleShape) body.getFixtureList().get(0).getShape()).setRadius(radius);
			if (lifeCount <= 0) {
				dead = true;
			}
		}
	}

	public float x() {
		return body.getPosition().x;
	}

	public float y() {
		return body.getPosition().y;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}
}
