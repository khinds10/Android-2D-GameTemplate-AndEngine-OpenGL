package com.kevinhinds.spacebotsfree.objects;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.scene.BaseScene;
import com.kevinhinds.spacebotsfree.scene.GameScene;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * a tile sprite extends sprite to easily define and attach to any scene
 * 
 * @author khinds
 */
public class Tile extends TiledSprite {

	private String name;
	private final String type;
	private final int id, tileIndex;
	private final float density, elastic, friction;
	private Body tileBody;
	private GameScene scene;

	/**
	 * create new tile from a tiledSprite with a specific tile index specified as well as if it's a physical tile a background or foreground tile
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
	public Tile(String name, int id, int tileIndex, String type, float x, float y, float density, float elastic, float friction, ITiledTextureRegion texture, VertexBufferObjectManager vbom) {
		super(x, y, texture, vbom);
		this.name = name;
		this.id = id;
		this.tileIndex = tileIndex;
		this.density = density;
		this.elastic = elastic;
		this.friction = friction;
		this.type = type;
		this.tileBody = null;
		this.scene = null;
	}

	/**
	 * attach this current tile to the scene in question with the tile index applied
	 * 
	 * @param scene
	 * @param physicsWorld
	 */
	public void createBodyAndAttach(GameScene scene, PhysicsWorld physicsWorld) {
		final FixtureDef tileFixtureDef = PhysicsFactory.createFixtureDef(density, elastic, friction);
		tileFixtureDef.restitution = 0;
		this.setCurrentTileIndex(tileIndex);

		// only apply physics to the tiles marked as such
		if (type.equals("physical") || type.equals("bounce") || type.equals("edge")) {
			String tileData = "tile";
			if (type.equals("bounce")) {
				tileData = "bounce";
			}
			if (type.equals("edge")) {
				tileData = "edge";
			}
			tileBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.StaticBody, tileFixtureDef);
			name = tileData + " - " +  name;
			tileBody.setUserData(name);
			physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, tileBody, true, true));
			this.setZIndex(0);
		}
		if (type.equals("foreground")) {
			this.setZIndex(1);
		}
		if (type.equals("background")) {
			this.setZIndex(-1);
		}
		this.scene = scene;
		this.scene.attachChild(this);
	}

	/**
	 * get name of this tile
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * get ID of this tile
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * tile gets broken by gamescene
	 * 
	 * @param scene
	 * @param gameScene
	 */
	public void breakTile(BaseScene thisScene) {
		Log.i(this.getName(), " has been broken");
		final PhysicsConnector physicsConnector = thisScene.physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(Tile.this);
		ResourceManager.getIntance().engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				if (physicsConnector != null) {
					Tile.this.setVisible(false);	
					Tile.this.tileBody.setActive(false);
					Tile.this.scene.randomGameRoundExplosion(Tile.this);
					Tile.this.scene.randomTileBreakingSound();
				}
			}
		});
	}
	
	/**
	 * by simple x and y coordinates create a new tile
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Tile getInstance(float x, float y, String type, String name) {
		return new Tile(name, id, tileIndex, type, x, y, density, elastic, friction, getTiledTextureRegion(), getVertexBufferObjectManager());
	}
}