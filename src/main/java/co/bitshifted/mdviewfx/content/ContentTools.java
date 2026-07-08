/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.content;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ContentTools {

  public void copyToClipboard(String content) {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    var clipboardContent = new ClipboardContent();
    clipboardContent.putString(content);
    clipboard.setContent(clipboardContent);
  }
}
