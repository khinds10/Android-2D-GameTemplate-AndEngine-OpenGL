package com.kevinhinds.spacebotsfree.player;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.GameConfiguration.playerWeapons;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.GameConfiguration.State;
import com.kevinhinds.spacebotsfree.objects.Bridge;
import com.kevinhinds.spacebotsfree.objects.Bullet;
import com.kevinhinds.spacebotsfree.objects.Flare;
import com.kevinhinds.spacebotsfree.objects.Bomb;
import com.kevinhinds.spacebotsfree.scene.EndScene;
import com.kevinhinds.spacebotsfree.scene.GameScene;
import com.kevinhinds.spacebotsfree.scene.SceneManager;
import com.kevinhinds.spacebotsfree.status.GameStatus;

/**
 * main character for the game
 * 
 * @author khinds
 */
public class Player {

	// physics world and scene for the player
	protected PhysicsWorld physicsWorld;
	protected GameScene gameScene;

	// player sprite and current state values
	public Body playerBody;
	public AnimatedSprite playerSprite;
	public boolean isJumping;
	public boolean isFalling;
	public boolean isKneeling;
	public boolean isArcadeMode;
	protected State moving;
	public State facing;
	protected int bulletNumber = 0;
	protected int flareNumber = 0;
	public int bulletStrength = 1;

	// player start level energy and life amounts
	public int lifeAmount = GameConfiguration.playerStartingLife;
	public int energyAmount = GameConfiguration.playerStartingEnergy;

	// set player visible again handler which unregisters this TimerHandler after one iteration
	public TimerHandler setPlayerVisible = new TimerHandler(0.05f, true, new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			playerSprite.setAlpha(1);
			playerVisible();
		}
	});

	/**
	 * create player on the scene in question
	 * 
	 * @param scene
	 * @param physicsWorld
	 */
	public Player(GameScene scene, PhysicsWorld playerPhysicsworld, boolean isArcadeMode) {
		this.isArcadeMode = isArcadeMode;
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(1, 0.0f, 0.0f);
		gameScene = scene;
		playerSprite = scene.createAnimatedSprite(ResourceManager.getIntance().camera.getWidth() / 2, ResourceManager.getIntance().camera.getHeight() - 150, ResourceManager.getIntance().playerRegion, scene.vbom);
		playerBody = PhysicsFactory.createBoxBody(scene.physicsWorld, playerSprite, BodyType.DynamicBody, playerFixtureDef);
		playerBody.setUserData("player");
		gameScene.physicsWorld.registerPhysicsConnector(new PhysicsConnector(playerSprite, playerBody, true, false));
		playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerStartLevelFrame });
		moving = State.STOP;
		facing = State.RIGHT;
		isJumping = false;
		isKneeling = false;
		physicsWorld = playerPhysicsworld;
		gameScene.attachChild(playerSprite);
	}

	/**
	 * create player on the end scene
	 * 
	 * @param endScene
	 * @param physicsWorld
	 */
	public Player(EndScene endScene, PhysicsWorld physicsWorld) {
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(1, 0.0f, 0.0f);
		playerSprite = endScene.createAnimatedSprite(ResourceManager.getIntance().camera.getWidth() / 4, ResourceManager.getIntance().camera.getHeight() - 150, ResourceManager.getIntance().playerRegion, endScene.vbom);
		playerBody = PhysicsFactory.createBoxBody(endScene.physicsWorld, playerSprite, BodyType.DynamicBody, playerFixtureDef);
		playerBody.setUserData("player");
		endScene.physicsWorld.registerPhysicsConnector(new PhysicsConnector(playerSprite, playerBody, true, false));
		playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerStartLevelFrame });
		moving = State.STOP;
		facing = State.RIGHT;
		isJumping = false;
		isKneeling = false;
		endScene.attachChild(playerSprite);
	}

	/**
	 * kneel left or right based on facing state, but if already kneeling then issue player stop to stand
	 */
	public void kneel() {
		if (!isJumping) {
			if (isKneeling) {
				stop();
				isKneeling = false;
				ResourceManager.getIntance().unloadSound.play();
			} else {
				if (facing == State.RIGHT) {
					playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerKneelRightFrame });
				} else {
					playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerKneelLeftFrame });
				}
				isKneeling = true;
				ResourceManager.getIntance().loadSound.play();
			}
		}
	}

	/**
	 * player moves right w/animation
	 */
	public void moveRight() {
		moving = State.RIGHT;
		facing = State.RIGHT;
		isKneeling = false;
		if (!isJumping && !isFalling) {
			playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed }, GameConfiguration.walkRightBeginFrame, GameConfiguration.walkRightEndFrame, true);
			playerBody.setLinearVelocity(GameConfiguration.playerWalkingVelocity, 0.0f);
		} else {
			float minJumpVelocity = GameConfiguration.playerFallingChangeDirectionVelocity;
			if (moving == State.RIGHT) {
				minJumpVelocity = GameConfiguration.playerFallingContinueDirectionVelocity;
			}
			if (facing == State.RIGHT) {
				playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpRightFrame });
			} else {
				playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpLeftFrame });
			}
			playerBody.setLinearVelocity(minJumpVelocity, GameConfiguration.playerStopWhileFallingVelocity);
		}
	}

	/**
	 * player moves left w/animation
	 */
	public void moveLeft() {
		moving = State.LEFT;
		facing = State.LEFT;
		isKneeling = false;
		if (!isJumping && !isFalling) {
			playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed, GameConfiguration.playerAnimationSpeed }, GameConfiguration.walkLeftBeginFrame, GameConfiguration.walkLeftEndFrame, true);
			playerBody.setLinearVelocity(-GameConfiguration.playerWalkingVelocity, 0.0f);
		} else {
			float minJumpVelocity = -GameConfiguration.playerFallingChangeDirectionVelocity;
			if (moving == State.LEFT) {
				minJumpVelocity = -GameConfiguration.playerFallingContinueDirectionVelocity;
			}
			if (facing == State.RIGHT) {
				playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpRightFrame });
			} else {
				playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpLeftFrame });
			}
			playerBody.setLinearVelocity(minJumpVelocity, GameConfiguration.playerStopWhileFallingVelocity);
		}
	}

	/**
	 * player stops
	 */
	public void stop() {
		float jumpmotion = 0;
		if (!isJumping) {
			if (facing == State.RIGHT) {
				playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerFaceRightFrame });
			} else {
				playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerFaceLeftFrame });
			}
			playerBody.setLinearVelocity(0f, 0f);
		} else {
			if (facing == State.RIGHT) {
				jumpmotion = GameConfiguration.playerContinueMovingWhileFallingVelocity;
				playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpRightFrame });
			} else {
				jumpmotion = -GameConfiguration.playerContinueMovingWhileFallingVelocity;
				playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpLeftFrame });
			}

			playerBody.setLinearVelocity(jumpmotion, GameConfiguration.playerStopWhileFallingVelocity);
		}
		moving = State.STOP;
	}

	/**
	 * upon contact with object we must stop any current jump as well as continue as before
	 */
	public void stopJumping() {
		if (isJumping) {
			isJumping = false;
			if (moving == State.RIGHT) {
				moveRight();
			}
			if (moving == State.LEFT) {
				moveLeft();
			}
			if (moving == State.STOP) {
				stop();
			}
		}
	}

	/**
	 * player jumps
	 */
	public void jump(float jumpVelocity) {
		isKneeling = false;
		if (!isJumping && !isFalling) {
			float jumpmotion = 0;
			isJumping = true;
			if (moving == State.RIGHT) {
				jumpmotion = GameConfiguration.playerFallingVelocity;
				playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpRightFrame });
			}
			if (moving == State.LEFT) {
				jumpmotion = -GameConfiguration.playerFallingVelocity;
				playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpLeftFrame });
			}
			if (moving == State.STOP) {
				jumpmotion = 0;
				if (facing == State.RIGHT) {
					playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpRightFrame });
				}
				if (facing == State.LEFT) {
					playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpLeftFrame });
				}
			}
			final Vector2 velocity = Vector2Pool.obtain(jumpmotion, jumpVelocity);
			playerBody.setLinearVelocity(velocity);
		}
	}

	/**
	 * player falls
	 */
	public void fall() {
		isFalling = true;
		if (facing == State.RIGHT) {
			playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpRightFrame });
		}
		if (facing == State.LEFT) {
			playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerJumpLeftFrame });
		}
	}

	/**
	 * player stops falling
	 */
	public void stopFalling() {
		if (isFalling) {
			isFalling = false;
			if (moving == State.RIGHT) {
				moveRight();
			}
			if (moving == State.LEFT) {
				moveLeft();
			}
			if (moving == State.STOP) {
				stop();
			}
		}
	}

	/**
	 * player shoots and adds a bullet sprite to the level being played
	 * 
	 * @param scene
	 */
	public void shoot(GameScene scene) {

		// if player is standing like he began the level but shot gun, then issue the stop command which makes him hold his gun up
		if (playerSprite.getCurrentTileIndex() == GameConfiguration.playerStartLevelFrame) {
			stop();
		}

		// if no ammo left then empty click sound and return
		if (energyAmount <= 0) {
			energyAmount = 0;
			ResourceManager.getIntance().gunClickSound.play();
			return;
		}

		bulletNumber++;
		String bulletName = "Bullet-" + Integer.toString(bulletNumber);

		// bullet based on if player is kneeling or not
		Bullet bullet = null;

		// more powerful and expensive gun if kneeling
		int gunShotEnergyAmount = 0;
		if (!isKneeling) {
			bullet = ResourceManager.getIntance().getGameBulletById(playerWeapons.YELLOW_PHASER.ordinal());
			gunShotEnergyAmount = GameConfiguration.playerEnergyShotAmount;
			ResourceManager.getIntance().laserSound.play();
		} else {
			bullet = ResourceManager.getIntance().getGameBulletById(playerWeapons.BLUE_BULLET.ordinal());
			gunShotEnergyAmount = GameConfiguration.playerKneelingShotAmount;
			ResourceManager.getIntance().cannonSound.play();
		}

		// decrease energy because the gun was shot and update the control
		energyAmount = energyAmount - gunShotEnergyAmount;
		scene.controls.setEnergyLevelByPercent((energyAmount * 100) / GameConfiguration.playerStartingEnergy);

		// the player X,Y is the top left corner, so if facing right the bullet should start about 50px over to the right
		int moveBulletX = 0;
		bullet.direction = State.LEFT;
		if (this.facing == State.RIGHT) {
			moveBulletX = 25;
			bullet.direction = State.RIGHT;
		}

		// if they player is kneeling
		if (isKneeling) {
			gameScene.level.addBullet(bullet.getInstance(bulletName, playerSprite.getX() + moveBulletX, playerSprite.getY() + 16, gunShotEnergyAmount));
		} else {
			gameScene.level.addBullet(bullet.getInstance(bulletName, playerSprite.getX() + moveBulletX, playerSprite.getY() + 10, gunShotEnergyAmount));
		}
		gameScene.level.getBulletByName(bulletName).createBodyAndAttach(playerSprite, facing, scene, physicsWorld);

		// increment that we hit an actor for level stats
		GameStatus.incrementGameLevelStat(GameStatus.levelStatsType.SHOTS);
	}

	/**
	 * create a new flare in the level
	 */
	public void tokenAbilityFlare(Scene scene) {
		Flare flare = ResourceManager.getIntance().getGameFlareById(0);
		flareNumber++;
		String flareName = "Flare-" + Integer.toString(flareNumber);
		gameScene.level.addFlare(flare.getInstance(flareName, playerSprite.getX(), playerSprite.getY(), facing));
		gameScene.level.getFlareByName(flareName).createBodyAndAttach(playerSprite, facing, scene, physicsWorld);
	}

	/**
	 * player creates a bridge as a special ability
	 */
	public void tokenAbilityCreateBridge() {
		Bridge bridge = new Bridge("bridge tile", 0, 0, playerSprite.getX(), playerSprite.getY(), 10, 10, 10, ResourceManager.getIntance().bridgeRegion, ResourceManager.getIntance().vbom);
		bridge.createBodyAndAttach(playerSprite, facing, gameScene, physicsWorld);
	}

	/**
	 * jump higher with the token ability jump
	 */
	public void tokenAbilityJump() {
		jump((float) (GameConfiguration.playerJumpVelocity * 1.5));
	}

	/**
	 * reload from token item press
	 */
	public void reload() {
		energyAmount = energyAmount + 15;
		if (energyAmount > GameConfiguration.playerStartingEnergy) {
			energyAmount = GameConfiguration.playerStartingEnergy;
		}
		gameScene.controls.setEnergyLevelByPercent((energyAmount * 100) / GameConfiguration.playerStartingEnergy);
	}

	/**
	 * float ability acquired from a token collection
	 */
	public void tokenAbilityBomb() {
		if (facing == State.RIGHT) {
			playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerKneelRightFrame });
		} else {
			playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerKneelLeftFrame });
		}
		new Bomb(gameScene, playerSprite, facing);
	}

	/**
	 * set player visible again by unregistering timer handler
	 */
	public void playerVisible() {
		gameScene.unregisterUpdateHandler(setPlayerVisible);
	}

	/**
	 * player takes damage in the level
	 */
	public void takeDamage(int damageAmount) {
		lifeAmount = lifeAmount - damageAmount;
		ResourceManager.getIntance().hitSound.play();

		// player sprite blinks quickly when hit
		playerSprite.setAlpha(0);
		gameScene.registerUpdateHandler(setPlayerVisible);

		// player dies
		if (lifeAmount <= 0) {

			// load the arcade or regular level status scenes
			if (isArcadeMode) {
				SceneManager.getInstance().loadArcadeStatusScene("dead");
			} else {
				SceneManager.getInstance().loadLevelStatusScene("dead");
			}
		}
		gameScene.controls.setLifeMeterLevelByPercent((lifeAmount * 100) / GameConfiguration.playerStartingLife);
	}

	/**
	 * revive player life from token item press
	 */
	public void revive() {
		lifeAmount = lifeAmount + 1;
		if (lifeAmount > GameConfiguration.playerStartingLife) {
			lifeAmount = GameConfiguration.playerStartingLife;
		}
		gameScene.controls.setLifeMeterLevelByPercent((lifeAmount * 100) / GameConfiguration.playerStartingLife);
	}
}