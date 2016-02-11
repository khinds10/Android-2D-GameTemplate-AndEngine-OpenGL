package com.kevinhinds.spacebotsfree;

import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;

/**
 * all constant values for game here pretty please
 * 
 * @author khinds
 */
@SuppressLint("UseSparseArrays")
public class GameConfiguration {

	// player animated sprite tile frames
	public static int playerAnimationSpeed = 110;
	public static int playerStartLevelFrame = 22;
	public static int playerFaceRightFrame = 16;
	public static int playerFaceLeftFrame = 17;
	public static int playerKneelRightFrame = 18;
	public static int playerKneelLeftFrame = 19;
	public static int playerJumpRightFrame = 20;
	public static int playerJumpLeftFrame = 21;

	// animated frames walking right begin to end
	public static int walkRightBeginFrame = 8;
	public static int walkRightEndFrame = 15;

	// animated frames walking left begin to end
	public static int walkLeftBeginFrame = 0;
	public static int walkLeftEndFrame = 7;

	// player sprite map
	public static int playerMapColumns = 8;
	public static int playerMapRows = 3;

	// player sprite velocities
	public static int playerWalkingVelocity = 4;
	public static int playerJumpVelocity = -7;
	public static int playerFallingVelocity = 4;
	public static int playerFloatingVelocity = 8;
	public static int playerFallingChangeDirectionVelocity = 1;
	public static int playerHurtVelocity = 3;
	public static int playerStopWhileFallingVelocity = 2;
	public static int playerContinueMovingWhileFallingVelocity = 2;
	public static int playerFallingContinueDirectionVelocity = 3;

	// player basic weapons identifiers
	public static enum playerWeapons {
		BLUE_BULLET, GREEN_BULLET, YELLOW_BULLET, PINK_PHASER, YELLOW_DOUBLE_PHASER, YELLOW_PHASER, RED_PHASER, ORANGE_PHASER
	}

	// set how much energy the player has starting the level and how much each gun shot decreases that energy by default
	public static int playerStartingEnergy = 15;
	public static int playerEnergyShotAmount = 1;
	public static int playerKneelingShotAmount = 5;

	// set how much life the player has starting the level and how much each damage decreases that life by default
	public static int playerStartingLife = 2;
	public static int playerLifeDamageAmount = 1;

	// player advanced abilities mapped to token IDs
	public static Map<Integer, String> playerAbilitiesTokensMapping = new HashMap<Integer, String>();

	static {
		playerAbilitiesTokensMapping = new HashMap<Integer, String>();
		playerAbilitiesTokensMapping.put(0, "RELOAD"); // White
		playerAbilitiesTokensMapping.put(1, "BOMB"); // Purple
		playerAbilitiesTokensMapping.put(2, "JUMP"); // Yellow
		playerAbilitiesTokensMapping.put(3, "LIFE"); // Green
		playerAbilitiesTokensMapping.put(4, "BRIDGE"); // Blue
		playerAbilitiesTokensMapping.put(5, "FLARE"); // Red
	}

	// platform sprite map
	public static int platformMapColumns = 22;
	public static int platformMapRows = 13;

	// water tiles are deadly so have a list to check if the player comes in contact with any of them
	public static String[] waterTiles = { "242", "243", "244", "245", "246", "247", "264", "265", "266", "267", "268", "269" };

	// item sprite map
	public static int itemMapColumns = 6;
	public static int itemMapRows = 1;

	// item button sprite map
	public static int itemButtonMapColumns = 6;
	public static int itemButtonMapRows = 1;

	// ship pieces sprite map
	public static int pieceMapColumns = 17;
	public static int pieceMapRows = 27;
	public static int pieceMapTileSize = 12;

	// bullet sprite map
	public static int bulletMapColumns = 3;
	public static int bulletMapRows = 3;
	public static int bulletVelocity = 50;

	// bomb sprite map
	public static int bombMapColumns = 7;
	public static int bombMapRows = 5;
	public static int bombAnimationSpeed = 15;

	// flare sprite map
	public static int flareMapColumns = 10;
	public static int flareMapRows = 10;
	public static int flareAnimationSpeed = 15;
	public static int flareVelocity = 6;

	// player life and energy meter map
	public static int lifeMapColumns = 5;
	public static int lifeMapRows = 1;

	public static int energyMapColumns = 1;
	public static int energyMapRows = 10;

	// actor sprite map and default velocities
	public static int actorMapColumns = 8;
	public static int actorMapRows = 30;
	public static int actorAnimationSpeed = 200;
	public static float actorMovementSpeed = 2;

	// explosions map and default options
	public static int explosionAnimationSpeed = 15;
	public static int explosionMapColumns = 14;
	public static int explosionMapRows = 17;
	public static int explosionDefault = 1;
	public static int flameMapColumns = 6;
	public static int flameMapRows = 5;
	public static int flameAnimationSpeed = 100;

	// sprite basic state identifiers
	public static enum State {
		UP, DOWN, LEFT, RIGHT, STOP
	}

	// how far up and off the screen the player can jump
	public static int offscreenJumpHeight = -150;

	// how tall is the bottom player controls area
	public static int buttonControlsHeight = 82;

	// level information including 1 for the arcade level itself
	public static int numberLevels = 21;

	// how many levels are in the free version
	public static int totalNumberFreeLevels = 5;

	// special level number for the arcade scene
	public static int arcadeLevelNumber = 21;

	// which villains appear in the arcade mode
	public static int[] arcadeVillains = { 1, 7, 18, 22, 24, 29 };

	// number of seconds to play for arcade mode complete
	public static int arcadeNumberSeconds = 45;

	// array of all the pieces to collect to win the game
	public static String shipPiecesToCollect = "8,25,42,58,59,60,75,76,77,92,93,94,109,110,111,126,127,128,140,141,142,143,144,145,146,147,148," + "157,158,159,160,161,162,163,164,165,173,174,175,176,177,178,179,180,181,182,191,192,193,194,195,196,197,198,199,205,206,207,208,209," + "210,211,212,213,214,215,216,217,218,219,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,239,240,241,242,243,244,245,246,247," + "248,249,250,251,252,253,255,256,257,258,259,260,261,262,263,264,265,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,282,283," + "284,285,286,287,288,292,293,294,296,296,297,298,299,300,301,302,309,311,312,313,314,315,316,317,318,319,326,328,329,330,331,332,333,334,335,336," + "345,346,347,348,349,350,351,362,363,364,365,366,367,368,381,382,383,384,385,405,410,427,444,447,453,456,439,422,402,396";

	// list of pieces to collect in each level, starting @ index 1 for readability
	public static String[] levelPieces = { "", "8,25,42,58,59,60", "75,76,77,92,93,94,348", "109,110,111,126,127,396", "128,140,141,142,143,402", "144,145,146,147,148,422,349", "157,158,159,160,161,439", "162,163,164,165,173,456,351", "174,175,176,177,178,453,362", "179,180,181,182,191,447", "192,193,194,195,427,444", "196,197,198,199,205,410,330", "206,207,208,209,210,405,331", "211,212,213,214,215,216,335", "217,218,219,222,223,224", "225,226,227,228,229,230,366", "231,232,233,234,235,236", "239,240,241,242,243,244,350", "245,246,247,248,249,250", "251,252,336,345,346,347", "253,255,256,257,258,259,364", "261,262,263,264,265,266", "267,268,269,270,271,272,333", "273,274,275,276,277,260,332", "278,279,280,281,282,385", "283,284,285,286,287,384", "288,292,293,294,296,296,334", "297,298,299,300,301,383,365", "302,309,311,367,368,381", "312,313,314,315,316,382,363", "317,318,319,326,328,329" };

	// player daydream begin levels mapped out
	public static Map<Integer, String> daydreamLevelsMapping = new HashMap<Integer, String>();

	static {
		daydreamLevelsMapping = new HashMap<Integer, String>();
		daydreamLevelsMapping.put(1, "Must have been days, 3 days? - - The wreckage and the sound of twisted metal. - I have to rebuild my ship. - - I think I can see something up ahead. - - ");
		daydreamLevelsMapping.put(4, "We broke off route around New Ceres. - Vade Envoy should be on the transponder by now, - but still just static. - This fog, the air could almost be breathable. - Where could they be!? - - ");
		daydreamLevelsMapping.put(9, "The Nyri Colony set up base here. - I see the markings. - Apart from the poisonous water and fumes - it's no surprise everyone is gone!");
		daydreamLevelsMapping.put(14, "I got something on the radio, - I swear it could be Rice Typho's voice. - There could be anything out here though, - this sector hasn't been explored - since  we lost Vega VII. - - God that was a long time ago. - - ");
		daydreamLevelsMapping.put(17, "Plasma Conduit and 4 converters - just found the last one - I swear I can get this thing to fly - - Vade always always warned me - When you're out there - you need to know your ship piece by piece! - ");
		daydreamLevelsMapping.put(20, "It's Finished! - - Computer Systems up - - Flight Navigation up - - Engines are firing! - - ");
	}
}