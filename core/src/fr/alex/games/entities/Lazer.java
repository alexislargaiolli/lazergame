package fr.alex.games.entities;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class Lazer {
	private static final float defaultLifeTime = 2f;
	private Vector2 origin;
	private Vector2 dest;
	private Vector2 head;
	private Vector2 queue;
	private Vector2 direction;
	private Vector2 step;
	private float speed;
	private float length;
	private int width;
	private boolean growing;
	private boolean touched;
	private boolean dead;
	private float lifeTime;

	public Lazer(Vector2 origin, Vector2 dest, float speed, float length,
			int width) {
		super();
		this.origin = origin;
		this.dest = dest;
		this.speed = speed;
		this.length = length;
		this.width = width;
		this.head = new Vector2(origin);
		this.queue = new Vector2(origin);
		this.growing = true;
		this.dead = false;
		this.touched = false;
		this.direction = new Vector2(dest).sub(origin).nor();
	}

	public void update(float delta) {
		if (!dead) {
			if (growing) {
				this.head.x += direction.x * speed * delta;
				this.head.y += direction.y * speed * delta;
				growing = !(head.dst(queue) >= length);
			} else {
				if (!touched) {
					this.head.x += direction.x * speed * delta;
					this.head.y += direction.y * speed * delta;
					touched = head.dst(dest) < 10;
				}
				this.queue.x += direction.x * speed * delta;
				this.queue.y += direction.y * speed * delta;
				if (queue.dst(dest) < 10) {
					dead = true;
				}
			}
		}
	}

	public Vector2 getOrigin() {
		return origin;
	}

	public void setOrigin(Vector2 origin) {
		this.origin = origin;
	}

	public Vector2 getDest() {
		return dest;
	}

	public void setDest(Vector2 dest) {
		this.dest = dest;
	}

	public Vector2 getHead() {
		return head;
	}

	public void setHead(Vector2 head) {
		this.head = head;
	}

	public Vector2 getQueue() {
		return queue;
	}

	public void setQueue(Vector2 queue) {
		this.queue = queue;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	@Override
	public String toString() {
		return "Lazer [origin=" + origin + ", dest=" + dest + ", speed=" + speed + ", length=" + length + ", width=" + width + "]";
	}

}
