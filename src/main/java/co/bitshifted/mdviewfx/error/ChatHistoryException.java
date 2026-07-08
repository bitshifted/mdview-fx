/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.error;

/** Thrown when an error occurs while loading chat history. */
public class ChatHistoryException extends RuntimeException {

  public ChatHistoryException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
