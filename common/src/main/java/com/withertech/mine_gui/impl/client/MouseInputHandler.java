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

import com.withertech.mine_gui.widget.WWidget;
import com.withertech.mine_gui.widget.data.InputResult;
import com.withertech.mine_gui.widget.data.ObservableProperty;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The implementation for mouse inputs.
 */
public final class MouseInputHandler<S extends Screen & MineGuiScreenImpl>
{
	private final S screen;
	private final ObservableProperty<@Nullable WWidget> hovered = ObservableProperty.<WWidget>of(null).build();

	public MouseInputHandler(S screen)
	{
		this.screen = screen;
		hovered.addListener((property, from, to) ->
		{
			if (from != null) from.setHovered(false);
			if (to != null) to.setHovered(true);
		});
	}

	/**
	 * Traverses the {@code function} up the widget tree until it finds a {@link InputResult#PROCESSED} result.
	 *
	 * @param bottom   the starting point for the traversal
	 * @param function the function to run
	 * @return the first widget to return {@link InputResult#PROCESSED}, or null if none found.
	 */
	@Nullable
	private static WWidget runTree(WWidget bottom, Function<WWidget, InputResult> function)
	{
		WWidget current = bottom;

		while (current != null)
		{
			InputResult result = function.apply(current);

			if (result == InputResult.PROCESSED)
			{
				break;
			} else
			{
				current = current.getParent();
			}
		}

		return current;
	}

	public void onMouseDown(int containerX, int containerY, int mouseButton)
	{
		if (screen.getLastResponder() == null)
		{
			WWidget lastResponder = screen.getDescription().getRootPanel().hit(containerX, containerY);
			screen.setLastResponder(lastResponder);
			if (lastResponder != null)
			{
				runTree(
						lastResponder,
						widget -> widget.onMouseDown(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), mouseButton)
				);
			}
		} else
		{
			// This is a drag instead
		}
	}

	public void onMouseUp(int containerX, int containerY, int mouseButton)
	{
		WWidget lastResponder = screen.getLastResponder();

		if (lastResponder != null)
		{
			int width = screen.width;
			int height = screen.height;

			runTree(
					lastResponder,
					widget -> widget.onMouseUp(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), mouseButton)
			);

			if (containerX >= 0 && containerY >= 0 && containerX < width && containerY < height)
			{
				runTree(
						lastResponder,
						widget -> widget.onClick(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), mouseButton)
				);
			}
		} else
		{
			runTree(
					screen.getDescription().getRootPanel().hit(containerX, containerY),
					widget -> widget.onMouseUp(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), mouseButton)
			);
		}

		screen.setLastResponder(null);
	}

	public void onMouseDrag(int containerX, int containerY, int mouseButton, double deltaX, double deltaY)
	{
		WWidget lastResponder = screen.getLastResponder();

		if (lastResponder != null)
		{
			lastResponder.onMouseDrag(containerX - lastResponder.getAbsoluteX(), containerY - lastResponder.getAbsoluteY(), mouseButton, deltaX, deltaY);
		} else
		{
			int width = screen.width;
			int height = screen.height;

			if (containerX < 0 || containerY < 0 || containerX >= width || containerY >= height) return;

			runTree(
					screen.getDescription().getRootPanel().hit(containerX, containerY),
					widget -> widget.onMouseDrag(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), mouseButton, deltaX, deltaY)
			);
		}
	}

	public void onMouseScroll(int containerX, int containerY, double amount)
	{
		runTree(
				screen.getDescription().getRootPanel().hit(containerX, containerY),
				widget -> widget.onMouseScroll(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), amount)
		);
	}

	public void onMouseMove(int containerX, int containerY)
	{
		WWidget hit = screen.getDescription().getRootPanel().hit(containerX, containerY);

		runTree(
				hit,
				widget -> widget.onMouseMove(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY())
		);

		@Nullable
		WWidget hoveredWidget = runTree(
				hit,
				widget -> InputResult.of(widget.canHover() && widget.isWithinBounds(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY()))
		);
		hovered.set(hoveredWidget);
	}
}
