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

import com.withertech.mine_gui.widget.data.Axis;
import com.withertech.mine_gui.widget.data.HorizontalAlignment;
import com.withertech.mine_gui.widget.data.Insets;
import com.withertech.mine_gui.widget.data.VerticalAlignment;

import java.util.Objects;

/**
 * Similar to the BoxLayout in Swing, this widget represents a list of widgets along an axis.
 *
 * @since 2.0.0
 */
public class WBox extends WPanelWithInsets
{
	/**
	 * The spacing between widgets.
	 */
	protected int spacing = 4;

	/**
	 * The axis that the widgets are laid out on.
	 */
	protected Axis axis;

	/**
	 * The horizontal alignment for this box's children.
	 *
	 * @since 2.1.0
	 */
	protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;

	/**
	 * The vertical alignment for this box's children.
	 *
	 * @since 2.1.0
	 */
	protected VerticalAlignment verticalAlignment = VerticalAlignment.TOP;

	/**
	 * Constructs a box.
	 *
	 * @param axis the box axis
	 * @throws NullPointerException if the axis is null
	 */
	public WBox(Axis axis)
	{
		this.axis = Objects.requireNonNull(axis, "axis");
	}

	/**
	 * Adds a widget to this box.
	 * If the widget is resizeable, resizes it to the provided dimensions.
	 *
	 * @param widget the widget
	 * @param width  the new width of the widget
	 * @param height the new height of the widget
	 */
	public void add(WWidget widget, int width, int height)
	{
		widget.setParent(this);
		children.add(widget);
		if (canResize())
		{
			widget.setSize(width, height);
		}
	}

	/**
	 * Adds a widget to this box.
	 * If the widget is resizeable, resizes it to 18x18.
	 *
	 * @param widget the widget
	 */
	public void add(WWidget widget)
	{
		add(widget, 18, 18);
	}

	@Override
	public void layout()
	{
		int dimension = axis.choose(insets.left(), insets.top());

		// Set position offset from alignment along the box axis
		if (axis == Axis.HORIZONTAL && horizontalAlignment != HorizontalAlignment.LEFT)
		{
			int widgetWidth = spacing * (children.size() - 1);
			for (WWidget child : children)
			{
				widgetWidth += child.getWidth();
			}

			if (horizontalAlignment == HorizontalAlignment.CENTER)
			{
				dimension = (getWidth() - widgetWidth) / 2;
			} else
			{ // right
				dimension = getWidth() - widgetWidth;
			}
		} else if (verticalAlignment != VerticalAlignment.TOP)
		{
			int widgetHeight = spacing * (children.size() - 1);
			for (WWidget child : children)
			{
				widgetHeight += child.getHeight();
			}

			if (verticalAlignment == VerticalAlignment.CENTER)
			{
				dimension = (getHeight() - widgetHeight) / 2;
			} else
			{ // bottom
				dimension = getHeight() - widgetHeight;
			}
		}

		for (int i = 0; i < children.size(); i++)
		{
			WWidget child = children.get(i);

			if (axis == Axis.HORIZONTAL)
			{
				int y = switch (verticalAlignment)
						{
							case TOP -> insets.top();
							case CENTER -> insets.top() + (getHeight() - insets.top() - insets.bottom() - child.getHeight()) / 2;
							case BOTTOM -> getHeight() - insets.bottom() - child.getHeight();
						};

				child.setLocation(dimension, y);
			} else
			{
				int x = switch (horizontalAlignment)
						{
							case LEFT -> insets.left();
							case CENTER -> insets.left() + (getWidth() - insets.left() - insets.right() - child.getWidth()) / 2;
							case RIGHT -> getWidth() - insets.right() - child.getWidth();
						};

				child.setLocation(x, dimension);
			}

			if (child instanceof WPanel) ((WPanel) child).layout();
			expandToFit(child, insets);

			if (i != children.size() - 1)
			{
				dimension += spacing;
			}

			dimension += axis.choose(child.getWidth(), child.getHeight());
		}
	}

	/**
	 * Gets the spacing between widgets.
	 *
	 * @return the spacing
	 */
	public int getSpacing()
	{
		return spacing;
	}

	/**
	 * Sets the spacing between widgets in this box.
	 *
	 * @param spacing the new spacing
	 * @return this box
	 */
	public WBox setSpacing(int spacing)
	{
		this.spacing = spacing;

		return this;
	}

	/**
	 * Gets the axis of this box.
	 *
	 * @return the axis
	 */
	public Axis getAxis()
	{
		return axis;
	}

	/**
	 * Sets the axis of this box.
	 *
	 * @param axis the new axis
	 * @return this box
	 * @throws NullPointerException if the axis is null
	 */
	public WBox setAxis(Axis axis)
	{
		this.axis = Objects.requireNonNull(axis, "axis");
		return this;
	}

	/**
	 * Gets the horizontal alignment of this box.
	 *
	 * @return the alignment
	 * @since 2.1.0
	 */
	public HorizontalAlignment getHorizontalAlignment()
	{
		return horizontalAlignment;
	}

	/**
	 * Sets the horizontal alignment of this box.
	 *
	 * @param alignment the new alignment
	 * @return this box
	 * @throws NullPointerException if the alignment is null
	 * @since 2.1.0
	 */
	public WBox setHorizontalAlignment(HorizontalAlignment alignment)
	{
		this.horizontalAlignment = Objects.requireNonNull(alignment, "alignment");
		return this;
	}

	/**
	 * Gets the vertical alignment of this box.
	 *
	 * @return the alignment
	 * @since 2.1.0
	 */
	public VerticalAlignment getVerticalAlignment()
	{
		return verticalAlignment;
	}

	/**
	 * Sets the vertical alignment of this box.
	 *
	 * @param alignment the new alignment
	 * @return this box
	 * @throws NullPointerException if the alignment is null
	 * @since 2.1.0
	 */
	public WBox setVerticalAlignment(VerticalAlignment alignment)
	{
		this.verticalAlignment = Objects.requireNonNull(alignment, "alignment");
		return this;
	}

	public WBox setInsets(Insets insets)
	{
		super.setInsets(insets);
		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof WBox wBox)) return false;
		if (!super.equals(o)) return false;
		return getSpacing() == wBox.getSpacing() && getAxis() == wBox.getAxis() && getHorizontalAlignment() == wBox.getHorizontalAlignment() && getVerticalAlignment() == wBox.getVerticalAlignment();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), getSpacing(), getAxis(), getHorizontalAlignment(), getVerticalAlignment());
	}

	@Override
	public String toString()
	{
		return "WBox{" +
				"spacing=" + spacing +
				", axis=" + axis +
				", horizontalAlignment=" + horizontalAlignment +
				", verticalAlignment=" + verticalAlignment +
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
