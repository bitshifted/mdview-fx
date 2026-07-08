/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.content;

import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class ExportToolbar extends HBox {

  private final Button pdfButton;
  private final Tooltip pdfTooltip;
  private final Button docxButton;
  private final Tooltip docxTooltip;
  private final Button mdButton;
  private final Tooltip mdTooltip;
  private final ProgressIndicator indicator;
  private final ExportTools exportTools;
  private final Label saveLabel;

  private final ResourceBundle resources;

  public ExportToolbar(ExportTools exportTools) {
    this(exportTools, ResourceBundle.getBundle("messages"), "/css/fx-style.css");
  }

  public ExportToolbar(ExportTools exportTools, ResourceBundle resources, String stylesheetUrl) {
    super(10);
    this.getStylesheets().add(getClass().getResource(stylesheetUrl).toExternalForm());
    this.getStyleClass().add("export-toolbar");
    this.resources = resources;
    this.exportTools = exportTools;
    pdfButton = new Button("PDF");
    pdfTooltip = new Tooltip(resources.getString("export.pdf.tooltip"));
    pdfButton.setTooltip(pdfTooltip);
    pdfButton.getStyleClass().addAll("toolbar-button", "icon-pdf");
    pdfButton.setOnAction(evt -> exportPdf());
    docxButton = new Button("DOCX");
    docxTooltip = new Tooltip(resources.getString("export.docx.tooltip"));
    docxButton.setTooltip(docxTooltip);
    docxButton.getStyleClass().addAll("toolbar-button", "icon-docx");
    docxButton.setOnAction(evt -> exportDocx());
    mdButton = new Button("MD");
    mdTooltip = new Tooltip(resources.getString("export.md.tooltip"));
    mdButton.getStyleClass().addAll("toolbar-button", "icon-md");
    mdButton.setTooltip(mdTooltip);
    mdButton.setOnAction(evt -> exportMarkdown());
    Stream.of(pdfTooltip, docxTooltip, mdTooltip)
        .forEach(t -> t.getStyleClass().add("toolbar-button-tooltip"));
    getChildren().add(new Label((resources.getString("export.label"))));
    indicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
    indicator.getStyleClass().add("toolbar-export-indicator");
    indicator.setVisible(false);
    saveLabel = new Label(resources.getString("export.progress.label"));
    saveLabel.setVisible(false);
    getChildren().addAll(pdfButton, docxButton, mdButton, indicator, saveLabel);
  }

  private void exportPdf() {
    var fileChooser = new FileChooser();
    fileChooser.setTitle(resources.getString("export.pdf.tooltip"));
    fileChooser
        .getExtensionFilters()
        .addAll(
            new FileChooser.ExtensionFilter(resources.getString("export.pdf.filter"), "*.pdf"),
            new FileChooser.ExtensionFilter(resources.getString("export.all.filter"), "*.*"));
    fileChooser.setInitialFileName("conversation.pdf");
    var target = fileChooser.showSaveDialog(getParent().getScene().getWindow());
    if (target != null) {
      updateSaveUi(true);
      CompletableFuture.runAsync(() -> exportTools.exportToPdf(target.toPath()))
          .thenRun(() -> updateSaveUi(false));
    }
  }

  private void exportDocx() {
    var fileChooser = new FileChooser();
    fileChooser.setTitle(resources.getString("export.docx.tooltip"));
    fileChooser
        .getExtensionFilters()
        .addAll(
            new FileChooser.ExtensionFilter(resources.getString("export.docx.filter"), "*.docx"),
            new FileChooser.ExtensionFilter(resources.getString("export.all.filter"), "*.*"));
    fileChooser.setInitialFileName("conversation.docx");
    var target = fileChooser.showSaveDialog(getParent().getScene().getWindow());
    if (target != null) {
      updateSaveUi(true);
      CompletableFuture.runAsync(() -> exportTools.exportToDocx(target.toPath()))
          .thenRun(() -> updateSaveUi(false));
    }
  }

  private void exportMarkdown() {
    var fileChooser = new FileChooser();
    fileChooser.setTitle(resources.getString("export.md.tooltip"));
    fileChooser
        .getExtensionFilters()
        .addAll(
            new FileChooser.ExtensionFilter(resources.getString("export.md.filter"), "*.md"),
            new FileChooser.ExtensionFilter(resources.getString("export.all.filter"), "*.*"));
    fileChooser.setInitialFileName("conversation.md");
    var target = fileChooser.showSaveDialog(getParent().getScene().getWindow());
    if (target != null) {
      updateSaveUi(true);
      CompletableFuture.runAsync(() -> exportTools.exportToMarkdown(target.toPath()))
          .thenRun(() -> updateSaveUi(false));
    }
  }

  private void updateSaveUi(boolean status) {
    indicator.setVisible(status);
    saveLabel.setVisible(status);
    pdfButton.setDisable(status);
    docxButton.setDisable(status);
    mdButton.setDisable(status);
  }
}
