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

package com.withertech.mine_gui.networking;

import com.withertech.mine_gui.SyncedGuiDescription;
import com.withertech.mine_gui.impl.ScreenNetworkingImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * {@code ScreenNetworking} handles screen-related network messages sent between the server and the client.
 *
 * <h2>Registering a message receiver</h2>
 * {@linkplain MessageReceiver Message receivers} can be registered by calling {@link #receive(ResourceLocation, MessageReceiver)}
 * on a {@code ScreenNetworking} for the receiving side. The {@code message} ID is a unique ID that matches between
 * the sender and the receiver.
 *
 * <p>Message receivers should be registered in the constructor of a {@link SyncedGuiDescription}.
 *
 * <h2>Sending messages</h2>
 * Messages can be sent by calling {@link #send(ResourceLocation, Consumer)} on a {@code ScreenNetworking}
 * for the sending side. The {@code message} ID should match up with a receiver registered on the <i>opposite</i>
 * side.
 *
 * <h2>Example</h2>
 * <pre>
 * {@code
 * private static final ResourceLocation MESSAGE_ID = new ResourceLocation("my_mod", "some_message");
 *
 * // Receiver
 * ScreenNetworking.of(this, NetworkSide.SERVER).receive(MESSAGE_ID, buf -> {
 * 	   // Example data: a lucky number as an int
 *     System.out.println("Your lucky number is " + buf.readInt() + "!");
 * });
 *
 * // Sending
 *
 * // We're sending from a button. The packet data is our lucky number, 123.
 * WButton button = ...;
 * button.setOnClick(() -> {
 *     ScreenNetworking.of(this, NetworkSide.CLIENT).send(MESSAGE_ID, buf -> buf.writeInt(123));
 * });
 * }
 * </pre>
 *
 * @since 3.3.0
 */
public interface ScreenNetworking
{
	/**
	 * Gets a networking handler for the GUI description that is active on the specified side.
	 *
	 * @param description the GUI description
	 * @param networkSide the network side
	 * @return the network handler
	 * @throws NullPointerException if either parameter is null
	 */
	static ScreenNetworking of(SyncedGuiDescription description, NetworkSide networkSide)
	{
		return ScreenNetworkingImpl.of(description, networkSide);
	}

	/**
	 * Registers a message receiver for the message.
	 *
	 * @param message  the screen message ID
	 * @param receiver the message receiver
	 * @throws IllegalStateException if the message has already been registered
	 * @throws NullPointerException  if either parameter is null
	 */
	void receive(ResourceLocation message, MessageReceiver receiver);

	/**
	 * Sends a screen message to the other side of the connection.
	 *
	 * @param message the screen message ID
	 * @param writer  a writer that writes the message contents to a packet buffer;
	 *                should not read the buffer
	 * @throws NullPointerException if either parameter is null
	 */
	void send(ResourceLocation message, Consumer<FriendlyByteBuf> writer);

	/**
	 * A handler for received screen messages.
	 */
	@FunctionalInterface
	interface MessageReceiver
	{
		/**
		 * Handles a received screen message.
		 *
		 * <p>This method should only read from the buffer, not write to it.
		 *
		 * @param buf the message packet buffer
		 */
		void onMessage(FriendlyByteBuf buf);
	}
}
