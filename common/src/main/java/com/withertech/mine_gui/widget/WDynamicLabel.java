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
import com.withertech.mine_gui.widget.data.HorizontalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Supplier;

/**
 * Dynamic labels are labels that pull their text from a {@code Supplier<String>}.
 * They can be used for automatically getting data from a block entity or another data source.
 *
 * <p>Translating strings in dynamic labels should be done using
 * {@link net.minecraft.client.resources.language.I18n#get(String, Object...)}.
 */
public class WDynamicLabel extends WWidget
{
	public static final int DEFAULT_TEXT_COLOR = 0x404040;
	public static final int DEFAULT_DARKMODE_TEXT_COLOR = 0xbcbcbc;
	protected Supplier<String> text;
	protected HorizontalAlignment alignment = HorizontalAlignment.LEFT;
	protected int color;
	protected int darkmodeColor;

	public WDynamicLabel(Supplier<String> text, int color)
	{
		this.text = text;
		this.color = color;
		this.darkmodeColor = (color == DEFAULT_TEXT_COLOR) ? DEFAULT_DARKMODE_TEXT_COLOR : color;
	}

	public WDynamicLabel(Supplier<String> text)
	{
		this(text, DEFAULT_TEXT_COLOR);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY)
	{
		String tr = text.get();
		ScreenDrawing.drawString(matrices, tr, alignment, x, y, this.getWidth(), MineGui.isDarkMode() ? darkmodeColor : color);
	}

	@Override
	public boolean canResize()
	{
		return true;
	}

	@Override
	public void setSize(int x, int y)
	{
		super.setSize(x, 20);
	}

	public WDynamicLabel setDarkmodeColor(int color)
	{
		darkmodeColor = color;
		return this;
	}

	public WDynamicLabel disableDarkmode()
	{
		this.darkmodeColor = this.color;
		return this;
	}

	public WDynamicLabel setColor(int color, int darkmodeColor)
	{
		this.color = color;
		this.darkmodeColor = darkmodeColor;
		return this;
	}

	public WDynamicLabel setText(Supplier<String> text)
	{
		this.text = text;
		return this;
	}

	public WDynamicLabel setAlignment(HorizontalAlignment align)
	{
		this.alignment = align;
		return this;
	}
}
