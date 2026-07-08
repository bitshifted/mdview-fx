/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx;

import co.bitshifted.mdviewfx.content.ExportTools;
import co.bitshifted.mdviewfx.history.ChatHistoryProvider;
import co.bitshifted.mdviewfx.model.*;
import io.jstach.jstachio.JStachio;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

/**
 * Markdown chat view specialized for AI assistant conversations. Extends {@link MarkdownChatView}
 * with streaming capabilities to display AI responses in real-time as they are generated.
 */
@Slf4j
public class MarkdownAiChatView extends MarkdownChatView {
  /** Flag indicating if a message streaming session is currently active. */
  private boolean streamStarted = false;

  /**
   * Create an AI chat view with default export toolbar and light theme.
   *
   * @param chatHistoryProvider the history provider for managing chat messages
   */
  public MarkdownAiChatView(ChatHistoryProvider chatHistoryProvider) {
    this(chatHistoryProvider, false, ViewMode.LIGHT);
  }

  /**
   * Create an AI chat view with light theme and configurable export toolbar.
   *
   * @param chatHistoryProvider the history provider for managing chat messages
   * @param viewMode the theme/view mode (light or dark)
   */
  public MarkdownAiChatView(ChatHistoryProvider chatHistoryProvider, ViewMode viewMode) {
    this(chatHistoryProvider, false, viewMode);
  }

  /**
   * Create an AI chat view with configurable export toolbar and theme.
   *
   * @param chatHistoryProvider the history provider for managing chat messages
   * @param noExportToolbar if true, hides the export toolbar; if false, shows it
   * @param viewMode the theme/view mode (light or dark)
   */
  public MarkdownAiChatView(
      ChatHistoryProvider chatHistoryProvider, boolean noExportToolbar, ViewMode viewMode) {
    super(
        chatHistoryProvider,
        "/ai-chat-view.html",
        noExportToolbar ? null : new ExportTools(chatHistoryProvider),
        viewMode);
  }

  /**
   * Prepare the UI for streaming a new AI response message. Must be called before streaming content
   * and before {@link #streamContent(String)}.
   *
   * @param messageId unique identifier for the message being streamed
   * @throws IllegalStateException if streaming is already in progress
   */
  public void prepareStreaming(String messageId) {
    if (streamStarted) {
      throw new IllegalStateException("Streaming has already started!");
    }
    var container = new AiMsgContainer(messageId, "img/ai.svg", "Assistant");
    var html = JStachio.render(container);
    Platform.runLater(
        () -> {
          var jsCmd =
              String.format(
                  "startStreaming('%s');",
                  Base64.getEncoder().encodeToString(html.getBytes(StandardCharsets.UTF_8)));
          log.debug("starting streaming");
          engine.executeScript(jsCmd);
          streamStarted = true;
        });
  }

  /**
   * Stream content chunks for the current AI message being generated. The content should be
   * markdown that will be parsed and rendered incrementally.
   *
   * @param content markdown content chunk to stream
   * @throws IllegalStateException if {@link #prepareStreaming(String)} was not called first
   */
  public void streamContent(String content) {
    if (!streamStarted) {
      throw new IllegalStateException("Streaming has not started. Call prepareStreaming() first");
    }
    log.debug("Streaming content: {}", content);
    var html = renderer.render(parser.parse(content));
    log.debug("Streaming html: {}", html);
    Platform.runLater(
        () -> {
          var jsCmd =
              String.format(
                  "stream('%s');",
                  Base64.getEncoder().encodeToString(html.getBytes(StandardCharsets.UTF_8)));
          engine.executeScript(jsCmd);
        });
  }

  /**
   * Complete the streaming of an AI message and store it in history. Call this after all {@link
   * #streamContent(String)} calls are done.
   *
   * @param completeMessage the full final markdown content of the streamed message
   * @param messageId the unique identifier for this message
   */
  public void streamComplete(String completeMessage, String messageId) {
    Platform.runLater(
        () -> {
          var jsCmd = String.format("streamComplete();");
          engine.executeScript(jsCmd);
          streamStarted = false;
        });
    var html = renderer.render(parser.parse(completeMessage));
    var message =
        new ChatMessage(
            messageId,
            MessageType.AI,
            completeMessage,
            new ChatUser("Assistant", "img/ai.svg"),
            ZonedDateTime.now());
    message.setRenderedHtml(html);
    chatHistoryProvider.completeStream(message);
  }
}
