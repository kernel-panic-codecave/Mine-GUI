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

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.withertech.mine_gui.GuiDescription;
import com.withertech.mine_gui.SyncedGuiDescription;
import com.withertech.mine_gui.impl.VisualLogger;
import com.withertech.mine_gui.impl.client.MineGuiScreenImpl;
import com.withertech.mine_gui.impl.client.MouseInputHandler;
import com.withertech.mine_gui.impl.client.NarrationHelper;
import com.withertech.mine_gui.widget.WPanel;
import com.withertech.mine_gui.widget.WWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

/**
 * A screen for a {@link SyncedGuiDescription}.
 *
 * @param <T> the description type
 */
public class MineGuiInventoryScreen<T extends SyncedGuiDescription> extends AbstractContainerScreen<T> implements MineGuiScreenImpl
{
	private final MouseInputHandler<MineGuiInventoryScreen<T>> mouseInputHandler = new MouseInputHandler<>(this);
	protected SyncedGuiDescription description;
	@Nullable
	protected WWidget lastResponder = null;

	/**
	 * Constructs a new screen without a title.
	 *
	 * @param description the GUI description
	 * @param inventory   the player inventory
	 * @since 5.2.0
	 */
	public MineGuiInventoryScreen(T description, Inventory inventory)
	{
		this(description, inventory, new TextComponent(""));
	}

	/**
	 * Constructs a new screen.
	 *
	 * @param description the GUI description
	 * @param inventory   the player inventory
	 * @param title       the screen title
	 * @since 5.2.0
	 */
	public MineGuiInventoryScreen(T description, Inventory inventory, Component title)
	{
		super(description, inventory, title);
		this.description = description;
		width = 18 * 9;
		height = 18 * 9;
		this.imageWidth = 18 * 9;
		this.imageHeight = 18 * 9;
		description.getRootPanel().validate(description);
	}

	/**
	 * Constructs a new screen without a title.
	 *
	 * @param description the GUI description
	 * @param player      the player
	 */
	public MineGuiInventoryScreen(T description, Player player)
	{
		this(description, player.getInventory());
	}

	/**
	 * Constructs a new screen.
	 *
	 * @param description the GUI description
	 * @param player      the player
	 * @param title       the screen title
	 */
	public MineGuiInventoryScreen(T description, Player player, Component title)
	{
		this(description, player.getInventory(), title);
	}

	/*
	 * RENDERING NOTES:
	 *
	 * * "width" and "height" are the width and height of the overall screen
	 * * "backgroundWidth" and "backgroundHeight" are the width and height of the panel to render
	 * * ~~"left" and "top" are *actually* self-explanatory~~
	 *   * "left" and "top" are now (1.15) "x" and "y". A bit less self-explanatory, I guess.
	 * * coordinates start at 0,0 at the topleft of the screen.
	 */

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

	@ApiStatus.Internal
	@Override
	public GuiDescription getDescription()
	{
		return description;
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
	 * Clears the heavyweight peers of this screen's GUI description.
	 */
	private void clearPeers()
	{
		description.slots.clear();
	}

	/**
	 * Repositions the root panel.
	 *
	 * @param screenWidth  the width of the screen
	 * @param screenHeight the height of the screen
	 */
	protected void reposition(int screenWidth, int screenHeight)
	{
		WPanel basePanel = description.getRootPanel();
		if (basePanel != null)
		{
			clearPeers();
			basePanel.validate(description);

			imageWidth = basePanel.getWidth();
			imageHeight = basePanel.getHeight();

			//DEBUG
			if (imageWidth < 16) imageWidth = 300;
			if (imageHeight < 16) imageHeight = 300;
		}

		titleLabelX = description.getTitlePos().x();
		titleLabelY = description.getTitlePos().y();

		if (!description.isFullscreen())
		{
			leftPos = (screenWidth / 2) - (imageWidth / 2);
			topPos = (screenHeight / 2) - (imageHeight / 2);
		} else
		{
			leftPos = 0;
			topPos = 0;

			if (basePanel != null)
			{
				basePanel.setSize(screenWidth, screenHeight);
			}
		}
	}

	@Override
	public boolean isPauseScreen()
	{
		//...yeah, we're going to go ahead and override that.
		return false;
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
		if (ch == GLFW.GLFW_KEY_ESCAPE || ch == GLFW.GLFW_KEY_TAB)
		{
			// special hardcoded keys, these will never be delivered to widgets
			return super.keyPressed(ch, keyCode, modifiers);
		} else
		{
			if (description.getFocus() == null)
			{
				return super.keyPressed(ch, keyCode, modifiers);
			} else
			{
				description.getFocus().onKeyPressed(ch, keyCode, modifiers);
				return true;
			}
		}
	}

	@Override
	public boolean keyReleased(int ch, int keyCode, int modifiers)
	{
		if (description.getFocus() == null) return false;
		description.getFocus().onKeyReleased(ch, keyCode, modifiers);
		return true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);
		int containerX = (int) mouseX - leftPos;
		int containerY = (int) mouseY - topPos;
		if (containerX < 0 || containerY < 0 || containerX >= width || containerY >= height) return result;
		mouseInputHandler.onMouseDown(containerX, containerY, mouseButton);

		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
	{ //Testing shows that STATE IS ACTUALLY BUTTON
		super.mouseReleased(mouseX, mouseY, mouseButton);
		int containerX = (int) mouseX - leftPos;
		int containerY = (int) mouseY - topPos;
		mouseInputHandler.onMouseUp(containerX, containerY, mouseButton);

		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
	{
		super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);

		int containerX = (int) mouseX - leftPos;
		int containerY = (int) mouseY - topPos;
		mouseInputHandler.onMouseDrag(containerX, containerY, mouseButton, deltaX, deltaY);

		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		if (description.getRootPanel() == null) return super.mouseScrolled(mouseX, mouseY, amount);

		int containerX = (int) mouseX - leftPos;
		int containerY = (int) mouseY - topPos;
		mouseInputHandler.onMouseScroll(containerX, containerY, amount);

		return true;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{
		if (description.getRootPanel() == null) return;

		int containerX = (int) mouseX - leftPos;
		int containerY = (int) mouseY - topPos;
		mouseInputHandler.onMouseMove(containerX, containerY);
	}

	@Override
	protected void renderBg(PoseStack matrices, float partialTicks, int mouseX, int mouseY) {} //This is just an AbstractContainerScreen thing; most Screens don't work this way.

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
				root.paint(matrices, leftPos, topPos, mouseX - leftPos, mouseY - topPos);
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				Scissors.checkStackIsEmpty();
			}
		}
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float partialTicks)
	{
		paint(matrices, mouseX, mouseY);

		super.render(matrices, mouseX, mouseY, partialTicks);
		Lighting.setupForFlatItems(); //Needed because super.render leaves dirty state

		if (description != null)
		{
			WPanel root = description.getRootPanel();
			if (root != null)
			{
				WWidget hitChild = root.hit(mouseX - leftPos, mouseY - topPos);
				if (hitChild != null)
					hitChild.renderTooltip(matrices, leftPos, topPos, mouseX - leftPos, mouseY - topPos);
			}
		}

		renderTooltip(matrices, mouseX, mouseY); //Draws the itemstack tooltips
		VisualLogger.render(matrices);
	}

	@Override
	protected void renderLabels(PoseStack matrices, int mouseX, int mouseY)
	{
		if (description != null && description.isTitleVisible())
		{
			int width = description.getRootPanel().getWidth();
			ScreenDrawing.drawString(matrices, getTitle().getVisualOrderText(), description.getTitleAlignment(), titleLabelX, titleLabelY, width - titleLabelX, description.getTitleColor());
		}

		// Don't draw the player inventory label as it's drawn by the widget itself
	}

	@Override
	protected void containerTick()
	{
		super.containerTick();
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
		if (!(o instanceof MineGuiInventoryScreen<?> that)) return false;
		return Objects.equals(mouseInputHandler, that.mouseInputHandler) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getLastResponder(), that.getLastResponder());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(mouseInputHandler, getDescription(), getLastResponder());
	}

	@Override
	public String toString()
	{
		return "MineGuiInventoryScreen{" +
				"mouseInputHandler=" + mouseInputHandler +
				", description=" + description +
				", lastResponder=" + lastResponder +
				", imageWidth=" + imageWidth +
				", imageHeight=" + imageHeight +
				", titleLabelX=" + titleLabelX +
				", titleLabelY=" + titleLabelY +
				", inventoryLabelX=" + inventoryLabelX +
				", inventoryLabelY=" + inventoryLabelY +
				", menu=" + menu +
				", playerInventoryTitle=" + playerInventoryTitle +
				", hoveredSlot=" + hoveredSlot +
				", leftPos=" + leftPos +
				", topPos=" + topPos +
				", quickCraftSlots=" + quickCraftSlots +
				", isQuickCrafting=" + isQuickCrafting +
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
