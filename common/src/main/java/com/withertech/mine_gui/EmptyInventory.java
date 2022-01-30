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

package com.withertech.mine_gui;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * An empty inventory that cannot hold any items.
 */
public class EmptyInventory implements Container
{
	public static final EmptyInventory INSTANCE = new EmptyInventory();

	private EmptyInventory() {}

	@Override
	public void clearContent() {}

	@Override
	public int getContainerSize()
	{
		return 0;
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public ItemStack getItem(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int slot, int count)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int slot, ItemStack stack)
	{
	}

	@Override
	public void setChanged()
	{
	}

	@Override
	public boolean stillValid(Player player)
	{
		return true;
	}

}
