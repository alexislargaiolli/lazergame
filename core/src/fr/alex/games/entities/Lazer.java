package fr.alex.games.entities;

import com.badlogic.gdx.math.Vector2;

public class Lazer {
	private Vector2 origin;
	private Vector2 dest;
	private Vector2 head;
	private Vector2 queue;
	private Vector2 direction;

	/**
	 * Lazer move speed
	 */
	private float speed;

	/**
	 * Length of the lazer
	 */
	private float length;

	/**
	 * Strength of the lazer (number of enemy the lazer can touch)
	 */
	private int strength;

	/**
	 * Number of enemy touched by the lazer
	 */
	private int shootCount;

	/**
	 * True if the lazer has to be removed
	 */
	private boolean dead;

	/**
	 * True if the lazer can't touched enemy anymore
	 */
	private boolean disable;

	private boolean stop;
	
	private int fromId;

	public Lazer(Vector2 origin, Vector2 dest, float speed, float length, int strength) {
		super();
		this.origin = origin;
		this.dest = dest;
		this.speed = speed;
		this.length = length;
		this.strength = strength;
		this.head = new Vector2(origin);
		this.queue = new Vector2(origin);
		this.dead = false;
		this.fromId = -1;
		computeDirection();
	}

	public void computeDirection() {
		this.direction = new Vector2(dest).sub(origin).nor();
	}

	public void update(float delta) {
		if (!stop) {
			this.head.x += direction.x * speed * delta;
			this.head.y += direction.y * speed * delta;
		}
		if (head.dst(queue) >= length || stop) {
			this.queue.x += direction.x * speed * delta;
			this.queue.y += direction.y * speed * delta;
		}
		if (stop && queue.dst(head) < 10) {
			dead = true;
		}
	}

	public void stop(){
		stop = true;
	}
	
	/**
	 * Decrease the strength of the lazer by 1
	 */
	public void decreaseStrength() {
		if (strength > 1) {
			this.strength--;
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

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	@Override
	public String toString() {
		return "Lazer [origin=" + origin + ", dest=" + dest + ", speed=" + speed + ", length=" + length + ", width=" + strength + "]";
	}

	public int getShootCount() {
		return shootCount;
	}

	public void setShootCount(int shootCount) {
		this.shootCount = shootCount;
	}

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	public int getFromId() {
		return fromId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public Vector2 getDirection() {
		return direction;
	}

	public void setDirection(Vector2 direction) {
		this.direction = direction;
	}

}
