# Smart Document Assistant

Smart Document Assistant is a Java command-line app that answers questions about a PDF or text file using Retrieval-Augmented Generation (RAG) with LangChain4j.

## Run Instructions

### Requirements

- Java 17 or newer
- Maven 3.9+ recommended
- An `OPENAI_API_KEY` environment variable

### 1. Set your API key

PowerShell:

```powershell
$env:OPENAI_API_KEY="sk-your-key-here"
```

macOS / Linux:

```bash
export OPENAI_API_KEY="sk-your-key-here"
```

### 2. Build the application

```bash
mvn clean package
```

### 3. Run the app

Run it with the included sample document:

```bash
java -jar target/smart-doc-assistant.jar
```

Or pass your own file path:

```bash
java -jar target/smart-doc-assistant.jar path/to/your-document.pdf
```

## Project Structure

```text
smart-doc-assistant/
├── pom.xml
├── Dockerfile
├── sample-docs/
│   └── sample-contract.txt
└── src/main/java/com/example/docassistant/
    ├── Main.java
    ├── IngestionService.java
    └── DocumentAssistant.java
```