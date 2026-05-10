# 🧠 KnowledgeOS: Learning Log & Technical Architecture

This document tracks the technical decisions, modern patterns, and core concepts implemented in the **KnowledgeOS** project.

---

## 🏗️ 1. Architecture: Modular Monolith
We chose a **Modular Monolith** over Microservices for the initial build.
- **Why?** Fast iteration, zero network latency between modules, and reduced operational complexity.
- **Future Proofing**: Each module (Content, Search, Revision) has its own repository and service boundaries, making future extraction into microservices trivial if needed.

## ☕ 2. Backend: Spring Boot 4.0.6 & Java 21
By using the latest **Spring Boot 4.0** (May 2026 release), we leverage:
- **Project Loom (Virtual Threads)**: Massively scalable I/O without the complexity of Reactive programming.
- **Java 21 Syntax**: Enhanced switch expressions, pattern matching, and record patterns.
- **Spring Framework 7.0**: Optimized for high-performance cloud-native execution.

## 🤖 3. AI Orchestration: Spring AI & ChatClient
Instead of writing low-level HTTP calls to Gemini, we use **Spring AI 2.0.0**.

### `ChatClient` (Fluent API)
The `ChatClient` is the modern way to talk to LLMs. It uses a fluent builder pattern:
```java
String response = chatClient.prompt()
    .user(contentToAnalyze)
    .call()
    .content();
```
- **Abstraction**: We can swap **Gemini 1.5 Flash** for **OpenAI GPT-5** or **Claude 4** just by changing a dependency in `pom.xml`.

## 💾 4. Database: PostgreSQL + pgvector
We aren't just using a database; we're using a **Vector Database**.
- **pgvector**: This PostgreSQL extension allows us to store "Embeddings" (mathematical representations of text).
- **Semantic Search**: In Phase 2, this will allow users to search for "Kafka retries" and find content about "Message reliability" even if the word "retry" isn't present.

## ⚡ 5. Async Processing: `@EnableAsync`
AI calls are slow (1-5 seconds). We should **never** block the user.
- **The Flow**: 
    1. User submits URL.
    2. Backend saves record as `PENDING` and returns `200 OK` immediately.
    3. `@Async` method triggers in the background to scrape text and call the AI.
    4. Frontend polls the status until it becomes `COMPLETED`.

## ⚛️ 6. Frontend: Next.js 16 & Tailwind CSS 4
The frontend uses the absolute bleeding edge of web tech:
- **Next.js 16 App Router**: Optimized server-side rendering and simplified routing.
- **Tailwind CSS 4**: A faster, CSS-first engine.
- **Glassmorphism Design**: Using `backdrop-filter: blur()` and transparent backgrounds to create a "Premium OS" feel.

---

## 🛠️ 7. Key Patterns Implemented

### Environment Management (`.env`)
We use `dotenv-java` to load secrets. This keeps sensitive API keys out of the codebase while making local development easy.

### HTML Extraction (Jsoup)
To convert a "passive URL" into "active knowledge," we use **Jsoup** to crawl the web page, strip away the ads/navigation, and extract only the meaningful text for the AI to analyze.

---

## 🚀 How to Run
1. **Docker**: `docker-compose up -d` (Infrastructure)
2. **Environment**: Add `GEMINI_API_KEY` to `.env`.
3. **Scripts**: 
   - `.\run.bat start`: Launches the entire ecosystem.
   - `.\run.bat stop`: Kills all processes.

---

## 📅 Next Learning Goals
- **Phase 2**: Vector Embeddings & Similarity Search.
- **Phase 3**: Knowledge Graph Visualization.
- **Phase 4**: Spaced Repetition Algorithms (Anki-style).
