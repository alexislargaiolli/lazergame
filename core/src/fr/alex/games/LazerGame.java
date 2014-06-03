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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import fr.alex.games.entities.Enemy;
import fr.alex.games.entities.Lazer;

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

	Level level;

	Vector2 origin;

	private float height, width;

	private boolean touched;
	private float deltaTouched;

	private int score;
	private int life;
	private BitmapFont bf;

	@Override
	public void create() {
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();

		height = Gdx.graphics.getHeight();
		width = Gdx.graphics.getWidth();

		origin = new Vector2(width * .5f, 0);
		Gdx.input.setInputProcessor(this);

		camera = new OrthographicCamera();
		camera.viewportHeight = Gdx.graphics.getHeight();
		camera.viewportWidth = Gdx.graphics.getWidth();
		camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
		camera.update();

		level = new Level();
		bf = new BitmapFont();

		debugRenderer = new Box2DDebugRenderer();
		init();
	}

	public void init() {
		lazers = new ArrayList<Lazer>();
		deadLazers = new ArrayList<Lazer>();
		enemies = new ArrayList<Enemy>();
		deadEnemies = new ArrayList<Enemy>();
		life = 3;
	}

	@Override
	public void render() {
		update(Gdx.graphics.getDeltaTime());
		draw(Gdx.graphics.getDeltaTime());
	}

	private void update(float delta) {
		level.getWorld().step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);
		updateLazer(delta);
		updateEnemies(delta);

		if (touched) {
			deltaTouched += delta;
		}
	}

	private void draw(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		bf.draw(batch, "Score: " + score, 10, height - 10);
		bf.draw(batch, "Life: " + life, 10, height - 30);
		bf.draw(batch, "Bodies count: " + level.getWorld().getContactCount(), 10, height - 50);
		bf.draw(batch, "Enemies count: " + enemies.size(), 10, height - 70);
		
		batch.end();
		renderer.begin(ShapeType.Line);
		for (int i = 0; i < lazers.size(); ++i) {
			Lazer lazer = lazers.get(i);
			renderer.line(lazer.getQueue().x - lazer.getWidth() * .5f, lazer.getQueue().y, lazer.getHead().x - lazer.getWidth() * .5f, lazer.getHead().y);
			renderer.line(lazer.getQueue().x + lazer.getWidth() * .5f, lazer.getQueue().y, lazer.getHead().x + lazer.getWidth() * .5f, lazer.getHead().y);
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
			for (int j = 0; j < enemies.size(); ++j) {
				Enemy enemy = enemies.get(j);
				if (!enemy.isDead() && enemy.hit(lazer)) {
					shootEnemy(enemy);
					for (int k = 0; k < enemies.size(); ++k) {
						Enemy cur = enemies.get(k);
						float dst = cur.getBody().getPosition().dst(enemy.getBody().getPosition());
						if (dst < 100) {

							Vector2 force = new Vector2(cur.getBody().getPosition()).sub(enemy.getBody().getPosition()).nor().scl(500000 * dst / 100);

							Gdx.app.log("force applied", force.toString());

							cur.getBody().applyLinearImpulse(force, cur.getBody().getWorldCenter(), true);

						}
					}
				}
			}
			if (lazer.isDead()) {
				deadLazers.add(lazer);
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

	private void destroyEnemy(Enemy enemy) {
		level.getWorld().destroyBody(enemy.getBody());		
		enemies.remove(enemy);
	}

	private void shootEnemy(Enemy enemy) {
		enemy.looseLife();
		if (enemy.isDead()) {
			deadEnemies.add(enemy);
			score++;
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

	public void spawnLazer(Vector2 dest, float speed) {

		Vector2 intersection = new Vector2();
		Intersector.intersectLines(origin.x, origin.y, dest.x, dest.y, 0, 0, 0, height, intersection);
		if (intersection.y < 0 || intersection.y > height) {
			Intersector.intersectLines(origin.x, origin.y, dest.x, dest.y, width, 0, width, height, intersection);
			if (intersection.y < 0 || intersection.y > height) {
				Intersector.intersectLines(origin.x, origin.y, dest.x, dest.y, 0, height, width, height, intersection);
			}
		}
		int lazerWidth = Math.round(1 + deltaTouched * 1.5f);
		Lazer lazer = new Lazer(origin, intersection, speed, 100, lazerWidth);
		Gdx.app.log("spawnLazer()", lazer.toString());
		lazers.add(lazer);
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
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
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touched = true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		spawnLazer(new Vector2(screenX, Gdx.graphics.getHeight() - screenY), 500f);
		touched = false;
		deltaTouched = 0;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
