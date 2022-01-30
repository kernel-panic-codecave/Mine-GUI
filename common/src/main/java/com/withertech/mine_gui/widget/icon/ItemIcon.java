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

package com.withertech.mine_gui.widget.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * An icon that draws an item stack.
 *
 * @since 2.2.0
 */
public class ItemIcon implements Icon
{
	private final ItemStack stack;

	/**
	 * Constructs an item icon.
	 *
	 * @param stack the drawn item stack
	 * @throws NullPointerException if the stack is null
	 */
	public ItemIcon(ItemStack stack)
	{
		this.stack = Objects.requireNonNull(stack, "stack");
	}

	/**
	 * Constructs an item icon with the item's default stack.
	 *
	 * @param item the drawn item
	 * @throws NullPointerException if the item is null
	 * @since 3.2.0
	 */
	public ItemIcon(Item item)
	{
		this(Objects.requireNonNull(item, "item").getDefaultInstance());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(PoseStack matrices, int x, int y, int size)
	{
		// TODO: Make this not ignore the actual matrices
		Minecraft client = Minecraft.getInstance();
		ItemRenderer renderer = client.getItemRenderer();
		PoseStack modelViewMatrices = RenderSystem.getModelViewStack();

		float scale = size != 16 ? ((float) size / 16f) : 1f;

		modelViewMatrices.pushPose();
		modelViewMatrices.translate(x, y, 0);
		modelViewMatrices.scale(scale, scale, 1);
		RenderSystem.applyModelViewMatrix();
		renderer.renderAndDecorateFakeItem(stack, 0, 0);
		modelViewMatrices.popPose();
		RenderSystem.applyModelViewMatrix();
	}
}
