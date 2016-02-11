package com.kevinhinds.spacebotsfree.level;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.util.SAXUtils;
import org.andengine.util.level.IEntityLoader;
import org.andengine.util.level.LevelLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.xml.sax.Attributes;

import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.objects.Actor;
import com.kevinhinds.spacebotsfree.objects.Item;
import com.kevinhinds.spacebotsfree.objects.Piece;
import com.kevinhinds.spacebotsfree.objects.Tile;
import com.kevinhinds.spacebotsfree.scene.GameScene;

import android.content.res.AssetManager;

public class LevelXMLBuilder {
	
	private final LevelLoader levelLoader;
	private final AssetManager assetManager;
	public Level level;

	// XML attributes
	private static final String TAG_ATTR_X = "x";
	private static final String TAG_ATTR_Y = "y";
	private static final String TAG_ANIMATION_SPEED = "animationSpeed";
	private static final String TAG_MOTION_SPEED = "movementSpeed";
	private static final String TAG_TYPE = "type";

	// PLATFORM XML attributes
	private static final String TAG_TILE = "platform";
	private static final String TAG_TILE_ATTR_TILE = "block";
	private static final String TAG_TILE_TYPE_TILE = "type";

	// ITEM XML attributes
	private static final String TAG_ITEM = "item";

	// PIECE XML attributes
	private static final String TAG_PIECE = "piece";

	// ACTOR XML attributes
	private static final String TAG_ACTOR = "actor";
	private static final String TAG_ACTOR_ATTR_TILE = "actor";
	private static final String TAG_ACTOR_STRING_WEAPON = "weapon";
	private static final String TAG_ACTOR_STRING_SHOOTS = "shoots";
	private static final String TAG_ACTOR_STRING_LIFE = "life";
	private static final String TAG_ACTOR_STRING_FACING = "facing";
	private static final String TAG_ACTOR_ATTR_EXPLOSION_TYPE = "explosion";

	/**
	 * construct level manager which will load into memory all the level specified by XML for the game
	 * 
	 * @param assetManager
	 */
	public LevelXMLBuilder(AssetManager assetManager) {
		levelLoader = new LevelLoader();
		levelLoader.setAssetBasePath("levels/");
		this.assetManager = assetManager;
	}

	/**
	 * load level information from XML via SAX parser
	 */
	public void createLevelFromXML(int levelNumber) {

		/**
		 * LOAD LEVEL INFO
		 */
		level = new Level();
		levelLoader.registerEntityLoader(LevelConstants.TAG_LEVEL, new IEntityLoader() {
			@Override
			public IEntity onLoadEntity(String name, Attributes attr) {
				final int width = SAXUtils.getIntAttributeOrThrow(attr, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
				final int height = SAXUtils.getIntAttributeOrThrow(attr, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
				level.setWidth(width);
				level.setHeight(height);
				return null;
			}
		});

		/**
		 * LOAD IN PLATFORMS FROM XML
		 */
		levelLoader.registerEntityLoader(TAG_TILE, new IEntityLoader() {

			@Override
			public IEntity onLoadEntity(final String name, final Attributes attr) {
				final int x = SAXUtils.getIntAttributeOrThrow(attr, TAG_ATTR_X);
				final int y = SAXUtils.getIntAttributeOrThrow(attr, TAG_ATTR_Y);
				final int id = SAXUtils.getIntAttributeOrThrow(attr, TAG_TILE_ATTR_TILE);
				final String type = SAXUtils.getAttributeOrThrow(attr, TAG_TILE_TYPE_TILE);
				Tile t = ResourceManager.getIntance().getGameTileById(id);
				level.addTile(t.getInstance(x, y, type, "Platform:" + Float.toString(x) + "-" + Float.toString(y) + "-ID:" + Integer.toString(id)));
				return null;
			}
		});

		try {
			levelLoader.loadLevelFromAsset(assetManager, "level" + Integer.toString(levelNumber) + "_platforms.xml");
		} catch (IOException e) {

		}

		/**
		 * LOAD IN ITEMS FROM XML
		 */
		levelLoader.registerEntityLoader(TAG_ITEM, new IEntityLoader() {

			@Override
			public IEntity onLoadEntity(final String name, final Attributes attr) {
				final int x = SAXUtils.getIntAttributeOrThrow(attr, TAG_ATTR_X);
				final int y = SAXUtils.getIntAttributeOrThrow(attr, TAG_ATTR_Y);
				final int id = SAXUtils.getIntAttributeOrThrow(attr, TAG_TYPE);
				final int animationSpeed = SAXUtils.getIntAttributeOrThrow(attr, TAG_ANIMATION_SPEED);
				final int movementSpeed = SAXUtils.getIntAttributeOrThrow(attr, TAG_MOTION_SPEED);
				Item i = ResourceManager.getIntance().getGameItemById(id);
				level.addItem(i.getInstance("Item: " + Float.toString(x) + "-" + Float.toString(y) + "-" + Integer.toString(id), String.valueOf(id), x, y, animationSpeed, movementSpeed));
				return null;
			}
		});

		try {
			levelLoader.loadLevelFromAsset(assetManager, "level" + Integer.toString(levelNumber) + "_items.xml");
		} catch (IOException e) {

		}

		/**
		 * LOAD IN SHIP PIECES FROM XML
		 */
		levelLoader.registerEntityLoader(TAG_PIECE, new IEntityLoader() {

			@Override
			public IEntity onLoadEntity(final String name, final Attributes attr) {
				final int x = SAXUtils.getIntAttributeOrThrow(attr, TAG_ATTR_X);
				final int y = SAXUtils.getIntAttributeOrThrow(attr, TAG_ATTR_Y);
				final int id = SAXUtils.getIntAttributeOrThrow(attr, TAG_TYPE);
				final int animationSpeed = SAXUtils.getIntAttributeOrThrow(attr, TAG_ANIMATION_SPEED);
				final int movementSpeed = SAXUtils.getIntAttributeOrThrow(attr, TAG_MOTION_SPEED);
				Piece p = ResourceManager.getIntance().getGamePieceById(id);
				level.addPiece(p.getInstance("Piece: " + Float.toString(x) + "-" + Float.toString(y) + "-" + Integer.toString(id), String.valueOf(id), x, y, animationSpeed, movementSpeed));
				return null;
			}
		});

		try {
			levelLoader.loadLevelFromAsset(assetManager, "level" + Integer.toString(levelNumber) + "_pieces.xml");
		} catch (IOException e) {

		}

		/**
		 * LOAD IN ACTORS FROM XML
		 */
		levelLoader.registerEntityLoader(LevelConstants.TAG_LEVEL, new IEntityLoader() {
			@Override
			public IEntity onLoadEntity(String name, Attributes attr) {
				final int width = SAXUtils.getIntAttributeOrThrow(attr, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
				final int height = SAXUtils.getIntAttributeOrThrow(attr, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
				level.setWidth(width);
				level.setHeight(height);
				return null;
			}
		});

		levelLoader.registerEntityLoader(TAG_ACTOR, new IEntityLoader() {

			@Override
			public IEntity onLoadEntity(final String name, final Attributes attr) {
				final int x = SAXUtils.getIntAttributeOrThrow(attr, TAG_ATTR_X);
				final int y = SAXUtils.getIntAttributeOrThrow(attr, TAG_ATTR_Y);
				final int id = SAXUtils.getIntAttributeOrThrow(attr, TAG_ACTOR_ATTR_TILE);
				final String type = SAXUtils.getAttributeOrThrow(attr, TAG_TYPE);
				final int life = SAXUtils.getIntAttributeOrThrow(attr, TAG_ACTOR_STRING_LIFE);
				final String weapon = SAXUtils.getAttributeOrThrow(attr, TAG_ACTOR_STRING_WEAPON);
				final String facing = SAXUtils.getAttributeOrThrow(attr, TAG_ACTOR_STRING_FACING);
				final Boolean shoots = SAXUtils.getBooleanAttributeOrThrow(attr, TAG_ACTOR_STRING_SHOOTS);
				final int animationSpeed = SAXUtils.getIntAttributeOrThrow(attr, TAG_ANIMATION_SPEED);
				final int movementSpeed = SAXUtils.getIntAttributeOrThrow(attr, TAG_MOTION_SPEED);
				final int explosionType = SAXUtils.getIntAttributeOrThrow(attr, TAG_ACTOR_ATTR_EXPLOSION_TYPE);
				Actor v = ResourceManager.getIntance().getGameActorById(id);

				// add new villain with a unique name based on current XML options set
				level.addActor(v.getInstance(x, y, type, life, weapon, facing, shoots, "Actor: " + Float.toString(x) + "-" + Float.toString(y) + "-" + Integer.toString(id), animationSpeed, movementSpeed, explosionType));
				return null;
			}
		});

		try {
			levelLoader.loadLevelFromAsset(assetManager, "level" + Integer.toString(levelNumber) + "_actors.xml");
		} catch (IOException e) {

		}
	}

	/**
	 * load level by id and apply it to the scene in question
	 * 
	 * @param id
	 * @param scene
	 * @param physicsWorld
	 */
	public void loadLevel(GameScene scene, PhysicsWorld physicsWorld) {
		level.load(scene, physicsWorld);
	}
}