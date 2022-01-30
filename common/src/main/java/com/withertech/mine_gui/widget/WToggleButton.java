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
import com.withertech.mine_gui.client.ScreenDrawing;
import com.withertech.mine_gui.impl.client.NarrationMessages;
import com.withertech.mine_gui.widget.data.InputResult;
import com.withertech.mine_gui.widget.data.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class WToggleButton extends WWidget
{
	// Default on/off images
	protected static final Texture DEFAULT_OFF_IMAGE = new Texture(new ResourceLocation(MineGui.MOD_ID, "textures/widget/toggle_off.png"));
	protected static final Texture DEFAULT_ON_IMAGE = new Texture(new ResourceLocation(MineGui.MOD_ID, "textures/widget/toggle_on.png"));
	protected static final Texture DEFAULT_FOCUS_IMAGE = new Texture(new ResourceLocation(MineGui.MOD_ID, "textures/widget/toggle_focus.png"));

	protected Texture onImage;
	protected Texture offImage;
	protected Texture focusImage = DEFAULT_FOCUS_IMAGE;

	@Nullable
	protected Component label = null;

	protected boolean isOn = false;
	@Nullable
	protected Consumer<Boolean> onToggle = null;

	protected int color = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkmodeColor = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;

	/**
	 * Constructs a toggle button with default images and no label.
	 */
	public WToggleButton()
	{
		this(DEFAULT_ON_IMAGE, DEFAULT_OFF_IMAGE);
	}

	/**
	 * Constructs a toggle button with default images.
	 *
	 * @param label the button label
	 */
	public WToggleButton(Component label)
	{
		this(DEFAULT_ON_IMAGE, DEFAULT_OFF_IMAGE);
		this.label = label;
	}

	/**
	 * Constructs a toggle button with custom images and no label.
	 *
	 * @param onImage  the toggled on image
	 * @param offImage the toggled off image
	 */
	public WToggleButton(ResourceLocation onImage, ResourceLocation offImage)
	{
		this(new Texture(onImage), new Texture(offImage));
	}

	/**
	 * Constructs a toggle button with custom images.
	 *
	 * @param onImage  the toggled on image
	 * @param offImage the toggled off image
	 * @param label    the button label
	 */
	public WToggleButton(ResourceLocation onImage, ResourceLocation offImage, Component label)
	{
		this(new Texture(onImage), new Texture(offImage), label);
	}

	/**
	 * Constructs a toggle button with custom images and no label.
	 *
	 * @param onImage  the toggled on image
	 * @param offImage the toggled off image
	 * @since 3.0.0
	 */
	public WToggleButton(Texture onImage, Texture offImage)
	{
		this.onImage = onImage;
		this.offImage = offImage;
	}

	/**
	 * Constructs a toggle button with custom images.
	 *
	 * @param onImage  the toggled on image
	 * @param offImage the toggled off image
	 * @param label    the button label
	 * @since 3.0.0
	 */
	public WToggleButton(Texture onImage, Texture offImage, Component label)
	{
		this.onImage = onImage;
		this.offImage = offImage;
		this.label = label;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY)
	{
		ScreenDrawing.texturedRect(matrices, x, y, 18, 18, isOn ? onImage : offImage, 0xFFFFFFFF);
		if (isFocused())
		{
			ScreenDrawing.texturedRect(matrices, x, y, 18, 18, focusImage, 0xFFFFFFFF);
		}

		if (label != null)
		{
			ScreenDrawing.drawString(matrices, label.getVisualOrderText(), x + 22, y + 6, MineGui.isDarkMode() ? darkmodeColor : color);
		}
	}

	@Override
	public boolean canResize()
	{
		return true;
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
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

		this.isOn = !this.isOn;
		onToggle(this.isOn);
		return InputResult.PROCESSED;
	}

	@Override
	public void onKeyPressed(int ch, int key, int modifiers)
	{
		if (isActivationKey(ch))
		{
			onClick(0, 0, 0);
		}
	}

	protected void onToggle(boolean on)
	{
		if (this.onToggle != null)
		{
			this.onToggle.accept(on);
		}
	}

	public boolean getToggle() {return this.isOn;}

	public void setToggle(boolean on) {this.isOn = on;}

	@Nullable
	public Consumer<Boolean> getOnToggle()
	{
		return this.onToggle;
	}

	public WToggleButton setOnToggle(@Nullable Consumer<Boolean> onToggle)
	{
		this.onToggle = onToggle;
		return this;
	}

	@Nullable
	public Component getLabel()
	{
		return label;
	}

	public WToggleButton setLabel(@Nullable Component label)
	{
		this.label = label;
		return this;
	}

	public WToggleButton setColor(int light, int dark)
	{
		this.color = light;
		this.darkmodeColor = dark;

		return this;
	}

	public Texture getOnImage()
	{
		return onImage;
	}

	public WToggleButton setOnImage(Texture onImage)
	{
		this.onImage = onImage;
		return this;
	}

	public Texture getOffImage()
	{
		return offImage;
	}

	public WToggleButton setOffImage(Texture offImage)
	{
		this.offImage = offImage;
		return this;
	}

	public Texture getFocusImage()
	{
		return focusImage;
	}

	public WToggleButton setFocusImage(Texture focusImage)
	{
		this.focusImage = focusImage;
		return this;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addNarrations(NarrationElementOutput builder)
	{
		Component onOff = isOn ? NarrationMessages.TOGGLE_BUTTON_ON : NarrationMessages.TOGGLE_BUTTON_OFF;
		Component title;

		if (label != null)
		{
			title = new TranslatableComponent(NarrationMessages.TOGGLE_BUTTON_NAMED_KEY, label, onOff);
		} else
		{
			title = new TranslatableComponent(NarrationMessages.TOGGLE_BUTTON_UNNAMED_KEY, onOff);
		}

		builder.add(NarratedElementType.TITLE, title);

		if (isFocused())
		{
			builder.add(NarratedElementType.USAGE, NarrationMessages.Vanilla.BUTTON_USAGE_FOCUSED);
		} else if (isHovered())
		{
			builder.add(NarratedElementType.USAGE, NarrationMessages.Vanilla.BUTTON_USAGE_HOVERED);
		}
	}
}
