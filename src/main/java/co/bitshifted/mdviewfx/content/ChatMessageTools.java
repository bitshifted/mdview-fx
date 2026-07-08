/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.content;

import co.bitshifted.mdviewfx.history.ChatHistoryProvider;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatMessageTools {

  private final ChatHistoryProvider chatHistoryProvider;
  private final Clipboard clipboard;

  public ChatMessageTools(ChatHistoryProvider provider) {
    this.chatHistoryProvider = provider;
    this.clipboard = Clipboard.getSystemClipboard();
  }

  public void copyMarkdown(String messageId) {
    log.debug("copying markdown for message {}", messageId);
    var opt = chatHistoryProvider.findMessageById(messageId);
    if (opt.isPresent()) {
      var content = new ClipboardContent();
      content.put(DataFormat.PLAIN_TEXT, opt.get().getMarkdown());
      clipboard.setContent(content);
    } else {
      log.error("Message not found for id: {}", messageId);
    }
  }

  public void copyHtml(String htmlContent) {
    log.debug("copying HTML: {}", htmlContent);
    var content = new ClipboardContent();
    content.put(DataFormat.HTML, htmlContent);
    content.put(DataFormat.PLAIN_TEXT, htmlContent);
    clipboard.setContent(content);
  }
}
