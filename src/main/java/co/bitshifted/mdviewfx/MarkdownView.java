/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx;

import static javafx.concurrent.Worker.*;

import co.bitshifted.mdviewfx.content.ContentTools;
import co.bitshifted.mdviewfx.content.ExportToolbar;
import co.bitshifted.mdviewfx.content.ExportTools;
import co.bitshifted.mdviewfx.model.ViewMode;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base class for markdown rendering views using JavaFX WebView. This class sets up the
 * markdown parser, HTML renderer, and web view components. Subclasses can extend this to provide
 * specific markdown display implementations (e.g., basic view, chat view).
 */
@Slf4j
public abstract class MarkdownView {

  /** Parser configuration and options for flexmark markdown processing. */
  protected final MutableDataSet options = new MutableDataSet();

  /** Tools for manipulating and exporting content. */
  protected final ContentTools contentTools = new ContentTools();

  /** Markdown parser configured with extensions. */
  protected final Parser parser;

  /** HTML renderer for converting parsed markdown to HTML. */
  protected final HtmlRenderer renderer;

  /** JavaFX WebView component that displays the rendered HTML. */
  protected final WebView webView;

  /** Web engine associated with the web view. */
  protected final WebEngine engine;

  /** Current view mode (light or dark theme). */
  protected final ViewMode viewMode;

  /** Root layout pane containing the web view and optional toolbars. */
  protected final BorderPane borderPane;

  /** Tools for exporting rendered content. */
  protected final ExportTools exportTools;

  /** Flag indicating if the web view has finished loading and is ready for scripting. */
  protected boolean ready = false;

  /**
   * Initialize the markdown view with an HTML page resource and optional export tools.
   *
   * @param pageResource the classpath resource path to the HTML page (e.g., "/basic-view.html")
   * @param viewMode the theme/view mode (light or dark)
   * @param exportTools tools for exporting content, or null if export is not needed
   */
  protected MarkdownView(String pageResource, ViewMode viewMode, ExportTools exportTools) {
    this.viewMode = viewMode;
    options.set(
        Parser.EXTENSIONS, List.of(StrikethroughExtension.create(), TablesExtension.create()));
    parser = Parser.builder(options).build();
    renderer = HtmlRenderer.builder(options).build();
    webView = new WebView();
    borderPane = new BorderPane();
    borderPane.setCenter(webView);
    this.exportTools = exportTools;
    if (exportTools != null) {
      borderPane.setBottom(new ExportToolbar(exportTools));
    }
    webView.setContextMenuEnabled(false);
    engine = webView.getEngine();
    var url = getClass().getResource(pageResource);
    if (url != null) {
      engine.load(url.toExternalForm());
      engine
          .getLoadWorker()
          .stateProperty()
          .addListener(
              (obs, oldState, newState) -> {
                log.debug("Current view state: {}", newState);
                if (newState == State.SUCCEEDED) {
                  ready = true;
                  engine.executeScript("setTheme('" + viewMode.getName() + "');");
                  init();
                }
              });

    } else {
      throw new IllegalArgumentException("Resource not found: " + pageResource);
    }
  }

  /**
   * Convenience constructor with parameter order (resource, exportTools, viewMode).
   *
   * @param pageResource the classpath resource path to the HTML page
   * @param exportTools tools for exporting content, or null if export is not needed
   * @param viewMode the theme/view mode (light or dark)
   */
  protected MarkdownView(String pageResource, ExportTools exportTools, ViewMode viewMode) {
    this(pageResource, viewMode, exportTools);
  }

  /**
   * Initialize subclass-specific behavior after the web view has loaded. This method is called
   * automatically when the HTML page loading completes.
   */
  protected abstract void init();

  /**
   * Get the root node for this markdown view.
   *
   * @return the BorderPane containing the web view and optional toolbars
   */
  public Node getView() {
    return borderPane;
  }

  /**
   * Sets dark or light view mode for markdown view.
   *
   * @param viewMode view mode to set
   */
  public void setViewMode(ViewMode viewMode) {
    Platform.runLater(() -> engine.executeScript("setTheme('" + viewMode.getName() + "');"));
  }
}
