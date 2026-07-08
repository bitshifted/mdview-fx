/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx;

import co.bitshifted.mdviewfx.content.ExportTools;
import co.bitshifted.mdviewfx.model.ViewMode;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.events.EventTarget;

/**
 * Basic markdown view that renders markdown content with side-by-side or single-pane layout.
 * Displays markdown rendered as HTML with copy-to-clipboard functionality, and export as
 * PDF/DOCX/markdown.
 */
@Slf4j
public class MarkdownBasicView extends MarkdownView {

  /** Property storing the current raw markdown content. */
  private StringProperty currentMarkdown = new SimpleStringProperty(null);

  /** Property storing the current rendered HTML content. */
  private StringProperty currentHtml = new SimpleStringProperty(null);

  /** Create a basic markdown view with default light theme and export toolbar. */
  public MarkdownBasicView() {
    this(ViewMode.LIGHT, false);
  }

  /**
   * Create a basic markdown view with light theme and configurable export toolbar.
   *
   * @param noExportToolbar if true, hides the export toolbar; if false, shows it
   */
  public MarkdownBasicView(boolean noExportToolbar) {
    this(ViewMode.LIGHT, noExportToolbar);
  }

  /**
   * Create a basic markdown view with specified theme and export toolbar configuration.
   *
   * @param viewMode the theme/view mode (light or dark)
   * @param noExportToolbar if true, hides the export toolbar; if false, shows it
   */
  public MarkdownBasicView(ViewMode viewMode, boolean noExportToolbar) {
    super("/basic-view.html", viewMode, noExportToolbar ? null : new ExportTools());
    currentMarkdown.addListener((obs, oldText, newText) -> internalLoadMarkdown());
  }

  @Override
  protected void init() {
    var doc = engine.getDocument();
    var mdCopy = doc.getElementById("md-copy");
    ((EventTarget) mdCopy)
        .addEventListener(
            "click", evt -> contentTools.copyToClipboard(currentMarkdown.get()), false);
    var htmlCopy = doc.getElementById("html-copy");
    ((EventTarget) htmlCopy)
        .addEventListener("click", evt -> contentTools.copyToClipboard(currentHtml.get()), false);
    internalLoadMarkdown();
  }

  /**
   * Load and display markdown content. The markdown will be parsed and rendered to HTML.
   *
   * @param text the raw markdown content to display
   */
  public void loadMarkdown(String text) {
    currentMarkdown.set(text);
  }

  private void internalLoadMarkdown() {
    if (!ready || currentMarkdown.get() == null) {
      log.warn("No markdown set or not ready");
      return;
    }
    var document = parser.parse(currentMarkdown.get());
    currentHtml.set(renderer.render(document));
    if (exportTools != null) {
      exportTools.updateContent(currentMarkdown.get(), currentHtml.get());
    }
    var updateScript =
        String.format(
            "updateContent('%s');",
            Base64.getEncoder().encodeToString(currentHtml.get().getBytes(StandardCharsets.UTF_8)));
    log.debug("executing script: {}", updateScript);
    var result = engine.executeScript(updateScript);
    log.debug("Script execution result: {}", result);
  }
}
