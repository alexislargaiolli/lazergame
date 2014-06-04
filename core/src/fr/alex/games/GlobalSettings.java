package fr.alex.games;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GlobalSettings {
	public static float width;
	
	public static float height;
	
	public static Rectangle viewport;
	
	public static Vector2 projectOnBound(Vector2 origin, Vector2 destination){
		Vector2 intersection = new Vector2();
		Intersector.intersectLines(origin.x, origin.y, destination.x, destination.y, 0, 0, 0, GlobalSettings.height, intersection);
		if (intersection.y < 0 || intersection.y > GlobalSettings.height) {
			Intersector.intersectLines(origin.x, origin.y, destination.x, destination.y, GlobalSettings.width, 0, GlobalSettings.width, GlobalSettings.height, intersection);
			if (intersection.y < 0 || intersection.y > GlobalSettings.height) {
				Intersector.intersectLines(origin.x, origin.y, destination.x, destination.y, 0, GlobalSettings.height, GlobalSettings.width, GlobalSettings.height, intersection);
			}
		}
		return intersection;
	}
}
