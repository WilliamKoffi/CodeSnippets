# Code Snippets

Small full-stack code-sharing app with an Angular frontend and a Spring Boot backend backed by PostgreSQL.

## Stack

- Frontend: Angular 22, Bun, Tailwind CSS v4, Angular signals, standalone components
- Backend: Spring Boot 4.1, Java 21, Maven
- Database: PostgreSQL 15

## Project Layout

```text
.
├── web/   # Angular app
├── api/   # Spring Boot API
├── bin/   # Local dev helpers
└── docs/  # Planning and schema notes
```

## Prerequisites

For local development, install:

- Java 21
- Bun 1.3.13
- Docker and Docker Compose

If you do not want to use Docker for the database, run PostgreSQL locally on:

- host: `localhost`
- port: `5432`
- database: `snippets_db`
- user: `postgres`
- password: `password`

## Quick Start

### Option 1: Start everything for local development

This starts PostgreSQL in Docker, then runs the API and web dev servers locally.

```bash
./bin/dev.sh
```

App URLs:

- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080`

### Option 2: Run each service manually

1. Install frontend dependencies:

```bash
cd web
bun install
```

2. Start PostgreSQL:

```bash
docker compose up -d db
```

3. Start the backend:

```bash
cd api
./mvnw spring-boot:run
```

4. Start the frontend in another terminal:

```bash
cd web
bun start
```

## Docker Full Stack

To run the database, API, and frontend all in containers:

```bash
docker compose up --build
```

To stop them:

```bash
docker compose down
```

## Default Development Data

The backend seeds demo users, tags, snippets, and solutions on startup.

Demo login:

- email: `ada@lovelace.com`
- password: `password`

Important: the seeder clears and recreates tables when the database is missing seeded snippet data or the `current_user` record. Treat the local database as disposable during development.

## Running Tests

Frontend:

```bash
cd web
bun run test
```

Backend:

```bash
cd api
./mvnw test
```

## Build Commands

Frontend:

```bash
cd web
bun run build
```

Backend:

```bash
cd api
./mvnw package
```

## Notes

- The frontend currently calls the backend directly at `http://localhost:8080`.
- Auth and snippet data are partly backend-backed and partly stored in frontend `localStorage`.
- Mail is configured for a local SMTP service on `localhost:1025` if you want to exercise password reset flows.
