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

package com.withertech.mine_gui.example.tile;

import com.withertech.mine_flux.api.IMFContainer;
import com.withertech.mine_flux.api.IMFStorage;
import com.withertech.mine_flux.util.EnergyUtil;
import com.withertech.mine_gui.PropertyDelegateHolder;
import com.withertech.mine_gui.example.container.TestContainer;
import com.withertech.mine_gui.example.registration.MineTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.IntStream;

public abstract class TestTile extends BlockEntity implements MenuProvider, WorldlyContainerHolder, IMFContainer, PropertyDelegateHolder
{
	public static final int ENERGY_SIZE = 1000;
	public static final int ENERGY_TRANSFER = 200;
	public static final int INV_SIZE = 9;
	protected final TestInventory inventory;
	protected final IMFStorage energy = EnergyUtil.create(ENERGY_SIZE, ENERGY_TRANSFER);

	public TestTile(BlockPos blockPos, BlockState blockState)
	{
		super(MineTiles.TEST_TILE.get(), blockPos, blockState);
		inventory = new TestInventory();
	}

	@Override
	public ContainerData getPropertyDelegate()
	{
		return new ContainerData()
		{
			@Override
			public int get(int i)
			{
				return switch (i)
						{
							case 0 -> energy.getEnergyStored();
							case 1 -> energy.getMaxEnergyStored();
							default -> throw new IllegalStateException("Unexpected value: " + i);
						};
			}

			@Override
			public void set(int i, int j)
			{

			}

			@Override
			public int getCount()
			{
				return 2;
			}
		};
	}

	@Override
	public Component getDisplayName()
	{
		return new TextComponent("Test");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player)
	{
		return new TestContainer(i, inventory, ContainerLevelAccess.create(level, worldPosition), getDisplayName());
	}

	@Override
	public WorldlyContainer getContainer(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos)
	{
		return inventory;
	}

	@Override
	public Optional<IMFStorage> getStorageFor(Object that)
	{
		if (that == null) return Optional.of(energy);
		if (that instanceof Direction)
		{
			return Optional.of(energy);
		}
		return Optional.empty();
	}



	@Override
	public void load(CompoundTag compoundTag)
	{
		if (!compoundTag.contains("Energy")) compoundTag.putInt("Energy", 0);
		energy.deserializeNBT(compoundTag.get("Energy"));
		inventory.fromTag(compoundTag.getList("Inventory", 0));
		super.load(compoundTag);
	}

	@Override
	protected void saveAdditional(CompoundTag compoundTag)
	{
		compoundTag.put("Energy", energy.serializeNBT());
		compoundTag.put("Inventory", inventory.createTag());
		super.saveAdditional(compoundTag);
	}

	public static class TestInventory extends SimpleContainer implements WorldlyContainer
	{
		public TestInventory()
		{
			super(INV_SIZE);
		}

		@Override
		public int[] getSlotsForFace(Direction direction)
		{
			return IntStream.rangeClosed(0, INV_SIZE - 1).toArray();
		}

		@Override
		public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction)
		{
			return true;
		}

		@Override
		public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction)
		{
			return true;
		}
	}
}
