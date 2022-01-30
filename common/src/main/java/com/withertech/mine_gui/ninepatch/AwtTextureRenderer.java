/*
 * Mine GUI
 * Copyright (C) 2022 WitherTech
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.withertech.mine_gui.ninepatch;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders a {@link BufferedImage} using AWT's {@link Graphics}.
 */
public final class AwtTextureRenderer implements TextureRenderer<BufferedImage>
{
	private final Graphics g;

	/**
	 * Constructs an {@code AwtTextureRenderer} from a {@link Graphics} object.
	 *
	 * @param g the {@link Graphics} used for drawing
	 */
	public AwtTextureRenderer(Graphics g)
	{
		this.g = g;
	}

	@Override
	public void draw(BufferedImage texture, int x, int y, int width, int height, float u1, float v1, float u2, float v2)
	{
		int iw = texture.getWidth();
		int ih = texture.getHeight();
		g.drawImage(texture, x, y, x + width, y + height, (int) (iw * u1), (int) (ih * v1), (int) (iw * u2), (int) (ih * v2), null);
	}
}
