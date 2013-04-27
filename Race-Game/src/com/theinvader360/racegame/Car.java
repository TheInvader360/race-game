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

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Car {
	public static final int WIDTH = 30;
	public static final int HEIGHT = 60;
	public static final Vector2 MAXVELOCITY = new Vector2(0, 500);
	public static final Vector2 MINVELOCITY = new Vector2(0, 20);
	public static final Vector2 ACCELERATION = new Vector2(0, 40);
	public static final Vector2 DECELERATION = new Vector2(0, -120);
	
	private Vector2 position = new Vector2();
	private Vector2 velocity = new Vector2();
	private Rectangle bounds;
	private Boolean decelerating;

	public Car (Vector2 position) {
		this.position = position;
		this.velocity.set(0, 0);
		this.bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
		this.decelerating = false;
	}

	public void update(float deltaTime) {
		if (!decelerating) {
			if (velocity.y < MAXVELOCITY.y) {
				velocity.add(ACCELERATION.x * deltaTime, ACCELERATION.y * deltaTime);
			}
		}
		if (decelerating) {
			if (velocity.y > MINVELOCITY.y) {
				velocity.add(DECELERATION.x * deltaTime, DECELERATION.y * deltaTime);
			}
		}
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		bounds.set(position.x, position.y, WIDTH, HEIGHT);
		decelerating = false;
	}

	public void steerCar (float deltaTime, float moveCar) {
		velocity.x = moveCar;
		update(deltaTime);
	}
	
	public void stopCar() {
		velocity.set(0, 0);
	}

	public void contactGrass() {
		decelerating = true;
	}
	
	public void contactFinishLine() {
		decelerating = true;
	}
	
	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public Rectangle getBounds() {
		return bounds;
	}
	
	public String getMph() {
		int mph = (int)(velocity.y/2);
		String formattedMph = String.format("%03d", mph);
		return formattedMph;
	}
}
