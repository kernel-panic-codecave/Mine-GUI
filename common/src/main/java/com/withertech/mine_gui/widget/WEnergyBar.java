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
import com.withertech.mine_gui.MineGui;
import com.withertech.mine_gui.client.ScreenDrawing;
import com.withertech.mine_gui.widget.data.Texture;
import dev.architectury.platform.Platform;
import net.fabricmc.loader.impl.discovery.ModResolutionException;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WEnergyBar extends WBar
{
	private static final ResourceLocation BG = new ResourceLocation(MineGui.MOD_ID, "textures/widget/energy_bg.png");
	private static final ResourceLocation BAR = new ResourceLocation(MineGui.MOD_ID, "textures/widget/energy_bar.png");

	protected void assertMineFlux()
	{
		if (!Platform.isModLoaded("mine_flux")) throw new RuntimeException(getClass().getSimpleName() + " requires Mine Flux API!");
	}

	public WEnergyBar(int field, int maxField)
	{
		super(BG, BAR, field, maxField);
		assertMineFlux();
	}

	public WEnergyBar(@Nullable Texture bg, @Nullable Texture bar, int field, int maxField)
	{
		super(bg, bar, field, maxField);
		assertMineFlux();
	}

	public WEnergyBar(ResourceLocation bg, ResourceLocation bar, int field, int maxField)
	{
		super(bg, bar, field, maxField);
		assertMineFlux();
	}

	@Override
	public void addTooltip(@NotNull TooltipBuilder information)
	{
		int value = (field >= 0) ? properties.get(field) : 0;
		int valMax = (max >= 0) ? properties.get(max) : maxValue;
		information.add(new TextComponent("Energy: (" + value + "/" + valMax + ") MF"));
	}

	@Override
	public int getWidth()
	{
		return 18;
	}

	@Override
	public int getHeight()
	{
		return 18 * 3;
	}

	@Override
	public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY)
	{
		boolean hovered = (mouseX >= 0 && mouseY >= 0 && mouseX < getWidth() && mouseY < getHeight());
		if (bg != null)
		{
			ScreenDrawing.texturedRect(matrices, x, y, getWidth(), getHeight(), bg.withUv(hovered?0.5f:0f, 0, hovered?1.0f:0.5f, 1), 0xFFFFFFFF);
		} else
		{
			ScreenDrawing.coloredRect(matrices, x, y, getWidth(), getHeight(), ScreenDrawing.colorAtOpacity(0x000000, 0.25f));
		}

		int maxVal = max >= 0 ? properties.get(max) : maxValue;
		float percent = properties.get(field) / (float) maxVal;
		if (percent < 0) percent = 0f;
		if (percent > 1) percent = 1f;

		int barMax = getHeight();
		percent = ((int) (percent * barMax)) / (float) barMax; //Quantize to bar size

		int barSize = (int) (barMax * percent);
		if (barSize <= 0) return;

		int left = x;
		int top = y + getHeight();
		top -= barSize;
		if (bar != null)
		{
			ScreenDrawing.texturedRect(matrices, left, top, getWidth(), barSize, bar.image(), bar.u1(), Mth.lerp(percent, bar.v2(), bar.v1()), bar.u2(), bar.v2(), 0xFFFFFFFF);
		} else
		{
			ScreenDrawing.coloredRect(matrices, left, top, getWidth(), barSize, ScreenDrawing.colorAtOpacity(0xFFFFFF, 0.5f));
		}

	}
}
