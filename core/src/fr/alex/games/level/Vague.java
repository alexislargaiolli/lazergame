package fr.alex.games.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import fr.alex.games.entities.Enemy;

public class Vague {
	private List<SpawnInfo> spawns;
	private List<SpawnInfo> spawnsToRemove;
	private List<Enemy> enemiesToSpawn;
	private Level level;
	private int index;

	public Vague(int index, Level level) {
		this.index = index;
		spawns = new ArrayList<SpawnInfo>();
		spawnsToRemove = new ArrayList<SpawnInfo>();
		enemiesToSpawn = new ArrayList<Enemy>();
		this.level = level;
	}

	public void update(float delta, float totalDeltaTime) {
		for (int i = 0; i < spawns.size(); ++i) {
			SpawnInfo spawn = spawns.get(i);
			if (spawn.getTimeSpawn() < totalDeltaTime) {
				spawnEnemy(spawn);
				spawnsToRemove.add(spawn);
			}
		}

		for (int i = 0; i < spawnsToRemove.size(); ++i) {
			SpawnInfo spawn = spawnsToRemove.get(i);
			spawns.remove(spawn);
		}
		spawnsToRemove.clear();
	}

	public void spawnEnemy(SpawnInfo spawnInfo) {
		// Random position
		float x = (float) (Math.random() * Gdx.graphics.getWidth());
		float y = Gdx.graphics.getHeight();

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		Body body = level.getWorld().createBody(bodyDef);
		Enemy enemy = new Enemy(body, spawnInfo.getLifeCount());
		enemiesToSpawn.add(enemy);
	}
	
	public boolean isOver(){
		return spawns.isEmpty() && enemiesToSpawn.isEmpty();
	}

	public List<Enemy> getEnemiesToSpawn() {
		return enemiesToSpawn;
	}

	public void setEnemiesToSpawn(List<Enemy> enemiesToSpawn) {
		this.enemiesToSpawn = enemiesToSpawn;
	}

	public List<SpawnInfo> getSpawns() {
		return spawns;
	}

	public void setSpawns(List<SpawnInfo> spawns) {
		this.spawns = spawns;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
