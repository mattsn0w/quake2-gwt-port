/*
Copyright (C) 2010 Copyright 2010 Google Inc.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package jake2.desktop;


import jake2.render.LineDrawing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

public class DesktopLineDrawing implements LineDrawing {
	Graphics2D g;
	int x0;
	int y0;
	private GeneralPath path;
	
	public DesktopLineDrawing(Graphics2D g) {
		this.g = g;
		
		g.setBackground(Color.BLACK);
		g.setColor(Color.GREEN);
		g.setFont(new Font("Courier New", Font.PLAIN, 8));
	}

	public void beginPath() {
		path = new GeneralPath();
	}

	public void clearRect(float x, float y, float w, float h) {
		g.clearRect((int) x, (int) y, (int) w, (int) h); 
	}

	
	public void lineTo(float x, float y) {
		path.lineTo(x, y);
	}

	public void setGlobalAlpha(float ga) {
		Color c = g.getColor();
		g.setColor(new Color(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, ga));
	}
	
	public void moveTo(float x, float y) {
		path.moveTo(x, y);
	}

	public void stroke() {
		g.draw(path);
	}

	public void fillText(String text, float x, float y) {
		g.drawString(text, x, y);
	}

	public void setStrokeStyleColor(String strokeStyle) {
		g.setColor(new Color(Integer.parseInt(strokeStyle.substring(1), 16)));
	}
}
