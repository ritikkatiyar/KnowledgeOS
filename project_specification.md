# AI Knowledge OS For Technical Learners

## PROJECT NAME
**Internal Name**: KnowledgeOS  
**Possible public names later**: RecallOS, StackMind, DevBrain, Synapse, ByteMemory

---

## CORE VISION
Build an AI-powered Knowledge Operating System for ambitious technical learners.

The system helps users:
- Capture scattered learning content
- Summarize it intelligently
- Organize it semantically
- Retrieve it later
- Revisit it through spaced repetition
- Convert passive consumption into long-term retained knowledge

---

## CORE USER PROBLEM
Users consume massive amounts of educational content (YouTube, LinkedIn, blogs, reels, PDFs, GitHub repos, Udemy courses). They save these resources but:
- Forget them
- Never revisit them
- Cannot retrieve them later
- Fail to connect concepts
- Remain stuck in learning chaos

**The product solves**: “knowledge fragmentation and forgetting.”

---

## PRIMARY TARGET USERS
### Initial Niche
Ambitious software engineers:
- Backend developers
- FAANG aspirants
- System design learners
- AI learners
- DSA practitioners

---

## MVP GOAL
The MVP must solve this loop:
**Save Content → AI Summarizes → Content Becomes Searchable → Knowledge Resurfaces Later**

If this loop works well, the product succeeds.

---

## PRODUCT PRINCIPLES
1.  **Retrieval > Storage**: The goal is retrieving and reusing knowledge later, not just saving it.
2.  **Compounding Learning**: Improve retention, understanding, implementation ability, and interview recall.
3.  **Minimal Friction**: Capturing knowledge must be extremely easy.
4.  **AI Assists, Not Dominates**: Users should edit summaries, add notes, and control tags. AI should augment workflows.

---

## ARCHITECTURE DECISION
**Use Modular Monolith** (NOT microservices initially).
- **Reason**: Solo development, fast iteration, lower operational complexity, easier debugging.
- **Design**: Modular boundaries for future extraction.

---

## TECH STACK
### Backend
- Java 21
- Spring Boot 3.x
- Spring AI
- Spring Data JPA
- PostgreSQL + pgvector
- Quartz Scheduler (later)
- Docker

### Frontend
- Next.js
- TypeScript
- TailwindCSS

### AI Stack
- OpenAI / Gemini APIs
- Embeddings API
- Vector similarity search

---

## INFRASTRUCTURE
- Docker Compose initially
- Local development first
- Cloud later

---

## MODULE STRUCTURE
`backend/`
- `content-module`
- `ingestion-module`
- `ai-processing-module`
- `search-module`
- `revision-module`
- `notification-module`
- `user-module`
- `common-module`

---

## PHASE 0 — PRODUCT DESIGN (2–3 days)
**Goal**: Clarify product behavior before coding.
- **Supported Content Types**: YouTube URLs, blogs, LinkedIn posts, PDFs, screenshots, manual notes.
- **AI Outputs**: Title, summary, tags, actionable insights, interview questions, implementation ideas.
- **User Flow**: Save → Extract → AI Process → Store → Retrieve → Resurface.

---

## PHASE 1 — CORE MVP FOUNDATION (Week 1)
**Goal**: Build “Save and summarize” ONLY.

### Backend Tasks
- Setup Spring Boot (Web, JPA, Postgres, AI, Lombok, Validation).
- **Core Entities**: Content (id, sourceType, sourceUrl, rawText, cleanedText, summary, title, status, createdAt).
- **APIs**:
    - `POST /api/content`: Save content (URL or raw text).
    - `GET /api/content/{id}`: Retrieve content.
    - `GET /api/content`: List all.
- **AI Processing**: Extract text → Send to LLM → Generate summary, title, tags → Store.

### Frontend Tasks
- **Pages**: Dashboard, Add Content, View Content.
- **UI**: Input URL, view summaries, content cards.

---

## PHASE 2 — SEMANTIC SEARCH (Week 2)
**Goal**: Enable “Find knowledge semantically.”

### Backend Tasks
- **Embeddings**: Generate for summaries/cleaned content; store in `pgvector`.
- **Search Module**: Semantic similarity, ranking, hybrid search later.
- **API**: `POST /api/search` (query → relevant content + score).

### Frontend Tasks
- Search Page with semantic search bar and ranked results.

---

## PHASE 3 — KNOWLEDGE ORGANIZATION (Week 3)
**Goal**: Move from bookmarks → structured knowledge.
- **AI Topic Classification**: (Kafka, Redis, Spring Boot, LLD, HLD, DSA, GenAI).
- **Entities**: Topic, ContentTopic mapping.
- **Related Content Engine**: Connect concepts (e.g., Outbox pattern ↔ Kafka retries).
- **Frontend**: Topic Pages (videos, blogs, notes, related concepts).

---

## PHASE 4 — REVISION ENGINE (Week 4)
**Goal**: Transform passive saving into retained learning.
- **Spaced Repetition**: Resurface content after 1, 7, 30 days.
- **Revision Table**: `RevisionSchedule` (nextRevisionDate, count, confidenceScore).
- **AI Revision Features**: Generate quizzes, flashcards, implementation/interview questions.

---

## PHASE 5 — BROWSER EXTENSION (Week 5)
**Goal**: Reduce capture friction.
- **One-click Save**: YouTube, LinkedIn, blogs, GitHub.
- **Flow**: Current Page → Extract Metadata → Send to Backend → AI Processing Starts.

---

## PHASE 6 — ADVANCED AI (Future)
- Personalized Learning Graph.
- AI Tutor Mode (“Explain X using Y”).
- Roadmap Generator.
- Interview Preparation Mode.
- Knowledge Graph Visualization.

---

## PHASE 7 — SCALING ARCHITECTURE
**ONLY after real usage.**
- Service Extraction (AI, Search, Notification, Ingestion).
- **DO NOT BUILD EARLY**: Microservices, Kafka, Event Sourcing, CQRS, Graph DB, K8s, Multi-agent systems.

---

## ENGINEERING PRINCIPLES
1.  **Store Raw Content**: Preserve original content for future AI model improvements.
2.  **Async AI Processing**: Never block requests; use Spring Async initially.
3.  **Prompt Versioning**: Store model and prompt versions for reprocessing.
4.  **Modular Boundaries**: Own services/entities/repositories per module.
5.  **Cost Awareness**: Batching, retries, caching, and cheap default models.

---

## SUCCESS METRICS
- **Initial**: The founder uses the app daily.
- **Product**: Users retrieve saved knowledge, revisit concepts, and improve retention.

**FINAL STRATEGIC GOAL**: Build “A Second Brain For Technical Learners”.
