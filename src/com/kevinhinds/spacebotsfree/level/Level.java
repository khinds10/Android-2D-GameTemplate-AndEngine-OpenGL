package com.kevinhinds.spacebotsfree.level;

import java.util.ArrayList;

import org.andengine.extension.physics.box2d.PhysicsWorld;

import com.kevinhinds.spacebotsfree.objects.Actor;
import com.kevinhinds.spacebotsfree.objects.Bullet;
import com.kevinhinds.spacebotsfree.objects.Flare;
import com.kevinhinds.spacebotsfree.objects.Item;
import com.kevinhinds.spacebotsfree.objects.Piece;
import com.kevinhinds.spacebotsfree.objects.Tile;
import com.kevinhinds.spacebotsfree.scene.GameScene;

/**
 * basic level object that attaches all actors and respective tiles requested for the game scene
 * 
 * @author khinds
 */
public class Level {

	public int width, height;
	public ArrayList<Tile> tiles = new ArrayList<Tile>();
	public ArrayList<Actor> actors = new ArrayList<Actor>();
	public ArrayList<Item> items = new ArrayList<Item>();
	public ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	public ArrayList<Flare> flares = new ArrayList<Flare>();
	public ArrayList<Piece> pieces = new ArrayList<Piece>();

	/**
	 * width of the level
	 * 
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * height of the level
	 * 
	 * @param height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * add a new tile to the level
	 * 
	 * @param t
	 */
	public void addTile(Tile t) {
		tiles.add(t);
	}

	/**
	 * add a new actor to the level
	 * 
	 * @param t
	 */
	public void addActor(Actor v) {
		actors.add(v);
	}

	/**
	 * add a new item to the level
	 * 
	 * @param t
	 */
	public void addItem(Item i) {
		items.add(i);
	}

	/**
	 * add a new piece to the level
	 * 
	 * @param p
	 */
	public void addPiece(Piece p) {
		pieces.add(p);
	}

	/**
	 * add a new flare to the level
	 * 
	 * @param f
	 */
	public void addFlare(Flare f) {
		flares.add(f);
	}

	/**
	 * add a new bullet to the level
	 * 
	 * @param b
	 */
	public void addBullet(Bullet b) {
		bullets.add(b);
	}

	/**
	 * find a particular tile by name
	 * 
	 * @param id
	 * @return
	 */
	public Tile getTileByName(String name) {
		for (Tile t : tiles)
			if (t.getName().equals(name))
				return t;
		return null;
	}

	/**
	 * find a particular actor by name
	 * 
	 * @param id
	 * @return
	 */
	public Actor getActorByName(String name) {
		for (Actor v : actors)
			if (v.getName().equals(name))
				return v;
		return null;
	}

	/**
	 * find a particular actor by name
	 * 
	 * @param name
	 * @return
	 */
	public Item getItemByName(String name) {
		for (Item i : items)
			if (i.getName().equals(name))
				return i;
		return null;
	}

	/**
	 * find a particular piece by name
	 * 
	 * @param name
	 * @return
	 */
	public Piece getPieceByName(String name) {
		for (Piece p : pieces)
			if (p.getName().equals(name))
				return p;
		return null;
	}

	/**
	 * find a particular bullet by name
	 * 
	 * @param name
	 * @return
	 */
	public Bullet getBulletByName(String name) {
		for (Bullet b : bullets)
			if (b.getName().equals(name))
				return b;
		return null;
	}

	/**
	 * find a particular flare by name
	 * 
	 * @param name
	 * @return
	 */
	public Flare getFlareByName(String name) {
		for (Flare f : flares)
			if (f.getName().equals(name))
				return f;
		return null;
	}

	/**
	 * for all the tiles, actors and items currently present in the current level, attach them to the scene in question
	 * 
	 * @param scene
	 * @param physicsWorld
	 */
	public void load(GameScene scene, PhysicsWorld physicsWorld) {
		for (Tile t : tiles) {
			t.createBodyAndAttach(scene, physicsWorld);
		}
		for (Actor a : actors) {
			a.createBodyAndAttach(scene, physicsWorld);
		}
		for (Item i : items) {
			i.createBodyAndAttach(scene, physicsWorld);
		}
		for (Piece p : pieces) {
			p.createBodyAndAttach(scene, physicsWorld);
		}
		scene.sortChildren();
	}
}