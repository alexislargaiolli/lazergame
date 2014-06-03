package fr.alex.games.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

import fr.alex.games.GlobalSettings;

public class Canon {
	private Vector2 lazerOrigin;
	private int energy;
	private int MAX_ENERGY;

	public Canon(Vector2 lazerOrigin) {
		super();
		this.lazerOrigin = lazerOrigin;
		MAX_ENERGY = 100;
		energy = MAX_ENERGY;
	}
	
	public Lazer fire(Vector2 dest, float speed, int strength) {
		Vector2 intersection = new Vector2();
		Intersector.intersectLines(lazerOrigin.x, lazerOrigin.y, dest.x, dest.y, 0, 0, 0, GlobalSettings.height, intersection);
		if (intersection.y < 0 || intersection.y > GlobalSettings.height) {
			Intersector.intersectLines(lazerOrigin.x, lazerOrigin.y, dest.x, dest.y, GlobalSettings.width, 0, GlobalSettings.width, GlobalSettings.height, intersection);
			if (intersection.y < 0 || intersection.y > GlobalSettings.height) {
				Intersector.intersectLines(lazerOrigin.x, lazerOrigin.y, dest.x, dest.y, 0, GlobalSettings.height, GlobalSettings.width, GlobalSettings.height, intersection);
			}
		}
		Lazer lazer = new Lazer(lazerOrigin, intersection, speed, 100, strength);
		Gdx.app.log("spawnLazer()", lazer.toString());
		return lazer;
	}
	
	public float clickTimeToLazerSpeed(float clickedTime){
		return 500f;
	}
	
	public int clickTimeToLazerStrength(float clickedTime){
		return Math.round(1 + clickedTime * 3f);
	}

	/**
	 * Decrease canon energy by 1 
	 */
	public void decreaseEnergy(){
		energy--;
	}
	
	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}
}
