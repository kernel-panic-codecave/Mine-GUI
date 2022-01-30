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

package com.withertech.mine_gui.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.withertech.mine_gui.impl.client.NinePatchTextureRendererImpl;
import com.withertech.mine_gui.ninepatch.NinePatch;
import com.withertech.mine_gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Nine-patch background painters paint rectangles using a special nine-patch texture.
 * The texture is divided into nine sections: four corners, four edges and a center part.
 * The edges and the center are either tiled or stretched, depending on the mode of the painter,
 * to fill the area between the corners. By default, the texture is tiled.
 *
 * <p>Nine-patch background painters can be created using {@link BackgroundPainter#AwtTextureRenderer
 * ContextualTextureRenderer
 * NinePatch
 * TextureRegion
 * TextureRenderercreateNinePatch(ResourceLocation)},
 * {@link #createNinePatch(Texture, Consumer)}, or with the constructor directly. The latter two let you customise
 * the look of the background more finely.
 *
 * <p>{@code NinePatchBackgroundPainter} has a customizable padding that can be applied.
 * By default there is no padding, but you can set it using {@link NinePatchBackgroundPainter#setPadding(int)}.
 *
 * @since 4.0.0
 */
@Environment(EnvType.CLIENT)
public final class NinePatchBackgroundPainter implements BackgroundPainter
{
	private final NinePatch<ResourceLocation> ninePatch;
	private int topPadding = 0;
	private int leftPadding = 0;
	private int bottomPadding = 0;
	private int rightPadding = 0;

	public NinePatchBackgroundPainter(NinePatch<ResourceLocation> ninePatch)
	{
		this.ninePatch = ninePatch;
	}

	public int getTopPadding()
	{
		return topPadding;
	}

	public NinePatchBackgroundPainter setTopPadding(int topPadding)
	{
		this.topPadding = topPadding;
		return this;
	}

	public int getLeftPadding()
	{
		return leftPadding;
	}

	public NinePatchBackgroundPainter setLeftPadding(int leftPadding)
	{
		this.leftPadding = leftPadding;
		return this;
	}

	public int getBottomPadding()
	{
		return bottomPadding;
	}

	public NinePatchBackgroundPainter setBottomPadding(int bottomPadding)
	{
		this.bottomPadding = bottomPadding;
		return this;
	}

	public int getRightPadding()
	{
		return rightPadding;
	}

	public NinePatchBackgroundPainter setRightPadding(int rightPadding)
	{
		this.rightPadding = rightPadding;
		return this;
	}

	public NinePatchBackgroundPainter setPadding(int padding)
	{
		this.topPadding = this.leftPadding = this.bottomPadding = this.rightPadding = padding;
		return this;
	}

	public NinePatchBackgroundPainter setPadding(int vertical, int horizontal)
	{
		this.topPadding = this.bottomPadding = vertical;
		this.leftPadding = this.rightPadding = horizontal;
		return this;
	}

	public NinePatchBackgroundPainter setPadding(int topPadding, int leftPadding, int bottomPadding, int rightPadding)
	{
		this.topPadding = topPadding;
		this.leftPadding = leftPadding;
		this.bottomPadding = bottomPadding;
		this.rightPadding = rightPadding;

		return this;
	}

	@Override
	public void paintBackground(PoseStack matrices, int left, int top, WWidget panel)
	{
		matrices.pushPose();
		matrices.translate(left - leftPadding, top - topPadding, 0);
		ninePatch.draw(NinePatchTextureRendererImpl.INSTANCE, matrices, panel.getWidth() + leftPadding + rightPadding, panel.getHeight() + topPadding + bottomPadding);
		matrices.popPose();
	}
}
