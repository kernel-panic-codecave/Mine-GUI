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

import com.withertech.mine_gui.widget.WPanel;
import com.withertech.mine_gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public final class NarrationHelper
{
	public static void addNarrations(WPanel rootPanel, NarrationElementOutput builder)
	{
		List<WWidget> narratableWidgets = getAllWidgets(rootPanel)
				.filter(WWidget::isNarratable)
				.collect(Collectors.toList());

		for (int i = 0, childCount = narratableWidgets.size(); i < childCount; i++)
		{
			WWidget child = narratableWidgets.get(i);
			if (!child.isFocused() && !child.isHovered()) continue;

			// replicates Screen.addElementNarrations
			if (narratableWidgets.size() > 1)
			{
				builder.add(NarratedElementType.POSITION, new TranslatableComponent(NarrationMessages.Vanilla.SCREEN_POSITION_KEY, i + 1, childCount));

				if (child.isFocused())
				{
					builder.add(NarratedElementType.USAGE, NarrationMessages.Vanilla.COMPONENT_LIST_USAGE);
				}
			}

			child.addNarrations(builder.nest());
		}
	}

	private static Stream<WWidget> getAllWidgets(WPanel panel)
	{
		return Stream.concat(Stream.of(panel), panel.streamChildren().flatMap(widget ->
		{
			if (widget instanceof WPanel nested)
			{
				return getAllWidgets(nested);
			}

			return Stream.of(widget);
		}));
	}
}
