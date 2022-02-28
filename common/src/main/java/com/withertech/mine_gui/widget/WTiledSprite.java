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

package com.withertech.mine_gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.withertech.mine_gui.client.ScreenDrawing;
import com.withertech.mine_gui.widget.data.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Objects;

/**
 * A sprite whose texture will be tiled.
 *
 * @since 2.0.0
 */
public class WTiledSprite extends WSprite
{
	private int tileWidth;
	private int tileHeight;

	/**
	 * Create a tiled sprite.
	 *
	 * @param tileWidth  The width a tile
	 * @param tileHeight The height of a tile
	 * @param image      The image to tile
	 */
	public WTiledSprite(int tileWidth, int tileHeight, ResourceLocation image)
	{
		super(image);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	/**
	 * Create a new animated tiled sprite.
	 *
	 * @param tileWidth  The width a tile
	 * @param tileHeight The height of a tile
	 * @param frameTime  How long in milliseconds to display for. (1 tick = 50 ms)
	 * @param frames     The locations of the frames of the animation.
	 */
	public WTiledSprite(int tileWidth, int tileHeight, int frameTime, ResourceLocation... frames)
	{
		super(frameTime, frames);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	/**
	 * Create a tiled sprite.
	 *
	 * @param tileWidth  The width a tile
	 * @param tileHeight The height of a tile
	 * @param image      The image to tile
	 * @since 3.0.0
	 */
	public WTiledSprite(int tileWidth, int tileHeight, Texture image)
	{
		super(image);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	/**
	 * Create a new animated tiled sprite.
	 *
	 * @param tileWidth  The width a tile
	 * @param tileHeight The height of a tile
	 * @param frameTime  How long in milliseconds to display for. (1 tick = 50 ms)
	 * @param frames     The locations of the frames of the animation.
	 * @since 3.0.0
	 */
	public WTiledSprite(int tileWidth, int tileHeight, int frameTime, Texture... frames)
	{
		super(frameTime, frames);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	/**
	 * Sets the tiling size. This determines how often the texture will repeat.
	 *
	 * @param width  the new tiling width
	 * @param height the new tiling height
	 */
	public void setTileSize(int width, int height)
	{
		tileWidth = width;
		tileHeight = height;
	}

	/**
	 * Gets the tile width of this sprite.
	 *
	 * @return the tile width
	 * @since 2.2.0
	 */
	public int getTileWidth()
	{
		return tileWidth;
	}

	/**
	 * Sets the tile width of this sprite.
	 *
	 * @param tileWidth the new tile width
	 * @return this sprite
	 * @since 2.2.0
	 */
	public WTiledSprite setTileWidth(int tileWidth)
	{
		this.tileWidth = tileWidth;
		return this;
	}

	/**
	 * Gets the tile height of this sprite.
	 *
	 * @return the tile height
	 * @since 2.2.0
	 */
	public int getTileHeight()
	{
		return tileHeight;
	}

	/**
	 * Sets the tile height of this sprite.
	 *
	 * @param tileHeight the new tile height
	 * @return this sprite
	 * @since 2.2.0
	 */
	public WTiledSprite setTileHeight(int tileHeight)
	{
		this.tileHeight = tileHeight;
		return this;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintFrame(PoseStack matrices, int x, int y, Texture texture)
	{
		// Y Direction (down)
		for (int tileYOffset = 0; tileYOffset < height; tileYOffset += tileHeight)
		{
			// X Direction (right)
			for (int tileXOffset = 0; tileXOffset < width; tileXOffset += tileWidth)
			{
				// draw the texture
				ScreenDrawing.texturedRect(
						matrices,
						// at the correct position using tileXOffset and tileYOffset
						x + tileXOffset, y + tileYOffset,
						// but using the set tileWidth and tileHeight instead of the full height and
						// width
						getTileWidth(), getTileHeight(),
						// render the current texture
						texture,
						tint);
			}
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof WTiledSprite that)) return false;
		if (!super.equals(o)) return false;
		return getTileWidth() == that.getTileWidth() && getTileHeight() == that.getTileHeight();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), getTileWidth(), getTileHeight());
	}

	@Override
	public String toString()
	{
		return "WTiledSprite{" +
				"currentFrame=" + currentFrame +
				", currentFrameTime=" + currentFrameTime +
				", frames=" + Arrays.toString(frames) +
				", frameTime=" + frameTime +
				", lastFrame=" + lastFrame +
				", singleImage=" + singleImage +
				", tint=" + tint +
				", tileWidth=" + tileWidth +
				", tileHeight=" + tileHeight +
				", parent=" + parent +
				", x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				", host=" + host +
				'}';
	}
}
