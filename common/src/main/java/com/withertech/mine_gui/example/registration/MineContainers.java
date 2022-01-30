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

package com.withertech.mine_gui.example.registration;

import com.withertech.mine_gui.example.container.TestContainer;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.inventory.MenuType;

public class MineContainers
{
	public static RegistrySupplier<MenuType<TestContainer>> TEST_CONTAINER;

	static
	{
		TEST_CONTAINER = MineRegistries.CONTAINERS.register("test_container", () -> MenuRegistry.ofExtended(TestContainer::new));
	}

	public static void register()
	{
	}
}
