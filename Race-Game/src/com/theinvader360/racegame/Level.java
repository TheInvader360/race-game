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

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Level {

	private Car car;
	private Timer timer;
	private Array<GrassyArea> grassyAreas;
	private FinishLine finishLine;
	private int startPosX;
	private int startPosY;
	private int screenWidth;
	private int roadWidth;
	private int roadOffset;
	private int rowHeight;
	private int levelRows; // number of rows in level
	private int[] levelRowRoadOffset; // keep track of roadOffset for each row
	private Random randomGenerator = new Random();

	public Level(int screenWidth, int roadWidth, int roadOffset, int levelLength, int rowHeight) {
		this.screenWidth = screenWidth;
		this.roadWidth = roadWidth;
		this.roadOffset = roadOffset;
		this.levelRows = levelLength;
		this.rowHeight = rowHeight;
		this.levelRowRoadOffset = new int[levelLength];
		this.grassyAreas = new Array<GrassyArea>();
		this.startPosX = (screenWidth/2)-(Car.WIDTH/2);
		this.startPosY = 20;
		this.car = new Car(new Vector2(startPosX, startPosY));
		this.timer = new Timer();
		generateOffsets();
		generateGrassyAreas();
		generateFinishLine();
		timer.start();
	}
	
	// TODO Generate new layout on restart for variety?
	public void restartLevel() {
		car.stopCar();
		car.setPosition(new Vector2(startPosX, startPosY));
		timer.reset();
		timer.start();
	}
		
	private void generateOffsets() {
		for (int row=0; row<levelRows; row++) { // for each row in the level
			levelRowRoadOffset[row] = roadOffset;
			// generate a random integer (between -4 and +4)
			int modifier = (randomGenerator.nextInt(9)) - 4;
			// apply modifier offset provided road would remain in bounds
			if ((roadOffset + modifier >= 1) && (roadOffset + modifier + roadWidth <= screenWidth)) {
				roadOffset += modifier;
			}
		}
	}
	
	private void generateGrassyAreas() { 
		for (int row=0; row<levelRows; row++) { // for each row in the level
			GrassyArea leftGrass = new GrassyArea(0, row * rowHeight, levelRowRoadOffset[row], rowHeight);
			grassyAreas.add(leftGrass);
			GrassyArea rightGrass = new GrassyArea(levelRowRoadOffset[row] + roadWidth, row * rowHeight, screenWidth - (levelRowRoadOffset[row] + roadWidth), rowHeight);
			grassyAreas.add(rightGrass);
		}
	}

	private void generateFinishLine() {
		finishLine = new FinishLine(0, levelRows * rowHeight, screenWidth, 40);		
	}
	
	public Car getCar() {
		return car;
	}
	
	public Timer getTimer() {
		return timer;
	}
	
	public Array<GrassyArea> getGrassyAreas() {
		return grassyAreas;
	}
	
	public FinishLine getFinishLine() {
		return finishLine;
	}
	
	public int getProgress(int minPosX, int maxPosX) {
		int range = maxPosX - minPosX;
		int posX = (int)(minPosX + (range * getProgressDecimalFraction()));
		return posX;
	}

	public float getProgressDecimalFraction() {
		float carNose = getCar().getBounds().getY() + getCar().getBounds().getHeight();
		float finishLine = getFinishLine().getBounds().getY();
		return (carNose/finishLine);
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public int getLevelLength() {
		return levelRows;
	}

	public int getScreenWidth() {
		return screenWidth;
	}
}
