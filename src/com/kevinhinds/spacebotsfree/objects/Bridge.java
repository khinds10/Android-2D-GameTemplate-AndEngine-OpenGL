package com.kevinhinds.spacebotsfree.objects;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kevinhinds.spacebotsfree.GameConfiguration.State;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * an bridge sprite extends sprite to easily define and attach to any scene
 * 
 * @author khinds
 */
public class Bridge extends TiledSprite {

	private final String name;
	private final int id;
	private final float density, elastic, friction;
	public Body bridgeBody;
	public Scene scene;

	/**
	 * create new bridge from a tiledSprite with a specific tile index specified as well as if it's a physical tile a background or foreground tile
	 * 
	 * @param name
	 * @param id
	 * @param tileIndex
	 * @param type
	 * @param x
	 * @param y
	 * @param density
	 * @param elastic
	 * @param friction
	 * @param texture
	 * @param vbom
	 */
	public Bridge(String name, int id, int tileIndex, float x, float y, float density, float elastic, float friction, ITiledTextureRegion texture, VertexBufferObjectManager vbom) {
		super(x, y, texture, vbom);
		this.name = name;
		this.id = id;
		this.density = density;
		this.elastic = elastic;
		this.friction = friction;
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
		float[] objCenterPos = new float[2];
		playerSprite.getSceneCenterCoordinates(objCenterPos);
		objCenterPos[1] = objCenterPos[1] + 10;
		if (facing == State.LEFT) {
			objCenterPos[0] = objCenterPos[0] - 60;
		} else {
			objCenterPos[0] = objCenterPos[0] + 25;
		}
		this.setX(objCenterPos[0]);
		this.setY(objCenterPos[1]);
		tileFixtureDef.restitution = 0;
		this.setCurrentTileIndex(0);
		bridgeBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.KinematicBody, tileFixtureDef);
		bridgeBody.setUserData(this.name);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, bridgeBody, true, true));
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
	 * by simple x and y coordinates create a new item
	 * 
	 * @param x
	 * @param y
	 * @param animationSpeed
	 * @param movementSpeed
	 * @return
	 */
	public Bridge getInstance(String name, int id, int tileIndex, float x, float y, float density, float elastic, float friction, ITiledTextureRegion texture, VertexBufferObjectManager vbom) {
		return new Bridge(name, id, tileIndex, x, y, density, elastic, friction, getTiledTextureRegion(), getVertexBufferObjectManager());
	}
}