/*******************************************************************************
 * Copyright (c) 2013 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.Random;

public class PerlinNoise {

	Random ran = new Random();

	public double SmoothNoise(int x, int y) {
		double corners = ( ran.nextDouble()+ran.nextDouble()+ran.nextDouble()+ran.nextDouble() ) / 16;
		double sides   = ( ran.nextDouble()+ran.nextDouble()+ran.nextDouble()+ran.nextDouble() ) /  8;
		double center  =  ran.nextDouble() / 4;
		return corners + sides + center;
	}

	public double Interpolate(double a, double b, double x){
		return  a*(1-x) + b*x;
	}

	public double InterpolatedNoise(double d, double e) {

		int integer_X    = (int)d;
		double fractional_X = d - integer_X;

		int integer_Y    = (int)e;
		double fractional_Y = e - integer_Y;

		double v1 = SmoothNoise(integer_X,     integer_Y);
		double v2 = SmoothNoise(integer_X + 1, integer_Y);
		double v3 = SmoothNoise(integer_X,     integer_Y + 1);
		double v4 = SmoothNoise(integer_X + 1, integer_Y + 1);

		double   i1 = Interpolate(v1 , v2 , fractional_X);
		double   i2 = Interpolate(v3 , v4 , fractional_X);

		return Interpolate(i1 , i2 , fractional_Y);

	}


	public int PerlinNoise_2D(float x, float y, int persistence, int octaves) {

		double total = 0;
		double p = 1 / persistence;
		int n = octaves - 1;

		for (int i = 0; i < n; i++) {

			double frequency = Math.pow(2, i);
			double amplitude = Math.pow(p, i);

			total = total + InterpolatedNoise(x * frequency, y * frequency) * amplitude;

		}

		return (int) (total*10);

	}

}
