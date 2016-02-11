package com.kevinhinds.spacebotsfree;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;
import org.andengine.util.color.Color;

import com.kevinhinds.spacebotsfree.GameConfiguration.State;
import com.kevinhinds.spacebotsfree.objects.Actor;
import com.kevinhinds.spacebotsfree.objects.Bullet;
import com.kevinhinds.spacebotsfree.objects.Flare;
import com.kevinhinds.spacebotsfree.objects.Item;
import com.kevinhinds.spacebotsfree.objects.Piece;
import com.kevinhinds.spacebotsfree.objects.Tile;

/**
 * deal with game resources via singleton design pattern for re-usability
 * 
 * @author khinds
 */
public class ResourceManager {

	private static final ResourceManager INSTANCE = new ResourceManager();

	// universal resources
	public Engine engine;
	public MainGameActivity activity;
	public Camera camera;
	public VertexBufferObjectManager vbom;

	// game fonts
	public Font gameFont;
	public Font gameFontLarge;
	public Font gameFontGray;
	public Font gameFontTiny;	
	public Font gameFontTinyBlue;
	public Font gameFontTinyGreen;
	public Font gameFontMedium;
	public Font gameRedFont;
	public Font gameBlueFont;
	public Font gameGreenFont;

	// level fonts color coded by status
	public Font levelSelectFontNone;
	public Font levelSelectFontPlay;
	public Font levelSelectFontOne;
	public Font levelSelectFontOneInfo;
	public Font levelSelectFontTwo;
	public Font levelSelectFontTwoInfo;
	public Font levelSelectFontThree;
	public Font levelSelectFontThreeInfo;

	public Font titleFont;
	public Font menuRedFont;
	public Font menuBlueFont;
	public Font menuGreenFont;
	public Font menuGrayFont;

	// menu regions
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	public ITextureRegion planetBackgroundRegion;
	public ITextureRegion multicolorBackgroundRegion;
	public ITextureRegion voidBackgroundRegion;
	public ITextureRegion colorgalaxyBackgroundRegion;
	public ITextureRegion illuminateBackgroundRegion;
	public ITextureRegion andengineRegion;
	public ITextureRegion playerLevelMetalRegion;
	public ITextureRegion playerLevelMetalSmallRegion;
	public ITextureRegion lockRegion;

	// player region
	public ITiledTextureRegion playerRegion;
	public ITiledTextureRegion playerEnergyRegion;
	public ITiledTextureRegion playerLifeRegion;

	// weapon region
	public ITiledTextureRegion bulletRegion;
	public ITiledTextureRegion bombRegion;
	public ITiledTextureRegion flareRegion;

	// adversary region
	public ITiledTextureRegion actorsRegion;
	public ITiledTextureRegion explosionRegion;

	// ship lift off flame and planet to fly away from
	private BuildableBitmapTextureAtlas flameTextureAtlas;
	private BuildableBitmapTextureAtlas planetTextureAtlas;
	public ITiledTextureRegion flameRegion;
	public ITextureRegion planetRegion;

	// controls regions
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	public ITextureRegion controlJumpRegion;
	public ITextureRegion controlShootRegion;
	public ITextureRegion controlLeftRegion;
	public ITextureRegion controlRightRegion;
	public ITextureRegion controlDownRegion;

	// level regions
	private BuildableBitmapTextureAtlas tileTextureAtlas;
	private BuildableBitmapTextureAtlas itemTextureAtlas;
	private BuildableBitmapTextureAtlas itemButtonTextureAtlas;
	private BuildableBitmapTextureAtlas pieceTextureAtlas;
	public ITiledTextureRegion platformRegion;
	public ITiledTextureRegion itemRegion;
	public ITiledTextureRegion itemButtonRegion;
	public ITiledTextureRegion bridgeRegion;

	// we have a piece region and a brighter high contrast piece region so the pieces to collect are easier to see on the level
	public ITiledTextureRegion pieceRegion;
	public ITiledTextureRegion pieceLevelRegion;
	public ITextureRegion completeShipRegion;

	// complete set of the game's actors, tiles and items to render on levels via XML descriptions
	private ArrayList<Actor> gameActors = new ArrayList<Actor>();
	private ArrayList<Tile> gameTiles = new ArrayList<Tile>();
	private ArrayList<Item> gameItems = new ArrayList<Item>();
	private ArrayList<Bullet> gameBullets = new ArrayList<Bullet>();
	private ArrayList<Flare> gameFlares = new ArrayList<Flare>();
	private ArrayList<Piece> shipPieces = new ArrayList<Piece>();

	// load all the music
	public Music titleMusic;
	public Music creditsMusic;
	public Music daydreamMusic;
	public Music deadMusic;
	public Music endingMusic;
	public Music finishedMusic;
	public Music levelMusic;

	// load all the sound effects
	public Sound laserSound;
	public Sound gunClickSound;
	public Sound impactSound;

	public Sound tokenSound;
	public Sound hitSound;
	public Sound aquireTokenSound;
	public Sound aquirePieceSound;
	public Sound powerUpSound;
	public Sound superJumpSound;
	public Sound bombSound;
	public Sound flareSound;

	// 3 different random explosions
	public Sound explosion1Sound;
	public Sound explosion2Sound;
	public Sound explosion3Sound;

	// 3 different random tile breaking sounds
	public Sound break1Sound;
	public Sound break2Sound;
	public Sound break3Sound;

	// special weapon while ducking
	public Sound loadSound;
	public Sound unloadSound;
	public Sound cannonSound;

	// player extra sounds
	public Sound runningSound;
	public Sound thumpSound;

	/**
	 * load resources to create the menu into memory
	 */
	public void loadMenuResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 4096, 4096, TextureOptions.BILINEAR);
		colorgalaxyBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu/colorgalaxy.jpg");
		illuminateBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu/galaxy.jpg");
		planetBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu/largeplanet.jpg");
		multicolorBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu/multicolor.jpg");
		voidBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu/void.jpg");
		andengineRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu/andengine.png");
		playerLevelMetalRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "items/metal.png");
		playerLevelMetalSmallRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "items/metal-small.png");
		lockRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu/lock.png");

		// have to run everything through black pawn to render it visibly
		try {
			menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			menuTextureAtlas.load();
		} catch (Exception e) {
			Debug.e(e);
		}
	}

	/**
	 * load resources for the game into memory
	 */
	public void loadGameResources() {

		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 4096, 4096, TextureOptions.BILINEAR);

		// player, weapons and life/energy meters
		playerRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "character/player.png", GameConfiguration.playerMapColumns, GameConfiguration.playerMapRows);
		bulletRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "weapons/bullet_strip.png", GameConfiguration.bulletMapColumns, GameConfiguration.bulletMapRows);
		bombRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "weapons/bomb.png", GameConfiguration.bombMapColumns, GameConfiguration.bombMapRows);
		flareRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "weapons/flare.png", GameConfiguration.flareMapColumns, GameConfiguration.flareMapRows);

		playerEnergyRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "character/energy.png", GameConfiguration.energyMapColumns, GameConfiguration.energyMapRows);
		playerLifeRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "character/life.png", GameConfiguration.lifeMapColumns, GameConfiguration.lifeMapRows);

		// adversaries and explosions
		actorsRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "actors/creatures.png", GameConfiguration.actorMapColumns, GameConfiguration.actorMapRows);
		explosionRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "explosions/explosions.png", GameConfiguration.explosionMapColumns, GameConfiguration.explosionMapRows);

		// player can create their own bridge tiles
		bridgeRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "items/bridge.png", 1, 1);

		// game controls */
		controlJumpRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "controls/jump.png");
		controlLeftRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "controls/left.png");
		controlRightRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "controls/right.png");
		controlShootRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "controls/shoot.png");
		controlDownRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "controls/down.png");

		// have to run everything through black pawn to render it visibly
		try {
			gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			gameTextureAtlas.load();
		} catch (Exception e) {
			Debug.e(e);
		}

		// load platforms
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/tiles/");
		tileTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		platformRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(tileTextureAtlas, activity, "platforms.png", GameConfiguration.platformMapColumns, GameConfiguration.platformMapRows);

		// have to run everything through black pawn to render it visibly
		try {
			tileTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			tileTextureAtlas.load();
		} catch (Exception e) {
			Debug.e(e);
		}

		// load items
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/items/");
		itemTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		itemRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(itemTextureAtlas, activity, "tokens.png", GameConfiguration.itemMapColumns, GameConfiguration.itemMapRows);

		// have to run everything through black pawn to render it visibly
		try {
			itemTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			itemTextureAtlas.load();
		} catch (Exception e) {
			Debug.e(e);
		}

		// load item buttons
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/items/");
		itemButtonTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		itemButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(itemButtonTextureAtlas, activity, "tokenbuttons.png", GameConfiguration.itemButtonMapColumns, GameConfiguration.itemButtonMapRows);

		// have to run everything through black pawn to render it visibly
		try {
			itemButtonTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			itemButtonTextureAtlas.load();
		} catch (Exception e) {
			Debug.e(e);
		}

		// load ship pieces
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/items/");
		pieceTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
		pieceRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(pieceTextureAtlas, activity, "ship.png", GameConfiguration.pieceMapColumns, GameConfiguration.pieceMapRows);
		pieceLevelRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(pieceTextureAtlas, activity, "shipcollect.png", GameConfiguration.pieceMapColumns, GameConfiguration.pieceMapRows);

		// load complete ship for end sequence
		completeShipRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(pieceTextureAtlas, activity, "ship.png");

		// have to run everything through black pawn to render it visibly
		try {
			pieceTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			pieceTextureAtlas.load();
		} catch (Exception e) {
			Debug.e(e);
		}

		// add all the gameActors from the actors sprite sheet to the list of available gameActors
		for (int i = 0; i <= (GameConfiguration.actorMapColumns * GameConfiguration.actorMapRows); i++) {
			gameActors.add(new Actor("Actor Image " + Integer.toString(i), i, i, "stationary", 0, "none", "right", false, GameConfiguration.actorAnimationSpeed, GameConfiguration.actorMovementSpeed, GameConfiguration.explosionDefault, 0, 0, 10f, 0f, 10f, ResourceManager.getIntance().actorsRegion, vbom));
		}

		// add all the gameTiles from the platform sprite sheet to the list of available gameTiles
		for (int i = 0; i <= (GameConfiguration.platformMapColumns * GameConfiguration.platformMapRows); i++) {
			gameTiles.add(new Tile("Platform Tile " + Integer.toString(i), i, i, "physical", 0, 0, 0f, 10f, 0f, ResourceManager.getIntance().platformRegion, vbom));
		}

		// add all the gameItems from the items sprite sheet to the list of available gameItems
		for (int i = 0; i <= (GameConfiguration.itemMapColumns * GameConfiguration.itemMapRows); i++) {
			gameItems.add(new Item("Game Item " + Integer.toString(i), String.valueOf(i), i, i, 0, 0, 0f, 0f, 0f, ResourceManager.getIntance().itemRegion, vbom));
		}

		// add all the gameBullets from the bullet sprite sheet to the list of available gameBullets
		for (int i = 0; i <= (GameConfiguration.bulletMapColumns * GameConfiguration.bulletMapRows); i++) {
			gameBullets.add(new Bullet("Bullet Sprite " + Integer.toString(i), i, i, 0, 0, 0f, 0f, 0f, ResourceManager.getIntance().bulletRegion, vbom, 1));
		}

		// add all the shipPieces from the ship sprite sheet to the list of available shipPieces
		for (int i = 0; i <= (GameConfiguration.pieceMapColumns * GameConfiguration.pieceMapRows); i++) {
			shipPieces.add(new Piece("Piece Sprite " + Integer.toString(i), String.valueOf(i), i, i, 0, 0, 0f, 0f, 0f, ResourceManager.getIntance().pieceLevelRegion, vbom));
		}

		// add the single game flare which is fired from a token ability
		gameFlares.add(new Flare("Flare Sprite " + Integer.toString(0), 0, 0, 0, 0, 0f, 0f, 0f, ResourceManager.getIntance().flareRegion, vbom, State.LEFT));

		// load ship flames for end sequence
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/explosions/");
		flameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 4096, 4096, TextureOptions.BILINEAR);
		flameRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(flameTextureAtlas, activity, "flame.png", GameConfiguration.flameMapColumns, GameConfiguration.flameMapRows);

		// have to run everything through black pawn to render it visibly
		try {
			flameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			flameTextureAtlas.load();
		} catch (Exception e) {
			Debug.e(e);
		}

		// load the planet to show on the end screen
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		planetTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
		planetRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(planetTextureAtlas, activity, "endplanet.png");

		// have to run everything through black pawn to render it visibly
		try {
			planetTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			planetTextureAtlas.load();
		} catch (Exception e) {
			Debug.e(e);
		}

	}

	/**
	 * load all music from assets
	 */
	public void loadMusic() {
		MusicFactory.setAssetBasePath("music/");
		try {
			titleMusic = MusicFactory.createMusicFromAsset(this.engine.getMusicManager(), this.activity, "title.ogg");
			creditsMusic = MusicFactory.createMusicFromAsset(this.engine.getMusicManager(), this.activity, "credits.ogg");
			daydreamMusic = MusicFactory.createMusicFromAsset(this.engine.getMusicManager(), this.activity, "daydream.ogg");
			deadMusic = MusicFactory.createMusicFromAsset(this.engine.getMusicManager(), this.activity, "dead.ogg");
			endingMusic = MusicFactory.createMusicFromAsset(this.engine.getMusicManager(), this.activity, "ending.ogg");
			finishedMusic = MusicFactory.createMusicFromAsset(this.engine.getMusicManager(), this.activity, "finished.ogg");
			levelMusic = MusicFactory.createMusicFromAsset(this.engine.getMusicManager(), this.activity, "level.ogg");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * load all sound effects from resources
	 */
	public void loadSoundEffects() {
		SoundFactory.setAssetBasePath("sfx/");
		try {
			laserSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "laser.ogg");
			gunClickSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "gunclick.ogg");
			impactSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "impact.ogg");
			hitSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "hit.ogg");
			aquireTokenSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "aquireToken.ogg");
			aquirePieceSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "piece.ogg");
			powerUpSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "powerUp.ogg");
			superJumpSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "jump.ogg");
			bombSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "bomb.ogg");
			flareSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "flare.ogg");
			tokenSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "token.ogg");
			explosion1Sound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "explosion1.ogg");
			explosion2Sound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "explosion2.ogg");
			explosion3Sound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "explosion3.ogg");
			break1Sound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "break1.ogg");
			break2Sound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "break2.ogg");
			break3Sound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "break3.ogg");
			loadSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "load.ogg");
			unloadSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "unload.ogg");
			cannonSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "cannon.ogg");
			runningSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "running.ogg");
			thumpSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), this.activity, "thump.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
	}

	/**
	 * stop any music playing
	 */
	public void stopAllMusic() {
		if (titleMusic.isPlaying()) {
			titleMusic.pause();
		}
		if (creditsMusic.isPlaying()) {
			creditsMusic.pause();
		}
		if (daydreamMusic.isPlaying()) {
			daydreamMusic.pause();
		}
		if (deadMusic.isPlaying()) {
			deadMusic.pause();
		}
		if (endingMusic.isPlaying()) {
			endingMusic.pause();
		}
		if (finishedMusic.isPlaying()) {
			finishedMusic.pause();
		}
	}

	/**
	 * find a particular tile by id
	 * 
	 * @param id
	 * @return
	 */
	public Actor getGameActorById(int id) {
		for (Actor t : gameActors)
			if (t.getId() == id)
				return t;
		return null;
	}

	/**
	 * find a particular tile by id
	 * 
	 * @param id
	 * @return
	 */
	public Tile getGameTileById(int id) {
		for (Tile t : gameTiles)
			if (t.getId() == id)
				return t;
		return null;
	}

	/**
	 * find a particular item by id
	 * 
	 * @param id
	 * @return
	 */
	public Item getGameItemById(int id) {
		for (Item i : gameItems)
			if (i.getId() == id)
				return i;
		return null;
	}

	/**
	 * find a particular item by id
	 * 
	 * @param id
	 * @return
	 */
	public Piece getGamePieceById(int id) {
		for (Piece p : shipPieces)
			if (p.getId() == id)
				return p;
		return null;
	}

	/**
	 * get current ship pieces in the game to display which ones user as already collected
	 * 
	 * @return
	 */
	public ArrayList<Piece> getShipPieces() {
		return this.shipPieces;
	}

	/**
	 * find a particular flare by id
	 * 
	 * @param id
	 * @return
	 */
	public Flare getGameFlareById(int id) {
		for (Flare f : gameFlares)
			if (f.getId() == id)
				return f;
		return null;
	}

	/**
	 * find a particular bullet by id
	 * 
	 * @param id
	 * @return
	 */
	public Bullet getGameBulletById(int id) {
		for (Bullet b : gameBullets)
			if (b.getId() == id)
				return b;
		return null;
	}

	/**
	 * load fonts for the game
	 */
	public void loadFonts() {

		FontFactory.setAssetBasePath("fonts/");

		gameFontLarge = buildFont("game.ttf", 30, android.graphics.Color.parseColor("#FFFAB5"));
		gameFontLarge.load();

		gameFont = buildFont("game.ttf", 20, android.graphics.Color.parseColor("#E0EFEF"));
		gameFont.load();

		gameFontGray = buildFont("game.ttf", 20, android.graphics.Color.parseColor("#D8D8D8"));
		gameFontGray.load();

		gameFontTiny = buildFont("game.ttf", 10, android.graphics.Color.parseColor("#D8D8D8"));
		gameFontTiny.load();		
		
		gameFontTinyBlue = buildFont("game.ttf", 12, android.graphics.Color.parseColor("#81C1D7"));
		gameFontTinyBlue.load();
		
		gameFontTinyGreen = buildFont("game.ttf", 12, android.graphics.Color.parseColor("#AFDB6F"));
		gameFontTinyGreen.load();		

		gameFontMedium = buildFont("game.ttf", 15, android.graphics.Color.parseColor("#D8D8D8"));
		gameFontMedium.load();

		gameRedFont = buildFont("game.ttf", 20, android.graphics.Color.parseColor("#DB8E77"));
		gameRedFont.load();

		gameBlueFont = buildFont("game.ttf", 20, android.graphics.Color.parseColor("#81C1D7"));
		gameBlueFont.load();

		gameGreenFont = buildFont("game.ttf", 20, android.graphics.Color.parseColor("#AFDB6F"));
		gameGreenFont.load();

		titleFont = buildFont("game.ttf", 42, android.graphics.Color.parseColor("#FFFAB5"));
		titleFont.load();

		menuRedFont = buildFont("game.ttf", 40, android.graphics.Color.parseColor("#DB8E77"));
		menuRedFont.load();

		menuBlueFont = buildFont("game.ttf", 40, android.graphics.Color.parseColor("#81C1D7"));
		menuBlueFont.load();

		menuGreenFont = buildFont("game.ttf", 40, android.graphics.Color.parseColor("#AFDB6F"));
		menuGreenFont.load();

		menuGrayFont = buildFont("game.ttf", 40, android.graphics.Color.parseColor("#D8D8D8"));
		menuGrayFont.load();

		// color coded game level select fonts
		levelSelectFontNone = buildFont("game.ttf", 30, android.graphics.Color.parseColor("#515151"));
		levelSelectFontNone.load();

		levelSelectFontPlay = buildFont("game.ttf", 30, android.graphics.Color.parseColor("#FFFCCD"));
		levelSelectFontPlay.load();

		// color codes for if you've gotten 1, 2 or 3 metals for area select screen
		levelSelectFontOne = buildFont("game.ttf", 30, android.graphics.Color.parseColor("#FFB273"));
		levelSelectFontOne.load();
		levelSelectFontOneInfo = buildFont("game.ttf", 15, android.graphics.Color.parseColor("#FFB273"));
		levelSelectFontOneInfo.load();

		levelSelectFontTwo = buildFont("game.ttf", 30, android.graphics.Color.parseColor("#F77F00"));
		levelSelectFontTwo.load();
		levelSelectFontTwoInfo = buildFont("game.ttf", 15, android.graphics.Color.parseColor("#F77F00"));
		levelSelectFontTwoInfo.load();

		levelSelectFontThree = buildFont("game.ttf", 30, android.graphics.Color.parseColor("#D64027"));
		levelSelectFontThree.load();
		levelSelectFontThreeInfo = buildFont("game.ttf", 15, android.graphics.Color.parseColor("#D64027"));
		levelSelectFontThreeInfo.load();
	}

	/**
	 * build the font from texture and return to apply to game font resources
	 * 
	 * @param fontFile
	 * @param fontSize
	 * @return
	 */
	private Font buildFont(String fontFile, int fontSize, int color) {
		final ITexture levelSelectFontTexture = new BitmapTextureAtlas(this.engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		return FontFactory.createFromAsset(this.engine.getFontManager(), levelSelectFontTexture, activity.getAssets(), fontFile, fontSize, true, color);
	}

	/**
	 * create a new menu item from text
	 * 
	 * @param font
	 * @param menuItemText
	 * @param itemId
	 * @param isAnimated
	 * @return
	 */
	public IMenuItem createTextMenuItem(Font font, CharSequence menuItemText, int itemId, boolean isAnimated) {
		Color color = new Color(1, 1, 1, 1);
		IMenuItem menuItem = null;
		if (isAnimated) {
			menuItem = new ScaleMenuItemDecorator(new ColorMenuItemDecorator(new TextMenuItem(itemId, font, menuItemText, vbom), color, color), 1.1f, 1);
		} else {
			menuItem = new ColorMenuItemDecorator(new TextMenuItem(itemId, font, menuItemText, vbom), color, color);
		}
		menuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		return menuItem;
	}

	/**
	 * prepare the manager to use via singleton in the future
	 * 
	 * @param engine
	 * @param activity
	 * @param camera
	 * @param vbom
	 */
	public static void prepareManager(Engine engine, MainGameActivity activity, Camera camera, VertexBufferObjectManager vbom) {
		getIntance().engine = engine;
		getIntance().activity = activity;
		getIntance().camera = camera;
		getIntance().vbom = vbom;
	}

	/**
	 * get existing resources from the resource manager via singleton
	 * 
	 * @return
	 */
	public static ResourceManager getIntance() {
		return INSTANCE;
	}
}