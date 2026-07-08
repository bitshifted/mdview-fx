/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.history;

import co.bitshifted.mdviewfx.model.ChatMessage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

/**
 * Provider for chat history, ie. the list of messages that have been previously sent within the
 * chat. Specific implementations must implement {@link
 * ChatHistoryProvider#doLoadMessages(ChatMessage)} method to actually load the list o fmessages.
 *
 * <p>Messages loaded from history are prepended to the beginning of message queue.
 */
@Slf4j
public abstract class ChatHistoryProvider {

  /** Queue of messages loaded from history. */
  protected final Deque<ChatMessage> messages = new ArrayDeque<>();

  /** Flag indicating whether a history load operation is currently in progress. */
  protected boolean isLoading = false;

  /** Callback invoked when messages are prepended to history. */
  protected Consumer<List<ChatMessage>> prependCallback;

  /** Callback invoked when messages are appended to history. */
  protected Consumer<List<ChatMessage>> appendCallback;

  private final Consumer<List<ChatMessage>> prependConsumer =
      l -> {
        l.stream().forEach(m -> messages.offerFirst(m));
        if (prependCallback != null) {
          prependCallback.accept(l);
        }
      };

  /**
   * Load messages from the history source. This abstract method must be implemented by subclasses
   * to define how messages are retrieved.
   *
   * @param lastMessage the most recent message currently in the queue, or null if queue is empty
   * @return list of messages loaded from history
   */
  protected abstract List<ChatMessage> doLoadMessages(ChatMessage lastMessage);

  /**
   * Loads messages from chat history. Upon successful load, calls {@link #prependCallback} to
   * process messages as needed. If callback is not set, messages stay in memory without processing.
   */
  public void loadMessageHistory() {
    log.debug("Loading message history...");
    if (isLoading) {
      return;
    }
    isLoading = true;
    CompletableFuture.supplyAsync(() -> doLoadMessages(messages.peekFirst()))
        .thenAccept(prependConsumer)
        .exceptionally(
            e -> {
              log.error("Exception loading message history", e);
              isLoading = false;
              return null;
            })
        .thenRun(
            () -> {
              isLoading = false;
              log.debug("Message history loaded");
            });
  }

  /**
   * Find a message by its unique identifier.
   *
   * @param id the message id to search for
   * @return an Optional containing the message if found, empty otherwise
   */
  public Optional<ChatMessage> findMessageById(String id) {
    return messages.stream().filter(m -> m.getId().equals(id)).findFirst();
  }

  /**
   * Appends messages to the end of chat history. It is expected that messages in the list are
   * ordered in time-ascending, ie. most recent messages is last.
   *
   * @param messages list of messages to append
   */
  public void appendMessages(List<ChatMessage> messages) {
    messages.stream().forEach(m -> this.messages.offerLast(m));
    if (appendCallback != null) {
      appendCallback.accept(messages);
    }
  }

  /**
   * Returns a snapshot of all messages currently in the history.
   *
   * @return list of all messages
   */
  public List<ChatMessage> getMessages() {
    return messages.stream().toList();
  }

  /**
   * Adds a single message to the end of the chat history. Typically called when a new message is
   * sent or received during the current session.
   *
   * @param message the message to add
   */
  public void completeStream(ChatMessage message) {
    this.messages.offerLast(message);
  }

  /**
   * Set the callback to be invoked when messages are appended.
   *
   * @param appendCallback consumer to process appended messages
   */
  public void setAppendCallback(Consumer<List<ChatMessage>> appendCallback) {
    this.appendCallback = appendCallback;
  }

  /**
   * Set the callback to be invoked when messages are prepended (loaded from history).
   *
   * @param prependCallback consumer to process prepended messages
   */
  public void setPrependCallback(Consumer<List<ChatMessage>> prependCallback) {
    this.prependCallback = prependCallback;
  }
}
