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

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A widget that displays an item or a list of items.
 *
 * @since 1.8.0
 */
public class WItem extends WWidget
{
	private List<ItemStack> items;
	private int duration = 25;
	private int ticks = 0;
	private int current = 0;

	public WItem(List<ItemStack> items)
	{
		setItems(items);
	}

	public WItem(Tag<? extends ItemLike> tag)
	{
		this(getRenderStacks(tag));
	}

	public WItem(ItemStack stack)
	{
		this(Collections.singletonList(stack));
	}

	/**
	 * Gets the render stacks ({@link Item#getStackForRender()}) of each item in a tag.
	 */
	private static List<ItemStack> getRenderStacks(Tag<? extends ItemLike> tag)
	{
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

		for (ItemLike item : tag.getValues())
		{
			builder.add(new ItemStack(item));
		}

		return builder.build();
	}

	@Override
	public boolean canResize()
	{
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void tick()
	{
		if (ticks++ >= duration)
		{
			ticks = 0;
			current = (current + 1) % items.size();
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY)
	{
		RenderSystem.enableDepthTest();

		Minecraft mc = Minecraft.getInstance();
		ItemRenderer renderer = mc.getItemRenderer();
		renderer.blitOffset = 100f;
		renderer.renderAndDecorateFakeItem(items.get(current), x + getWidth() / 2 - 9, y + getHeight() / 2 - 9);
		renderer.blitOffset = 0f;
	}

	/**
	 * Returns the animation duration of this {@code WItem}.
	 *
	 * <p>Defaults to 25 screen ticks.
	 */
	public int getDuration()
	{
		return duration;
	}

	public WItem setDuration(int duration)
	{
		this.duration = duration;
		return this;
	}

	public List<ItemStack> getItems()
	{
		return items;
	}

	/**
	 * Sets the item list of this {@code WItem} and resets the animation state.
	 *
	 * @param items the new item list
	 * @return this instance
	 */
	public WItem setItems(List<ItemStack> items)
	{
		Objects.requireNonNull(items, "stacks == null!");
		if (items.isEmpty()) throw new IllegalArgumentException("The stack list is empty!");

		this.items = items;

		// Reset the state
		current = 0;
		ticks = 0;

		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof WItem wItem)) return false;
		if (!super.equals(o)) return false;
		return getDuration() == wItem.getDuration() && ticks == wItem.ticks && current == wItem.current && Objects.equals(getItems(), wItem.getItems());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), getItems(), getDuration(), ticks, current);
	}

	@Override
	public String toString()
	{
		return "WItem{" +
				"items=" + items +
				", duration=" + duration +
				", ticks=" + ticks +
				", current=" + current +
				", parent=" + parent +
				", x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				", host=" + host +
				'}';
	}
}
