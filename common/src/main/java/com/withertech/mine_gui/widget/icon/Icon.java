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

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A square icon for a widget such as a button.
 *
 * @see ItemIcon
 * @see TextureIcon
 * @since 2.2.0
 */
public interface Icon
{
	/**
	 * Paints this icon.
	 *
	 * @param matrices the GUI matrix stack
	 * @param x        the X coordinate
	 * @param y        the Y coordinate
	 * @param size     the size of this icon in pixels (size N means a N*N square)
	 */
	@Environment(EnvType.CLIENT)
	void paint(PoseStack matrices, int x, int y, int size);
}
