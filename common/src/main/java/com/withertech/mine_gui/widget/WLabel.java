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
import com.withertech.mine_gui.impl.client.MineGuiConfig;
import com.withertech.mine_gui.widget.data.HorizontalAlignment;
import com.withertech.mine_gui.widget.data.InputResult;
import com.withertech.mine_gui.widget.data.VerticalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A single-line label widget.
 */
public class WLabel extends WWidget
{
	/**
	 * The default text color for light mode labels.
	 */
	public static final int DEFAULT_TEXT_COLOR = 0x404040;
	/**
	 * The default text color for {@linkplain MineGuiConfig#darkMode dark mode} labels.
	 */
	public static final int DEFAULT_DARKMODE_TEXT_COLOR = 0xbcbcbc;
	protected Component text;
	protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
	protected VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
	protected int color;
	protected int darkmodeColor;

	/**
	 * Constructs a new label.
	 *
	 * @param text  the text of the label
	 * @param color the color of the label
	 */
	public WLabel(String text, int color)
	{
		this(new TextComponent(text), color);
	}

	/**
	 * Constructs a new label.
	 *
	 * @param text  the text of the label
	 * @param color the color of the label
	 */
	public WLabel(Component text, int color)
	{
		this.text = text;
		this.color = color;
		this.darkmodeColor = (color == DEFAULT_TEXT_COLOR) ? DEFAULT_DARKMODE_TEXT_COLOR : color;
	}

	/**
	 * Constructs a new label with the {@linkplain #DEFAULT_TEXT_COLOR default text color}.
	 *
	 * @param text the text of the label
	 */
	public WLabel(String text)
	{
		this(text, DEFAULT_TEXT_COLOR);
	}

	/**
	 * Constructs a new label with the {@linkplain #DEFAULT_TEXT_COLOR default text color}.
	 *
	 * @param text the text of the label
	 * @since 1.8.0
	 */
	public WLabel(Component text)
	{
		this(text, DEFAULT_TEXT_COLOR);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY)
	{
		Minecraft mc = Minecraft.getInstance();
		Font renderer = mc.font;
		int yOffset = switch (verticalAlignment)
				{
					case CENTER -> height / 2 - renderer.lineHeight / 2;
					case BOTTOM -> height - renderer.lineHeight;
					case TOP -> 0;
				};

		ScreenDrawing.drawString(matrices, text.getVisualOrderText(), horizontalAlignment, x, y + yOffset, this.getWidth(), MineGui.isDarkMode() ? darkmodeColor : color);

		Style hoveredTextStyle = getTextStyleAt(mouseX, mouseY);
		ScreenDrawing.drawTextHover(matrices, hoveredTextStyle, x + mouseX, y + mouseY);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onClick(int x, int y, int button)
	{
		Style hoveredTextStyle = getTextStyleAt(x, y);
		if (hoveredTextStyle != null)
		{
			Screen screen = Minecraft.getInstance().screen;
			if (screen != null)
			{
				return InputResult.of(screen.handleComponentClicked(hoveredTextStyle));
			}
		}

		return InputResult.IGNORED;
	}

	/**
	 * Gets the text style at the specific widget-space coordinates.
	 *
	 * @param x the X coordinate in widget space
	 * @param y the Y coordinate in widget space
	 * @return the text style at the position, or null if not found
	 */
	@Environment(EnvType.CLIENT)
	@Nullable
	public Style getTextStyleAt(int x, int y)
	{
		if (isWithinBounds(x, y))
		{
			return Minecraft.getInstance().font.getSplitter().componentStyleAtWidth(text, x);
		}
		return null;
	}

	@Override
	public boolean canResize()
	{
		return true;
	}

	@Override
	public void setSize(int x, int y)
	{
		super.setSize(x, Math.max(8, y));
	}

	/**
	 * Gets the dark mode color of this label.
	 *
	 * @return the color
	 * @since 2.0.0
	 */
	public int getDarkmodeColor()
	{
		return darkmodeColor;
	}

	/**
	 * Sets the dark mode color of this label.
	 *
	 * @param color the new color
	 * @return this label
	 */
	public WLabel setDarkmodeColor(int color)
	{
		darkmodeColor = color;
		return this;
	}

	/**
	 * Disables separate dark mode coloring by copying the dark color to be the light color.
	 *
	 * @return this label
	 */
	public WLabel disableDarkmode()
	{
		this.darkmodeColor = this.color;
		return this;
	}

	/**
	 * Gets the light mode color of this label.
	 *
	 * @return the color
	 */
	public int getColor()
	{
		return color;
	}

	/**
	 * Sets the light mode color of this label.
	 *
	 * @param color the new color
	 * @return this label
	 */
	public WLabel setColor(int color)
	{
		this.color = color;
		return this;
	}

	/**
	 * Sets the light and dark mode colors of this label.
	 *
	 * @param color         the new light color
	 * @param darkmodeColor the new dark color
	 * @return this label
	 */
	public WLabel setColor(int color, int darkmodeColor)
	{
		this.color = color;
		this.darkmodeColor = darkmodeColor;
		return this;
	}

	/**
	 * Gets the text of this label.
	 *
	 * @return the text
	 */
	public Component getText()
	{
		return text;
	}

	/**
	 * Sets the text of this label.
	 *
	 * @param text the new text
	 * @return this label
	 */
	public WLabel setText(Component text)
	{
		this.text = text;
		return this;
	}

	/**
	 * Gets the horizontal text alignment of this label.
	 *
	 * @return the alignment
	 * @since 2.0.0
	 */
	public HorizontalAlignment getHorizontalAlignment()
	{
		return horizontalAlignment;
	}

	/**
	 * Sets the horizontal text alignment of this label.
	 *
	 * @param align the new text alignment
	 * @return this label
	 */
	public WLabel setHorizontalAlignment(HorizontalAlignment align)
	{
		this.horizontalAlignment = align;
		return this;
	}

	/**
	 * Gets the vertical text alignment of this label.
	 *
	 * @return the alignment
	 * @since 2.0.0
	 */
	public VerticalAlignment getVerticalAlignment()
	{
		return verticalAlignment;
	}

	/**
	 * Sets the vertical text alignment of this label.
	 *
	 * @param align the new text alignment
	 * @return this label
	 */
	public WLabel setVerticalAlignment(VerticalAlignment align)
	{
		this.verticalAlignment = align;
		return this;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addNarrations(NarrationElementOutput builder)
	{
		builder.add(NarratedElementType.TITLE, text);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof WLabel wLabel)) return false;
		if (!super.equals(o)) return false;
		return getColor() == wLabel.getColor() && getDarkmodeColor() == wLabel.getDarkmodeColor() && Objects.equals(getText(), wLabel.getText()) && getHorizontalAlignment() == wLabel.getHorizontalAlignment() && getVerticalAlignment() == wLabel.getVerticalAlignment();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), getText(), getHorizontalAlignment(), getVerticalAlignment(), getColor(), getDarkmodeColor());
	}

	@Override
	public String toString()
	{
		return "WLabel{" +
				"text=" + text +
				", horizontalAlignment=" + horizontalAlignment +
				", verticalAlignment=" + verticalAlignment +
				", color=" + color +
				", darkmodeColor=" + darkmodeColor +
				", parent=" + parent +
				", x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				", host=" + host +
				'}';
	}
}
