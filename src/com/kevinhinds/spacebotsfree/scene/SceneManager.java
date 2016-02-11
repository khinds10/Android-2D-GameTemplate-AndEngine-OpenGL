package com.kevinhinds.spacebotsfree.scene;

import org.andengine.audio.music.Music;
import org.andengine.engine.Engine;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.status.GameStatus;

/**
 * set scenes currently being displayed in the game via singleton
 * 
 * @author khinds
 */
public class SceneManager {

	private BaseScene menuScene;
	private BaseScene gameScene;
	private BaseScene levelScene;
	private BaseScene tutorialScene;
	private DayDreamScene dayDreamScene;
	private static final SceneManager INSTANCE = new SceneManager();
	private BaseScene currentScene;
	private Engine engine = ResourceManager.getIntance().engine;

	/**
	 * set the scene to beginning main menu
	 * 
	 * @param cb
	 */
	public void startGame(OnCreateSceneCallback cb) {

		// load all the resources for the game to start
		ResourceManager.getIntance().loadMenuResources();
		ResourceManager.getIntance().loadGameResources();
		ResourceManager.getIntance().loadMusic();
		ResourceManager.getIntance().loadSoundEffects();
		ResourceManager.getIntance().loadFonts();
		returnToMenuScene();
		cb.onCreateSceneFinished(menuScene);
	}

	/**
	 * return to menu
	 */
	public void loadTutorialScene() {
		tutorialScene = new TutorialScene();
		setScene(tutorialScene, ResourceManager.getIntance().creditsMusic);
		currentScene.createScene();
	}

	/**
	 * return to menu
	 */
	public void returnToMenuScene() {
		menuScene = new MainMenuScene();
		setScene(menuScene, ResourceManager.getIntance().titleMusic);
		currentScene.createScene();
	}

	/**
	 * load game credits
	 */
	public void loadCreditsScene() {
		levelScene = new CreditsMenuScene();
		setScene(levelScene, ResourceManager.getIntance().creditsMusic);
		currentScene.createScene();
	}

	/**
	 * load level selector
	 */
	public void loadLevelSelectScene() {
		levelScene = new LevelSelectMenuScene();
		setScene(levelScene, null);
		currentScene.createScene();
	}

	/**
	 * set the scene to the game
	 * 
	 * @param levelNumber
	 */
	public void setGameScene(int levelNumber) {

		// if a daydream is mapped to this level, then show the daydream scene before the level begins
		if (GameConfiguration.daydreamLevelsMapping.keySet().contains(levelNumber)) {
			this.loadDayDreamScene(levelNumber);
		} else {
			playGameScene(levelNumber);
		}
	}

	/**
	 * play the arcade scene
	 */
	public void playArcadeScene() {

		// game stats become reset and most recent level is set
		GameStatus.setMostRecentlLevel(GameConfiguration.arcadeLevelNumber);
		GameStatus.resetGameLevelStats();
		GameStatus.flagCurrentLevelTime();

		gameScene = new ArcadeScene();
		gameScene.setGameLevel(GameConfiguration.arcadeLevelNumber);
		setScene(gameScene, null);
		currentScene.createScene();
	}

	/**
	 * set the scene to play the game! (after possible daydream scene)
	 * 
	 * @param levelNumber
	 */
	public void playGameScene(int levelNumber) {
		
		// if we're in the last level one level before what is the "arcade" level then we've finished the game
		if (levelNumber == (GameConfiguration.numberLevels - 1)) {
			GameStatus.setMostRecentlLevel(1);
			gameScene = new EndScene();
			gameScene.setGameLevel(levelNumber);
			setScene(gameScene, ResourceManager.getIntance().endingMusic);
			currentScene.createScene();
		} else {

			// if they most recently played the arcade level then it's back to the most recent highest completed level to play
			if (levelNumber == GameConfiguration.arcadeLevelNumber) {
				levelNumber = GameStatus.getHighestCompletedLevel();
			}
			
			// game stats become reset and most recent level is set
			GameStatus.setMostRecentlLevel(levelNumber);
			GameStatus.resetGameLevelStats();
			GameStatus.flagCurrentLevelTime();
			
			gameScene = new GameScene();
			gameScene.setGameLevel(levelNumber);
			setScene(gameScene, null);
			currentScene.createScene();
		}
	}

	/**
	 * load a daydream scene
	 * 
	 * @param level
	 */
	public void loadDayDreamScene(int level) {
		dayDreamScene = new DayDreamScene();
		dayDreamScene.setLevelToPlayNext(level);
		setScene(dayDreamScene, ResourceManager.getIntance().daydreamMusic);
		currentScene.createScene();
	}

	/**
	 * show current level status
	 * 
	 * @param status
	 */
	public void loadLevelStatusScene(String status) {

		GameStatus.flagCurrentLevelTime();
		levelScene = new LevelStatusScene();
		levelScene.setStatus(status);

		if (status.equals("dead")) {
			setScene(levelScene, ResourceManager.getIntance().deadMusic);
		} else {
			setScene(levelScene, ResourceManager.getIntance().finishedMusic);
		}
		currentScene.createScene();
	}

	/**
	 * show current level status for arcade mode
	 * 
	 * @param status
	 */
	public void loadArcadeStatusScene(String status) {

		GameStatus.flagCurrentLevelTime();
		levelScene = new ArcadeStatusScene();
		levelScene.setStatus(status);

		if (status.equals("dead")) {
			setScene(levelScene, ResourceManager.getIntance().deadMusic);
		} else {
			setScene(levelScene, ResourceManager.getIntance().finishedMusic);
		}
		currentScene.createScene();
	}

	/**
	 * dispose current scene and apply the new one specified
	 * 
	 * @param scene
	 * @param music
	 */
	public void setScene(BaseScene scene, Music music) {

		// stop any music and play new scene music if it's present
		ResourceManager.getIntance().stopAllMusic();
		if (music != null) {
			music.seekTo(0);
			music.setVolume(0.2f);
			music.resume();
		}

		// dispose and load new scene
		if (currentScene != null) {
			currentScene.disposeScene();
		}
		engine.setScene(scene);
		currentScene = scene;
	}

	/**
	 * get scene manager via singleton
	 * 
	 * @return
	 */
	public static SceneManager getInstance() {
		return INSTANCE;
	}

	/**
	 * get the current scene the game is playing
	 * 
	 * @return
	 */
	public BaseScene getCurrentScene() {
		return currentScene;
	}

	/**
	 * play the same level again
	 */
	public void playLevelAgain() {
		playGameScene(GameStatus.getMostRecentLevel());
	}

	/**
	 * if the next level after the one the player has most recently played then return true
	 * 
	 * @return
	 */
	public boolean hasNextLevelAvailable() {
		if (GameStatus.levelStatusByLevelNumber(GameStatus.getMostRecentLevel() + 1) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * go to the highest level that the player has completed and play it
	 */
	public void playNextLevel() {
		if (GameStatus.levelStatusByLevelNumber(GameStatus.getMostRecentLevel() + 1) > 0) {
			setGameScene(GameStatus.getMostRecentLevel() + 1);
		} else {
			setGameScene(GameStatus.getMostRecentLevel());
		}
	}
}