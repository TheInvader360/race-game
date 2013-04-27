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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class MenuScreen implements Screen {

	private RaceGame game;
    private Stage stage;
    private Skin skin;
    private Table table;
    private OrthographicCamera cam;

    public MenuScreen(final RaceGame game) {
        this.game = game;
        int width = RaceGame.VIRTUAL_WIDTH;
        int height = RaceGame.VIRTUAL_HEIGHT;
        this.stage = new Stage(width, height, true);
        
        cam = new OrthographicCamera(RaceGame.VIRTUAL_WIDTH, RaceGame.VIRTUAL_HEIGHT);
 
        Gdx.input.setInputProcessor(stage);
        Table table = getTable();
        table.add("Race Game").spaceBottom(30);
        table.row();
        // register the "Play" button
        TextButton playButton = new TextButton("Play", getSkin());
        playButton.setClickListener(new ClickListener() {
        	@Override
            public void click(Actor actor, float x, float y) {
        		game.setScreen(game.gameScreen);
            }
        });
        table.add(playButton).size(100, 50).uniform().spaceBottom(10);
        table.row();
    }

    private Skin getSkin() {
        if(skin == null) {
            FileHandle skinFile = Gdx.files.internal("uiskin.json");
            skin = new Skin(skinFile);
        }
        return skin;
    }

    private Table getTable() {
        if(table == null) {
            table = new Table(getSkin());
            table.setFillParent(true);
            stage.addActor(table);
        }
        return table;
    }

    @Override
    public void render(float delta) {
        // update camera
        cam.update();
        cam.apply(Gdx.gl10);
        // set viewport
        Gdx.gl.glViewport((int)RaceGame.viewport.x, (int)RaceGame.viewport.y, (int)RaceGame.viewport.width, (int)RaceGame.viewport.height);
        // clear previous frame
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    	
    	// (1) process logic
        stage.act(delta);
        // (2) draw result
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public void hide() {
    	dispose();
    }
    
    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
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
    public void pause() {
    }
    
    @Override
    public void resume() {
    }    
}
