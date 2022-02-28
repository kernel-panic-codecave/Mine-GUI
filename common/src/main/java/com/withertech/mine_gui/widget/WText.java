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
import com.withertech.mine_gui.widget.data.InputResult;
import com.withertech.mine_gui.widget.data.VerticalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A multiline label widget.
 *
 * @since 1.8.0
 */
public class WText extends WWidget
{
	protected Component text;
	protected int color;
	protected int darkmodeColor;
	protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
	protected VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
	@Environment(EnvType.CLIENT)
	private List<FormattedCharSequence> wrappedLines;
	private boolean wrappingScheduled = false;

	public WText(Component text)
	{
		this(text, WLabel.DEFAULT_TEXT_COLOR);
	}

	public WText(Component text, int color)
	{
		this.text = Objects.requireNonNull(text, "text must not be null");
		this.color = color;
		this.darkmodeColor = (color == WLabel.DEFAULT_TEXT_COLOR) ? WLabel.DEFAULT_DARKMODE_TEXT_COLOR : color;
	}

	@Override
	public void setSize(int x, int y)
	{
		super.setSize(x, y);
		wrappingScheduled = true;
	}

	@Override
	public boolean canResize()
	{
		return true;
	}

	@Environment(EnvType.CLIENT)
	private void wrapLines()
	{
		Font font = Minecraft.getInstance().font;
		wrappedLines = font.split(text, width);
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
		Font font = Minecraft.getInstance().font;
		int lineIndex = y / font.lineHeight;

		if (lineIndex >= 0 && lineIndex < wrappedLines.size())
		{
			FormattedCharSequence line = wrappedLines.get(lineIndex);
			return font.getSplitter().componentStyleAtWidth(line, x);
		}

		return null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY)
	{
		if (wrappedLines == null || wrappingScheduled)
		{
			wrapLines();
			wrappingScheduled = false;
		}

		Font font = Minecraft.getInstance().font;

		int yOffset = switch (verticalAlignment)
				{
					case CENTER -> height / 2 - font.lineHeight * wrappedLines.size() / 2;
					case BOTTOM -> height - font.lineHeight * wrappedLines.size();
					case TOP -> 0;
				};

		for (int i = 0; i < wrappedLines.size(); i++)
		{
			FormattedCharSequence line = wrappedLines.get(i);
			int c = MineGui.isDarkMode() ? darkmodeColor : color;

			ScreenDrawing.drawString(matrices, line, horizontalAlignment, x, y + yOffset + i * font.lineHeight, width, c);
		}

		Style hoveredTextStyle = getTextStyleAt(mouseX, mouseY);
		ScreenDrawing.drawTextHover(matrices, hoveredTextStyle, x + mouseX, y + mouseY);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onClick(int x, int y, int button)
	{
		if (button != 0) return InputResult.IGNORED; // only left clicks

		Style hoveredTextStyle = getTextStyleAt(x, y);
		if (hoveredTextStyle != null)
		{
			boolean processed = Minecraft.getInstance().screen.handleComponentClicked(hoveredTextStyle);
			return InputResult.of(processed);
		}

		return InputResult.IGNORED;
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
	public WText setText(Component text)
	{
		Objects.requireNonNull(text, "text is null");
		this.text = text;
		wrappingScheduled = true;

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
	 * @return this text widget
	 */
	public WText setColor(int color)
	{
		this.color = color;
		return this;
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
	 * @param darkmodeColor the new color
	 * @return this text widget
	 */
	public WText setDarkmodeColor(int darkmodeColor)
	{
		this.darkmodeColor = darkmodeColor;
		return this;
	}

	/**
	 * Sets the light and dark mode colors of this label.
	 *
	 * @param color         the new light color
	 * @param darkmodeColor the new dark color
	 * @return this text widget
	 */
	public WText setColor(int color, int darkmodeColor)
	{
		setColor(color);
		setDarkmodeColor(darkmodeColor);
		return this;
	}

	/**
	 * Disables separate dark mode coloring by copying the dark color to be the light color.
	 *
	 * @return this text widget
	 */
	public WText disableDarkmode()
	{
		this.darkmodeColor = this.color;
		return this;
	}

	/**
	 * Gets the horizontal alignment of this text widget.
	 *
	 * @return the alignment
	 * @since 1.9.0
	 */
	public HorizontalAlignment getHorizontalAlignment()
	{
		return horizontalAlignment;
	}

	/**
	 * Sets the horizontal alignment of this text widget.
	 *
	 * @param horizontalAlignment the new alignment
	 * @return this widget
	 * @since 1.9.0
	 */
	public WText setHorizontalAlignment(HorizontalAlignment horizontalAlignment)
	{
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	/**
	 * Gets the vertical alignment of this text widget.
	 *
	 * @return the alignment
	 * @since 2.0.0
	 */
	public VerticalAlignment getVerticalAlignment()
	{
		return verticalAlignment;
	}

	/**
	 * Sets the vertical alignment of this text widget.
	 *
	 * @param verticalAlignment the new alignment
	 * @return this widget
	 * @since 2.0.0
	 */
	public WText setVerticalAlignment(VerticalAlignment verticalAlignment)
	{
		this.verticalAlignment = verticalAlignment;
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
		if (!(o instanceof WText wText)) return false;
		if (!super.equals(o)) return false;
		return getColor() == wText.getColor() && getDarkmodeColor() == wText.getDarkmodeColor() && wrappingScheduled == wText.wrappingScheduled && Objects.equals(getText(), wText.getText()) && getHorizontalAlignment() == wText.getHorizontalAlignment() && getVerticalAlignment() == wText.getVerticalAlignment() && Objects.equals(wrappedLines, wText.wrappedLines);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), getText(), getColor(), getDarkmodeColor(), getHorizontalAlignment(), getVerticalAlignment(), wrappedLines, wrappingScheduled);
	}

	@Override
	public String toString()
	{
		return "WText{" +
				"text=" + text +
				", color=" + color +
				", darkmodeColor=" + darkmodeColor +
				", horizontalAlignment=" + horizontalAlignment +
				", verticalAlignment=" + verticalAlignment +
				", wrappedLines=" + wrappedLines +
				", wrappingScheduled=" + wrappingScheduled +
				", parent=" + parent +
				", x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				", host=" + host +
				'}';
	}
}
