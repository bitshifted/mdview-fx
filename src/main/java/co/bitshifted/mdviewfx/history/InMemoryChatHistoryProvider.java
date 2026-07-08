/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.history;

import co.bitshifted.mdviewfx.model.ChatMessage;
import java.util.List;
import java.util.function.Function;

/** Chat history provider that keeps all history in memory using a customizable message loader. */
public class InMemoryChatHistoryProvider extends ChatHistoryProvider {

  /** Function to load messages from memory based on the last message. */
  private final Function<ChatMessage, List<ChatMessage>> messageLoader;

  /** Create an in-memory chat history provider with an empty history. */
  public InMemoryChatHistoryProvider() {
    this(msg -> List.of());
  }

  /**
   * Create an in-memory chat history provider with a custom message loader.
   *
   * @param messageLoader function that returns messages to load based on the last message
   */
  public InMemoryChatHistoryProvider(Function<ChatMessage, List<ChatMessage>> messageLoader) {
    this.messageLoader = messageLoader;
  }

  @Override
  protected List<ChatMessage> doLoadMessages(ChatMessage lastMessage) {
    return messageLoader.apply(lastMessage);
  }
}
