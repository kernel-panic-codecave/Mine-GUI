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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A builder for widget tooltips.
 *
 * @since 3.0.0
 */
@Environment(EnvType.CLIENT)
public final class TooltipBuilder
{
	final List<FormattedCharSequence> lines = new ArrayList<>();

	int size()
	{
		return lines.size();
	}

	/**
	 * Adds the lines to this builder.
	 *
	 * @param lines the lines
	 * @return this builder
	 */
	public TooltipBuilder add(Component... lines)
	{
		for (Component line : lines)
		{
			this.lines.add(line.getVisualOrderText());
		}

		return this;
	}

	/**
	 * Adds the lines to this builder.
	 *
	 * @param lines the lines
	 * @return this builder
	 */
	public TooltipBuilder add(FormattedCharSequence... lines)
	{
		Collections.addAll(this.lines, lines);

		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof TooltipBuilder that)) return false;
		return Objects.equals(lines, that.lines);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(lines);
	}

	@Override
	public String toString()
	{
		return "TooltipBuilder{" +
				"lines=" + lines +
				'}';
	}
}
