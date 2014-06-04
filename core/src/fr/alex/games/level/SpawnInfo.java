package fr.alex.games.level;

public class SpawnInfo {
	float timeSpawn;
	int lifeCount;

	public SpawnInfo(float timeSpan, int lifeCount) {
		super();
		this.timeSpawn = timeSpan;
		this.lifeCount = lifeCount;
	}

	public float getTimeSpawn() {
		return timeSpawn;
	}

	public void setTimeSpawn(float timeSpawn) {
		this.timeSpawn = timeSpawn;
	}

	public int getLifeCount() {
		return lifeCount;
	}

	public void setLifeCount(int lifeCount) {
		this.lifeCount = lifeCount;
	}
}
