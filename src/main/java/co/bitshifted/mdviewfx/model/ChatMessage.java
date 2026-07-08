/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.model;

import io.jstach.jstache.JStache;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@JStache(path = "templates/chat-msg-template.mustache")
/**
 * Represents a chat message with associated metadata (author, type, timestamp) and both markdown
 * and rendered HTML content.
 */
public class ChatMessage {

  /** Unique identifier for the message. */
  @Getter private final String id;

  /** The type/category of this message (user, member, AI). */
  @Getter private final MessageType type;

  /** The time the message was created or received. */
  @Getter private final ZonedDateTime timestamp;

  /** The user who authored the message. */
  @Getter private ChatUser user;

  /** Raw markdown content of the message. */
  @Getter @Setter private String markdown;

  /** HTML produced from rendering the markdown. */
  @Getter @Setter private String renderedHtml;

  /**
   * Create a message with an auto-generated timestamp and an anonymous user.
   *
   * @param id unique id for the message
   * @param type message type
   * @param markdown raw markdown content
   */
  public ChatMessage(String id, MessageType type, String markdown) {
    this(id, type, markdown, new ChatUser("Anonymous", "img/user.svg"), ZonedDateTime.now());
  }

  /**
   * Create a message with all properties supplied.
   *
   * @param id unique id for the message
   * @param type message type
   * @param markdown raw markdown content
   * @param user author of the message
   * @param timestamp message creation timestamp
   */
  public ChatMessage(
      String id, MessageType type, String markdown, ChatUser user, ZonedDateTime timestamp) {
    this.id = id;
    this.type = type;
    this.markdown = markdown;
    this.user = user;
    this.timestamp = timestamp;
  }
}
