/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.content;

import co.bitshifted.mdviewfx.history.ChatHistoryProvider;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

@Slf4j
public class ExportTools {

  private final String htmlDoc =
"""
    <html>
    <body>
    {{content}}
    </body>
    </html>
""";

  private final Optional<ChatHistoryProvider> chatHistoryProvider;
  private final StaticContent staticContent;

  public ExportTools() {
    this(null);
  }

  public ExportTools(ChatHistoryProvider chatHistoryProvider) {
    this.chatHistoryProvider = Optional.ofNullable(chatHistoryProvider);
    this.staticContent = new StaticContent();
  }

  public void updateContent(String markdown, String html) {
    this.staticContent.setMarkdown(markdown);
    this.staticContent.setHtml(html);
  }

  public void exportToPdf(Path outputPath) {
    log.debug("Exporting content to .pdf format");
    var htmlContent = collectHtmlContent();
    log.debug("PDF export: output HTML:\n" + htmlContent);
    try (var os = new FileOutputStream(outputPath.toFile())) {
      PdfRendererBuilder builder = new PdfRendererBuilder();
      builder.withHtmlContent(htmlContent, null);
      builder.toStream(os);
      builder.run();
    } catch (Exception e) {
      log.error("Error exporting to PDF", e);
      throw new RuntimeException(e);
    }
  }

  public void exportToDocx(Path outputPath) {
    log.debug("Exporting content to .docx format");
    var htmlContent = collectHtmlContent();
    // export to .docx
    try {
      var wordMLPackage = WordprocessingMLPackage.createPackage();
      var importer = new XHTMLImporterImpl(wordMLPackage);
      wordMLPackage.getMainDocumentPart().getContent().addAll(importer.convert(htmlContent, null));
      wordMLPackage.save(outputPath.toFile());
    } catch (Exception ex) {
      log.error("Error exporting to DOCX", ex);
      throw new RuntimeException(ex);
    }
  }

  public void exportToMarkdown(Path outputPath) {
    log.debug("Exporting content to .md format, path={}", outputPath.toFile().getAbsolutePath());
    var sb = new StringBuilder();
    if (chatHistoryProvider.isPresent()) {
      chatHistoryProvider.get().getMessages().stream()
          .forEach(
              msg -> {
                if (msg.getUser() != null && msg.getUser().name() != null) {
                  sb.append("### ").append(msg.getUser().name()).append("\n\n");
                }
                sb.append(msg.getMarkdown()).append("\n");
                // ad horizontal rule as message delimiter
                sb.append("\n").append("---").append("\n");
              });
    } else {
      sb.append(staticContent.getMarkdown());
    }

    try {
      Files.writeString(outputPath, sb.toString());
    } catch (Exception ex) {
      log.error("Failed to export to .md format");
      throw new RuntimeException(ex);
    }
  }

  private String collectHtmlContent() {
    log.debug("Collecting content...");
    var sb = new StringBuilder();
    if (chatHistoryProvider.isPresent()) {
      chatHistoryProvider.get().getMessages().stream()
          .forEach(
              msg -> {
                System.out.println("part: " + msg.getRenderedHtml());
                // set message user nfo
                if (msg.getUser() != null && msg.getUser().name() != null) {
                  sb.append("<h3>").append(msg.getUser().name()).append("</h3>").append("\n");
                }
                sb.append(msg.getRenderedHtml());
                // append message delimiter
                sb.append("\n").append("<hr/>").append("\n");
              });
    } else {
      sb.append(staticContent.getHtml());
    }

    return htmlDoc.replace("{{content}}", sb.toString());
  }
}
