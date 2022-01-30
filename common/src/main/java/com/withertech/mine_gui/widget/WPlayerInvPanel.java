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

import com.withertech.mine_gui.GuiDescription;
import com.withertech.mine_gui.client.BackgroundPainter;
import com.withertech.mine_gui.impl.client.NarrationMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

/**
 * A player inventory widget that has a visually separate hotbar.
 */
public class WPlayerInvPanel extends WPlainPanel
{
	private final WItemSlot inv;
	private final WItemSlot hotbar;
	@Nullable
	private final WWidget label;

	/**
	 * Constructs a player inventory panel with a label.
	 *
	 * @param playerInventory the player inventory
	 */
	public WPlayerInvPanel(Inventory playerInventory)
	{
		this(playerInventory, true);
	}

	/**
	 * Constructs a player inventory panel.
	 *
	 * @param playerInventory the player inventory
	 * @param hasLabel        whether there should be an "Inventory" label
	 * @since 2.0.0
	 */
	public WPlayerInvPanel(Inventory playerInventory, boolean hasLabel)
	{
		this(playerInventory, hasLabel ? createInventoryLabel(playerInventory) : null);
	}

	/**
	 * Constructs a player inventory panel.
	 *
	 * @param playerInventory the player inventory
	 * @param label           the label widget, can be null
	 * @since 2.0.0
	 */
	public WPlayerInvPanel(Inventory playerInventory, @Nullable WWidget label)
	{
		int y = 0;

		this.label = label;
		if (label != null)
		{
			this.add(label, 0, 0, label.getWidth(), label.getHeight());
			y += label.getHeight();
		}

		inv = WItemSlot.ofPlayerStorage(playerInventory);
		hotbar = new WItemSlot(playerInventory, 0, 9, 1, false)
		{
			@Override
			protected Component getNarrationName()
			{
				return NarrationMessages.Vanilla.HOTBAR;
			}
		};
		this.add(inv, 0, y);
		this.add(hotbar, 0, y + 58);
	}

	/**
	 * Creates a vanilla-style inventory label for a player inventory.
	 *
	 * @param playerInventory the player inventory
	 * @return the created label
	 * @since 3.1.0
	 */
	public static WLabel createInventoryLabel(Inventory playerInventory)
	{
		WLabel label = new WLabel(playerInventory.getDisplayName());
		label.setSize(9 * 18, 11);
		return label;
	}

	@Override
	public boolean canResize()
	{
		return false;
	}

	/**
	 * Sets the background painter of this inventory widget's slots.
	 *
	 * @param painter the new painter
	 * @return this panel
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public WPanel setBackgroundPainter(BackgroundPainter painter)
	{
		super.setBackgroundPainter(null);
		inv.setBackgroundPainter(painter);
		hotbar.setBackgroundPainter(painter);
		return this;
	}

	@Override
	public void validate(GuiDescription c)
	{
		super.validate(c);
		if (c != null && label instanceof WLabel)
		{
			((WLabel) label).setColor(c.getTitleColor());
		}
	}
}
