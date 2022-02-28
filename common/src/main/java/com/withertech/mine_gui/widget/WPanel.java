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
import com.withertech.mine_gui.GuiDescription;
import com.withertech.mine_gui.client.BackgroundPainter;
import com.withertech.mine_gui.widget.data.Insets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Panels are widgets that contain other widgets.
 */
public abstract class WPanel extends WWidget
{
	/**
	 * The widgets contained within this panel.
	 *
	 * <p>The list is mutable.
	 */
	protected final List<WWidget> children = new WidgetList(this, new ArrayList<>());
	@Environment(EnvType.CLIENT)
	private BackgroundPainter backgroundPainter = null;

	/**
	 * Removes the widget from this panel.
	 *
	 * @param w the removed widget
	 */
	public void remove(WWidget w)
	{
		children.remove(w);
	}

	@Override
	public boolean canResize()
	{
		return true;
	}

	/**
	 * Gets the current {@link BackgroundPainter} of this panel.
	 *
	 * @return the painter
	 */
	@Environment(EnvType.CLIENT)
	public BackgroundPainter getBackgroundPainter()
	{
		return this.backgroundPainter;
	}

	/**
	 * Sets the {@link BackgroundPainter} of this panel.
	 *
	 * @param painter the new painter
	 * @return this panel
	 */
	@Environment(EnvType.CLIENT)
	public WPanel setBackgroundPainter(BackgroundPainter painter)
	{
		this.backgroundPainter = painter;
		return this;
	}

	/**
	 * Uses this Panel's layout rules to reposition and resize components to fit nicely in the panel.
	 */
	public void layout()
	{
		for (WWidget child : children)
		{
			if (child instanceof WPanel) ((WPanel) child).layout();
			expandToFit(child);
		}
	}

	/**
	 * Expands this panel be at least as large as the widget.
	 *
	 * @param w the widget
	 */
	protected void expandToFit(WWidget w)
	{
		expandToFit(w, Insets.NONE);
	}

	/**
	 * Expands this panel be at least as large as the widget.
	 *
	 * @param w      the widget
	 * @param insets the layout insets
	 * @since 4.0.0
	 */
	protected void expandToFit(WWidget w, Insets insets)
	{
		int pushRight = w.getX() + w.getWidth() + insets.right();
		int pushDown = w.getY() + w.getHeight() + insets.bottom();
		this.setSize(Math.max(this.getWidth(), pushRight), Math.max(this.getHeight(), pushDown));
	}

	/**
	 * Finds the most specific child node at this location.
	 */
	@Override
	public WWidget hit(int x, int y)
	{
		if (children.isEmpty()) return this;
		for (int i = children.size() - 1; i >= 0; i--)
		{ //Backwards so topmost widgets get priority
			WWidget child = children.get(i);
			if (x >= child.getX() &&
					y >= child.getY() &&
					x < child.getX() + child.getWidth() &&
					y < child.getY() + child.getHeight())
			{
				return child.hit(x - child.getX(), y - child.getY());
			}
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Subclasses should call {@code super.validate(c)} to ensure that children are validated.
	 *
	 * @param c the host GUI description
	 */
	@Override
	public void validate(GuiDescription c)
	{
		super.validate(c);
		layout();
		for (WWidget child : children)
		{
			child.validate(c);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY)
	{
		if (backgroundPainter != null) backgroundPainter.paintBackground(matrices, x, y, this);

		for (WWidget child : children)
		{
			child.paint(matrices, x + child.getX(), y + child.getY(), mouseX - child.getX(), mouseY - child.getY());
		}
	}

	/**
	 * Ticks all children of this panel.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void tick()
	{
		for (WWidget child : children) child.tick();
	}

	@Nullable
	@Override
	public WWidget cycleFocus(boolean lookForwards)
	{
		return cycleFocus(lookForwards, null);
	}

	/**
	 * Cycles the focus inside this panel.
	 *
	 * @param lookForwards whether this should cycle forwards (true) or backwards (false)
	 * @param pivot        the widget that should be cycled around (can be null for beginning / end)
	 * @return the next focused widget, or null if should exit to the parent panel
	 * @since 2.0.0
	 */
	@Nullable
	public WWidget cycleFocus(boolean lookForwards, @Nullable WWidget pivot)
	{
		if (pivot == null)
		{
			if (lookForwards)
			{
				for (WWidget child : children)
				{
					WWidget result = checkFocusCycling(lookForwards, child);
					if (result != null) return result;
				}
			} else if (!children.isEmpty())
			{
				for (int i = children.size() - 1; i >= 0; i--)
				{
					WWidget child = children.get(i);
					WWidget result = checkFocusCycling(lookForwards, child);
					if (result != null) return result;
				}
			}
		} else
		{
			int currentIndex = children.indexOf(pivot);

			if (currentIndex == -1)
			{ // outside widget
				currentIndex = lookForwards ? 0 : children.size() - 1;
			}

			if (lookForwards)
			{
				if (currentIndex < children.size() - 1)
				{
					for (int i = currentIndex + 1; i < children.size(); i++)
					{
						WWidget child = children.get(i);
						WWidget result = checkFocusCycling(lookForwards, child);
						if (result != null) return result;
					}
				}
			} else
			{ // look forwards = false
				if (currentIndex > 0)
				{
					for (int i = currentIndex - 1; i >= 0; i--)
					{
						WWidget child = children.get(i);
						WWidget result = checkFocusCycling(lookForwards, child);
						if (result != null) return result;
					}
				}
			}
		}

		return null;
	}

	@Nullable
	private WWidget checkFocusCycling(boolean lookForwards, WWidget child)
	{
		if (child.canFocus() || child instanceof WPanel)
		{
			return child.cycleFocus(lookForwards);
		}

		return null;
	}

	@Override
	public void onShown()
	{
		for (WWidget child : children)
		{
			child.onShown();
		}
	}

	@Override
	public void onHidden()
	{
		super.onHidden();

		for (WWidget child : children)
		{
			child.onHidden();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Subclasses should call {@code super.addPainters()} to ensure that children have proper default painters.
	 *
	 * @since 3.0.0
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void addPainters()
	{
		for (WWidget child : children)
		{
			child.addPainters();
		}
	}

	/**
	 * {@return a stream of all visible top-level widgets in this panel}
	 *
	 * @since 4.2.0
	 */
	public final Stream<WWidget> streamChildren()
	{
		return children.stream();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof WPanel wPanel)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(children, wPanel.children) && Objects.equals(getBackgroundPainter(), wPanel.getBackgroundPainter());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), children, getBackgroundPainter());
	}

	@Override
	public String toString()
	{
		return "WPanel{" +
				"children=" + children +
				", backgroundPainter=" + backgroundPainter +
				", parent=" + parent +
				", x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				", host=" + host +
				'}';
	}

	private static final class WidgetList extends AbstractList<WWidget>
	{
		private final WPanel owner;
		private final List<WWidget> backing;

		private WidgetList(WPanel owner, List<WWidget> backing)
		{
			this.owner = owner;
			this.backing = backing;
		}

		@Override
		public WWidget get(int index)
		{
			return backing.get(index);
		}

		private void checkWidget(WWidget widget)
		{
			if (widget == null)
			{
				throw new NullPointerException("Adding null widget to " + owner);
			}

			int n = 0;
			WWidget parent = owner;
			while (parent != null)
			{
				if (widget == parent)
				{
					if (n == 0)
					{
						throw new IllegalArgumentException("Adding panel to itself: " + widget);
					} else
					{
						throw new IllegalArgumentException("Adding level " + n + " parent recursively to " + owner + ": " + widget);
					}
				}

				parent = parent.getParent();
				n++;
			}
		}

		@Override
		public WWidget set(int index, WWidget element)
		{
			checkWidget(element);
			return backing.set(index, element);
		}

		@Override
		public void add(int index, WWidget element)
		{
			checkWidget(element);
			backing.add(index, element);
		}

		@Override
		public WWidget remove(int index)
		{
			return backing.remove(index);
		}

		@Override
		public int size()
		{
			return backing.size();
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof WidgetList wWidgets)) return false;
			if (!super.equals(o)) return false;
			return Objects.equals(owner, wWidgets.owner) && Objects.equals(backing, wWidgets.backing);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(super.hashCode(), owner, backing);
		}

		@Override
		public String toString()
		{
			return "WidgetList{" +
					"owner=" + owner +
					", backing=" + backing +
					", modCount=" + modCount +
					'}';
		}
	}
}
