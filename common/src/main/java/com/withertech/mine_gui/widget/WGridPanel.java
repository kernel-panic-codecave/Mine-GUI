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

import com.withertech.mine_gui.widget.data.Insets;

import java.util.Objects;

/**
 * A panel that positions children in a grid.
 */
public class WGridPanel extends WPanelWithInsets
{
	/**
	 * The grid size in pixels.
	 * Defaults to 18, which is the size of one item slot.
	 */
	protected int grid = 18;

	/**
	 * Constructs a grid panel with the default grid size.
	 */
	public WGridPanel() {}

	/**
	 * Constructs a grid panel with a custom grid size.
	 *
	 * @param gridSize the grid size in pixels
	 */
	public WGridPanel(int gridSize) {this.grid = gridSize;}

	/**
	 * Adds a widget to this panel.
	 *
	 * <p>If the widget {@linkplain WWidget#canResize() can be resized},
	 * it will be resized to ({@link #grid}, {@link #grid}).
	 *
	 * @param w the widget
	 * @param x the X position in grid cells
	 * @param y the Y position in grid cells
	 */
	public void add(WWidget w, int x, int y)
	{
		children.add(w);
		w.parent = this;
		w.setLocation(x * grid + insets.left(), y * grid + insets.top());
		if (w.canResize())
		{
			w.setSize(grid, grid);
		}

		expandToFit(w, insets);
	}

	/**
	 * Adds a widget to this panel and resizes it to a custom size.
	 *
	 * @param w      the widget
	 * @param x      the X position in grid cells
	 * @param y      the Y position in grid cells
	 * @param width  the new width in grid cells
	 * @param height the new height in grid cells
	 */
	public void add(WWidget w, int x, int y, int width, int height)
	{
		children.add(w);
		w.parent = this;
		w.setLocation(x * grid + insets.left(), y * grid + insets.top());
		if (w.canResize())
		{
			w.setSize(width * grid, height * grid);
		}

		expandToFit(w, insets);
	}

	@Override
	public WGridPanel setInsets(Insets insets)
	{
		super.setInsets(insets);
		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof WGridPanel that)) return false;
		if (!super.equals(o)) return false;
		return grid == that.grid;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), grid);
	}

	@Override
	public String toString()
	{
		return "WGridPanel{" +
				"grid=" + grid +
				", children=" + children +
				", insets=" + insets +
				", parent=" + parent +
				", x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				", host=" + host +
				'}';
	}
}
