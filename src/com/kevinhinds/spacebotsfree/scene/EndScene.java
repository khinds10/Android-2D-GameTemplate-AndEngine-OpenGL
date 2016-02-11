package com.kevinhinds.spacebotsfree.scene;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.util.color.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.player.Player;

import android.hardware.SensorManager;
import android.util.Log;

/**
 * main menu scene for the game
 * 
 * @author khinds
 */
public class EndScene extends BaseScene {

	private Player player;
	private Body fixtureBody;
	private Sprite ship;
	private Body shipBody;

	private int halfSecondsPast = 0;

	private boolean playerInShip = false;
	private boolean engineStarted = false;
	private boolean planetShown = false;

	private long[] flameAnimationSpeed = new long[] { GameConfiguration.flameAnimationSpeed, GameConfiguration.flameAnimationSpeed, GameConfiguration.flameAnimationSpeed, GameConfiguration.flameAnimationSpeed, GameConfiguration.flameAnimationSpeed, GameConfiguration.flameAnimationSpeed, GameConfiguration.flameAnimationSpeed, GameConfiguration.flameAnimationSpeed, GameConfiguration.flameAnimationSpeed };
	private AnimatedSprite flame;
	private Sprite planet;
	private float currentPlanetScale = 10f;
	private Sprite spriteBG;

	// credits to show in order
	private String developed = "DEVELOPED BY\n\t\t\t\tKEVIN HINDS at BITSTREET APPS\n";
	private String artwork = "ARTWORK\n\t\t\t\tM484GAMES & OPENGAMEART.ORG\n\nSHIP ARTWORK\n\t\t\tSKORPIO\n";
	private String music = "MUSIC\n\t\t\t\tWZZARD\n";
	private String end = "THE END...";
	private Text developedCredits = null;
	private Text artworkCredits = null;
	private Text musicCredits = null;
	private Text endCredits = null;

	@Override
	public void createScene() {

		// every 1 seconds update scene timer
		this.registerUpdateHandler(new TimerHandler(0.5f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if (!playerInShip) {
					player.moveRight();
				} else {
					player.stop();
					player.playerSprite.setAlpha(0);
					startEngine();
				}

				// keep track of half seconds past to do further animations
				halfSecondsPast = halfSecondsPast + 1;

				// fade out ship animation
				if (halfSecondsPast > 7) {
					ship.setAlpha(0.5f);
					flame.setAlpha(0.5f);
				}
				if (halfSecondsPast > 8) {
					ship.setAlpha(0.3f);
					flame.setAlpha(0.3f);
				}
				if (halfSecondsPast > 9) {
					ship.setAlpha(0.2f);
					flame.setAlpha(0.2f);
				}
				if (halfSecondsPast > 10) {
					ship.setAlpha(0.0f);
					flame.setAlpha(0.0f);
				}

				// show and scale planet
				if (halfSecondsPast > 11) {
					showPlanet();
				}
				if (halfSecondsPast > 12) {
					setPlanetScale(halfSecondsPast);
				}

				// start setting credits to appear
				if (halfSecondsPast > 26) {
					setCredits(halfSecondsPast);
				}
			}
		}));

		 physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);

		// put player on the end scene
		player = new Player(this, physicsWorld);

		// create ground for player to walk on
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.0f, 0.0f);
		final Rectangle ground = new Rectangle(0, camera.getHeight() - GameConfiguration.buttonControlsHeight, camera.getWidth(), 2, vbom);
		ground.setZIndex(-2);
		ground.setColor(Color.BLACK);
		fixtureBody = PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		fixtureBody.setUserData("ground");
		attachChild(ground);

		// create ship
		final FixtureDef shipFixtureDef = PhysicsFactory.createFixtureDef(1000, 0.0f, 1000);
		ship = new Sprite(400, 100, ResourceManager.getIntance().completeShipRegion, this.vbom);
		shipBody = PhysicsFactory.createBoxBody(physicsWorld, ship, BodyType.DynamicBody, shipFixtureDef);
		shipBody.setUserData("ship");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(ship, shipBody, true, false));
		attachChild(ship);
	}

	/**
	 * start ship engine
	 */
	private void startEngine() {
		if (!engineStarted) {
			flame = this.createAnimatedSprite(440, 365, ResourceManager.getIntance().flameRegion, vbom);
			flame.animate(flameAnimationSpeed, 7, 15, true);
			attachChild(flame);
		}
		engineStarted = true;
	}

	/**
	 * show the planet
	 */
	private void showPlanet() {
		if (!planetShown) {

			// show the planet background region
			spriteBG = new Sprite(0, 0, ResourceManager.getIntance().voidBackgroundRegion, ResourceManager.getIntance().vbom);
			attachChild(spriteBG);

			// show the planet
			planet = new Sprite(400, 100, ResourceManager.getIntance().planetRegion, this.vbom);
			attachChild(planet);
			planet.setScale(currentPlanetScale);
		}
		planetShown = true;
	}

	/**
	 * set planet scale zoom out to zero size
	 * 
	 * @param halfSecondsPast
	 */
	private void setPlanetScale(int halfSecondsPast) {
		this.registerUpdateHandler(new TimerHandler(0.01f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				currentPlanetScale = (float) (currentPlanetScale - 0.0017);
				if (currentPlanetScale > 0) {
					planet.setScale(currentPlanetScale);
				}
			}
		}));
	}

	/**
	 * set current credits displayed on end scene
	 * 
	 * @param halfSecondsPast
	 */
	private void setCredits(int halfSecondsPast) {

		if (developedCredits == null) {

			developedCredits = new Text(100, 100, ResourceManager.getIntance().gameFontMedium, developed, ResourceManager.getIntance().vbom);
			this.attachChild(developedCredits);

			artworkCredits = new Text(100, 100, ResourceManager.getIntance().gameFontMedium, artwork, ResourceManager.getIntance().vbom);
			this.attachChild(artworkCredits);

			musicCredits = new Text(100, 100, ResourceManager.getIntance().gameFontMedium, music, ResourceManager.getIntance().vbom);
			this.attachChild(musicCredits);

			endCredits = new Text(100, 100, ResourceManager.getIntance().gameFontMedium, end, ResourceManager.getIntance().vbom);
			this.attachChild(endCredits);

			developedCredits.setVisible(false);
			artworkCredits.setVisible(false);
			musicCredits.setVisible(false);
			endCredits.setVisible(false);

		} else {

			if (halfSecondsPast > 26) {
				developedCredits.setVisible(true);
				artworkCredits.setVisible(false);
				musicCredits.setVisible(false);
				endCredits.setVisible(false);
			}

			if (halfSecondsPast > 36) {
				developedCredits.setVisible(false);
				artworkCredits.setVisible(true);
				musicCredits.setVisible(false);
				endCredits.setVisible(false);
			}

			if (halfSecondsPast > 46) {
				developedCredits.setVisible(false);
				artworkCredits.setVisible(false);
				musicCredits.setVisible(true);
				endCredits.setVisible(false);
			}

			if (halfSecondsPast > 56) {
				developedCredits.setVisible(false);
				artworkCredits.setVisible(false);
				musicCredits.setVisible(false);
				endCredits.setVisible(true);
			}
		}
	}

	/**
	 * contact listener for the game scene
	 * 
	 * @return
	 */
	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {

			// track begin of contact between fixtures
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				Log.e("beginContact", x1.getBody().getUserData() + " - " + x2.getBody().getUserData());

				// process game scene collision
				if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null) {

					String x1BodyName = (String) x1.getBody().getUserData();
					String x2BodyName = (String) x2.getBody().getUserData();

					if (x1BodyName.contains("player")) {
						if (x2BodyName.contains("ship")) {
							playerInShip = true;
						}
					}
				}
			}

			public void endContact(Contact contact) {

			}

			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		};
		return contactListener;
	}

	@Override
	public void onBackPressed() {
		SceneManager.getInstance().returnToMenuScene();
	}
}