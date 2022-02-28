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

import com.withertech.mine_gui.GuiDescription;
import com.withertech.mine_gui.MineGui;
import com.withertech.mine_gui.ValidatedSlot;
import com.withertech.mine_gui.widget.WGridPanel;
import com.withertech.mine_gui.widget.WLabel;
import com.withertech.mine_gui.widget.WPanel;
import com.withertech.mine_gui.widget.WWidget;
import com.withertech.mine_gui.widget.data.HorizontalAlignment;
import com.withertech.mine_gui.widget.data.Insets;
import com.withertech.mine_gui.widget.data.Vec2i;
import net.minecraft.world.inventory.ContainerData;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A GuiDescription without any associated Minecraft classes
 */
public class LightweightGuiDescription implements GuiDescription
{
	protected WPanel rootPanel = new WGridPanel().setInsets(Insets.ROOT_PANEL);
	protected ContainerData propertyDelegate;
	protected WWidget focus;

	protected int titleColor = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkmodeTitleColor = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;
	protected boolean fullscreen = false;
	protected boolean titleVisible = true;
	protected HorizontalAlignment titleAlignment = HorizontalAlignment.LEFT;
	private Vec2i titlePos = new Vec2i(8, 6);

	@Override
	public WPanel getRootPanel()
	{
		return rootPanel;
	}

	@Override
	public int getTitleColor()
	{
		return (MineGui.isDarkMode()) ? darkmodeTitleColor : titleColor;
	}

	@Override
	public GuiDescription setRootPanel(WPanel panel)
	{
		this.rootPanel = panel;
		return this;
	}

	@Override
	public GuiDescription setTitleColor(int color)
	{
		this.titleColor = color;
		this.darkmodeTitleColor = (color == WLabel.DEFAULT_TEXT_COLOR) ? WLabel.DEFAULT_DARKMODE_TEXT_COLOR : color;
		return this;
	}

	@Override
	public GuiDescription setTitleColor(int lightColor, int darkColor)
	{
		this.titleColor = lightColor;
		this.darkmodeTitleColor = darkColor;
		return this;
	}

	@Override
	public void addPainters()
	{
		if (this.rootPanel != null && !fullscreen)
		{
			this.rootPanel.setBackgroundPainter(BackgroundPainter.VANILLA);
		}
	}

	@Override
	public void addSlotPeer(ValidatedSlot slot)
	{
		//NO-OP
	}

	@Override
	@Nullable
	public ContainerData getPropertyDelegate()
	{
		return propertyDelegate;
	}

	@Override
	public GuiDescription setPropertyDelegate(ContainerData delegate)
	{
		this.propertyDelegate = delegate;
		return this;
	}

	@Override
	public boolean isFocused(WWidget widget)
	{
		return widget == focus;
	}

	@Override
	public WWidget getFocus()
	{
		return focus;
	}

	@Override
	public void requestFocus(WWidget widget)
	{
		//TODO: Are there circumstances where focus can't be stolen?
		if (focus == widget) return; //Nothing happens if we're already focused
		if (!widget.canFocus()) return; //This is kind of a gotcha but needs to happen
		if (focus != null) focus.onFocusLost();
		focus = widget;
		focus.onFocusGained();
	}

	@Override
	public void releaseFocus(WWidget widget)
	{
		if (focus == widget)
		{
			focus = null;
			widget.onFocusLost();
		}
	}

	@Override
	public boolean isFullscreen()
	{
		return fullscreen;
	}

	@Override
	public void setFullscreen(boolean fullscreen)
	{
		this.fullscreen = fullscreen;
	}

	@Override
	public boolean isTitleVisible()
	{
		return titleVisible;
	}

	@Override
	public void setTitleVisible(boolean titleVisible)
	{
		this.titleVisible = titleVisible;
	}

	@Override
	public HorizontalAlignment getTitleAlignment()
	{
		return titleAlignment;
	}

	@Override
	public void setTitleAlignment(HorizontalAlignment titleAlignment)
	{
		this.titleAlignment = titleAlignment;
	}

	@Override
	public Vec2i getTitlePos()
	{
		return titlePos;
	}

	@Override
	public void setTitlePos(Vec2i titlePos)
	{
		this.titlePos = titlePos;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof LightweightGuiDescription that)) return false;
		return getTitleColor() == that.getTitleColor() && darkmodeTitleColor == that.darkmodeTitleColor && isFullscreen() == that.isFullscreen() && isTitleVisible() == that.isTitleVisible() && Objects.equals(getRootPanel(), that.getRootPanel()) && Objects.equals(getPropertyDelegate(), that.getPropertyDelegate()) && Objects.equals(getFocus(), that.getFocus()) && getTitleAlignment() == that.getTitleAlignment() && Objects.equals(getTitlePos(), that.getTitlePos());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getRootPanel(), getPropertyDelegate(), getFocus(), getTitleColor(), darkmodeTitleColor, isFullscreen(), isTitleVisible(), getTitleAlignment(), getTitlePos());
	}

	@Override
	public String toString()
	{
		return "LightweightGuiDescription{" +
				"rootPanel=" + rootPanel +
				", propertyDelegate=" + propertyDelegate +
				", focus=" + focus +
				", titleColor=" + titleColor +
				", darkmodeTitleColor=" + darkmodeTitleColor +
				", fullscreen=" + fullscreen +
				", titleVisible=" + titleVisible +
				", titleAlignment=" + titleAlignment +
				", titlePos=" + titlePos +
				'}';
	}
}
