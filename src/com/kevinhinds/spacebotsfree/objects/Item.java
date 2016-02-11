package com.kevinhinds.spacebotsfree.objects;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.scene.GameScene;

/**
 * an item sprite extends sprite to easily define and attach to any scene
 * 
 * @author khinds
 */
public class Item extends TiledSprite {

	private final String name;
	private final String type;
	private final int id, tileIndex;
	private final float density, elastic, friction;
	public Body itemBody;
	public Scene scene;

	/**
	 * create new item from a tiledSprite with a specific tile index specified as well as if it's a physical tile a background or foreground tile
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
	public Item(String name, String type, int id, int tileIndex, float x, float y, float density, float elastic, float friction, ITiledTextureRegion texture, VertexBufferObjectManager vbom) {
		super(x, y, texture, vbom);
		this.name = name;
		this.type = type;
		this.id = id;
		this.tileIndex = tileIndex;
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
	public void createBodyAndAttach(Scene scene, PhysicsWorld physicsWorld) {

		this.scene = scene;
		final FixtureDef tileFixtureDef = PhysicsFactory.createFixtureDef(density, elastic, friction);
		tileFixtureDef.restitution = 0;
		this.setCurrentTileIndex(tileIndex);
		itemBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.KinematicBody, tileFixtureDef);
		itemBody.setUserData(this.name);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, itemBody, true, true));
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
	 * get type of this item
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * player collects item
	 * 
	 * @param player
	 * 
	 * @param scene
	 * 
	 * @param gameScene
	 */
	public void collect(GameScene thisScene) {
		Log.i(this.getType(), "Item Collected " + this.getType());
		final PhysicsConnector physicsConnector = thisScene.physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(Item.this);
		ResourceManager.getIntance().engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				if (physicsConnector != null) {
					itemBody.setActive(false);
					itemBody.setUserData("collected");
					scene.detachChild(Item.this);
				}
			}
		});
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
	public Item getInstance(String name, String type, float x, float y, int animationSpeed, int movementSpeed) {
		return new Item(name, type, id, tileIndex, x, y, density, elastic, friction, getTiledTextureRegion(), getVertexBufferObjectManager());
	}
}