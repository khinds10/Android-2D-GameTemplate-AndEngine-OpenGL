package com.kevinhinds.spacebotsfree.scene;

import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.util.color.Color;

import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.level.Collision;
import com.kevinhinds.spacebotsfree.level.Level;
import com.kevinhinds.spacebotsfree.level.LevelXMLBuilder;
import com.kevinhinds.spacebotsfree.objects.Actor;
import com.kevinhinds.spacebotsfree.objects.Explosion;
import com.kevinhinds.spacebotsfree.player.Controls;
import com.kevinhinds.spacebotsfree.player.Player;
import com.kevinhinds.spacebotsfree.status.GameStatus;
import com.kevinhinds.spacebotsfree.status.StatusListManager;

/**
 * main game scene which contains multiple levels
 * 
 * @author khinds
 */
public class GameScene extends BaseScene {

	public Player player;
	public Body fixtureBody;
	public Level level;
	public LevelXMLBuilder levelXMLBuilder;
	public Controls controls;
	public String currentPiecesObtained;

	/**
	 * construct the game scene with the level XML builder
	 */
	public GameScene() {
		levelXMLBuilder = new LevelXMLBuilder(activity.getAssets());
	}

	@Override
	public void createScene() {

		setBackground();
		createHUD();
		createPhysics();
		addPlayer();
		createControls();
		createLevel(this.levelNumber);
		level = levelXMLBuilder.level;
		currentPiecesObtained = "";

		// every 1 seconds update scene timer
		this.registerUpdateHandler(new TimerHandler(1, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {

				// update current level time and accuracy stats
				controls.updateLevelStats();

				// for all current actors apply move command if they're not dead
				for (Actor a : level.actors) {
					if (a.actorBody != null) {
						if (!a.actorBody.getUserData().equals("deceased")) {
							a.move();
						}
					}
				}
			}
		}));
	}

	@Override
	public void onBackPressed() {
		SceneManager.getInstance().loadLevelSelectScene();
	}

	/**
	 * set the background for the gamescene
	 */
	protected void setBackground() {
		setBackground(new Background(Color.BLACK));
	}

	/**
	 * create the heads up display (game controls) that move with the player through the level
	 */
	protected void createHUD() {
		gameHUD = new HUD();
		camera.setHUD(gameHUD);
	}

	/**
	 * apply physics to the gamescene we'll use the new improved MaxStepPhysicsWorld which extends the regular PhysicsWorld
	 */
	protected void createPhysics() {
		physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}

	/**
	 * add new player to this scene
	 */
	protected void addPlayer() {
		player = new Player(this, physicsWorld, false);
	}

	/**
	 * create controls for the game for the player's character
	 */
	protected void createControls() {
		controls = new Controls(this, player, false);
	}

	/**
	 * create level for this game scene
	 * 
	 * @param levelNumber
	 */
	protected void createLevel(int levelNumber) {

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.0f, 0.0f);
		final Rectangle roof = new Rectangle(0, GameConfiguration.offscreenJumpHeight, camera.getWidth(), 2, vbom);
		roof.setZIndex(-2);
		roof.setColor(Color.BLACK);

		final Rectangle ground = new Rectangle(0, camera.getHeight() - GameConfiguration.buttonControlsHeight, camera.getWidth(), 2, vbom);
		ground.setZIndex(-2);
		ground.setColor(Color.BLACK);

		final Rectangle left = new Rectangle(0, GameConfiguration.offscreenJumpHeight, 2, camera.getHeight() * 2, vbom);
		left.setZIndex(-2);
		left.setColor(Color.BLACK);

		final Rectangle right = new Rectangle(camera.getWidth() - 2, 0, 2, camera.getHeight() * 2, vbom);
		right.setZIndex(-2);
		right.setColor(Color.BLACK);

		fixtureBody = PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		fixtureBody.setUserData("ground");
		attachChild(ground);

		fixtureBody = PhysicsFactory.createBoxBody(physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		fixtureBody.setUserData("roof");
		attachChild(roof);

		fixtureBody = PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		fixtureBody.setUserData("leftWall");
		attachChild(left);

		fixtureBody = PhysicsFactory.createBoxBody(physicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		fixtureBody.setUserData("rightWall");
		attachChild(right);

		// load all levels from XML
		levelXMLBuilder.createLevelFromXML(levelNumber);
		levelXMLBuilder.loadLevel(GameScene.this, physicsWorld);
	}

	/**
	 * contact listener for the game scene
	 * 
	 * @return
	 */
	protected ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {

			// track begin of contact between fixtures
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				Log.i("beginContact", x1.getBody().getUserData() + " - " + x2.getBody().getUserData());

				// process game scene collision
				if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null) {
					new Collision((String) x1.getBody().getUserData(), (String) x2.getBody().getUserData(), level, GameScene.this);
				}
			}

			/**
			 * track end of contact between fixtures
			 */
			public void endContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				Log.i("endContact", x1.getBody().getUserData() + " - " + x2.getBody().getUserData());

				String x1BodyName = (String) x1.getBody().getUserData();
				String x2BodyName = (String) x2.getBody().getUserData();

				// player begins to fall when loses contact with a bounce tile (edges of platforms)
				if (x1BodyName != null && x2BodyName != null) {
					if (x1BodyName.contains("player")) {
						if (x2BodyName.contains("bounce")) {
							player.fall();
						}
					}
				}
			}

			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		};
		return contactListener;
	}

	/**
	 * from the current list of pieces needed to complete a level, if the player has all the pieces then the level is complete!
	 */
	public void checkLevelComplete() {

		// if you've collected all the pieces locally in this level to complete, then it's finished
		int thisLevelID = SceneManager.getInstance().getCurrentScene().levelNumber;
		String[] pieceslist = GameConfiguration.levelPieces[thisLevelID].split(",");
		boolean levelCompleted = true;
		for (String piece : pieceslist) {
			if (!StatusListManager.containsValue(currentPiecesObtained, piece)) {
				levelCompleted = false;
			}
		}

		// level is complete, set next level to status = 1 (playable) and the current level status as 2, 3 or 4 based on player performance
		if (levelCompleted) {

			// initially set the level to "completed" or 2 and the next one as 1 or "playable", the level status scene calculates if they've performed at a 3 or 4 status for the level
			GameStatus.setLevelStatusByLevelNumber(thisLevelID, "2");
			GameStatus.setLevelStatusByLevelNumber(++thisLevelID, "1");
			SceneManager.getInstance().loadLevelStatusScene("success");
		}
	}

	/**
	 * random explosion sound from currently available sounds
	 */
	public void randomExplosionSound() {
		Random random = new Random();
		int explosion = random.nextInt(3) + 1;
		if (explosion == 1) {
			ResourceManager.getIntance().explosion1Sound.play();
		}
		if (explosion == 2) {
			ResourceManager.getIntance().explosion2Sound.play();
		}
		if (explosion == 3) {
			ResourceManager.getIntance().explosion3Sound.play();
		}
	}

	/**
	 * random tile breaking sound from currently available sounds
	 */
	public void randomTileBreakingSound() {
		Random random = new Random();
		int explosion = random.nextInt(3) + 1;
		if (explosion == 1) {
			ResourceManager.getIntance().break1Sound.play();
		}
		if (explosion == 2) {
			ResourceManager.getIntance().break2Sound.play();
		}
		if (explosion == 3) {
			ResourceManager.getIntance().break3Sound.play();
		}
	}

	/**
	 * for the exploding sprite in question create a random explosion for it (mushroom cloud shape)
	 * 
	 * @param explodingSprite
	 */
	public void randomGameMushroomExplosion(Sprite explodingSprite) {
		Random random = new Random();
		int explosion = random.nextInt(4);
		new Explosion(GameScene.this, explodingSprite, explosion);
	}

	/**
	 * for the exploding sprite in question create a random explosion for it (round cloud shape)
	 * 
	 * @param explodingSprite
	 */
	public void randomGameRoundExplosion(Sprite explodingSprite) {
		Random random = new Random();
		int explosion = random.nextInt(11) + 5;
		new Explosion(GameScene.this, explodingSprite, explosion);
	}
}