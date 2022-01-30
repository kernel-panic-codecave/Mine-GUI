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

import com.mojang.blaze3d.vertex.PoseStack;
import com.withertech.mine_gui.MineGui;
import com.withertech.mine_gui.client.BackgroundPainter;
import com.withertech.mine_gui.client.ScreenDrawing;
import com.withertech.mine_gui.impl.client.NarrationMessages;
import com.withertech.mine_gui.widget.data.Axis;
import com.withertech.mine_gui.widget.data.HorizontalAlignment;
import com.withertech.mine_gui.widget.data.InputResult;
import com.withertech.mine_gui.widget.icon.Icon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

// TODO: Different tab positions

/**
 * A panel that contains creative inventory-style tabs on the top.
 *
 * @since 3.0.0
 */
public class WTabPanel extends WPanel
{
	private static final int TAB_PADDING = 4;
	private static final int TAB_WIDTH = 28;
	private static final int TAB_HEIGHT = 30;
	private static final int ICON_SIZE = 16;
	private final WBox tabRibbon = new WBox(Axis.HORIZONTAL).setSpacing(1);
	private final List<WTab> tabWidgets = new ArrayList<>();
	private final WCardPanel mainPanel = new WCardPanel();

	/**
	 * Constructs a new tab panel.
	 */
	public WTabPanel()
	{
		add(tabRibbon, 0, 0);
		add(mainPanel, 0, TAB_HEIGHT);
	}

	private void add(WWidget widget, int x, int y)
	{
		children.add(widget);
		widget.setParent(this);
		widget.setLocation(x, y);
		expandToFit(widget);
	}

	/**
	 * Adds a tab to this panel.
	 *
	 * @param tab the added tab
	 */
	public void add(Tab tab)
	{
		WTab tabWidget = new WTab(tab);

		if (tabWidgets.isEmpty())
		{
			tabWidget.selected = true;
		}

		tabWidgets.add(tabWidget);
		tabRibbon.add(tabWidget, TAB_WIDTH, TAB_HEIGHT + TAB_PADDING);
		mainPanel.add(tab.getWidget());
	}

	/**
	 * Configures and adds a tab to this panel.
	 *
	 * @param widget       the contained widget
	 * @param configurator the tab configurator
	 */
	public void add(WWidget widget, Consumer<Tab.Builder> configurator)
	{
		Tab.Builder builder = new Tab.Builder(widget);
		configurator.accept(builder);
		add(builder.build());
	}

	@Override
	public void setSize(int x, int y)
	{
		super.setSize(x, y);
		tabRibbon.setSize(x, TAB_HEIGHT);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addPainters()
	{
		super.addPainters();
		mainPanel.setBackgroundPainter(BackgroundPainter.VANILLA);
	}

	/**
	 * The data of a tab.
	 */
	public static class Tab
	{
		@Nullable
		private final Component title;
		@Nullable
		private final Icon icon;
		private final WWidget widget;
		@Nullable
		private final Consumer<TooltipBuilder> tooltip;

		private Tab(@Nullable Component title, @Nullable Icon icon, WWidget widget, @Nullable Consumer<TooltipBuilder> tooltip)
		{
			if (title == null && icon == null)
			{
				throw new IllegalArgumentException("A tab must have a title or an icon");
			}

			this.title = title;
			this.icon = icon;
			this.widget = Objects.requireNonNull(widget, "widget");
			this.tooltip = tooltip;
		}

		/**
		 * Gets the title of this tab.
		 *
		 * @return the title, or null if there's no title
		 */
		@Nullable
		public Component getTitle()
		{
			return title;
		}

		/**
		 * Gets the icon of this tab.
		 *
		 * @return the icon, or null if there's no title
		 */
		@Nullable
		public Icon getIcon()
		{
			return icon;
		}

		/**
		 * Gets the contained widget of this tab.
		 *
		 * @return the contained widget
		 */
		public WWidget getWidget()
		{
			return widget;
		}

		/**
		 * Adds this widget's tooltip to the {@code tooltip} builder.
		 *
		 * @param tooltip the tooltip builder
		 */
		@Environment(EnvType.CLIENT)
		public void addTooltip(TooltipBuilder tooltip)
		{
			if (this.tooltip != null)
			{
				this.tooltip.accept(tooltip);
			}
		}

		/**
		 * A builder for tab data.
		 */
		public static final class Builder
		{
			private final WWidget widget;
			private final List<Component> tooltip = new ArrayList<>();
			@Nullable
			private Component title;
			@Nullable
			private Icon icon;

			/**
			 * Constructs a new tab data builder.
			 *
			 * @param widget the contained widget
			 * @throws NullPointerException if the widget is null
			 */
			public Builder(WWidget widget)
			{
				this.widget = Objects.requireNonNull(widget, "widget");
			}

			/**
			 * Sets the tab title.
			 *
			 * @param title the new title
			 * @return this builder
			 * @throws NullPointerException if the title is null
			 */
			public Builder title(Component title)
			{
				this.title = Objects.requireNonNull(title, "title");
				return this;
			}

			/**
			 * Sets the tab icon.
			 *
			 * @param icon the new icon
			 * @return this builder
			 * @throws NullPointerException if the icon is null
			 */
			public Builder icon(Icon icon)
			{
				this.icon = Objects.requireNonNull(icon, "icon");
				return this;
			}

			/**
			 * Adds lines to the tab's tooltip.
			 *
			 * @param lines the added lines
			 * @return this builder
			 * @throws NullPointerException if the line array is null
			 */
			public Builder tooltip(Component... lines)
			{
				Objects.requireNonNull(lines, "lines");
				Collections.addAll(tooltip, lines);

				return this;
			}

			/**
			 * Adds lines to the tab's tooltip.
			 *
			 * @param lines the added lines
			 * @return this builder
			 * @throws NullPointerException if the line collection is null
			 */
			public Builder tooltip(Collection<? extends Component> lines)
			{
				Objects.requireNonNull(lines, "lines");
				tooltip.addAll(lines);
				return this;
			}

			/**
			 * Builds a tab from this builder.
			 *
			 * @return the built tab
			 */
			public Tab build()
			{
				Consumer<TooltipBuilder> tooltip = null;

				if (!this.tooltip.isEmpty())
				{
					//noinspection Convert2Lambda
					tooltip = new Consumer<>()
					{
						@Environment(EnvType.CLIENT)
						@Override
						public void accept(TooltipBuilder builder)
						{
							builder.add(Builder.this.tooltip.toArray(new Component[0]));
						}
					};
				}

				return new Tab(title, icon, widget, tooltip);
			}
		}
	}

	/**
	 * Internal background painter instances for tabs.
	 */
	@Environment(EnvType.CLIENT)
	final static class Painters
	{
		static final BackgroundPainter SELECTED_TAB = BackgroundPainter.createLightDarkVariants(
				BackgroundPainter.createNinePatch(new ResourceLocation(MineGui.MOD_ID, "textures/widget/tab/selected_light.png")).setTopPadding(2),
				BackgroundPainter.createNinePatch(new ResourceLocation(MineGui.MOD_ID, "textures/widget/tab/selected_dark.png")).setTopPadding(2)
		);

		static final BackgroundPainter UNSELECTED_TAB = BackgroundPainter.createLightDarkVariants(
				BackgroundPainter.createNinePatch(new ResourceLocation(MineGui.MOD_ID, "textures/widget/tab/unselected_light.png")),
				BackgroundPainter.createNinePatch(new ResourceLocation(MineGui.MOD_ID, "textures/widget/tab/unselected_dark.png"))
		);

		static final BackgroundPainter SELECTED_TAB_FOCUS_BORDER = BackgroundPainter.createNinePatch(new ResourceLocation(MineGui.MOD_ID, "textures/widget/tab/focus.png")).setTopPadding(2);
		static final BackgroundPainter UNSELECTED_TAB_FOCUS_BORDER = BackgroundPainter.createNinePatch(new ResourceLocation(MineGui.MOD_ID, "textures/widget/tab/focus.png"));
	}

	private final class WTab extends WWidget
	{
		private final Tab data;
		boolean selected = false;

		WTab(Tab data)
		{
			this.data = data;
		}

		@Override
		public boolean canFocus()
		{
			return true;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public InputResult onClick(int x, int y, int button)
		{
			super.onClick(x, y, button);

			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

			for (WTab tab : tabWidgets)
			{
				tab.selected = (tab == this);
			}

			mainPanel.setSelectedCard(data.getWidget());
			WTabPanel.this.layout();
			return InputResult.PROCESSED;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void onKeyPressed(int ch, int key, int modifiers)
		{
			if (isActivationKey(ch))
			{
				onClick(0, 0, 0);
			}
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY)
		{
			Font renderer = Minecraft.getInstance().font;
			Component title = data.getTitle();
			Icon icon = data.getIcon();

			if (title != null)
			{
				int width = TAB_WIDTH + renderer.width(title);
				if (icon == null) width = Math.max(TAB_WIDTH, width - ICON_SIZE);

				if (this.width != width)
				{
					setSize(width, this.height);
					getParent().layout();
				}
			}

			(selected ? Painters.SELECTED_TAB : Painters.UNSELECTED_TAB).paintBackground(matrices, x, y, this);
			if (isFocused())
			{
				(selected ? Painters.SELECTED_TAB_FOCUS_BORDER : Painters.UNSELECTED_TAB_FOCUS_BORDER).paintBackground(matrices, x, y, this);
			}

			int iconX = 6;

			if (title != null)
			{
				int titleX = (icon != null) ? iconX + ICON_SIZE + 1 : 0;
				int titleY = (height - TAB_PADDING - renderer.lineHeight) / 2 + 1;
				int width = (icon != null) ? this.width - iconX - ICON_SIZE : this.width;
				HorizontalAlignment align = (icon != null) ? HorizontalAlignment.LEFT : HorizontalAlignment.CENTER;

				int color;
				if (MineGui.isDarkMode())
				{
					color = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;
				} else
				{
					color = selected ? WLabel.DEFAULT_TEXT_COLOR : 0xEEEEEE;
				}

				ScreenDrawing.drawString(matrices, title.getVisualOrderText(), align, x + titleX, y + titleY, width, color);
			}

			if (icon != null)
			{
				icon.paint(matrices, x + iconX, y + (height - TAB_PADDING - ICON_SIZE) / 2, ICON_SIZE);
			}
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void addTooltip(TooltipBuilder tooltip)
		{
			data.addTooltip(tooltip);
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void addNarrations(NarrationElementOutput builder)
		{
			Component label = data.getTitle();

			if (label != null)
			{
				builder.add(NarratedElementType.TITLE, new TranslatableComponent(NarrationMessages.TAB_TITLE_KEY, label));
			}

			builder.add(NarratedElementType.POSITION, new TranslatableComponent(NarrationMessages.TAB_POSITION_KEY, tabWidgets.indexOf(this) + 1, tabWidgets.size()));
		}
	}
}
