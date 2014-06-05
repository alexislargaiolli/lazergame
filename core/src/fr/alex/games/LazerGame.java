package fr.alex.games;

import java.util.ArrayList;
import java.util.List;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
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
	public static RayHandler rayHandler;

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
	private Texture bg;
	private Texture ast;
	private Texture prisme;
	private Texture lazerText;

	private int score;
	private int life;
	private BitmapFont bf;

	@Override
	public void create() {
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
		bg = new Texture(Gdx.files.internal("bg.png"));
		ast = new Texture(Gdx.files.internal("ast1.png"));
		prisme = new Texture(Gdx.files.internal("prisme.png"));
		lazerText = new Texture(Gdx.files.internal("lazer.png"));

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

		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		rayHandler = new RayHandler(level.getWorld());
		rayHandler.setAmbientLight(.5f, .5f, .5f, .5f);
		rayHandler.setCulling(false);
		rayHandler.setBlur(true);
		rayHandler.setBlurNum(1);

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

		Light light = new PointLight(rayHandler, 32);
		light.setDistance(16f);
		light.attachToBody(body, 0, 0.5f);
		light.setColor(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);

		prismes.add(new Primes(1, body));
		new PointLight(rayHandler, 32);

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
		rayHandler.update();
		level.update(delta);

		if (level.getCurrentVague() != null) {
			if (level.getCurrentVague().isOver()) {
				level.nextVague();
			} else {
				for (Enemy e : level.getCurrentVague().getEnemiesToSpawn()) {
					Light light = new PointLight(rayHandler, 32);
					light.setDistance(100f);
					light.attachToBody(e.getBody(), 0, 0);
					light.setColor(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);
					enemies.add(e);
				}
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

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bg, 0, 0, GlobalSettings.width, GlobalSettings.height);

		// renderer.begin(ShapeType.Filled);
		// batch.begin();
		for (int j = 0; j < prismes.size(); ++j) {
			Primes p = prismes.get(j);
			batch.draw(prisme, p.getCenter().x, p.getCenter().y, 40, 30, 0, 0, 40, 30, false, false);
			/*
			 * renderer.circle(p.getCenter().x, p.getCenter().y, 5f);
			 * renderer.circle(p.getVertices()[0], p.getVertices()[1], 5f);
			 * renderer.circle(p.getVertices()[2], p.getVertices()[3], 5f);
			 * renderer.circle(p.getVertices()[4], p.getVertices()[5], 5f);
			 */
		}

		for (int i = 0; i < enemies.size(); ++i) {
			Enemy enemy = enemies.get(i);
			float x = enemy.getBody().getPosition().x;
			float y = enemy.getBody().getPosition().y;
			float width = enemy.getRadius() * 2f;
			// batch.draw(ast, x, y, width, width, 0, 0, 130, 122, false,
			// false);
			batch.draw(ast, x - width * .5f, y - width * .5f, width * .5f, width * .5f, width, width, 1, 1, (float) Math.toDegrees(enemy.getBody().getAngle()), 0, 0, 130, 122, false, false);
			// renderer.circle(enemy.x(), enemy.y(), enemy.getRadius());
		}
		
		for (int i = 0; i < lazers.size(); ++i) {
			Lazer lazer = lazers.get(i);
			//renderer.line(lazer.getQueue().x - lazer.getStrength(), lazer.getQueue().y, lazer.getHead().x - lazer.getStrength(), lazer.getHead().y);
			//renderer.line(lazer.getQueue().x + lazer.getStrength(), lazer.getQueue().y, lazer.getHead().x + lazer.getStrength(), lazer.getHead().y);
		
			batch.draw(lazerText, lazer.getQueue().x, lazer.getQueue().y, 0, 0, 10, lazer.getCurrentLength(), 1, 1, 90+lazer.getDirection().angle(), 0, 0, 20, 20, false, false);
		}
		// renderer.end();
		batch.end();

		rayHandler.setCombinedMatrix(camera.combined);
		rayHandler.render();
		debugRenderer.render(level.getWorld(), camera.combined);

		/*renderer.begin(ShapeType.Line);
		for (int i = 0; i < lazers.size(); ++i) {
			Lazer lazer = lazers.get(i);
			renderer.line(lazer.getQueue().x - lazer.getStrength(), lazer.getQueue().y, lazer.getHead().x - lazer.getStrength(), lazer.getHead().y);
			renderer.line(lazer.getQueue().x + lazer.getStrength(), lazer.getQueue().y, lazer.getHead().x + lazer.getStrength(), lazer.getHead().y);
		}
		renderer.end();*/

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
					// cur.getBody().applyLinearImpulse(force,
					// cur.getBody().getWorldCenter(), true);
					// cur.getBody().applyForceToCenter(force, true);

					cur.getBody().applyForce(force, new Vector2(cur.getBody().getWorldCenter()).sub(10, 10), true);
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
		lazers.addAll(prisme.handleHit(lazer));
		/*
		 * for (int i = 0; i < prisme.getSpawnPoints().length; ++i) { Lazer l =
		 * new Lazer(prisme.getCenter(), prisme.getSpawnPoints()[i],
		 * lazer.getSpeed(), lazer.getLength(), lazer.getStrength());
		 * l.setFromId(prisme.getPrismeId()); lazers.add(l); }
		 */
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
		Vector2 dir = new Vector2(lazerDest).sub(canon.getLazerOrigin());
		//Light light = new ConeLight(rayHandler, 16, new Color(1f, 0, 0, 1f), 1, canon.getLazerOrigin().x, canon.getLazerOrigin().y, dir.angle(), 1);
		
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
