package fr.alex.games.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GeometryUtils;
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
	float[] vertices;

	public Primes(int id, Body body) {
		super();
		this.prismeId = id;
		this.body = body;
		/*
		 * spawnPoints = new Vector2[6]; spawnPoints[0] = new
		 * Vector2(body.getPosition().x + 10, body.getPosition().y + 20);
		 * spawnPoints[1] = new Vector2(body.getPosition().x + 30,
		 * body.getPosition().y + 20); spawnPoints[2] = new
		 * Vector2(body.getPosition().x + 20, body.getPosition().y);
		 * PolygonShape box = new PolygonShape(); float[] vertices = new
		 * float[12]; vertices[0] = 10; vertices[1] = 40; vertices[2] = 20;
		 * vertices[3] = 30; vertices[4] = 20; vertices[5] = 10; vertices[6] =
		 * 10; vertices[7] = 0; vertices[8] = 0; vertices[9] = 10; vertices[10]
		 * = 0; vertices[11] = 30; box.set(vertices); for (int i = 0; i <
		 * vertices.length; ++i) { if (i % 2 == 0) { vertices[i] +=
		 * body.getPosition().x; } else { vertices[i] += body.getPosition().y; }
		 * } int i = 0; int j = 0; while (i < 11) { if (i == 10) { float x =
		 * Interpolation.linear.apply(vertices[i], vertices[0], .5f); float y =
		 * Interpolation.linear.apply(vertices[i + 1], vertices[1], .5f);
		 * spawnPoints[j] = new Vector2(x, y);
		 * 
		 * } else{ float x = Interpolation.linear.apply(vertices[i], vertices[i
		 * + 2], .5f); float y = Interpolation.linear.apply(vertices[i + 1],
		 * vertices[i + 3], .5f); spawnPoints[j] = new Vector2(x, y); } j++; i
		 * += 2; }
		 */
		PolygonShape triangle = new PolygonShape();
		vertices = new float[6];
		vertices[0] = 0;
		vertices[1] = 0;
		vertices[2] = 20;
		vertices[3] = 30;
		vertices[4] = 40;
		vertices[5] = 0;
		triangle.set(vertices);
		vertices[0] += body.getPosition().x;
		vertices[1] += body.getPosition().y;
		vertices[2] += body.getPosition().x;
		vertices[3] += body.getPosition().y;
		vertices[4] += body.getPosition().x;
		vertices[5] += body.getPosition().y;
		polygon = new Polygon(vertices);
		center = new Vector2();
		GeometryUtils.triangleCentroid(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5], center);
		body.createFixture(triangle, 0.0f);

	}

	public boolean hit(Lazer lazer) {
		return Intersector.intersectSegmentPolygon(lazer.getQueue(), lazer.getHead(), polygon);
	}

	public List<Lazer> handleHit(Lazer lazer) {
		List<Lazer> lazers = new ArrayList<Lazer>();
		Vector2 intersection1 = new Vector2();
		Vector2 intersection2 = new Vector2();
		Vector2 intersection3 = new Vector2();
		Gdx.app.log("before", lazer.getHead().toString());
		Vector2 head = new Vector2(lazer.getOrigin()).interpolate(lazer.getDest(), 2, Interpolation.linear);
		Gdx.app.log("after", head.toString());
		boolean p1 = Intersector.intersectSegments(vertices[0], vertices[1], vertices[2], vertices[3], lazer.getOrigin().x, lazer.getOrigin().y, head.x, head.y, intersection1);
		boolean p2 = Intersector.intersectSegments(vertices[2], vertices[3], vertices[4], vertices[5], lazer.getOrigin().x, lazer.getOrigin().y, head.x, head.y, intersection2);
		boolean p3 = Intersector.intersectSegments(vertices[0], vertices[1], vertices[4], vertices[5], lazer.getOrigin().x, lazer.getOrigin().y, head.x, head.y, intersection3);

		Gdx.app.log("test", p1 + " " + p2 + " " + p3);
		//Vector2 ori = null;
		float x1=0, y1=0, x2=0, y2=0;
		if (p1 && p2) {
			Gdx.app.log("p1 p2", "");
			//ori = new Vector2(vertices[0], vertices[1]).interpolate(new Vector2(vertices[4], vertices[5]), .5f, Interpolation.linear);
			x1 = vertices[0];
			y1 = vertices[1];
			x2 = vertices[4];
			y2 = vertices[5];
		} else if (p1 && p3) {
			Gdx.app.log("p1 p3", "");
			//ori = new Vector2(vertices[0], vertices[1]).interpolate(new Vector2(vertices[2], vertices[3]), .5f, Interpolation.linear);
			x1 = vertices[0];
			y1 = vertices[1];
			x2 = vertices[2];
			y2 = vertices[3];
		} else if (p2 && p3) {
			Gdx.app.log("p2 p3", "");
			//ori = new Vector2(vertices[2], vertices[3]).interpolate(new Vector2(vertices[4], vertices[5]), .5f, Interpolation.linear);
			x1 = vertices[2];
			y1 = vertices[3];
			x2 = vertices[4];
			y2 = vertices[5];
		}
		for (int i = 0; i < 5; ++i) {
			Vector2 ori = new Vector2(x1, y1).interpolate(new Vector2(x2, y2), (i + 2) * .1f, Interpolation.linear);
			Lazer l = new Lazer(center, ori, lazer.getSpeed(), lazer.getLength(), lazer.getStrength());
			l.setFromId(prismeId);
			lazers.add(l);
		}
		return lazers;
	}

	/*
	 * public List<Lazer> handleLazer(Lazer lazer){
	 * 
	 * }
	 */

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

	public float[] getVertices() {
		return vertices;
	}

	public void setVertices(float[] vertices) {
		this.vertices = vertices;
	}

}
