# Notes App

Minimal CRUD Notes App with React + FastAPI and shareable URLs.

## Setup & Run

### Backend (Spring Boot)
```bash
mvn spring-boot:run
```
Backend runs on http://localhost:8000

### Frontend (React)
```bash
npm install
npm start
```
Frontend runs on http://localhost:3000

## API Endpoints

- `GET /api/notes` - List all notes
- `POST /api/notes` - Create note
- `GET /api/notes/{id}` - Get note
- `PUT /api/notes/{id}` - Update note  
- `DELETE /api/notes/{id}` - Delete note
- `GET /api/notes/{id}/share` - Get shareable URL
- `GET /api/shared/{token}` - View shared note

## Features

- Create, read, update, delete notes
- Share notes with expiring URLs (7 days)
- Minimal UI with inline editing
- In-memory storage (for demo)

## RAG Pipeline Design

### Chunking Strategy
- Split notes by sentences (max 200 chars)
- Preserve title-content relationship
- Overlap: 20 characters between chunks

### Embeddings
- Model: sentence-transformers/all-MiniLM-L6-v2
- Dimension: 384
- Encode title + content together

### Vector Store
- Chroma DB (local)
- Collection: "notes_embeddings"
- Metadata: {note_id, title, created_at}

### Retriever Settings
- Similarity: cosine
- Top-k: 5 relevant chunks
- Score threshold: 0.7

### Prompt Shape
```
Context: {retrieved_chunks}
Query: {user_question}
Instructions: Answer based on the notes context. If no relevant notes found, say "No relevant notes found."
```

### Evaluation
- Metrics: Retrieval accuracy, response relevance
- Test queries: "Find notes about X", "What did I write about Y?"
- Ground truth: Manual annotation of 50 note-query pairs