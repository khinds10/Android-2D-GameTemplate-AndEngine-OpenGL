package com.kevinhinds.spacebotsfree.objects;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.GameConfiguration.State;
import com.kevinhinds.spacebotsfree.scene.BaseScene;

/**
 * flare generated from player
 * 
 * @author khinds
 */
public class Flare extends AnimatedSprite {

	private final String name;
	private final int id, tileIndex;
	private final float density, elastic, friction;
	public Body flareBody;
	public Scene scene;
	public int strength = 1;
	public State direction;
	private long[] animationSpeed;
	private int startTile;
	private int endTile;
	float x, y;

	/**
	 * create a new flare
	 * 
	 * @param name
	 * @param id
	 * @param tileIndex
	 * @param x
	 * @param y
	 * @param density
	 * @param elastic
	 * @param friction
	 * @param texture
	 * @param vbom
	 * @param direction
	 */
	public Flare(String name, int id, int tileIndex, float x, float y, float density, float elastic, float friction, ITiledTextureRegion texture, VertexBufferObjectManager vbom, State direction) {
		super(x, y, texture, vbom);
		this.name = name;
		this.id = id;
		this.tileIndex = tileIndex;
		this.density = density;
		this.elastic = elastic;
		this.friction = friction;
		this.direction = direction;
		this.x = x;
		this.y = y;
	}

	/**
	 * attach this current item to the scene in question with the tile index applied
	 * 
	 * @param scene
	 * @param physicsWorld
	 */
	public void createBodyAndAttach(AnimatedSprite playerSprite, State facing, Scene scene, PhysicsWorld physicsWorld) {

		animationSpeed = new long[] { GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed,
				GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed, GameConfiguration.flareAnimationSpeed };

		// depending on the player facing direction we offset the animation indexg
		final FixtureDef tileFixtureDef = PhysicsFactory.createFixtureDef(density, elastic, friction);
		this.scene = scene;

		// apply left or right facing x and acceleration values
		int direction = 1;
		int startTileOffset = 0;

		if (facing == State.LEFT) {
			startTileOffset = startTileOffset + 50;
			direction = -1;
			this.x = this.x - 45;
		} else {
			this.x = this.x + 40;
		}
		this.setX(this.x);
		this.setY(this.y - 10);

		// we'll start counting from 1 but the sprite sheet counts from zero
		startTile = 9 + startTileOffset;
		endTile = startTile + 29;

		tileFixtureDef.restitution = 0;

		// explosion animates through and then disappears
		this.animate(animationSpeed, startTile, endTile, true);

		flareBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, tileFixtureDef);
		flareBody.setUserData(this.name);
		flareBody.setGravityScale(0);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, flareBody, true, true));
		final Vector2 velocity = Vector2Pool.obtain((direction * GameConfiguration.flareVelocity), 0);
		flareBody.setLinearVelocity(velocity);
		scene.attachChild(this);
	}

	/**
	 * get name of this item
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * get ID of this item
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * by simple x and y coordinates create a new flare
	 * 
	 * @param x
	 * @param y
	 * @param animationSpeed
	 * @param movementSpeed
	 * @return
	 */
	public Flare getInstance(String name, float x, float y, State direction) {
		return new Flare(name, id, tileIndex, x, y, density, elastic, friction, getTiledTextureRegion(), getVertexBufferObjectManager(), direction);
	}

	/**
	 * bullet hits an object and disappears
	 * 
	 * @param thisScene
	 */
	public void hitObject(BaseScene thisScene) {

		new Explosion(thisScene, this, 5);
		ResourceManager.getIntance().flareSound.stop();
		ResourceManager.getIntance().impactSound.play();

		final PhysicsConnector physicsConnector = thisScene.physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(Flare.this);
		ResourceManager.getIntance().engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				if (physicsConnector != null) {
					String currentFlareName = (String) flareBody.getUserData();
					flareBody.setActive(false);
					flareBody.setUserData(currentFlareName + "_deceased");
					scene.detachChild(Flare.this);
				}
			}
		});
	}
}