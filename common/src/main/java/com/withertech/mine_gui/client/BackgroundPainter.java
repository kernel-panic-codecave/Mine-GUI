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

package com.withertech.mine_gui.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.withertech.mine_gui.MineGui;
import com.withertech.mine_gui.ninepatch.NinePatch;
import com.withertech.mine_gui.ninepatch.TextureRegion;
import com.withertech.mine_gui.widget.WItemSlot;
import com.withertech.mine_gui.widget.WWidget;
import com.withertech.mine_gui.widget.data.Texture;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Background painters are used to paint the background of a widget.
 * The background painter instance of a widget can be changed to customize the look of a widget.
 */
@FunctionalInterface
public interface BackgroundPainter
{
	/**
	 * The {@code VANILLA} background painter draws a vanilla-like GUI panel using nine-patch textures.
	 *
	 * <p>This background painter uses {@code libgui:textures/widget/panel_light.png} as the light texture and
	 * {@code libgui:textures/widget/panel_dark.png} as the dark texture.
	 *
	 * <p>This background painter is the default painter for root panels.
	 * * You can override {@link io.github.cottonmc.cotton.gui.GuiDescription#addPainters()} to customize the painter yourself.
	 *
	 * @since 1.5.0
	 */
	BackgroundPainter VANILLA = createLightDarkVariants(
			createNinePatch(new ResourceLocation(MineGui.MOD_ID, "textures/widget/panel_light.png")),
			createNinePatch(new ResourceLocation(MineGui.MOD_ID, "textures/widget/panel_dark.png"))
	);
	/**
	 * The {@code SLOT} background painter draws item slots or slot-like widgets.
	 */
	BackgroundPainter SLOT = (matrices, left, top, panel) ->
	{
		if (!(panel instanceof WItemSlot))
		{
			ScreenDrawing.drawBeveledPanel(matrices, left - 1, top - 1, panel.getWidth() + 2, panel.getHeight() + 2, 0xB8000000, 0x4C000000, 0xB8FFFFFF);
		} else
		{
			WItemSlot slot = (WItemSlot) panel;
			for (int x = 0; x < slot.getWidth() / 18; ++x)
			{
				for (int y = 0; y < slot.getHeight() / 18; ++y)
				{
					int index = x + y * (slot.getWidth() / 18);
					int lo = 0xB8000000;
					int bg = 0x4C000000;
					//this will cause a slightly discolored bottom border on vanilla backgrounds but it's necessary for color support, it shouldn't be *too* visible unless you're looking for it
					int hi = 0xB8FFFFFF;
					if (slot.isBigSlot())
					{
						ScreenDrawing.drawBeveledPanel(matrices, (x * 18) + left - 4, (y * 18) + top - 4, 26, 26,
								lo, bg, hi);
						if (slot.getFocusedSlot() == index)
						{
							int sx = (x * 18) + left - 4;
							int sy = (y * 18) + top - 4;
							ScreenDrawing.coloredRect(matrices, sx, sy, 26, 1, 0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx, sy + 1, 1, 26 - 1, 0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx + 26 - 1, sy + 1, 1, 26 - 1, 0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx + 1, sy + 26 - 1, 26 - 1, 1, 0xFF_FFFFA0);
						}
					} else
					{
						ScreenDrawing.drawBeveledPanel(matrices, (x * 18) + left, (y * 18) + top, 16 + 2, 16 + 2,
								lo, bg, hi);
						if (slot.getFocusedSlot() == index)
						{
							int sx = (x * 18) + left;
							int sy = (y * 18) + top;
							ScreenDrawing.coloredRect(matrices, sx, sy, 18, 1, 0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx, sy + 1, 1, 18 - 1, 0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx + 18 - 1, sy + 1, 1, 18 - 1, 0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx + 1, sy + 18 - 1, 18 - 1, 1, 0xFF_FFFFA0);
						}
					}
				}
			}
		}
	};

	/**
	 * Creates a colorful gui panel painter. This painter paints the panel using the specified color.
	 *
	 * @param panelColor the panel background color
	 * @return a colorful gui panel painter
	 * @see ScreenDrawing#drawGuiPanel(PoseStack, int, int, int, int, int)
	 */
	static BackgroundPainter createColorful(int panelColor)
	{
		return (matrices, left, top, panel) ->
		{
			ScreenDrawing.drawGuiPanel(matrices, left, top, panel.getWidth(), panel.getHeight(), panelColor);
		};
	}

	/**
	 * Creates a colorful gui panel painter that has a custom contrast between the shadows and highlights.
	 *
	 * @param panelColor the panel background color
	 * @param contrast   the contrast between the shadows and highlights
	 * @return a colorful gui panel painter
	 */
	static BackgroundPainter createColorful(int panelColor, float contrast)
	{
		return (matrices, left, top, panel) ->
		{
			int shadowColor = ScreenDrawing.multiplyColor(panelColor, 1.0f - contrast);
			int hilightColor = ScreenDrawing.multiplyColor(panelColor, 1.0f + contrast);

			ScreenDrawing.drawGuiPanel(matrices, left, top, panel.getWidth(), panel.getHeight(), shadowColor, panelColor, hilightColor, 0xFF000000);
		};
	}

	/**
	 * Creates a new nine-patch background painter.
	 *
	 * <p>The resulting painter has a corner size of 4 px and a corner UV of 0.25.
	 *
	 * @param texture the background painter texture
	 * @return a new nine-patch background painter
	 * @see NinePatchBackgroundPainter
	 * @since 1.5.0
	 */
	static NinePatchBackgroundPainter createNinePatch(ResourceLocation texture)
	{
		return createNinePatch(new Texture(texture), builder -> builder.cornerSize(4).cornerUv(0.25f));
	}

	/**
	 * Creates a new nine-patch background painter with a custom configuration.
	 *
	 * @param texture      the background painter texture
	 * @param configurator a consumer that configures the {@link NinePatch.Builder}
	 * @return the created nine-patch background painter
	 * @see NinePatch
	 * @see NinePatch.Builder
	 * @see NinePatchBackgroundPainter
	 * @since 4.0.0
	 */
	static NinePatchBackgroundPainter createNinePatch(Texture texture, Consumer<NinePatch.Builder<ResourceLocation>> configurator)
	{
		TextureRegion<ResourceLocation> region = new TextureRegion<>(texture.image(), texture.u1(), texture.v1(), texture.u2(), texture.v2());
		var builder = NinePatch.builder(region);
		configurator.accept(builder);
		return new NinePatchBackgroundPainter(builder.build());
	}

	/**
	 * Creates a background painter that uses either the {@code light} or the {@code dark} background painter
	 * depending on the current setting.
	 *
	 * @param light the light mode background painter
	 * @param dark  the dark mode background painter
	 * @return a new background painter that chooses between the two inputs
	 * @since 1.5.0
	 */
	static BackgroundPainter createLightDarkVariants(BackgroundPainter light, BackgroundPainter dark)
	{
		return (matrices, left, top, panel) ->
		{
			if (MineGui.isDarkMode()) dark.paintBackground(matrices, left, top, panel);
			else light.paintBackground(matrices, left, top, panel);
		};
	}

	/**
	 * Paint the specified panel to the screen.
	 *
	 * @param left  The absolute position of the left of the panel, in gui-screen coordinates
	 * @param top   The absolute position of the top of the panel, in gui-screen coordinates
	 * @param panel The panel being painted
	 */
	void paintBackground(PoseStack matrices, int left, int top, WWidget panel);
}
