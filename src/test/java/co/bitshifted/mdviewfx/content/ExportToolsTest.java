/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.content;

import co.bitshifted.mdviewfx.history.ChatHistoryProvider;
import co.bitshifted.mdviewfx.history.InMemoryChatHistoryProvider;
import co.bitshifted.mdviewfx.model.ChatMessage;
import co.bitshifted.mdviewfx.model.MessageType;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExportToolsTest {

  private final ChatHistoryProvider provider = new InMemoryChatHistoryProvider();

  @BeforeEach
  void setup() {
    var userMsg =
        new ChatMessage(UUID.randomUUID().toString(), MessageType.USER, "Hello, **world**!");
    userMsg.setRenderedHtml("<p>Hello, <strong>world</strong>!</p>");
    var aiMsg =
        new ChatMessage(UUID.randomUUID().toString(), MessageType.AI, "This is a *test* message.");
    aiMsg.setRenderedHtml("<p>This is a <em>test</em> message.</p>");
    var messages = List.of(userMsg, aiMsg);
    provider.appendMessages(messages);
  }

  @Test
  void shouldRenderPdfCorrectly() throws Exception {
    var tools = new ExportTools(provider);
    var outPath = Files.createTempFile("render", ".pdf");
    tools.exportToPdf(outPath);
  }

  @Test
  void shouldRenderDocxCorrectly() throws Exception {
    var tools = new ExportTools(provider);
    var outPath = Files.createTempFile("render", ".docx");
    tools.exportToDocx(outPath);
  }

  @Test
  void shouldRenderMarkdownCorrectly() throws Exception {
    var tools = new ExportTools(provider);
    var outPath = Files.createTempFile("render", ".md");
    tools.exportToMarkdown(outPath);
  }
}
