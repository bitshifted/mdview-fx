/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

module co.bitshifted.mdviewfx {
  requires java.net.http;
  requires javafx.base;
  requires javafx.graphics;
  requires javafx.web;
  requires javafx.controls;
  requires javafx.fxml;
  requires flexmark.all;
  requires static lombok;
  requires io.jstach.jstachio;
  requires com.google.gson;
  requires org.slf4j;
  requires openhtmltopdf.pdfbox;
  requires jdk.jsobject;
  requires flexmark.util.data;
  requires flexmark;
  requires flexmark.ext.tables;
  requires flexmark.ext.gfm.strikethrough;
  requires docx4j_ImportXHTML;
  requires org.docx4j.core;
  requires error.prone.annotations;

  //    requires org.docx4j.JAXB.ReferenceImpl;
  //    requires org.docx4j.ImportXHTML;

  exports co.bitshifted.mdviewfx;
  exports co.bitshifted.mdviewfx.model;
  exports co.bitshifted.mdviewfx.history;
  exports co.bitshifted.mdviewfx.error;
}
