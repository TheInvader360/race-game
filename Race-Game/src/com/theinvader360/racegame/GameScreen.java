/*******************************************************************************
 *  Copyright 2012 Darren Tate
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/

package com.theinvader360.racegame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {
	private RaceGame game;
    private OrthographicCamera cam;
    private OrthographicCamera hudCam;
    private TextureRegion carTextureRegion;
    private TextureRegion roadTextureRegion;
    private TextureRegion grassTextureRegion;
    private TextureRegion finishTextureRegion;
    private TextureRegion hudLayoutTextureRegion;
    private TextureRegion progressIndicatorTextureRegion;
    private SpriteBatch spriteBatch;
    private SpriteBatch hudSpriteBatch;
	private Level level;
	private BitmapFont hudTimeFont;
	private BitmapFont hudSpeedFont;

	public GameScreen(RaceGame game) {
		this.game = game;
		TextureAtlas atlas;
		atlas = new TextureAtlas(Gdx.files.internal("images.pack"));
		carTextureRegion = atlas.findRegion("car");
		roadTextureRegion = atlas.findRegion("road");
		grassTextureRegion = atlas.findRegion("grass");
		finishTextureRegion = atlas.findRegion("finish");
		hudLayoutTextureRegion = atlas.findRegion("hudLayout");
		progressIndicatorTextureRegion = atlas.findRegion("progressIndicator");
		level = new Level(RaceGame.VIRTUAL_WIDTH, 60, 90, 1500, 10);
		cam = new OrthographicCamera(RaceGame.VIRTUAL_WIDTH, RaceGame.VIRTUAL_HEIGHT);
		hudCam = new OrthographicCamera(RaceGame.VIRTUAL_WIDTH, RaceGame.VIRTUAL_HEIGHT);
	    spriteBatch = new SpriteBatch();
	    hudSpriteBatch = new SpriteBatch();
	    hudTimeFont = new BitmapFont(Gdx.files.internal("hudTime.fnt"), Gdx.files.internal("hudTime.png"), false);
	    hudSpeedFont = new BitmapFont(Gdx.files.internal("hudSpeed.fnt"), Gdx.files.internal("hudSpeed.png"), false);
    }

	@Override
	public void render(float deltaTime) {
		handleInput(deltaTime);
		checkCollisions();
		renderPlayarea();
		renderHud();
	}

	private void renderPlayarea() {
		// camera focus: x-axis middle of level, y-axis is above car, z-axis ignored
		cam.position.set(RaceGame.VIRTUAL_WIDTH/2, level.getCar().getPosition().y + RaceGame.VIRTUAL_HEIGHT/2 - 20, 0.0f);
		// clear screen
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	   	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	   	// call cam.update() after changes to cam (code.google.com/p/libgdx/wiki/OrthographicCamera)
	   	cam.update();
	   	// and then set projection and model-view matrix
	   	cam.apply(Gdx.gl10);
        // set viewport
        Gdx.gl.glViewport((int)RaceGame.viewport.x, (int)RaceGame.viewport.y, (int)RaceGame.viewport.width, (int)RaceGame.viewport.height);
	   	// setProjectionMatrix before drawing sprites (code.google.com/p/libgdx-users/wiki/Sprites)
	   	spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        spriteBatch.draw(roadTextureRegion, 0, 0, RaceGame.VIRTUAL_WIDTH, level.getLevelLength()*level.getRowHeight());
        for(GrassyArea grassyArea: level.getGrassyAreas()) spriteBatch.draw(grassTextureRegion, grassyArea.getBounds().x, grassyArea.getBounds().y, grassyArea.getBounds().width, grassyArea.getBounds().height);
    	spriteBatch.draw(finishTextureRegion, level.getFinishLine().getBounds().x, level.getFinishLine().getBounds().y, level.getFinishLine().getBounds().width, level.getFinishLine().getBounds().height);
    	spriteBatch.draw(carTextureRegion, level.getCar().getPosition().x, level.getCar().getPosition().y, Car.WIDTH, Car.HEIGHT);
		spriteBatch.end();
	}
	
	private void renderHud() {
		// camera focus: x-axis middle of level, y-axis middle of level, z-axis ignored
		hudCam.position.set(RaceGame.VIRTUAL_WIDTH/2, RaceGame.VIRTUAL_HEIGHT/2, 0.0f);
		// update camera
	    hudCam.update();
	    hudCam.apply(Gdx.gl10);
	    // set the projection matrix
	    hudSpriteBatch.setProjectionMatrix(hudCam.combined);
	    // draw something
	    hudSpriteBatch.begin();
	    hudSpriteBatch.draw(hudLayoutTextureRegion, 0, RaceGame.VIRTUAL_HEIGHT - 42, RaceGame.VIRTUAL_WIDTH, 42);
    	hudTimeFont.draw(hudSpriteBatch, level.getTimer().getElapsed() , 10.0f, RaceGame.VIRTUAL_HEIGHT - 6);
    	hudSpeedFont.draw(hudSpriteBatch, level.getCar().getMph(), RaceGame.VIRTUAL_WIDTH/2 - 23, RaceGame.VIRTUAL_HEIGHT - 4);
    	// x-position is a value between 10 and 223 (based on car progress through the level)
    	hudSpriteBatch.draw(progressIndicatorTextureRegion, level.getProgress(10,223), RaceGame.VIRTUAL_HEIGHT -38, 7, 7);
	    hudSpriteBatch.end();
	}

	private void handleInput(float deltaTime) {
		if (Gdx.app.getType() == Application.ApplicationType.Android) {
			if (-Gdx.input.getAccelerometerX() < 0) {
				if (0 < level.getCar().getBounds().x) {
					level.getCar().steerCar(deltaTime, -Gdx.input.getAccelerometerX() * 50);
				}
			}
			else if (-Gdx.input.getAccelerometerX() > 0) {
				if (level.getCar().getBounds().x + level.getCar().getBounds().width < level.getScreenWidth()) {
					level.getCar().steerCar(deltaTime, -Gdx.input.getAccelerometerX() * 50);
				}
			}
			else {
				level.getCar().steerCar(deltaTime, -Gdx.input.getAccelerometerX() * 50);
			}
		}

		if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				if (0 < level.getCar().getBounds().x) {
					level.getCar().steerCar(deltaTime, -100f);
				}
			}
			else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				if (level.getCar().getBounds().x + level.getCar().getBounds().width < level.getScreenWidth()) {
					level.getCar().steerCar(deltaTime, 100f);
				}
			}
			else {
				level.getCar().steerCar(deltaTime, 0f);
			}
		}
	}
	
	// currently super inefficient, checking all blocks in the level is unnecessary
	// revisit later, just check for collisions with blocks near car position...
	private void checkCollisions() {
		int numGrassyAreas = level.getGrassyAreas().size;
		for (int i = 0; i < numGrassyAreas; i++) {
			GrassyArea grassyArea = level.getGrassyAreas().get(i);
			if (grassyArea.getBounds().overlaps(level.getCar().getBounds())) {
				level.getCar().contactGrass();
				Gdx.input.vibrate(100);
			}
		}
		if (level.getFinishLine().getBounds().overlaps(level.getCar().getBounds())) {
			level.getCar().contactFinishLine();
			level.getTimer().stop();
			levelEndSequence();
		}	
	}
	
	private void levelEndSequence() {
		// TODO - animated scene followed by transition to result screen
		game.setScreen(game.menuScreen);
	}

    @Override
    public void resize(int width, int height) {
        // calculate new viewport
        float aspectRatio = (float)width/(float)height;
        float scale = 1f;
        Vector2 crop = new Vector2(0f, 0f); 
        
        if(aspectRatio > RaceGame.ASPECT_RATIO)
        {
            scale = (float)height/(float)RaceGame.VIRTUAL_HEIGHT;
            crop.x = (width - RaceGame.VIRTUAL_WIDTH*scale)/2f;
        }
        else if(aspectRatio < RaceGame.ASPECT_RATIO)
        {
            scale = (float)width/(float)RaceGame.VIRTUAL_WIDTH;
            crop.y = (height - RaceGame.VIRTUAL_HEIGHT*scale)/2f;
        }
        else
        {
            scale = (float)width/(float)RaceGame.VIRTUAL_WIDTH;
        }

        float w = (float)RaceGame.VIRTUAL_WIDTH*scale;
        float h = (float)RaceGame.VIRTUAL_HEIGHT*scale;
        RaceGame.viewport = new Rectangle(crop.x, crop.y, w, h);
	}

    @Override
	public void show() {
		level.restartLevel();
	}
    
	@Override
	public void hide() {
	}
	@Override
	public void pause() {
	}
	@Override
	public void resume() {
	}
	@Override
	public void dispose() {
	}
}
