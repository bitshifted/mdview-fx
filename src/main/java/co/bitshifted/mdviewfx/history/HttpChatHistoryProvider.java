/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.history;

import co.bitshifted.mdviewfx.error.ChatHistoryException;
import co.bitshifted.mdviewfx.model.ChatMessage;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
/** Chat history provider that loads messages from an HTTP endpoint. */
public class HttpChatHistoryProvider extends ChatHistoryProvider {

  /** HTTP client for making requests. */
  private final HttpClient httpClient;

  /** Supplier for creating HTTP requests to the history endpoint. */
  private final Supplier<HttpRequest> requestSupplier;

  /** Parser function for converting HTTP response to a list of messages. */
  private final Function<HttpResponse<String>, List<ChatMessage>> responseParser;

  /**
   * Create an HTTP chat history provider.
   *
   * @param requestSupplier supplier that creates the HTTP request for fetching history
   * @param responseParser function to parse HTTP response body into chat messages
   */
  public HttpChatHistoryProvider(
      Supplier<HttpRequest> requestSupplier,
      Function<HttpResponse<String>, List<ChatMessage>> responseParser) {
    this.httpClient = HttpClient.newHttpClient();
    this.requestSupplier = requestSupplier;
    this.responseParser = responseParser;
  }

  @Override
  protected List<ChatMessage> doLoadMessages(ChatMessage lastMessage) {
    var request = requestSupplier.get();
    try {
      log.debug("Sending HTTP request to {}", request.uri());
      var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      log.debug("Received HTTP response with status {}", response.statusCode());
      var messages = responseParser.apply(response);
      log.debug("Parsed {} messages from response", messages.size());
      return messages;
    } catch (Throwable e) {
      log.error("Failed to load chat history from HTTP endpoint", e);
      throw new ChatHistoryException("Failed to load chat history from HTTP endpoint", e);
    }
  }
}
