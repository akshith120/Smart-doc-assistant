# Smart Document Assistant

Smart Document Assistant is a Java command-line app that answers questions about a PDF or text file using Retrieval-Augmented Generation (RAG) with [LangChain4j](https://github.com/langchain4j/langchain4j).

## Features

- Load PDF and plain-text documents.
- Split documents into searchable chunks.
- Retrieve the most relevant context for each question.
- Keep short chat history for follow-up questions.
- Package as a single runnable JAR with Maven.

## Project Structure

```text
smart-doc-assistant/
├── pom.xml
├── README.md
├── sample-docs/
│   └── sample-contract.txt
└── src/main/java/com/example/docassistant/
    ├── Main.java
    ├── IngestionService.java
    └── DocumentAssistant.java
```

## Requirements

- Java 17 or newer
- Maven 3.9+ recommended
- An OpenAI API key for chat generation

## Configuration

Set your API key before running the app.

PowerShell:

```powershell
$env:OPENAI_API_KEY="sk-your-key-here"
```

macOS / Linux:

```bash
export OPENAI_API_KEY="sk-your-key-here"
```

## Build

Create the runnable JAR:

```bash
mvn clean package
```

The build produces `target/smart-doc-assistant.jar`.

## Run

Run the app with the included sample document:

```bash
java -jar target/smart-doc-assistant.jar
```

Or point it at your own file:

```bash
java -jar target/smart-doc-assistant.jar path/to/your-document.pdf
```

The app supports `.pdf`, `.txt`, and other plain-text files.

## Docker

Build the container image:

```bash
docker build -t smart-doc-assistant .
```

Run it with your API key:

```bash
docker run --rm -it -e OPENAI_API_KEY="sk-your-key-here" smart-doc-assistant
```

To process your own local document, mount it into the container and pass the container path as the argument:

```powershell
docker run --rm -it `
    -e OPENAI_API_KEY="sk-your-key-here" `
    -v "${PWD}\sample-docs:/docs" `
    smart-doc-assistant /docs/sample-contract.txt
```

On macOS / Linux, use the same image name with your platform's shell and volume syntax.

## Example Usage

```text
You: Are there any auto-renewal clauses in this document?
Assistant: ...

You: What happens if the client terminates early?
Assistant: ...

You: exit
```

## How It Works

1. `Main.java` reads the document path and sets up the models.
2. `IngestionService.java` loads the file, splits it into chunks, and stores embeddings in memory.
3. `DocumentAssistant.java` defines the chat interface used by LangChain4j.
4. `AiServices` combines retrieval, memory, and the chat model into one conversational API.

## GitHub Ready

This repository is ready for GitHub because it now includes:

- a project README with setup, usage, and architecture notes
- a `.gitignore` tuned for Maven and common IDE/build artifacts

## Push to GitHub

If this folder is already a Git repository:

```bash
git status
git add README.md .gitignore .dockerignore Dockerfile pom.xml src sample-docs
git commit -m "Prepare project for GitHub"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-new-repo>.git
git push -u origin main
```

If this folder is not yet a Git repository:

```bash
git init
git add README.md .gitignore .dockerignore Dockerfile pom.xml src sample-docs
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-new-repo>.git
git push -u origin main
```

## Next Improvements

- Add a license file.
- Replace the in-memory vector store with persistent storage.
- Add a web API so the assistant can be used from a browser or frontend.
