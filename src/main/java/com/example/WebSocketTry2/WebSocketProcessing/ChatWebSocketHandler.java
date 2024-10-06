package com.example.WebSocketTry2.WebSocketProcessing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private List<WebSocketSession> sessions = new ArrayList<>();
    private List<String> messages = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        for (String message : messages) {
            session.sendMessage(new TextMessage(message));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        String formattedMessage = chatMessage.getName() + ": " + chatMessage.getMessage();
        messages.add(formattedMessage);
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(new TextMessage(formattedMessage));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    private static class ChatMessage {
        private String name;
        private String message;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
