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
import com.withertech.mine_gui.impl.client.NarrationMessages;
import com.withertech.mine_gui.widget.data.Axis;
import com.withertech.mine_gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import java.util.Objects;

public class WScrollBar extends WWidget
{
	private static final int SCROLLING_SPEED = 4;

	protected Axis axis = Axis.HORIZONTAL;
	protected int value;
	protected int maxValue = 100;
	protected int window = 16;

	protected int anchor = -1;
	protected int anchorValue = -1;
	protected boolean sliding = false;

	/**
	 * Constructs a horizontal scroll bar.
	 */
	public WScrollBar()
	{
	}

	/**
	 * Constructs a scroll bar with a custom axis.
	 *
	 * @param axis the axis
	 */
	public WScrollBar(Axis axis)
	{
		this.axis = axis;
	}

	private static void drawBeveledOutline(PoseStack matrices, int x, int y, int width, int height, int topleft, int bottomright)
	{
		ScreenDrawing.coloredRect(matrices, x, y, width, 1, topleft); //Top shadow
		ScreenDrawing.coloredRect(matrices, x, y + 1, 1, height - 1, topleft); //Left shadow
		ScreenDrawing.coloredRect(matrices, x + width - 1, y + 1, 1, height - 1, bottomright); //Right hilight
		ScreenDrawing.coloredRect(matrices, x + 1, y + height - 1, width - 1, 1, bottomright); //Bottom hilight
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY)
	{
		if (MineGui.isDarkMode())
		{
			ScreenDrawing.drawBeveledPanel(matrices, x, y, width, height, 0xFF_212121, 0xFF_2F2F2F, 0xFF_5D5D5D);
		} else
		{
			ScreenDrawing.drawBeveledPanel(matrices, x, y, width, height, 0xFF_373737, 0xFF_8B8B8B, 0xFF_FFFFFF);
		}
		if (maxValue <= 0) return;

		// Handle colors
		int top, middle, bottom;

		if (sliding)
		{
			if (MineGui.isDarkMode())
			{
				top = 0xFF_6C6C6C;
				middle = 0xFF_2F2F2F;
				bottom = 0xFF_212121;
			} else
			{
				top = 0xFF_FFFFFF;
				middle = 0xFF_8B8B8B;
				bottom = 0xFF_555555;
			}
		} else if (isWithinBounds(mouseX, mouseY))
		{
			if (MineGui.isDarkMode())
			{
				top = 0xFF_5F6A9D;
				middle = 0xFF_323F6E;
				bottom = 0xFF_0B204A;
			} else
			{
				top = 0xFF_CFD0F7;
				middle = 0xFF_8791C7;
				bottom = 0xFF_343E75;
			}
		} else
		{
			if (MineGui.isDarkMode())
			{
				top = 0xFF_6C6C6C;
				middle = 0xFF_414141;
				bottom = 0xFF_212121;
			} else
			{
				top = 0xFF_FFFFFF;
				middle = 0xFF_C6C6C6;
				bottom = 0xFF_555555;
			}
		}

		if (axis == Axis.HORIZONTAL)
		{
			ScreenDrawing.drawBeveledPanel(matrices, x + 1 + getHandlePosition(), y + 1, getHandleSize(), height - 2, top, middle, bottom);

			if (isFocused())
			{
				drawBeveledOutline(matrices, x + 1 + getHandlePosition(), y + 1, getHandleSize(), height - 2, 0xFF_FFFFA7, 0xFF_8C8F39);
			}
		} else
		{
			ScreenDrawing.drawBeveledPanel(matrices, x + 1, y + 1 + getHandlePosition(), width - 2, getHandleSize(), top, middle, bottom);

			if (isFocused())
			{
				drawBeveledOutline(matrices, x + 1, y + 1 + getHandlePosition(), width - 2, getHandleSize(), 0xFF_FFFFA7, 0xFF_8C8F39);
			}
		}
	}

	@Override
	public boolean canResize()
	{
		return true;
	}

	@Override
	public boolean canFocus()
	{
		return true;
	}

	/**
	 * Gets the on-axis size of the scrollbar handle in gui pixels
	 */
	public int getHandleSize()
	{
		float percentage = (window >= maxValue) ? 1f : window / (float) maxValue;
		int bar = (axis == Axis.HORIZONTAL) ? width - 2 : height - 2;
		int result = (int) (percentage * bar);
		if (result < 6) result = 6;
		return result;
	}

	/**
	 * Gets the number of pixels the scrollbar handle is able to move along its track from one end to the other.
	 */
	public int getMovableDistance()
	{
		int bar = (axis == Axis.HORIZONTAL) ? width - 2 : height - 2;
		return bar - getHandleSize();
	}

	public int pixelsToValues(int pixels)
	{
		int bar = getMovableDistance();
		float percent = pixels / (float) bar;
		return (int) (percent * (maxValue - window));
	}

	public int getHandlePosition()
	{
		float percent = value / (float) Math.max(maxValue - window, 1);
		return (int) (percent * getMovableDistance());
	}

	/**
	 * Gets the maximum scroll value achievable; this will typically be the maximum value minus the
	 * window size
	 */
	public int getMaxScrollValue()
	{
		return maxValue - window;
	}

	protected void adjustSlider(int x, int y)
	{

		int delta = 0;
		if (axis == Axis.HORIZONTAL)
		{
			delta = x - anchor;
		} else
		{
			delta = y - anchor;
		}

		int valueDelta = pixelsToValues(delta);
		int valueNew = anchorValue + valueDelta;

		if (valueNew > getMaxScrollValue()) valueNew = getMaxScrollValue();
		if (valueNew < 0) valueNew = 0;
		this.value = valueNew;
	}

	@Override
	public InputResult onMouseDown(int x, int y, int button)
	{
		//TODO: Clicking before or after the handle should jump instead of scrolling
		requestFocus();

		if (axis == Axis.HORIZONTAL)
		{
			anchor = x;
			anchorValue = value;
		} else
		{
			anchor = y;
			anchorValue = value;
		}
		sliding = true;
		return InputResult.PROCESSED;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onMouseDrag(int x, int y, int button, double deltaX, double deltaY)
	{
		adjustSlider(x, y);
		return InputResult.PROCESSED;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onMouseUp(int x, int y, int button)
	{
		//TODO: Clicking before or after the handle should jump instead of scrolling
		anchor = -1;
		anchorValue = -1;
		sliding = false;
		return InputResult.PROCESSED;
	}

	@Override
	public void onKeyPressed(int ch, int key, int modifiers)
	{
		WAbstractSlider.Direction direction = axis == Axis.HORIZONTAL
				? WAbstractSlider.Direction.RIGHT
				: WAbstractSlider.Direction.DOWN;

		if (WAbstractSlider.isIncreasingKey(ch, direction))
		{
			if (value < getMaxScrollValue())
			{
				value++;
			}
		} else if (WAbstractSlider.isDecreasingKey(ch, direction))
		{
			if (value > 0)
			{
				value--;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onMouseScroll(int x, int y, double amount)
	{
		setValue(getValue() + (int) -amount * SCROLLING_SPEED);
		return InputResult.PROCESSED;
	}

	public int getValue()
	{
		return value;
	}

	public WScrollBar setValue(int value)
	{
		this.value = value;
		checkValue();
		return this;
	}

	public int getMaxValue()
	{
		return maxValue;
	}

	public WScrollBar setMaxValue(int max)
	{
		this.maxValue = max;
		checkValue();
		return this;
	}

	public int getWindow()
	{
		return window;
	}

	public WScrollBar setWindow(int window)
	{
		this.window = window;
		return this;
	}

	/**
	 * Checks that the current value is in the correct range
	 * and adjusts it if needed.
	 */
	protected void checkValue()
	{
		if (this.value > maxValue - window)
		{
			this.value = maxValue - window;
		}
		if (this.value < 0) this.value = 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addNarrations(NarrationElementOutput builder)
	{
		builder.add(NarratedElementType.TITLE, NarrationMessages.SCROLL_BAR_TITLE);
		builder.add(NarratedElementType.USAGE, NarrationMessages.SLIDER_USAGE);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof WScrollBar that)) return false;
		if (!super.equals(o)) return false;
		return getValue() == that.getValue() && getMaxValue() == that.getMaxValue() && getWindow() == that.getWindow() && anchor == that.anchor && anchorValue == that.anchorValue && sliding == that.sliding && axis == that.axis;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), axis, getValue(), getMaxValue(), getWindow(), anchor, anchorValue, sliding);
	}

	@Override
	public String toString()
	{
		return "WScrollBar{" +
				"axis=" + axis +
				", value=" + value +
				", maxValue=" + maxValue +
				", window=" + window +
				", anchor=" + anchor +
				", anchorValue=" + anchorValue +
				", sliding=" + sliding +
				", parent=" + parent +
				", x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				", host=" + host +
				'}';
	}
}
