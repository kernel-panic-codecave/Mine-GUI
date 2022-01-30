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

package com.withertech.mine_gui.example.container;

import com.withertech.mine_gui.SyncedGuiDescription;
import com.withertech.mine_gui.example.gui.TestPanel;
import com.withertech.mine_gui.example.registration.MineContainers;
import com.withertech.mine_gui.example.tile.TestTile;
import com.withertech.mine_gui.widget.WButton;
import com.withertech.mine_gui.widget.WGridPanel;
import com.withertech.mine_gui.widget.WTabPanel;
import com.withertech.mine_gui.widget.data.Insets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class TestContainer extends SyncedGuiDescription
{
	public TestContainer(int syncId, Inventory playerInventory, FriendlyByteBuf buf)
	{
		this(syncId, playerInventory, ContainerLevelAccess.NULL, buf.readComponent());
	}

	public TestContainer(int syncId, Inventory playerInventory, ContainerLevelAccess context, Component title)
	{
		super(MineContainers.TEST_CONTAINER.get(), syncId, playerInventory, getBlockInventory(context, TestTile.INV_SIZE), getBlockPropertyDelegate(context, 2));
		WTabPanel root = new WTabPanel();
		WGridPanel main = new WGridPanel();
		WGridPanel sub = new WGridPanel();
		setTitleVisible(false);
		main.setInsets(Insets.ROOT_PANEL);
		main.add(new TestPanel(blockInventory, title), 0, 0);
		sub.setInsets(Insets.ROOT_PANEL);

		sub.add(new WButton().setOnClick(() ->
		{}), 0, 0);

		main.add(this.createPlayerInventoryPanel(), 1, 3);
		root.add(main, builder -> builder.title(new TextComponent("Main")));
		root.add(sub, builder -> builder.title(new TextComponent("Sub")));
		setRootPanel(root);
		getRootPanel().validate(this);
	}
}


