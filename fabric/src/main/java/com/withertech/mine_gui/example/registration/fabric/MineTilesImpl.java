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

package com.withertech.mine_gui.example.registration.fabric;

import com.withertech.mine_gui.example.registration.MineRegistries;
import com.withertech.mine_gui.example.registration.MineTiles;
import com.withertech.mine_gui.example.tile.fabric.TestTileFabric;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public class MineTilesImpl
{
	static
	{
		MineTiles.TEST_TILE = MineRegistries.TILES.register("test_tile", () -> FabricBlockEntityTypeBuilder.create(TestTileFabric::new).build());
	}
	public static void register()
	{
	}
}
