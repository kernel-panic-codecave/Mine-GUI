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

import com.withertech.mine_gui.example.client.screen.TestScreen;
import com.withertech.mine_gui.example.container.TestContainer;
import com.withertech.mine_gui.example.registration.MineContainers;
import com.withertech.mine_gui.example.registration.MineRegistries;
import com.withertech.mine_gui.impl.ScreenNetworkingImpl;
import com.withertech.mine_gui.impl.client.MineGuiConfig;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.registry.menu.MenuRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineGui
{
	public static final Logger LOG = LogManager.getLogger(MineGui.class);
	public static final String MOD_ID = "mine_gui";

	public static void init()
	{
		if (isExampleContentEnabled())
			MineRegistries.register();
		Platform.getMod(MOD_ID).registerConfigurationScreen(screen -> AutoConfig.getConfigScreen(MineGuiConfig.class, screen).get());
		ScreenNetworkingImpl.init();
	}

	public static void clientInit()
	{
		if (isExampleContentEnabled())
			MenuRegistry.<TestContainer, TestScreen>registerScreenFactory(MineContainers.TEST_CONTAINER.get(), (containerMenu, inventory, component) -> new TestScreen(containerMenu, inventory.player, component));
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, ScreenNetworkingImpl.SCREEN_MESSAGE_S2C, ScreenNetworkingImpl::handle);
	}

	private static boolean isExampleContentEnabled()
	{
		return AutoConfig.getConfigHolder(MineGuiConfig.class).getConfig().enableExampleContent;
	}

	/**
	 * Returns whether MineGui is running in dark mode and widgets should use dark theming.
	 *
	 * @return true if widgets should use dark theming, false otherwise
	 */
	public static boolean isDarkMode()
	{
		return AutoConfig.getConfigHolder(MineGuiConfig.class).getConfig().darkMode;
	}
}