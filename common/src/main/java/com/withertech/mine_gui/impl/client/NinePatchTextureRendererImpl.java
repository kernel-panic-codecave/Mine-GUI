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

package com.withertech.mine_gui.impl.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.withertech.mine_gui.client.ScreenDrawing;
import com.withertech.mine_gui.ninepatch.ContextualTextureRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * An implementation of LibNinePatch's {@link ContextualTextureRenderer} for identifiers.
 */
public enum NinePatchTextureRendererImpl implements ContextualTextureRenderer<ResourceLocation, PoseStack>
{
	INSTANCE;

	@Override
	public void draw(ResourceLocation texture, PoseStack matrices, int x, int y, int width, int height, float u1, float v1, float u2, float v2)
	{
		ScreenDrawing.texturedRect(matrices, x, y, width, height, texture, u1, v1, u2, v2, 0xFF_FFFFFF);
	}
}
