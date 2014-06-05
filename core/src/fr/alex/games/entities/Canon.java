package fr.alex.games.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class Canon {
	private Vector2 lazerOrigin;
	/**
	 * Number of lazers the canon shoot on each shot
	 */
	private int lazerCount;
	private int energy;
	private int MAX_ENERGY;

	public Canon(Vector2 lazerOrigin) {
		super();
		this.lazerOrigin = lazerOrigin;
		MAX_ENERGY = 100;
		energy = MAX_ENERGY;
		lazerCount = 1;
	}

	public List<Lazer> fire(Vector2 dest, float speed, int strength) {
		List<Lazer> lazers = new ArrayList<Lazer>();
		if (lazerCount == 1) {
			Lazer lazer = new Lazer(lazerOrigin, dest, speed, 100, strength);
			lazers.add(lazer);
			Gdx.app.log("lazer spawn", lazer.toString());
		} else {
			for(int i=0; i<lazerCount; ++i){
				float deltaX = Interpolation.linear.apply(-15, 15, (float)i/((float)lazerCount-1));
				Vector2 ori = new Vector2(lazerOrigin.x + deltaX, lazerOrigin.y);
				Vector2 des = new Vector2(dest.x + deltaX, dest.y);
				Lazer lazer = new Lazer(ori, des, speed, 100, strength);
				Gdx.app.log("lazer spawn", lazer.toString());
				lazers.add(lazer);
			}
		}
		return lazers;
	}

	public float clickTimeToLazerSpeed(float clickedTime) {
		return 500f;
	}

	public int clickTimeToLazerStrength(float clickedTime) {
		return Math.round(1 + clickedTime * 3f);
	}

	/**
	 * Decrease canon energy by 1
	 */
	public void decreaseEnergy() {
		energy--;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public Vector2 getLazerOrigin() {
		return lazerOrigin;
	}

	public void setLazerOrigin(Vector2 lazerOrigin) {
		this.lazerOrigin = lazerOrigin;
	}
}
