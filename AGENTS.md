# Project Overview for AI Agents

This document provides context for AI agents working with the `mdview-fx` project.

## Project Description

`mdview-fx` is a JavaFX-based library for viewing Markdown content. It offers three main functionalities:

1.  **Static Markdown Viewing**: Renders static Markdown files into an HTML view.
2.  **Chat View**: Provides a simple chat interface that renders Markdown messages.
3.  **AI Chat View**: An AI-powered chat interface with real-time message streaming.

The library uses `Flexmark-java` for Markdown processing and displays the content in a JavaFX `WebView`.

## Project Structure

The project follows a standard Maven layout:

*   `pom.xml`: The Maven project file, containing all dependencies and build configurations.
*   `src/main/java`: Contains the main source code.
    *   `co.bitshifted.mdviewfx`: The main package.
        *   `MarkdownBasicView.java`: For static Markdown viewing.
        *   `MarkdownChatView.java`: For the simple chat interface.
        *   `MarkdownAiChatView.java`: For the AI-powered chat.
        *   `history/`: Contains chat history provider implementations (`InMemoryChatHistoryProvider`, `HttpChatHistoryProvider`).
*   `src/main/resources`: Contains resources like CSS and JavaScript for the views.
*   `sample-app/`: A sample application demonstrating the library's usage.
*   `README.md`: The main documentation file.

## Key Technologies

*   **Java 25+**
*   **JavaFX**
*   **Maven**
*   **Flexmark-java** (for Markdown processing)

## How to Work with the Code

1.  **Understand the Core Classes**: `MarkdownBasicView`, `MarkdownChatView`, and `MarkdownAiChatView` are the entry points for the different view types.
2.  **Chat History**: The `ChatHistoryProvider` is a key abstraction for handling chat messages. You can create custom implementations to integrate with different backends.
3.  **UI Customization**: The views can be customized (e.g., light/dark mode) through constructor parameters.
4.  **Sample App**: Refer to the `sample-app` for practical examples of how to use the library.

This information should help you understand the project's architecture and purpose. If you need more details, refer to the `README.md` or the source code.
