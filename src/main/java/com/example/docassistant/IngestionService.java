package com.example.docassistant;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Handles Steps 3 and 4 of the pipeline:
 *   - Loading the source file from disk
 *   - Splitting it into overlapping/non-overlapping text chunks
 *   - Embedding each chunk into a vector
 *   - Storing those vectors in an in-memory database
 *
 * Isolating this logic keeps Main.java focused on wiring, and makes it easy
 * to swap InMemoryEmbeddingStore for a persistent store (e.g. Chroma,
 * Pinecone, PGVector) later without touching the rest of the app.
 */
public class IngestionService {

    private final EmbeddingModel embeddingModel;

    public IngestionService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public EmbeddingStore<TextSegment> ingest(Path filePath) {
        Document document = loadDocument(filePath);

        // Split into ~300-character chunks. Small chunks improve retrieval
        // precision; a 0-character overlap keeps things simple for this demo
        // (increase overlap if answers start missing context that spans chunk
        // boundaries).
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);

        System.out.printf("Loaded '%s' -> split into %d chunks%n", filePath.getFileName(), segments.size());

        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);

        System.out.println("Embedded and stored all chunks in the in-memory vector store.");

        return embeddingStore;
    }

    private Document loadDocument(Path filePath) {
        String fileName = filePath.getFileName().toString().toLowerCase();
        try {
            if (fileName.endsWith(".pdf")) {
                return new ApachePdfBoxDocumentParser().parse(Files.newInputStream(filePath));
            } else {
                // Falls back to plain-text parsing for .txt, .md, etc.
                return new TextDocumentParser().parse(Files.newInputStream(filePath));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read document: " + filePath, e);
        }
    }
}
