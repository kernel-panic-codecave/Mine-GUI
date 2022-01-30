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

import com.withertech.mine_gui.MineGui;
import com.withertech.mine_gui.SyncedGuiDescription;
import com.withertech.mine_gui.networking.NetworkSide;
import com.withertech.mine_gui.networking.ScreenNetworking;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class ScreenNetworkingImpl implements ScreenNetworking
{
	// Packet structure:
	//   syncId: int
	//   message: identifier
	//   rest: buf

	public static final ResourceLocation SCREEN_MESSAGE_S2C = new ResourceLocation(MineGui.MOD_ID, "screen_message_s2c");
	public static final ResourceLocation SCREEN_MESSAGE_C2S = new ResourceLocation(MineGui.MOD_ID, "screen_message_c2s");

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<SyncedGuiDescription, ScreenNetworkingImpl> instanceCache = new WeakHashMap<>();

	private final Map<ResourceLocation, ScreenNetworking.MessageReceiver> messages = new HashMap<>();
	private final NetworkSide side;
	private final SyncedGuiDescription description;

	private ScreenNetworkingImpl(SyncedGuiDescription description, NetworkSide side)
	{
		this.description = description;
		this.side = side;
	}

	public static void init()
	{
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, SCREEN_MESSAGE_C2S, ScreenNetworkingImpl::handle);
	}

	public static void handle(FriendlyByteBuf buf, NetworkManager.PacketContext context)
	{
		AbstractContainerMenu screenHandler = context.getPlayer().containerMenu;

		// Packet data
		int syncId = buf.readVarInt();
		ResourceLocation messageId = buf.readResourceLocation();

		if (!(screenHandler instanceof SyncedGuiDescription))
		{
			LOGGER.error("Received message packet for screen handler {} which is not a SyncedGuiDescription", screenHandler);
			return;
		} else if (syncId != screenHandler.containerId)
		{
			LOGGER.error("Received message for sync ID {}, current sync ID: {}", syncId, screenHandler.containerId);
			return;
		}

		ScreenNetworkingImpl networking = instanceCache.get(screenHandler);

		if (networking != null)
		{
			MessageReceiver receiver = networking.messages.get(messageId);

			if (receiver != null)
			{
				buf.retain();
				context.queue(() ->
				{
					try
					{
						receiver.onMessage(buf);
					} catch (Exception e)
					{
						LOGGER.error("Error handling screen message {} for {} on side {}", messageId, screenHandler, networking.side, e);
					} finally
					{
						buf.release();
					}
				});
			} else
			{
				LOGGER.warn("Message {} not registered for {} on side {}", messageId, screenHandler, networking.side);
			}
		} else
		{
			LOGGER.warn("GUI description {} does not use networking", screenHandler);
		}
	}

	public static ScreenNetworking of(SyncedGuiDescription description, NetworkSide networkSide)
	{
		Objects.requireNonNull(description, "description");
		Objects.requireNonNull(networkSide, "networkSide");

		if (description.getNetworkSide() == networkSide)
		{
			return instanceCache.computeIfAbsent(description, it -> new ScreenNetworkingImpl(description, networkSide));
		} else
		{
			return DummyNetworking.INSTANCE;
		}
	}

	public void receive(ResourceLocation message, MessageReceiver receiver)
	{
		Objects.requireNonNull(message, "message");
		Objects.requireNonNull(receiver, "receiver");

		if (!messages.containsKey(message))
		{
			messages.put(message, receiver);
		} else
		{
			throw new IllegalStateException("Message " + message + " on side " + side + " already registered");
		}
	}

	@Override
	public void send(ResourceLocation message, Consumer<FriendlyByteBuf> writer)
	{
		Objects.requireNonNull(message, "message");
		Objects.requireNonNull(writer, "writer");

		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeVarInt(description.containerId);
		buf.writeResourceLocation(message);
		writer.accept(buf);
		description.getPacketSender().accept(side == NetworkSide.SERVER ? SCREEN_MESSAGE_S2C : SCREEN_MESSAGE_C2S, buf);
	}

	private static final class DummyNetworking extends ScreenNetworkingImpl
	{
		static final DummyNetworking INSTANCE = new DummyNetworking();

		private DummyNetworking()
		{
			super(null, null);
		}

		@Override
		public void receive(ResourceLocation message, MessageReceiver receiver)
		{
			// NO-OP
		}

		@Override
		public void send(ResourceLocation message, Consumer<FriendlyByteBuf> writer)
		{
			// NO-OP
		}
	}
}
