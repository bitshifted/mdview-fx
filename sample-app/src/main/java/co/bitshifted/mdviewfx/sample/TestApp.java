/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.sample;

import co.bitshifted.mdviewfx.MarkdownAiChatView;
import co.bitshifted.mdviewfx.MarkdownBasicView;
import co.bitshifted.mdviewfx.MarkdownChatView;
import co.bitshifted.mdviewfx.history.InMemoryChatHistoryProvider;
import co.bitshifted.mdviewfx.model.ChatMessage;
import co.bitshifted.mdviewfx.model.ChatUser;
import co.bitshifted.mdviewfx.model.MessageType;
import co.bitshifted.mdviewfx.model.ViewMode;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class TestApp extends Application {

  private String longMarkdown =
      """
            # Start response
            This is example markdown response for streaming. It contains a lot of markdown symbols, such as
            *italic*, **bold**, ~~strikethrough~~, `code` and so on.

            ## List example
            This section contains list examples:

            Unordered list:
            * item 1
            * item 2
            * item 3

            Ordered list:
            1. item 1
            1. item 2
            1. item 3

            ## Code block
            This is sample code block:
            ```
            public class MyClass {
                private String data;
            }
            ```
            """;

  private String markdown =
      """
           # Markdown sample

           Markdown: *italic*, **bold**, `code`
           ```
           public class MyClass {
               private String data;
           }
           ```
           Unordered list:
           * item 1
           * item 2

           Orderd list:
           1. item 1
           1. item2

           This is **bold** text and ~~strikethrough~~ text.

          | Header 1 | Header 2 |
          | -------- | -------- |
          | Cell 1   | Cell 2   |

          ## Quotes

          > This is quote
          > one more line
            """;

  @Override
  public void start(javafx.stage.Stage primaryStage) throws Exception {
    var tabs = new TabPane();
    var basicTab = new Tab("Basic View");

    var view = new MarkdownBasicView(true);
    view.loadMarkdown(markdown);
    basicTab.setContent(view.getView());

    var chatTab = new Tab("Chat View");
    var historyProvider = new InMemoryChatHistoryProvider();
    var bp = new BorderPane();
    var chatView = new MarkdownChatView(historyProvider);
    bp.setCenter(chatView.getView());
    var hbox = new HBox(10);
    var text = new TextArea();
    text.addEventFilter(
        javafx.scene.input.KeyEvent.KEY_PRESSED,
        evt -> {
          if (evt.getCode() == javafx.scene.input.KeyCode.ENTER) {
            if (evt.isShiftDown()) {
              System.out.println("Shift+Enter detected: Adding new line");
              text.insertText(text.getCaretPosition(), "\n");
              evt.consume();
            } else {
              System.out.println("Enter pressed");
              historyProvider.appendMessages(
                  List.of(
                      new ChatMessage(
                          UUID.randomUUID().toString(),
                          MessageType.USER,
                          text.getText(),
                          new ChatUser("User", "https://i.pravatar.cc/300"),
                          ZonedDateTime.now())));
              text.clear();
              evt.consume();
            }
          }
        });
    hbox.getChildren().addAll(text);
    bp.setBottom(hbox);
    chatTab.setContent(bp);
    // AI tab
    var aiHistory = new InMemoryChatHistoryProvider();
    var aiTab = new Tab("AI Chat");
    var aiPane = new BorderPane();
    var aiView = new MarkdownAiChatView(aiHistory, ViewMode.DARK);
    aiPane.setCenter(aiView.getView());
    var aiHbox = new HBox(10);
    var aiText = new TextArea();
    aiText.addEventFilter(
        javafx.scene.input.KeyEvent.KEY_PRESSED,
        evt -> {
          if (evt.getCode() == javafx.scene.input.KeyCode.ENTER) {
            if (evt.isShiftDown()) {
              System.out.println("Shift+Enter detected: Adding new line");
              aiText.insertText(text.getCaretPosition(), "\n");
              evt.consume();
            } else {
              System.out.println("Enter pressed");
              aiHistory.appendMessages(
                  List.of(
                      new ChatMessage(
                          UUID.randomUUID().toString(), MessageType.USER, aiText.getText())));
              aiText.clear();
              evt.consume();
            }
          }
        });
    var tokens = longMarkdown.toCharArray();
    var index = new AtomicInteger(4);
    AtomicBoolean finished = new AtomicBoolean(false);
    String responseMsgId = UUID.randomUUID().toString();
    var keyframe =
        new KeyFrame(
            Duration.millis(100),
            event -> {
              if (index.get() >= tokens.length && !finished.get()) {
                aiView.streamComplete(new String(tokens), responseMsgId);
                finished.set(true);
                return;
              }
              var substr = new String(tokens, 0, index.get());
              aiView.streamContent(substr);
              index.setPlain(index.get() + 4);
            });
    var stream = new Button("Stream");
    stream.setOnAction(
        e -> {
          aiView.prepareStreaming(responseMsgId);
          var timeline = new Timeline(keyframe);
          timeline.setCycleCount(115);
          timeline.play();
        });
    aiHbox.getChildren().addAll(aiText, stream);
    aiPane.setBottom(aiHbox);
    aiTab.setContent(aiPane);

    tabs.getTabs().addAll(aiTab, chatTab, basicTab);
    var scene = new javafx.scene.Scene(tabs, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
    historyProvider.loadMessageHistory();
    aiHistory.loadMessageHistory();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
