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

package com.withertech.mine_gui.widget.icon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.withertech.mine_gui.client.ScreenDrawing;
import com.withertech.mine_gui.widget.data.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

/**
 * An icon that draws a texture.
 *
 * @since 2.2.0
 */
public class TextureIcon implements Icon
{
	private final Texture texture;
	private float opacity = 1f;
	private int color = 0xFF_FFFFFF;

	/**
	 * Constructs a new texture icon.
	 *
	 * @param texture the identifier of the icon texture
	 */
	public TextureIcon(ResourceLocation texture)
	{
		this(new Texture(texture));
	}

	/**
	 * Constructs a new texture icon.
	 *
	 * @param texture the identifier of the icon texture
	 * @since 3.0.0
	 */
	public TextureIcon(Texture texture)
	{
		this.texture = texture;
	}

	/**
	 * Gets the opacity of the texture.
	 *
	 * @return the opacity
	 */
	public float getOpacity()
	{
		return opacity;
	}

	/**
	 * Sets the opacity of the texture.
	 *
	 * @param opacity the new opacity between 0 (fully transparent) and 1 (fully opaque)
	 * @return this icon
	 */
	public TextureIcon setOpacity(float opacity)
	{
		this.opacity = opacity;
		return this;
	}

	/**
	 * Gets the color tint of the texture.
	 *
	 * @return the color tint
	 */
	public int getColor()
	{
		return color;
	}

	/**
	 * Sets the color tint of the texture.
	 *
	 * @param color the new color tint
	 * @return this icon
	 */
	public TextureIcon setColor(int color)
	{
		this.color = color;
		return this;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(PoseStack matrices, int x, int y, int size)
	{
		ScreenDrawing.texturedRect(matrices, x, y, size, size, texture, color, opacity);
	}
}
