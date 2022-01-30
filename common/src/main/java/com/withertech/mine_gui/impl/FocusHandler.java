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

package com.withertech.mine_gui.impl;

import com.withertech.mine_gui.GuiDescription;
import com.withertech.mine_gui.widget.WPanel;
import com.withertech.mine_gui.widget.WWidget;

/**
 * The implementation for focus cycling.
 */
public final class FocusHandler
{
	public static void cycleFocus(GuiDescription host, boolean lookForwards)
	{
		boolean result;
		WWidget focus = host.getFocus();
		if (focus == null)
		{
			result = cycleFocus(host, lookForwards, host.getRootPanel(), null);
		} else
		{
			result = cycleFocus(host, lookForwards, focus, null);
		}

		if (!result)
		{
			// Try again from the beginning
			cycleFocus(host, lookForwards, host.getRootPanel(), null);
		}
	}

	private static boolean cycleFocus(GuiDescription host, boolean lookForwards, WWidget widget, WWidget pivot)
	{
		WWidget next = widget instanceof WPanel
				? ((WPanel) widget).cycleFocus(lookForwards, pivot)
				: widget.cycleFocus(lookForwards);

		if (next != null)
		{
			host.requestFocus(next);
			return true;
		} else
		{
			WPanel parent = widget.getParent();
			if (parent != null)
			{
				return cycleFocus(host, lookForwards, parent, widget);
			}
		}

		return false;
	}
}
