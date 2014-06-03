package fr.alex.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Level {
	World world;

	public Level() {
		world = new World(new Vector2(0, -10), true);
		
		//top
		createWall(0, Gdx.graphics.getHeight() + 50, Gdx.graphics.getWidth(), 10);
		
		//bot
		createWall(0, -10, Gdx.graphics.getWidth(), 10);
		
		//left
		createWall(-10, 0, 10, Gdx.graphics.getHeight() + 50);
		
		//right
		createWall(Gdx.graphics.getWidth() + 10, 0, 10, Gdx.graphics.getHeight() + 50);
	}
	
	private void createWall(float x, float y, float width, float height){
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(new Vector2(x, y));
		Body groundBody = world.createBody(groundBodyDef);
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(width, height);
		groundBody.createFixture(groundBox, 0.0f);
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
