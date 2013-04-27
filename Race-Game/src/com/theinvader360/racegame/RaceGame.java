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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Rectangle;
 
public class RaceGame extends Game {
	public static final int VIRTUAL_WIDTH = 240;
	public static final int VIRTUAL_HEIGHT = 320;
	public static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;
	public static Rectangle viewport;
	
	MenuScreen menuScreen;
	GameScreen gameScreen;

	@Override
	public void create() {
		menuScreen = new MenuScreen(this);
		gameScreen = new GameScreen(this);
		setScreen(menuScreen);
	}
}
