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

package com.withertech.mine_gui.forge;

import com.withertech.mine_gui.MineGui;
import com.withertech.mine_gui.impl.client.MineGuiConfig;
import dev.architectury.platform.forge.EventBuses;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MineGui.MOD_ID)
public class MineGuiForge
{
	public MineGuiForge()
	{
		// Submit our event bus to let architectury register our content on the right time
		EventBuses.registerModEventBus(MineGui.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLClientSetup);
		AutoConfig.register(MineGuiConfig.class, Toml4jConfigSerializer::new);
		MineGui.init();
	}

	@SubscribeEvent
	public void onFMLClientSetup(FMLClientSetupEvent event)
	{
		MineGui.clientInit();
	}
}