package fr.alex.games.entities;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Primes {
	Body body;
	Polygon polygon;
	Vector2[] spawnPoints;
	Vector2 center;
	int prismeId;

	public Primes(int id, Body body) {
		super();
		this.prismeId = id;
		this.body = body;
		spawnPoints = new Vector2[6];
		spawnPoints[0] = new Vector2(body.getPosition().x + 10, body.getPosition().y + 20);
		spawnPoints[1] = new Vector2(body.getPosition().x + 30, body.getPosition().y + 20);
		spawnPoints[2] = new Vector2(body.getPosition().x + 20, body.getPosition().y);
		PolygonShape box = new PolygonShape();
		// box.setAsBox(50, 50);
		/*
		 * float[] vertices = new float[6]; vertices[0] = 0; vertices[1] = 0;
		 * vertices[2] = 20; vertices[3] = 40; vertices[4] = 40; vertices[5] =
		 * 0;
		 */
		float[] vertices = new float[12];
		vertices[0] = 10;
		vertices[1] = 40;
		vertices[2] = 20;
		vertices[3] = 30;
		vertices[4] = 20;
		vertices[5] = 10;
		vertices[6] = 10;
		vertices[7] = 0;
		vertices[8] = 0;
		vertices[9] = 10;
		vertices[10] = 0;
		vertices[11] = 30;
		box.set(vertices);
		for (int i = 0; i < vertices.length; ++i) {
			if (i % 2 == 0) {
				vertices[i] += body.getPosition().x;
			} else {
				vertices[i] += body.getPosition().y;
			}
		}
		int i = 0;
		int j = 0;
		while (i < 11) {
			if (i == 10) {
				float x = Interpolation.linear.apply(vertices[i], vertices[0], .5f);
				float y = Interpolation.linear.apply(vertices[i + 1], vertices[1], .5f);
				spawnPoints[j] = new Vector2(x, y);
			}
			else{
				float x = Interpolation.linear.apply(vertices[i], vertices[i + 2], .5f);
				float y = Interpolation.linear.apply(vertices[i + 1], vertices[i + 3], .5f);
				spawnPoints[j] = new Vector2(x, y);
			}
			j++;
			i += 2;
		}
		polygon = new Polygon(vertices);
		center = new Vector2(body.getPosition().x + 10, body.getPosition().y + 20);
		body.createFixture(box, 0.0f);
	}

	public boolean hit(Lazer lazer) {
		return Intersector.intersectSegmentPolygon(lazer.getQueue(), lazer.getHead(), polygon);
	}
	
	public boolean contains(Lazer lazer){
		return polygon.contains(lazer.getQueue().x, lazer.getQueue().y) && polygon.contains(lazer.getHead().x, lazer.getHead().y);
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public Vector2[] getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(Vector2[] spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	public Vector2 getCenter() {
		return center;
	}

	public void setCenter(Vector2 center) {
		this.center = center;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}

	public int getPrismeId() {
		return prismeId;
	}

	public void setPrismeId(int prismeId) {
		this.prismeId = prismeId;
	}

}
