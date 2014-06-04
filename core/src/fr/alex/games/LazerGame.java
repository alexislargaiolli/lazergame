package fr.alex.games;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import fr.alex.games.entities.Canon;
import fr.alex.games.entities.Enemy;
import fr.alex.games.entities.Lazer;
import fr.alex.games.entities.Primes;
import fr.alex.games.level.Level;

public class LazerGame extends ApplicationAdapter implements InputProcessor {

	static final float BOX_STEP = 1 / 60f;
	static final int BOX_VELOCITY_ITERATIONS = 6;
	static final int BOX_POSITION_ITERATIONS = 2;
	public static final float WORLD_TO_BOX = 0.01f;
	public static final float BOX_WORLD_TO = 100f;

	Box2DDebugRenderer debugRenderer;
	OrthographicCamera camera;

	SpriteBatch batch;
	ShapeRenderer renderer;
	List<Lazer> lazers;
	List<Lazer> deadLazers;

	List<Enemy> enemies;
	List<Enemy> deadEnemies;

	List<Primes> prismes;
	List<Primes> deadPrismes;

	Level level;
	Canon canon;

	private boolean touched;
	private float deltaTouched;
	private float drainedEnery;

	private int score;
	private int life;
	private BitmapFont bf;

	@Override
	public void create() {
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();

		GlobalSettings.width = Gdx.graphics.getWidth();
		GlobalSettings.height = Gdx.graphics.getHeight();
		GlobalSettings.viewport = new Rectangle(0, 0, GlobalSettings.width, GlobalSettings.height);

		Gdx.input.setInputProcessor(this);

		camera = new OrthographicCamera();
		camera.viewportHeight = Gdx.graphics.getHeight();
		camera.viewportWidth = Gdx.graphics.getWidth();
		camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
		camera.update();

		level = new Level();
		bf = new BitmapFont();

		debugRenderer = new Box2DDebugRenderer();
		canon = new Canon(new Vector2(GlobalSettings.width * .5f, 0));
		init();
	}

	public void init() {
		lazers = new ArrayList<Lazer>();
		deadLazers = new ArrayList<Lazer>();
		enemies = new ArrayList<Enemy>();
		deadEnemies = new ArrayList<Enemy>();
		prismes = new ArrayList<Primes>();
		deadPrismes = new ArrayList<Primes>();

		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(new Vector2(200, 300));
		Body body = level.getWorld().createBody(bodyDef);
		prismes.add(new Primes(1, body));

		life = 3;
		level.nextVague();
	}

	@Override
	public void render() {
		update(Gdx.graphics.getDeltaTime());
		draw(Gdx.graphics.getDeltaTime());
	}

	private void update(float delta) {
		level.getWorld().step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);
		level.update(delta);
		if (level.getCurrentVague() != null) {
			if (level.getCurrentVague().isOver()) {
				level.nextVague();
			} else {
				enemies.addAll(level.getCurrentVague().getEnemiesToSpawn());
				level.getCurrentVague().getEnemiesToSpawn().clear();
			}
		}
		updateLazer(delta);
		updateEnemies(delta);

		if (touched) {
			deltaTouched += delta;
			drainedEnery += delta;
			if (drainedEnery - .2f > 0) {
				canon.decreaseEnergy();
				drainedEnery = 0;
			}
		}
	}

	private void draw(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if (!level.isOver()) {
			bf.draw(batch, "Vague: " + level.getCurrentVague().getIndex(), 10, GlobalSettings.height - 10);
		} else {
			bf.draw(batch, "Vague: terminé", 10, GlobalSettings.height - 10);
		}
		bf.draw(batch, "Score: " + score, 10, GlobalSettings.height - 30);
		bf.draw(batch, "Life: " + life, 10, GlobalSettings.height - 50);
		bf.draw(batch, "Energy: " + canon.getEnergy() + "%", 10, GlobalSettings.height - 70);
		bf.draw(batch, "Bodies count: " + level.getWorld().getContactCount(), 10, GlobalSettings.height - 90);
		bf.draw(batch, "Enemies count: " + enemies.size(), 10, GlobalSettings.height - 110);

		batch.end();
		renderer.begin(ShapeType.Line);
		for (int i = 0; i < lazers.size(); ++i) {
			Lazer lazer = lazers.get(i);
			renderer.line(lazer.getQueue().x - lazer.getStrength(), lazer.getQueue().y, lazer.getHead().x - lazer.getStrength(), lazer.getHead().y);
			renderer.line(lazer.getQueue().x + lazer.getStrength(), lazer.getQueue().y, lazer.getHead().x + lazer.getStrength(), lazer.getHead().y);
		}
		renderer.end();

		renderer.begin(ShapeType.Filled);
		for (int j = 0; j < prismes.size(); ++j) {
			Primes p = prismes.get(j);
			renderer.circle(p.getCenter().x, p.getCenter().y, 5f);
		}
		for (int i = 0; i < enemies.size(); ++i) {
			Enemy enemy = enemies.get(i);
			renderer.circle(enemy.x(), enemy.y(), enemy.getRadius());
		}
		renderer.end();

		debugRenderer.render(level.getWorld(), camera.combined);
	}

	public void updateLazer(float delta) {
		for (int i = 0; i < lazers.size(); ++i) {
			Lazer lazer = lazers.get(i);
			lazer.update(delta);
			if (isLazerOutOfBound(lazer)) {
				lazer.setDead(true);
			}
			if (lazer.isDead()) {
				deadLazers.add(lazer);
			} else {
				if (!lazer.isDisable()) {
					for (int j = 0; j < enemies.size(); ++j) {
						Enemy enemy = enemies.get(j);
						if (!enemy.isDead() && enemy.hit(lazer)) {
							shootEnemy(lazer, enemy);
						}
					}
					for (int j = 0; j < prismes.size(); ++j) {
						Primes prisme = prismes.get(j);
						if (lazer.getFromId() != prisme.getPrismeId() && prisme.hit(lazer)) {
							shootPrisme(lazer, prisme);
						}
					}
				}
			}
		}
		for (int i = 0; i < deadLazers.size(); ++i) {
			Gdx.app.log("lazer destroy", "");
			lazers.remove(deadLazers.get(i));
		}
		deadLazers.clear();
	}

	public void updateEnemies(float delta) {
		for (int i = 0; i < enemies.size(); ++i) {
			Enemy enemy = enemies.get(i);
			if (enemy.getBody().getPosition().y < 40) {
				looseEnemy(enemy);
			}
		}
		for (int i = 0; i < deadEnemies.size(); ++i) {
			Enemy enemy = deadEnemies.get(i);
			destroyEnemy(enemy);
		}
		deadEnemies.clear();
	}

	/**
	 * 
	 * @param enemy
	 */
	private void destroyEnemy(Enemy enemy) {
		level.getWorld().destroyBody(enemy.getBody());
		enemies.remove(enemy);
	}

	/**
	 * Handle collision between a lazer and an enemy
	 * 
	 * @param lazer
	 * @param enemy
	 */
	private void shootEnemy(Lazer lazer, Enemy enemy) {
		enemy.looseLife();
		lazer.decreaseStrength();
		if (lazer.getStrength() == 1) {			
			lazer.stop();
		}
		if (enemy.isDead()) {
			deadEnemies.add(enemy);
			score++;
			exploseEnemy(enemy);
		}
	}

	/**
	 * Handle explosion of a enemy
	 * 
	 * @param enemy
	 */
	private void exploseEnemy(Enemy enemy) {
		for (int k = 0; k < enemies.size(); ++k) {
			Enemy cur = enemies.get(k);
			if (!cur.isDead()) {
				float dst = cur.getBody().getPosition().dst(enemy.getBody().getPosition());
				if (dst < 100) {
					Vector2 force = new Vector2(cur.getBody().getPosition()).sub(enemy.getBody().getPosition()).nor().scl(500000 * dst);
					Gdx.app.log("force applied", force.toString());
					cur.getBody().applyLinearImpulse(force, cur.getBody().getWorldCenter(), true);
				}
			}
		}
	}

	private void looseEnemy(Enemy enemy) {
		deadEnemies.add(enemy);
		life--;
	}

	private void createEnemy(float x, float y) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		Body body = level.getWorld().createBody(bodyDef);
		Enemy enemy = new Enemy(body, (int) (1 + Math.round(Math.random() * 3)));
		enemies.add(enemy);
	}

	private void shootPrisme(Lazer lazer, Primes prisme) {
		lazer.stop();
		lazer.setFromId(prisme.getPrismeId());
		for (int i = 0; i < prisme.getSpawnPoints().length; ++i) {			
			Lazer l = new Lazer(prisme.getCenter(), prisme.getSpawnPoints()[i], lazer.getSpeed(), lazer.getLength(), lazer.getStrength());
			l.setFromId(prisme.getPrismeId());
			lazers.add(l);
		}
		Gdx.app.log("prisme", "");
	}

	public boolean isLazerOutOfBound(Lazer lazer) {
		return !GlobalSettings.viewport.contains(lazer.getHead()) && !GlobalSettings.viewport.contains(lazer.getQueue());
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touched = true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector2 lazerDest = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
		float lazerSpeed = canon.clickTimeToLazerSpeed(deltaTouched);
		int lazerStrength = canon.clickTimeToLazerStrength(deltaTouched);
		lazers.addAll(canon.fire(lazerDest, lazerSpeed, lazerStrength));
		touched = false;
		deltaTouched = 0;
		drainedEnery = 0;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (Keys.SPACE == keycode) {
			float x = (float) (Math.random() * Gdx.graphics.getWidth());
			float y = Gdx.graphics.getHeight();
			createEnemy(x, y);
		}
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {

		return false;
	}

	@Override
	public boolean keyTyped(char character) {

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		return false;
	}

	@Override
	public boolean scrolled(int amount) {

		return false;
	}

}
