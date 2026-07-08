/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx;

import co.bitshifted.mdviewfx.content.ChatMessageTools;
import co.bitshifted.mdviewfx.content.ExportTools;
import co.bitshifted.mdviewfx.history.ChatHistoryProvider;
import co.bitshifted.mdviewfx.model.ChatMessage;
import co.bitshifted.mdviewfx.model.ViewMode;
import com.google.gson.Gson;
import io.jstach.jstachio.JStachio;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;

/**
 * Markdown view for displaying chat messages with history support. Renders messages from a {@link
 * ChatHistoryProvider} in a scrollable container, supporting both message prepending (history
 * loading) and appending (new messages).
 */
@Slf4j
public class MarkdownChatView extends MarkdownView {
  /** JSON serialization utility. */
  private final Gson gson = new Gson();

  /** Provider for loading and managing chat history. */
  protected final ChatHistoryProvider chatHistoryProvider;

  /** Tools for processing and rendering chat messages. */
  protected final ChatMessageTools chatMessageTools;

  /** Flag indicating if the chat view is ready for interactions. */
  private boolean ready = false;

  /**
   * Create a chat view with default export toolbar and light theme.
   *
   * @param chatHistoryProvider the history provider for managing chat messages
   */
  public MarkdownChatView(ChatHistoryProvider chatHistoryProvider) {
    this(chatHistoryProvider, false, ViewMode.LIGHT);
  }

  /**
   * Create a chat view with configurable export toolbar and theme.
   *
   * @param chatHistoryProvider the history provider for managing chat messages
   * @param noExportToolbar if true, hides the export toolbar; if false, shows it
   * @param viewMode the theme/view mode (light or dark)
   */
  @SuppressWarnings("removal")
  public MarkdownChatView(
      ChatHistoryProvider chatHistoryProvider, boolean noExportToolbar, ViewMode viewMode) {
    this(
        chatHistoryProvider,
        "/chat-view.html",
        noExportToolbar ? null : new ExportTools(chatHistoryProvider),
        viewMode);
  }

  /**
   * Protected constructor for subclasses to specify a custom HTML page resource.
   *
   * @param chatHistoryProvider the history provider for managing chat messages
   * @param pageResource the classpath resource path to the HTML page
   * @param exportTools tools for exporting content, or null if export is not needed
   * @param viewMode the theme/view mode (light or dark)
   */
  protected MarkdownChatView(
      ChatHistoryProvider chatHistoryProvider,
      String pageResource,
      ExportTools exportTools,
      ViewMode viewMode) {
    super(pageResource, exportTools, viewMode);
    this.chatHistoryProvider = chatHistoryProvider;
    chatMessageTools = new ChatMessageTools(this.chatHistoryProvider);
    this.chatHistoryProvider.setAppendCallback(msgs -> appendCallback(msgs));
    this.chatHistoryProvider.setPrependCallback(msgs -> prependCallback(msgs));
  }

  @Override
  protected void init() {
    var window = (JSObject) engine.executeScript("window");
    window.setMember("chatHistoryProvider", chatHistoryProvider);
    window.setMember("chatMsgTools", chatMessageTools);
    engine.executeScript("document.addEventListener('scroll', scrollHandler);");
  }

  private void appendCallback(List<ChatMessage> msgs) {
    var data = new ArrayList<String>();
    msgs.stream()
        .forEach(
            h -> {
              var html = renderer.render(parser.parse(h.getMarkdown()));
              h.setRenderedHtml(html);
              data.add(JStachio.render(h));
            });
    log.debug("append buffer: {}", data);
    var json = gson.toJson(data);
    var jsCommand =
        String.format(
            "appendMessage('%s');",
            Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8)));
    engine.executeScript(jsCommand);
  }

  private void prependCallback(List<ChatMessage> msgs) {
    if (msgs.isEmpty()) {
      return;
    }
    var data = new ArrayList<String>();
    msgs.stream()
        .forEach(
            h -> {
              var html = renderer.render(parser.parse(h.getMarkdown()));
              h.setRenderedHtml(html);
              data.add(JStachio.render(h));
            });
    log.debug("prepend buffer: {}", data);
    var json = gson.toJson(data);
    Platform.runLater(
        () -> {
          var jsCommand =
              String.format(
                  "prependMessages('%s');",
                  Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8)));
          engine.executeScript(jsCommand);
        });
  }
}
