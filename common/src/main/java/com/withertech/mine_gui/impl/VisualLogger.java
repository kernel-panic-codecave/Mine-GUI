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

package com.withertech.mine_gui.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.withertech.mine_gui.client.ScreenDrawing;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * A "logger" that renders its messages on the screen in dev envs.
 */
public final class VisualLogger
{
	private static final List<Component> WARNINGS = new ArrayList<>();

	private final Logger logger;
	private final Class<?> clazz;

	public VisualLogger(Class<?> clazz)
	{
		logger = LogManager.getLogger(clazz);
		this.clazz = clazz;
	}

	@Environment(EnvType.CLIENT)
	public static void render(PoseStack matrices)
	{
		var client = Minecraft.getInstance();
		var textRenderer = client.font;
		int width = client.getWindow().getGuiScaledWidth();
		List<FormattedCharSequence> lines = new ArrayList<>();

		for (Component warning : WARNINGS)
		{
			lines.addAll(textRenderer.split(warning, width));
		}

		int fontHeight = textRenderer.lineHeight;
		int y = 0;

		for (var line : lines)
		{
			ScreenDrawing.coloredRect(matrices, 2, 2 + y, textRenderer.width(line), fontHeight, 0x88_000000);
			ScreenDrawing.drawString(matrices, line, 2, 2 + y, 0xFF_FFFFFF);
			y += fontHeight;
		}
	}

	public static void reset()
	{
		WARNINGS.clear();
	}

	public void error(String message, Object... params)
	{
		log(message, params, Level.ERROR, ChatFormatting.RED);
	}

	public void warn(String message, Object... params)
	{
		log(message, params, Level.WARN, ChatFormatting.GOLD);
	}

	private void log(String message, Object[] params, Level level, ChatFormatting formatting)
	{
		logger.log(level, message, params);

		if (Platform.isDevelopmentEnvironment())
		{
			var text = new TextComponent(clazz.getSimpleName() + '/');
			text.append(new TextComponent(level.name()).withStyle(formatting));
			text.append(new TextComponent(": " + ParameterizedMessage.format(message, params)));

			WARNINGS.add(text);
		}
	}
}
