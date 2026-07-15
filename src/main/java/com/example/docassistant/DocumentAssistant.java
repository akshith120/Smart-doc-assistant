package com.example.docassistant;

/**
 * This interface is never implemented by hand. LangChain4j's AiServices
 * generates a dynamic proxy behind the scenes that:
 *   1. Takes the incoming userMessage
 *   2. Uses it to query the ContentRetriever (vector search) for relevant chunks
 *   3. Stuffs those chunks + chat history + the question into a prompt
 *   4. Sends that prompt to the ChatModel
 *   5. Returns the plain-text answer
 *
 * Keeping this as a plain interface is the core "magic" of LangChain4j:
 * it lets you interact with a fully wired RAG pipeline as if it were a
 * simple Java method call.
 */
public interface DocumentAssistant {

    String chat(String userMessage);
}
