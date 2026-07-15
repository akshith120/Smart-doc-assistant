package com.example.docassistant;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * Smart Document Assistant
 * -------------------------
 * A command-line RAG (Retrieval-Augmented Generation) application.
 * Point it at a PDF or text file and ask questions about its contents.
 *
 * Usage:
 *   export OPENAI_API_KEY=sk-...
 *   mvn clean package
 *   java -jar target/smart-doc-assistant.jar path/to/document.pdf
 *
 * If no file path is given, it defaults to sample-docs/sample-contract.txt
 * so you can try the app immediately without your own file.
 */
public class Main {

    public static void main(String[] args) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("ERROR: Please set the OPENAI_API_KEY environment variable.");
            System.err.println("  export OPENAI_API_KEY=sk-your-key-here");
            System.exit(1);
        }

        Path documentPath = args.length > 0
                ? Path.of(args[0])
                : Path.of("sample-docs/sample-contract.txt");

        if (!documentPath.toFile().exists()) {
            System.err.println("ERROR: File not found: " + documentPath.toAbsolutePath());
            System.exit(1);
        }

        // ---- Step 2: Initialize models ----
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .temperature(0.2)
                .build();

        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

        // ---- Steps 3 & 4: Load, chunk, embed, store ----
        IngestionService ingestionService = new IngestionService(embeddingModel);
        EmbeddingStore<TextSegment> embeddingStore = ingestionService.ingest(documentPath);

        // ---- Step 5: Build retriever + memory + AiServices ----
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.6)
                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        DocumentAssistant assistant = AiServices.builder(DocumentAssistant.class)
                .chatModel(chatModel)
                .contentRetriever(contentRetriever)
                .chatMemory(chatMemory)
                .build();

        // ---- Step 6: Interactive console loop ----
        System.out.println("\nSmart Document Assistant ready. Ask questions about: " + documentPath);
        System.out.println("Type 'exit' or 'quit' to stop.\n");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("You: ");
            String question = scanner.nextLine();
            if (question == null || question.isBlank()) {
                continue;
            }
            if (question.equalsIgnoreCase("exit") || question.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            String answer = assistant.chat(question);
            System.out.println("Assistant: " + answer + "\n");
        }
        scanner.close();
    }
}
