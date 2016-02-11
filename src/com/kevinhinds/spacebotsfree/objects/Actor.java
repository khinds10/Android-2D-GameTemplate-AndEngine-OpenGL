package com.kevinhinds.spacebotsfree.objects;

import java.util.Random;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.player.Player;
import com.kevinhinds.spacebotsfree.scene.BaseScene;
import com.kevinhinds.spacebotsfree.scene.GameScene;
import com.kevinhinds.spacebotsfree.status.GameStatus;

/**
 * a tile sprite extends sprite to easily define and attach to any scene
 * 
 * @author khinds
 */
public class Actor extends AnimatedSprite {

	protected String facing, name, type, weapon;
	protected int id, life, spriteRow, animationSpeed, explosionType;
	protected float density, elastic, friction, x, y, movementSpeed;
	protected boolean shoots;
	public Body actorBody;
	public Scene scene;

	/**
	 * create new adversary from a tiledSprite with a specific tile index specified as well as if it's a physical tile a background or foreground tile
	 * 
	 * @param name
	 * @param id
	 * @param tileIndex
	 * @param type
	 * @param life
	 * @param weapon
	 * @param facing
	 * @param shoots
	 * @param x
	 * @param y
	 * @param density
	 * @param elastic
	 * @param friction
	 * @param texture
	 * @param vbom
	 * @param animationSpeed
	 * @param movementSpeed
	 * @param explosionType
	 */
	public Actor(String name, int id, int spriteRow, String type, int life, String weapon, String facing, Boolean shoots, int animationSpeed, float movementSpeed, int explosionType, float x, float y, float density, float elastic, float friction, ITiledTextureRegion texture, VertexBufferObjectManager vbom) {
		super(x, y, texture, vbom);
		this.x = x;
		this.y = y;
		this.name = name;
		this.id = id;
		this.spriteRow = spriteRow;
		this.density = density;
		this.elastic = elastic;
		this.friction = friction;
		this.type = type;
		this.weapon = weapon;
		this.facing = facing;
		this.life = life;
		this.shoots = shoots;
		this.animationSpeed = animationSpeed;
		this.movementSpeed = movementSpeed;
		this.explosionType = explosionType;
		Log.i(this.getName(), "Life: " + Integer.toString(this.life));
	}

	/**
	 * attach this current tile to the scene in question with the tile index applied
	 * 
	 * @param scene
	 * @param physicsWorld
	 */
	public void createBodyAndAttach(Scene scene, PhysicsWorld physicsWorld) {

		this.scene = scene;
		final FixtureDef tileFixtureDef = PhysicsFactory.createFixtureDef(density, elastic, friction);
		tileFixtureDef.restitution = 0;

		// get the villian on the sprite row specified, if the sprite is facing left, then the tile is higher on the same tile row to face the other way
		this.spriteRow = (GameConfiguration.actorMapColumns * this.spriteRow) - (GameConfiguration.actorMapColumns);

		// create the start animation frame based on which way the player is facing
		int animationFrameStart = this.spriteRow;
		if (this.facing.equals("right")) {
			animationFrameStart = animationFrameStart + 5;
			movementSpeed = -movementSpeed;
		}
		this.setCurrentTileIndex(animationFrameStart);

		// different behavior based on type
		if (this.type.equals("standing")) {
			actorBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.StaticBody, tileFixtureDef);
		} else if (this.type.equals("flying")) {
			actorBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, tileFixtureDef);
			actorBody.setLinearVelocity(movementSpeed, 0.0f);
			actorBody.setGravityScale(0);
		} else {
			actorBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, tileFixtureDef);
			actorBody.setLinearVelocity(movementSpeed, 0.0f);
		}

		// name the actor and hook it in to the physics world
		actorBody.setUserData(this.name);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, actorBody, true, false));

		// the actor animates as it comes to life
		scene.attachChild(this);
		final Vector2 velocity = Vector2Pool.obtain(movementSpeed, 0);
		actorBody.setLinearVelocity(velocity);

		this.animate(new long[] { animationSpeed, animationSpeed, animationSpeed }, animationFrameStart, animationFrameStart + 2, true);
	}

	/**
	 * keep the actors moving
	 */
	public void move() {
		if (!this.type.equals("standing")) {

			final Vector2 stopVelocity = Vector2Pool.obtain(0, 0);
			actorBody.setLinearVelocity(stopVelocity);

			final Vector2 moveVelocity = Vector2Pool.obtain(movementSpeed, 0);
			actorBody.setLinearVelocity(moveVelocity);
		}
	}

	/**
	 * actors change direction on contact with other objects
	 */
	public void changeDirection() {

		if (!this.type.equals("standing")) {

			// change facing direction and animation tiles to animate through
			int animationFrameStart = this.spriteRow;
			if (this.facing.equals("right")) {
				this.facing = "left";
			} else {
				this.facing = "right";
				animationFrameStart = animationFrameStart + 5;
			}
			this.setCurrentTileIndex(animationFrameStart);
			this.animate(new long[] { animationSpeed, animationSpeed, animationSpeed }, animationFrameStart, animationFrameStart + 2, true);

			movementSpeed = -movementSpeed;
			move();
		}
	}

	/**
	 * get name of this actor
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * get ID of this actor
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * actor takes a hit of damage
	 * 
	 * @param gameScene
	 * 
	 * @param gameScene
	 * @param player
	 */
	public void takeDamage(int hitPoints, GameScene gameScene, final Player player) {

		// increment that we hit an actor for level stats
		GameStatus.incrementGameLevelStat(GameStatus.levelStatsType.HITS);

		life = life - hitPoints;
		Log.i(this.getName(), "Life: " + Integer.toString(this.life));
		if (this.life <= 0) {
			die(gameScene, player);
		}
	}

	/**
	 * bullet hits object
	 * 
	 * @param player
	 * 
	 * @param scene
	 * 
	 * @param gameScene
	 */
	public void die(BaseScene thisScene, final Player player) {
		Log.i(this.getName(), "Has Died");		
		
		// increment that we hit an actor for level stats
		GameStatus.incrementGameLevelStat(GameStatus.levelStatsType.KILLS);

		new Explosion(thisScene, this, explosionType);

		// random explosion sound is played on actor death
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

		final PhysicsConnector physicsConnector = thisScene.physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(Actor.this);
		ResourceManager.getIntance().engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				if (physicsConnector != null) {
					actorBody.setActive(false);
					actorBody.setUserData("deceased");
					scene.detachChild(Actor.this);
				}
			}
		});
	}

	/**
	 * by simple x and y coordinates create a new villain
	 * 
	 * @param x
	 * @param y
	 * @param life
	 * @param weapon
	 * @param facing
	 * @param shoots
	 * @return
	 */
	public Actor getInstance(float x, float y, String type, int life, String weapon, String facing, Boolean shoots, String name, int animationSpeed, float movementSpeed, int explosionType) {
		return new Actor(name, id, spriteRow, type, life, weapon, facing, shoots, animationSpeed, movementSpeed, explosionType, x, y, density, elastic, friction, getTiledTextureRegion(), getVertexBufferObjectManager());
	}
}