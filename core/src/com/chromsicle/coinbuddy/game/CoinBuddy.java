package com.chromsicle.coinbuddy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinBuddy extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man; //uses an array because there's multiple sprites we want to show for him
	Texture dizzy;
	int manState = 0; //this is for animating the different states
	int pause = 0;
	float gravity = 0.2f; //used for the gravity physics
	float velocity = 0; //used for the gravity physics
	int manY = 0; //y-position of the dude
	Rectangle manRect; //used for detection

	BitmapFont font; //used to display the score
	int score = 0;

	int gameState = 0;

	//coins
	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<>(); //used for detection
	Texture coin;
	int coinCount; //use to ensure proper spacing between coins

	//bombs
	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<>(); //used for detection
	Texture bomb;
	int bombCount; //use to ensure proper spacing between bombs

	Random random;

	//this is called when you open the game for the very first time
	//it gets your game ready to go, things you only want to do one time
	@Override
	public void create () {
		batch = new SpriteBatch();
		//set up the background
		background = new Texture("bg.png");

		//set up the man frames
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		//set up the dude y-position value
		manY = Gdx.graphics.getHeight() / 2;

		//set up the coin
		coin = new Texture("coin.png");
		//set up the bomb
		bomb = new Texture("bomb.png");
		//set up random
		random = new Random();
		//set up the font
		font = new BitmapFont();
		font.setColor(Color.WHITE); //font color
		font.getData().setScale(10); //font size

		dizzy = new Texture("dizzy-1.png");
	}

	//method for making multiple coins going across the screen
	public void makeCoin() {
		float height = random.nextFloat() * Gdx.graphics.getHeight(); //random for different heights
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	//method for making multiple bombs going across the screen
	public void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight(); //random for different heights
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	//this gets run over and over again until the game is finished
	@Override
	public void render () {
		//get the background to show up on the screen
		batch.begin();
		//draw the background
		//texture, xy position, length/width
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		//game state
		if (gameState == 1) {
			//game is live

			//coins, if there's less than 100, make a new coin
			if (coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}

			//clear out everything inside the coin rectangles
			coinRectangles.clear();
			//draw the coins
			for (int i = 0; i < coinXs.size(); i++) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				//update coinX is move slowly to the left
				coinXs.set(i, coinXs.get(i) - 4);
				//draw the rectangles for detection around the coins
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			//bombs, if there's less than 100, make a new bomb
			if (bombCount < 250) {
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}

			//clear out everything inside the bomb rectangles
			bombRectangles.clear();
			//draw the bombs
			for (int i = 0; i < bombXs.size(); i++) {
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				//update bombX is move slowly to the left
				bombXs.set(i, bombXs.get(i) - 6); //bombs move a bit faster than coins
				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			//make him jump
			//if the screen is touched, this will run once
			if (Gdx.input.justTouched()) {
				velocity = -10;
			}

			//the images cycle really fast so slow it
			if (pause < 8) {
				pause++;
			} else {
				pause = 0;
				//cycle through all the states
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			//gravity physics
			velocity += gravity;
			manY -= velocity;

			//if the guy reaches the bottom, keep him there
			if (manY <= 0) {
				manY = 0;
			}
		} else if (gameState == 0) {
			//game is waiting to start
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			//game is over, reset everything to a starting position
			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount = 0;
			}
		}

		//if in the game over state, draw the dizzy guy, if not,
		if (gameState == 2) {
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		} else {
			//draw the coin dude
			//to position it in the middle you have to / 2 but also account for his own dimensions
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}

		//set up the man's rectangle
		manRect = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

		//check for coin collision
		//go through all the coins
		for(int i = 0; i  < coinRectangles.size(); i++) {
			if (Intersector.overlaps(manRect, coinRectangles.get(i))) {
				//Gdx.app.log("Coin: ", "COLLISION!");
				score++;

				//once the coin is touched remove it
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		//check for bomb collision
		for(int i = 0; i  < bombRectangles.size(); i++) {
			if (Intersector.overlaps(manRect, bombRectangles.get(i))) {
				//Gdx.app.log("Bomb: ", "BOOM!");
				gameState = 2;

			}
		}

		//draw the font on the screen for the score
		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}

	//we won't really use this but it's normally used when you want to start a new game
	@Override
	public void dispose () {
		batch.dispose();
	}
}
