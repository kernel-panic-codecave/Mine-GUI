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
import com.withertech.mine_gui.GuiDescription;
import com.withertech.mine_gui.impl.VisualLogger;
import com.withertech.mine_gui.impl.client.MineGuiScreenImpl;
import com.withertech.mine_gui.impl.client.MouseInputHandler;
import com.withertech.mine_gui.impl.client.NarrationHelper;
import com.withertech.mine_gui.widget.WPanel;
import com.withertech.mine_gui.widget.WWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class MineGuiClientScreen extends Screen implements MineGuiScreenImpl
{
	private final MouseInputHandler<MineGuiClientScreen> mouseInputHandler = new MouseInputHandler<>(this);
	protected GuiDescription description;
	protected int left = 0;
	protected int top = 0;
	/**
	 * The X coordinate of the screen title.
	 * This is relative to the root panel's top-left corner.
	 *
	 * @since 2.0.0
	 */
	protected int titleX;
	/**
	 * The Y coordinate of the screen title.
	 * This is relative to the root panel's top-left corner.
	 *
	 * @since 2.0.0
	 */
	protected int titleY;
	@Nullable
	protected WWidget lastResponder = null;

	public MineGuiClientScreen(GuiDescription description)
	{
		this(new TextComponent(""), description);
	}

	public MineGuiClientScreen(Component title, GuiDescription description)
	{
		super(title);
		this.description = description;
		description.getRootPanel().validate(description);
	}

	@Override
	public GuiDescription getDescription()
	{
		return description;
	}

	@Override
	public void init()
	{
		super.init();
		minecraft.keyboardHandler.setSendRepeatsToGui(true);

		WPanel root = description.getRootPanel();
		if (root != null) root.addPainters();
		description.addPainters();
		reposition(width, height);
	}

	@Override
	public void removed()
	{
		super.removed();
		this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
		VisualLogger.reset();
	}

	@Nullable
	@Override
	public WWidget getLastResponder()
	{
		return lastResponder;
	}

	@Override
	public void setLastResponder(@Nullable WWidget lastResponder)
	{
		this.lastResponder = lastResponder;
	}

	/**
	 * Repositions the root panel.
	 *
	 * @param screenWidth  the width of the screen
	 * @param screenHeight the height of the screen
	 */
	protected void reposition(int screenWidth, int screenHeight)
	{
		if (description != null)
		{
			WPanel root = description.getRootPanel();
			if (root != null)
			{
				titleX = description.getTitlePos().x();
				titleY = description.getTitlePos().y();

				if (!description.isFullscreen())
				{
					this.left = (screenWidth - root.getWidth()) / 2;
					this.top = (screenHeight - root.getHeight()) / 2;
				} else
				{
					this.left = 0;
					this.top = 0;

					root.setSize(screenWidth, screenHeight);
				}
			}
		}
	}

	private void paint(PoseStack matrices, int mouseX, int mouseY)
	{
		renderBackground(matrices);

		if (description != null)
		{
			WPanel root = description.getRootPanel();
			if (root != null)
			{
				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				Scissors.refreshScissors();
				root.paint(matrices, left, top, mouseX - left, mouseY - top);
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				Scissors.checkStackIsEmpty();
			}

			if (getTitle() != null && description.isTitleVisible())
			{
				int width = description.getRootPanel().getWidth();
				ScreenDrawing.drawString(matrices, getTitle().getVisualOrderText(), description.getTitleAlignment(), left + titleX, top + titleY, width - titleX, description.getTitleColor());
			}
		}
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float partialTicks)
	{
		paint(matrices, mouseX, mouseY);

		super.render(matrices, mouseX, mouseY, partialTicks);

		if (description != null)
		{
			WPanel root = description.getRootPanel();
			if (root != null)
			{
				WWidget hitChild = root.hit(mouseX - left, mouseY - top);
				if (hitChild != null) hitChild.renderTooltip(matrices, left, top, mouseX - left, mouseY - top);
			}
		}

		VisualLogger.render(matrices);
	}


	@Override
	public void tick()
	{
		super.tick();
		if (description != null)
		{
			WPanel root = description.getRootPanel();
			if (root != null)
			{
				root.tick();
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		if (description.getRootPanel() == null) return super.mouseClicked(mouseX, mouseY, mouseButton);
		WWidget focus = description.getFocus();
		if (focus != null)
		{

			int wx = focus.getAbsoluteX();
			int wy = focus.getAbsoluteY();

			if (mouseX >= wx && mouseX < wx + focus.getWidth() && mouseY >= wy && mouseY < wy + focus.getHeight())
			{
				//Do nothing, focus will get the click soon
			} else
			{
				//Invalidate the component first
				description.releaseFocus(focus);
			}
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
		int containerX = (int) mouseX - left;
		int containerY = (int) mouseY - top;
		if (containerX < 0 || containerY < 0 || containerX >= width || containerY >= height) return true;
		mouseInputHandler.onMouseDown(containerX, containerY, mouseButton);

		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
	{
		if (description.getRootPanel() == null) return super.mouseReleased(mouseX, mouseY, mouseButton);
		super.mouseReleased(mouseX, mouseY, mouseButton);
		int containerX = (int) mouseX - left;
		int containerY = (int) mouseY - top;
		mouseInputHandler.onMouseUp(containerX, containerY, mouseButton);

		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
	{
		if (description.getRootPanel() == null) return super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
		super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);

		int containerX = (int) mouseX - left;
		int containerY = (int) mouseY - top;
		mouseInputHandler.onMouseDrag(containerX, containerY, mouseButton, deltaX, deltaY);

		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		if (description.getRootPanel() == null) return super.mouseScrolled(mouseX, mouseY, amount);

		int containerX = (int) mouseX - left;
		int containerY = (int) mouseY - top;
		mouseInputHandler.onMouseScroll(containerX, containerY, amount);

		return true;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{
		if (description.getRootPanel() == null) return;

		int containerX = (int) mouseX - left;
		int containerY = (int) mouseY - top;
		mouseInputHandler.onMouseMove(containerX, containerY);
	}

	@Override
	public boolean charTyped(char ch, int keyCode)
	{
		if (description.getFocus() == null) return false;
		description.getFocus().onCharTyped(ch);
		return true;
	}

	@Override
	public boolean keyPressed(int ch, int keyCode, int modifiers)
	{
		if (super.keyPressed(ch, keyCode, modifiers)) return true;
		if (description.getFocus() == null) return false;
		description.getFocus().onKeyPressed(ch, keyCode, modifiers);
		return true;
	}

	@Override
	public boolean keyReleased(int ch, int keyCode, int modifiers)
	{
		if (description.getFocus() == null) return false;
		description.getFocus().onKeyReleased(ch, keyCode, modifiers);
		return true;
	}

	//@Override
	//public Element getFocused() {
	//return this;
	//}

	@Override
	public void renderTextHover(PoseStack matrices, @Nullable Style textStyle, int x, int y)
	{
		renderComponentHoverEffect(matrices, textStyle, x, y);
	}

	@Override
	public boolean changeFocus(boolean lookForwards)
	{
		if (description != null)
		{
			description.cycleFocus(lookForwards);
		}

		return true;
	}

	@Override
	protected void updateNarratedWidget(NarrationElementOutput builder)
	{
		if (description != null) NarrationHelper.addNarrations(description.getRootPanel(), builder);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof MineGuiClientScreen that)) return false;
		return left == that.left && top == that.top && titleX == that.titleX && titleY == that.titleY && Objects.equals(mouseInputHandler, that.mouseInputHandler) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getLastResponder(), that.getLastResponder());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(mouseInputHandler, getDescription(), left, top, titleX, titleY, getLastResponder());
	}

	@Override
	public String toString()
	{
		return "MineGuiClientScreen{" +
				"mouseInputHandler=" + mouseInputHandler +
				", description=" + description +
				", left=" + left +
				", top=" + top +
				", titleX=" + titleX +
				", titleY=" + titleY +
				", lastResponder=" + lastResponder +
				", title=" + title +
				", minecraft=" + minecraft +
				", itemRenderer=" + itemRenderer +
				", width=" + width +
				", height=" + height +
				", passEvents=" + passEvents +
				", font=" + font +
				'}';
	}
}
