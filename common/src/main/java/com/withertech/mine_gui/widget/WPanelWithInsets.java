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
 * A panel that has {@linkplain Insets layout insets}.
 *
 * @since 4.0.0
 */
public abstract class WPanelWithInsets extends WPanel
{
	/**
	 * The layout insets of this panel.
	 * They control how far from the panel's edges the widgets are placed.
	 */
	protected Insets insets = Insets.NONE;

	/**
	 * Gets the layout insets of this panel.
	 *
	 * @return the insets
	 */
	public Insets getInsets()
	{
		return insets;
	}

	/**
	 * Sets the layout insets of this panel.
	 * Subclasses are encouraged to override this method to return their more specific type
	 * (such as {@link WGridPanel}).
	 *
	 * <p>If there are already widgets in this panel when the insets are modified,
	 * the panel is resized and the widgets are moved according to the insets.
	 *
	 * @param insets the insets, should not be null
	 * @return this panel
	 */
	public WPanelWithInsets setInsets(Insets insets)
	{
		Insets old = this.insets;
		this.insets = Objects.requireNonNull(insets, "insets");

		setSize(getWidth() - old.left() - old.right(), getHeight() - old.top() - old.bottom());

		for (WWidget child : children)
		{
			child.setLocation(child.getX() - old.left() + insets.left(), child.getY() - old.top() + insets.top());
			expandToFit(child, insets);
		}

		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof WPanelWithInsets that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(getInsets(), that.getInsets());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), getInsets());
	}

	@Override
	public String toString()
	{
		return "WPanelWithInsets{" +
				"children=" + children +
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
