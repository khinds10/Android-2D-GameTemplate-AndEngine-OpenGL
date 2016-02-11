package com.kevinhinds.spacebotsfree.player;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;

import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.scene.GameScene;
import com.kevinhinds.spacebotsfree.status.GameStatus;

/**
 * create controls for the character in the game
 * 
 * @author khinds
 */
public class Controls {

	private GameScene scene;
	private Player player;
	public TokenButton[] abilityButton;
	public Text[] abilityButtonCounts;
	public Text[] abilityButtonTitles;

	int abilityButtonHorizontalStart = 315;
	int abilityButtonHorizontalNext = 60;

	public TiledSprite lifeMeter;
	public Text lifeMeterLabel;

	public TiledSprite energyMeter;
	public Text energyMeterLabel;
	public Text currentAreaPlayedLabel;
	public Text currentAreaTime;
	public Text currentAreaMarksmanship;
	protected int currentLeveltime = 0;

	/**
	 * create the controls for the scene
	 * 
	 * @param scene
	 * @param player
	 */
	public Controls(GameScene scene, Player player, boolean isArcade) {
		this.scene = scene;
		this.player = player;
		createPlayerJoystick();
		createPlayerJump();
		createPlayerShoot();
		createPlayerAbilityButtons();
		displayInitialPlayerStats(isArcade);
	}

	/**
	 * display player stats at the beginning of the level such as hit points and gun strength and what area they're on
	 * 
	 * @param isArcade
	 */
	public void displayInitialPlayerStats(boolean isArcade) {

		lifeMeter = new TiledSprite(scene.camera.getWidth() - 100, 12, ResourceManager.getIntance().playerLifeRegion, scene.vbom);
		lifeMeter.setCurrentTileIndex(3);
		scene.gameHUD.attachChild(lifeMeter);

		lifeMeterLabel = new Text(scene.camera.getWidth() - 132, 10, ResourceManager.getIntance().gameFontTiny, "Life", ResourceManager.getIntance().vbom);
		scene.gameHUD.attachChild(lifeMeterLabel);

		energyMeter = new TiledSprite(scene.camera.getWidth() - 200, 12, ResourceManager.getIntance().playerEnergyRegion, scene.vbom);
		energyMeter.setCurrentTileIndex(2);
		scene.gameHUD.attachChild(energyMeter);

		energyMeterLabel = new Text(scene.camera.getWidth() - 255, 10, ResourceManager.getIntance().gameFontTiny, "Energy", ResourceManager.getIntance().vbom);
		scene.gameHUD.attachChild(energyMeterLabel);

		// set the area label to the current level played, else to "arcade" for arcade mode
		String areaLabel = "Area : " + Integer.toString(GameStatus.getMostRecentLevel());
		if (isArcade) {
			areaLabel = "Arcade";
		}
		currentAreaPlayedLabel = new Text(10, 10, ResourceManager.getIntance().gameFontMedium, areaLabel, ResourceManager.getIntance().vbom);
		scene.gameHUD.attachChild(currentAreaPlayedLabel);

		currentAreaTime = new Text(275, 10, ResourceManager.getIntance().gameFontTiny, "Time : 0", ResourceManager.getIntance().vbom);
		scene.gameHUD.attachChild(currentAreaTime);

		currentAreaMarksmanship = new Text(375, 10, ResourceManager.getIntance().gameFontTiny, "Accuracy: 100.0", ResourceManager.getIntance().vbom);
		scene.gameHUD.attachChild(currentAreaMarksmanship);
	}

	/**
	 * update onscreen level stats (each second from game scene TimerHandler)
	 */
	public void updateLevelStats() {

		// update that one more second has passed
		currentLeveltime = currentLeveltime + 1;
		currentAreaTime.setText("Time : " + Integer.toString(currentLeveltime));

		// calculate marksmanship
		int levelHits = GameStatus.getGameLevelStats(GameStatus.levelStatsType.HITS);
		int levelShots = GameStatus.getGameLevelStats(GameStatus.levelStatsType.SHOTS);
		float marksmanshipRating = 100;
		if (levelShots > 0) {
			marksmanshipRating = (levelHits * 100 / levelShots);
		}
		currentAreaMarksmanship.setText("Accuracy: " + Float.toString(marksmanshipRating));
	}

	/**
	 * for a given percent of life left update the life meter on the heads up display to say so
	 * 
	 * @param percentLife
	 */
	public void setLifeMeterLevelByPercent(int percentLife) {
		if (percentLife > 100) {
			lifeMeter.setCurrentTileIndex(4);
		} else if (percentLife > 75) {
			lifeMeter.setCurrentTileIndex(3);
		} else if (percentLife > 50) {
			lifeMeter.setCurrentTileIndex(2);
		} else if (percentLife > 25) {
			lifeMeter.setCurrentTileIndex(1);
		} else {
			lifeMeter.setCurrentTileIndex(0);
		}
	}

	/**
	 * for a given percent of energy left update the energy meter on the heads up display to say so
	 * 
	 * @param percentEnergy
	 */
	public void setEnergyLevelByPercent(int percentEnergy) {
		if (percentEnergy > 200) {
			energyMeter.setCurrentTileIndex(0);
		} else if (percentEnergy > 100) {
			energyMeter.setCurrentTileIndex(1);
		} else if (percentEnergy > 86) {
			energyMeter.setCurrentTileIndex(2);
		} else if (percentEnergy > 72) {
			energyMeter.setCurrentTileIndex(3);
		} else if (percentEnergy > 58) {
			energyMeter.setCurrentTileIndex(4);
		} else if (percentEnergy > 44) {
			energyMeter.setCurrentTileIndex(5);
		} else if (percentEnergy > 30) {
			energyMeter.setCurrentTileIndex(6);
		} else if (percentEnergy > 16) {
			energyMeter.setCurrentTileIndex(7);
		} else if (percentEnergy > 2) {
			energyMeter.setCurrentTileIndex(8);
		} else {
			energyMeter.setCurrentTileIndex(9);
		}
	}

	/**
	 * create one ability button on the HUD for each possible kind of token you can aquire in the level
	 */
	public void createPlayerAbilityButtons() {
		abilityButton = new TokenButton[6];
		abilityButtonCounts = new Text[6];
		abilityButtonTitles = new Text[6];

		int horizontalPosition = abilityButtonHorizontalStart;
		int horizontalTitlesPosition = abilityButtonHorizontalStart + 50;
		for (int i = 0; i < abilityButton.length; i++) {

			// create special ability titles
			abilityButtonTitles[i] = new Text(scene.camera.getWidth() - horizontalTitlesPosition + 40, scene.camera.getHeight() - 68, ResourceManager.getIntance().gameFontTiny, GameConfiguration.playerAbilitiesTokensMapping.get(i), ResourceManager.getIntance().vbom);
			abilityButtonTitles[i].setVisible(false);
			scene.gameHUD.attachChild(abilityButtonTitles[i]);

			// create special ability counts
			abilityButtonCounts[i] = new Text(scene.camera.getWidth() - horizontalPosition + 40, scene.camera.getHeight() - 18, ResourceManager.getIntance().gameFontTiny, "0", ResourceManager.getIntance().vbom);
			abilityButtonCounts[i].setVisible(false);
			scene.gameHUD.attachChild(abilityButtonCounts[i]);

			// create special ability buttons
			abilityButton[i] = new TokenButton(i, 0, i, scene.camera.getWidth() - horizontalPosition, scene.camera.getHeight() - 56, ResourceManager.getIntance().itemButtonRegion, scene.vbom) {
				@Override
				public boolean onAreaTouched(final TouchEvent event, final float x, final float y) {
					if (event.isActionDown()) {
						if (countAvailable > 0) {
							switch (this.playerAbilityID) {
							case 0: // RELOAD
								ResourceManager.getIntance().powerUpSound.play();
								player.reload();
								use();
								updateTokenButtonText(this.playerAbilityID);
								break;
							case 1: // BOMB
								ResourceManager.getIntance().bombSound.play();
								player.tokenAbilityBomb();
								use();
								updateTokenButtonText(this.playerAbilityID);
								break;
							case 2: // JUMP
								if (!player.isJumping) {
									ResourceManager.getIntance().superJumpSound.play();
									player.tokenAbilityJump();
									use();
									updateTokenButtonText(this.playerAbilityID);
								}
								break;
							case 3: // LIFE
								ResourceManager.getIntance().tokenSound.play();
								player.revive();
								use();
								updateTokenButtonText(this.playerAbilityID);
								break;
							case 4: // BRIDGE
								ResourceManager.getIntance().thumpSound.play();
								player.tokenAbilityCreateBridge();
								use();
								updateTokenButtonText(this.playerAbilityID);
								break;
							case 5: // FLARE
								ResourceManager.getIntance().flareSound.play();
								player.tokenAbilityFlare(scene);
								use();
								updateTokenButtonText(this.playerAbilityID);
								break;
							default:
								break;
							}
						}
					}
					return true;
				}

				@Override
				protected void preDraw(GLState glstate, Camera camera) {
					super.preDraw(glstate, camera);
					glstate.enableDither();
				}
			};
			abilityButton[i].attachToHUD(this.scene);
			horizontalPosition += abilityButtonHorizontalNext;
			horizontalTitlesPosition += abilityButtonHorizontalNext;
		}
	}

	/**
	 * based on token id collected from game level, update the cooresponding control to show it's available
	 * 
	 * @param tokenCollected
	 */
	public void updatePlayerAbilityButtons(int tokenCollected) {
		abilityButton[tokenCollected].aquire();
		updateTokenButtonText(tokenCollected);
	}

	/**
	 * update the text next to the token button to show how many the player has, else make it disappear if zero are left
	 * 
	 * @param tokenCollected
	 */
	public void updateTokenButtonText(int tokenCollected) {
		int countAvailable = abilityButton[tokenCollected].countAvailable;
		abilityButtonCounts[tokenCollected].setText(String.valueOf(countAvailable));
		if (countAvailable > 0) {
			abilityButtonCounts[tokenCollected].setVisible(true);
			abilityButtonTitles[tokenCollected].setVisible(true);
		} else {
			abilityButtonCounts[tokenCollected].setVisible(false);
			abilityButtonTitles[tokenCollected].setVisible(false);
		}
	}

	/**
	 * create the (d-pad) style joystick
	 */
	private void createPlayerJoystick() {

		Sprite right = new Sprite(90, scene.camera.getHeight() - 80, ResourceManager.getIntance().controlRightRegion, scene.vbom) {

			@Override
			public boolean onAreaTouched(final TouchEvent event, final float x, final float y) {
				if (event.isActionDown()) {
					player.moveRight();
				} else if (event.isActionUp()) {
					player.stop();
				}
				return true;
			}

			@Override
			protected void preDraw(GLState glstate, Camera camera) {
				super.preDraw(glstate, camera);
				glstate.enableDither();
			}
		};
		scene.gameHUD.registerTouchArea(right);
		scene.gameHUD.attachChild(right);

		Sprite left = new Sprite(10, scene.camera.getHeight() - 80, ResourceManager.getIntance().controlLeftRegion, scene.vbom) {

			@Override
			public boolean onAreaTouched(final TouchEvent event, final float x, final float y) {
				if (event.isActionDown()) {
					player.moveLeft();
				} else if (event.isActionUp()) {
					player.stop();
				}
				return true;
			}

			@Override
			protected void preDraw(GLState glstate, Camera camera) {
				super.preDraw(glstate, camera);
				glstate.enableDither();
			}
		};
		scene.gameHUD.registerTouchArea(left);
		scene.gameHUD.attachChild(left);

		Sprite down = new Sprite(scene.camera.getWidth() - 260, scene.camera.getHeight() - 80, ResourceManager.getIntance().controlDownRegion, scene.vbom) {

			@Override
			public boolean onAreaTouched(final TouchEvent event, final float x, final float y) {
				if (event.isActionDown()) {
					player.kneel();
				}
				return true;
			}

			@Override
			protected void preDraw(GLState glstate, Camera camera) {
				super.preDraw(glstate, camera);
				glstate.enableDither();
			}
		};
		scene.gameHUD.registerTouchArea(down);
		scene.gameHUD.attachChild(down);
	}

	/**
	 * create the player jump button the game HUD
	 */
	private void createPlayerJump() {
		Sprite jump = new Sprite(scene.camera.getWidth() - 90, scene.camera.getHeight() - 80, ResourceManager.getIntance().controlJumpRegion, scene.vbom) {

			@Override
			public boolean onAreaTouched(final TouchEvent event, final float x, final float y) {
				if (event.isActionDown()) {
					player.jump(GameConfiguration.playerJumpVelocity);
				}
				return true;
			}

			@Override
			protected void preDraw(GLState glstate, Camera camera) {
				super.preDraw(glstate, camera);
				glstate.enableDither();
			}
		};
		scene.gameHUD.registerTouchArea(jump);
		scene.gameHUD.attachChild(jump);
	}

	/**
	 * create the player shoot button the game HUD
	 */
	private void createPlayerShoot() {
		Sprite shoot = new Sprite(scene.camera.getWidth() - 175, scene.camera.getHeight() - 80, ResourceManager.getIntance().controlShootRegion, scene.vbom) {

			@Override
			public boolean onAreaTouched(final TouchEvent event, final float x, final float y) {
				if (event.isActionDown()) {
					player.shoot(scene);
				}
				return true;
			}

			@Override
			protected void preDraw(GLState glstate, Camera camera) {
				super.preDraw(glstate, camera);
				glstate.enableDither();
			}
		};
		scene.gameHUD.registerTouchArea(shoot);
		scene.gameHUD.attachChild(shoot);
	}
}