package fr.alex.games;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Level {
	World world;
	List<Vague> vagues;
	Vague currentVague;
	float totalTime;

	public Level() {
		world = new World(new Vector2(0, -10), true);
		vagues = new ArrayList<Vague>();
		
		//top
		createWall(0, Gdx.graphics.getHeight() + 50, Gdx.graphics.getWidth(), 10);
		
		//bot
		createWall(0, -10, Gdx.graphics.getWidth(), 10);
		
		//left
		createWall(-10, 0, 10, Gdx.graphics.getHeight() + 50);
		
		//right
		createWall(Gdx.graphics.getWidth() + 10, 0, 10, Gdx.graphics.getHeight() + 50);
		
		/*Vague v = new Vague(this);
		v.getSpawns().add(new SpawnInfo(2, 1));
		v.getSpawns().add(new SpawnInfo(2, 1));
		v.getSpawns().add(new SpawnInfo(2, 1));
		v.getSpawns().add(new SpawnInfo(4.5f, 2));
		v.getSpawns().add(new SpawnInfo(4, 1));
		v.getSpawns().add(new SpawnInfo(4, 1));
		v.getSpawns().add(new SpawnInfo(8, 3));
		v.getSpawns().add(new SpawnInfo(9, 2));
		v.getSpawns().add(new SpawnInfo(10, 3));
		vagues.add(v);*/
		random();
	}
	
	public void random(){
		int vagueCount = (int) (Math.round(5 + Math.random() * 5));
		for(int i = 0; i< vagueCount; ++i){
			int spawnCount = (int) (Math.round(5 + Math.random() * 5));
			Vague v = new Vague(i+1, this);
			for(int j = 0; j< spawnCount; ++j){
				int life = (int) (1 + Math.round(Math.random() * 3));
				float time = Math.round(Math.random() * spawnCount);
				v.getSpawns().add(new SpawnInfo(time, life));
			}
			vagues.add(v);
		}
	}
	
	public void update(float delta){
		totalTime += delta;
		if(currentVague != null){
			currentVague.update(delta, totalTime);
		}
	}
	
	public void nextVague(){
		if(!vagues.isEmpty()){
			totalTime = 0;
			currentVague = vagues.remove(0);
		}
	}
	
	public boolean isOver(){
		return vagues.isEmpty();
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

	public List<Vague> getVagues() {
		return vagues;
	}

	public void setVagues(List<Vague> vagues) {
		this.vagues = vagues;
	}

	public Vague getCurrentVague() {
		return currentVague;
	}

	public void setCurrentVague(Vague currentVague) {
		this.currentVague = currentVague;
	}
}
