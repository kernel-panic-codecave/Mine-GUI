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

package com.withertech.mine_gui.widget.data;

/**
 * The layout insets of a panel. The insets describe how many pixels should be around the panel's contents.
 * For example, root panels have 7 pixels around their contents, which is set via {@link #ROOT_PANEL}.
 *
 * @param top    the top (Y-) inset size
 * @param left   the left (X-) inset size
 * @param bottom the bottom (Y+) inset size
 * @param right  the right (X+) inset size
 * @since 4.0.0
 */
public record Insets(int top, int left, int bottom, int right)
{
	/**
	 * Empty layout insets that do not provide any borders around content.
	 */
	public static final Insets NONE = new Insets(0);

	/**
	 * The default insets of a root panel, providing 7 pixels around the content on all sides.
	 */
	public static final Insets ROOT_PANEL = new Insets(7);

	/**
	 * Constructs layout insets.
	 *
	 * @param top    the top (Y-) inset size
	 * @param left   the left (X-) inset size
	 * @param bottom the bottom (Y+) inset size
	 * @param right  the right (X+) inset size
	 */
	public Insets
	{
		if (top < 0) throw new IllegalArgumentException("top cannot be negative, found " + top);
		if (left < 0) throw new IllegalArgumentException("left cannot be negative, found " + left);
		if (bottom < 0) throw new IllegalArgumentException("bottom cannot be negative, found " + bottom);
		if (right < 0) throw new IllegalArgumentException("right cannot be negative, found " + right);
	}

	/**
	 * Constructs layout insets.
	 *
	 * @param vertical   the vertical (Y) size of the insets
	 * @param horizontal the horizontal (X) size of the insets
	 */
	public Insets(int vertical, int horizontal)
	{
		this(vertical, horizontal, vertical, horizontal);
	}

	/**
	 * Constructs layout insets.
	 *
	 * @param size the size of the insets on all sides
	 */
	public Insets(int size)
	{
		this(size, size, size, size);
	}
}
