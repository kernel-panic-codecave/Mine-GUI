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

package com.withertech.mine_gui.example.gui;

import com.withertech.mine_gui.GuiDescription;
import com.withertech.mine_gui.client.BackgroundPainter;
import com.withertech.mine_gui.example.tile.TestTile;
import com.withertech.mine_gui.widget.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;

public class TestPanel extends WGridPanel
{

	private final WLabel title;
	private final WItemSlot invSlots;
	private final WEnergyBar energyBar;

	public TestPanel(Container inv, Component comp)
	{
		title = new WLabel(comp);
		invSlots = new WItemSlot(inv, 0, TestTile.INV_SIZE, 1, false);
		energyBar = (WEnergyBar) new WEnergyBar(0, 1).withTooltip("");
		this.add(title, 1, 0);
		this.add(invSlots, 2, 1);
		this.add(energyBar, 0, 0);
	}

	@Override
	public int getWidth()
	{
		return 11 * 18;
	}

	@Override
	public WPanel setBackgroundPainter(BackgroundPainter painter)
	{
		super.setBackgroundPainter(null);
		invSlots.setBackgroundPainter(painter);
		return this;
	}

	@Override
	public void validate(GuiDescription c)
	{
		super.validate(c);
		title.setColor(c.getTitleColor());
	}
}
