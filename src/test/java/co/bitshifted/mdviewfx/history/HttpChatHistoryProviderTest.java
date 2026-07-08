/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.history;

import co.bitshifted.mdviewfx.model.ChatMessage;
import co.bitshifted.mdviewfx.model.MessageType;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@WireMockTest
public class HttpChatHistoryProviderTest {

  private Function<HttpResponse<String>, List<ChatMessage>> responseParser =
      response -> List.of(new ChatMessage("1", MessageType.PARTICIPANT, "Hello, world!"));

  @Test
  void shouldLoadChatHistoryFromHttpEndpoint(WireMockRuntimeInfo wireMockRuntimeInfo)
      throws InterruptedException {
    var wmPort = wireMockRuntimeInfo.getHttpPort();
    var url = String.format("http://localhost:%d/chat-history", wmPort);

    Supplier<HttpRequest> requestSupplier =
        () -> HttpRequest.newBuilder().uri(java.net.URI.create(url)).GET().build();

    WireMock.stubFor(
        WireMock.get("/chat-history")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "[{\"id\":\"1\",\"type\":\"TEXT\",\"markdown\":\"Hello, world!\"}]")));

    var provider = new HttpChatHistoryProvider(requestSupplier, responseParser);
    provider.loadMessageHistory();

    // Wait for async operation to complete with polling
    long timeout = System.currentTimeMillis() + 5000; // 5 second timeout
    while (System.currentTimeMillis() < timeout) {
      if (provider.findMessageById("1").isPresent()) {
        break;
      }
      Thread.sleep(100);
    }

    Assertions.assertTrue(provider.findMessageById("1").isPresent());
  }
}
