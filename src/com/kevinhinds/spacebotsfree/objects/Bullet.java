package com.kevinhinds.spacebotsfree.objects;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;
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
import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.GameConfiguration.State;
import com.kevinhinds.spacebotsfree.scene.BaseScene;

/**
 * bullet generated from player
 * 
 * @author khinds
 */
public class Bullet extends TiledSprite {

	private final String name;
	private final int id, tileIndex;
	private final float density, elastic, friction;
	public Body bulletBody;
	public Scene scene;
	public int strength;
	public State direction;

	public Bullet(String name, int id, int tileIndex, float x, float y, float density, float elastic, float friction, ITiledTextureRegion texture, VertexBufferObjectManager vbom, int strength) {
		super(x, y, texture, vbom);
		this.name = name;
		this.id = id;
		this.tileIndex = tileIndex;
		this.density = density;
		this.elastic = elastic;
		this.friction = friction;
		this.direction = State.LEFT;
		this.strength = strength;
	}

	/**
	 * attach this current item to the scene in question with the tile index applied
	 * 
	 * @param scene
	 * @param physicsWorld
	 */
	public void createBodyAndAttach(AnimatedSprite playerSprite, State facing, Scene scene, PhysicsWorld physicsWorld) {

		// get the direction of the player facing
		final FixtureDef tileFixtureDef = PhysicsFactory.createFixtureDef(density, elastic, friction);
		this.scene = scene;

		// apply left or right facing x and acceleration values
		int direction = 1;
		if (facing == State.LEFT) {
			direction = -1;
		}

		tileFixtureDef.restitution = 0;
		this.setCurrentTileIndex(tileIndex);
		bulletBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, tileFixtureDef);
		bulletBody.setUserData(this.name);
		bulletBody.setGravityScale(0);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, bulletBody, true, true));
		final Vector2 velocity = Vector2Pool.obtain((direction * GameConfiguration.bulletVelocity), 0);
		bulletBody.setLinearVelocity(velocity);
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
	 * by simple x and y coordinates create a new bullet
	 * 
	 * @param x
	 * @param y
	 * @param animationSpeed
	 * @param movementSpeed
	 * @return
	 */
	public Bullet getInstance(String name, float x, float y, int strength) {
		return new Bullet(name, id, tileIndex, x, y, density, elastic, friction, getTiledTextureRegion(), getVertexBufferObjectManager(), strength);
	}

	/**
	 * bullet hits an object and disappears
	 * 
	 * @param thisScene
	 */
	public void hitObject(BaseScene thisScene) {

		new Explosion(thisScene, this, 5);
		ResourceManager.getIntance().impactSound.play();

		final PhysicsConnector physicsConnector = thisScene.physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(Bullet.this);
		ResourceManager.getIntance().engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				if (physicsConnector != null) {
					String currentBulletName = (String) bulletBody.getUserData();
					bulletBody.setActive(false);
					bulletBody.setUserData(currentBulletName + "_deceased");
					scene.detachChild(Bullet.this);
				}
			}
		});
	}
}