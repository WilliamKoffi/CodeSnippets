# API Development Plan

This plan outlines the architecture, database integration, and API endpoints for each domain of the **Code Snippets** application, starting with the **Auth Domain**. The application is backed by Spring Boot and PostgreSQL, fully run and coordinated using Docker Compose.

---

## 🏗️ Phase 1: Infrastructure Setup (Docker & DB Connection)

### 1. Docker Compose Configuration
We will add a PostgreSQL database service to the existing [docker-compose.yml](file:///home/llyam/lab/projects/code-snippets/docker-compose.yml).
- **Service Name**: `db`
- **Image**: `postgres:15-alpine`
- **Database**: `snippets_db`
- **Port**: `5432`
- **Data Persistence**: Named volume `pgdata` to prevent data loss.

### 2. Maven Dependencies (`api/pom.xml`)
We will add standard spring boot starters for database connection and data layer logic:
- `spring-boot-starter-data-jpa`: Object-Relational Mapping (Hibernate).
- `postgresql`: PostgreSQL JDBC Driver.
- `spring-boot-starter-validation`: For validating request body constraints (e.g. valid email, not empty name).

### 3. Application Properties (`application.properties`)
We will configure database source connections, JPA behavior, and dialect details:
- Dynamic variables pointing to `localhost` for local development, and Docker-configured environment variables for container environments.
- Hibernate DDL Auto-update (`update` mode) for automatic table creation during development.

---

## 🔐 Phase 2: Auth Domain (Current Milestone)

The Auth Domain maps to the `users` table. It manages user registration, login, profile updates, and authentication state.

### 1. Database Schema (`users` table)
```sql
CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY, -- We'll support UUIDs as strings
    name VARCHAR(100) NOT NULL,
    handle VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Added to support real auth
    avatar TEXT,
    reputation INTEGER DEFAULT 0 NOT NULL,
    role VARCHAR(20) CHECK (role IN ('frontend', 'backend', 'fullstack', 'devops', '')),
    level VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Spring Boot Implementation Structure
We will create a packaged structure under `com.example.api.domains.auth`:
- **Entity**: `User.java` (JPA Entity representing the table)
- **Repository**: `UserRepository.java` (Spring Data JPA Interface)
- **DTOs**:
  - `RegisterRequest.java` (fields: `name`, `handle`, `email`, `password`, `role`)
  - `LoginRequest.java` (fields: `email`, `password`)
  - `UserResponse.java` (returning profile details excluding passwords)
- **Service**: `AuthService.java` (business logic for authentication, password hashing, and user creation)
- **Controller**: `AuthController.java` (REST Endpoints exposing HTTP APIs)

### 3. Proposed Endpoints
| HTTP Method | Path | Request Body | Response Body | Description |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/register` | `RegisterRequest` | `UserResponse` | Creates a new user profile with hashed password |
| **POST** | `/api/auth/login` | `LoginRequest` | `UserResponse` | Verifies user credentials and returns session user |
| **GET** | `/api/auth/users/{id}` | None | `UserResponse` | Fetches a user's details |
| **PUT** | `/api/auth/profile` | `UpdateProfileRequest` | `UserResponse` | Updates display name, handle, role, level, or avatar |

---

## 📝 Phase 3: Snippets & Tags Domain

Manages creation, retrieval, and updating of code snippets/bugs and associated tags.

### 1. Database Schema
- `snippets` table (stores titles, description, code, type, author association).
- `tags` table (stores unique tag names).
- `snippet_tags` junction table.

### 2. Proposed Endpoints
| HTTP Method | Path | Query Params / Body | Description |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/snippets` | `search`, `tag`, `type` | Retrieves snippets, optionally filtered |
| **GET** | `/api/snippets/{id}` | None | Retrieves a single snippet with full author details and solutions |
| **POST** | `/api/snippets` | `CreateSnippetRequest` | Creates a new snippet or bug report |
| **GET** | `/api/tags` | None | Returns list of tags and their usage counts |

---

## 💬 Phase 4: Solutions Domain

Handles submitting solutions/fixes to snippets, accepting solutions, and listing comments.

### 1. Database Schema
- `solutions` table (stores content, optional code block, accepted flag, and snippet/author references).

### 2. Proposed Endpoints
| HTTP Method | Path | Request Body | Description |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/snippets/{id}/solutions` | `CreateSolutionRequest` | Adds a solution to a snippet |
| **PUT** | `/api/solutions/{id}/accept` | None | Accepts a solution (only author of snippet can do this) |

---

## 👍 Phase 5: Interactions Domain (Likes, Bookmarks, & Votes)

Tracks social/gamification actions.

### 1. Database Schema
- `snippet_likes` (junction table for likes).
- `snippet_bookmarks` (junction table for saved snippets).
- `solution_votes` (junction table with payload: up/down).

### 2. Proposed Endpoints
| HTTP Method | Path | Request Body | Description |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/snippets/{id}/like` | None (toggles like) | Likes/unlikes a snippet |
| **POST** | `/api/snippets/{id}/save` | None (toggles save) | Bookmarks/unbookmarks a snippet |
| **POST** | `/api/solutions/{id}/vote` | `VoteRequest` (`up` / `down`) | Casts or updates a vote on a solution |

---

## 🚀 Execution Steps for Phase 2 (Auth Domain)
1. Update [docker-compose.yml](file:///home/llyam/lab/projects/code-snippets/docker-compose.yml) to add the PostgreSQL container.
2. Update [pom.xml](file:///home/llyam/lab/projects/code-snippets/api/pom.xml) with JPA and Postgres dependencies.
3. Configure database connections in [application.properties](file:///home/llyam/lab/projects/code-snippets/api/src/main/resources/application.properties).
4. Create the `User` JPA Entity and database integration.
5. Create business logic and REST controllers for `/api/auth/register`, `/api/auth/login`, and profile updates.
6. Verify and test the service using local Docker container startup.
