/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.model;

/** UI theme/view mode used by the application (for example light or dark themes). */
public enum ViewMode {
  /** Light (bright) theme. */
  LIGHT("light"),

  /** Dark theme. */
  DARK("dark");

  private final String name;

  ViewMode(String name) {
    this.name = name;
  }

  /**
   * Returns the external name used in UI or persisted settings.
   *
   * @return mode name (for example "light" or "dark")
   */
  public String getName() {
    return name;
  }
}
