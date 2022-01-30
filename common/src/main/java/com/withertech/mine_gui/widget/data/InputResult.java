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
 * Specifies whether an input event was ignored or processed.
 * Used for mouse input events.
 *
 * @since 4.0.0
 */
public enum InputResult
{
	PROCESSED,
	IGNORED;

	/**
	 * Gets the corresponding input result for a {@code processed} boolean.
	 *
	 * @param processed whether an input event was processed
	 * @return {@link #PROCESSED} if true, {@link #IGNORED} otherwise
	 */
	public static InputResult of(boolean processed)
	{
		return processed ? PROCESSED : IGNORED;
	}
}
