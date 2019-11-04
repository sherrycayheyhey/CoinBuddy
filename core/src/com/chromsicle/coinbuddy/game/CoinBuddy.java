package com.chromsicle.coinbuddy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CoinBuddy extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man; //uses an array because there's multiple sprites we want to show for him
	int manState = 0; //this is for animating the different states
	int pause = 0;
	float gravity = 0.2f; //used for the gravity physics
	float velocity = 0; //used for the gravity physics
	int manY = 0; //y-position of the dude

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
	}

	//this gets run over and over again until the game is finished
	@Override
	public void render () {
		//get the background to show up on the screen
		batch.begin();
		//draw the background
		//texture, xy position, length/width
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		//make him jump
		//if the screen is touch, this will run once
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


		//draw the coin dude
		//to position it in the middle you have to / 2 but also account for his own dimensions
		batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		batch.end();
	}

	//we won't really use this but it's normally used when you want to start a new game
	@Override
	public void dispose () {
		batch.dispose();
	}
}
