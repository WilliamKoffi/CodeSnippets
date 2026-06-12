# AGENTS.md

## Project Overview

This repository contains a small full-stack "Code Snippets" application. The frontend is an Angular 22 single-page app in `web/`, built with Bun, Tailwind CSS v4, Angular signals, standalone components, lazy routes, Lucide icons, and a Three.js landing-page animation. The backend is a Spring Boot 4.1 / Java 21 API in `api/`, built with Maven, Spring Web MVC, Spring Data JPA, and PostgreSQL.

The backend now implements user auth/profile APIs and a first-pass snippets API for listing, showing, and creating snippets plus listing tags. The frontend is hybrid: auth is backend-backed, snippet and tag data now hydrate from backend endpoints, but likes, saves, solution voting, some solution content, and fallback creation behavior still live in frontend state and `localStorage`.

## Source Map

| Path | Purpose | Notes for agents |
|---|---|---|
| `api/` | Spring Boot backend application. | Java 21, Maven wrapper, Spring Boot 4.1. Auth and snippets domains are both implemented in code now. |
| `api/src/main/java/com/example/api/ApiApplication.java` | Backend runtime bootstrap. | Standard `@SpringBootApplication` entrypoint. |
| `api/src/main/java/com/example/api/DatabaseSeeder.java` | Startup seed routine for users, tags, snippets, and solutions. | High-impact file: can clear and reseed tables on startup when its guard condition does not pass. Read it before changing IDs, seed assumptions, or startup data behavior. |
| `api/src/main/java/com/example/api/HelloController.java` | Simple `/hello` endpoint. | Likely scaffolding/smoke-test code. Do not infer API conventions from this alone. |
| `api/src/main/java/com/example/api/domains/auth/` | Auth/profile HTTP layer and `User` entity. | Controllers are split by responsibility: registration, session, profile, and recovery. Read the whole folder before changing auth behavior. |
| `api/src/main/java/com/example/api/domains/auth/domain/` | Auth workflow helpers. | Contains stateless domain operations such as `Registration`, `Session`, `Profile`, `Recovery`, `Account`, and `Password`. There is no longer a single `AuthService`. |
| `api/src/main/java/com/example/api/domains/auth/requests/` | Auth request DTOs. | Java records for login, register, update-profile, and reset-password payloads. |
| `api/src/main/java/com/example/api/domains/auth/responses/` | Auth response DTOs. | `UserResponse` is the backend auth/profile contract the frontend persists locally. |
| `api/src/main/java/com/example/api/domains/auth/repositories/` | Auth persistence layer. | `UserRepository` lives here now. |
| `api/src/main/java/com/example/api/domains/snippets/` | Snippet and tag HTTP layer. | `SnippetController` serves `/api/snippets`; `TagController` serves `/api/tags`. |
| `api/src/main/java/com/example/api/domains/snippets/catalogs/` | Snippet/tag orchestration components. | `Snippet` and `Tag` here are Spring components that coordinate repositories and domain objects. Read these before changing snippet search, publish, or tag-summary behavior. |
| `api/src/main/java/com/example/api/domains/snippets/domain/` | Snippet JPA entities and affordances. | Contains `Snippet`, `Solution`, and `Tag`. Schema-impacting and contract-sensitive. |
| `api/src/main/java/com/example/api/domains/snippets/requests/` | Snippet request DTOs. | `CreateSnippetRequest` lives here. |
| `api/src/main/java/com/example/api/domains/snippets/responses/` | Snippet/tag response DTOs. | Response names currently differ from frontend type names; read these with `web/src/app/shared/types.ts` before changing integration behavior. |
| `api/src/main/java/com/example/api/domains/snippets/repositories/` | Snippet persistence layer. | JPA repositories and the custom filtered snippet query live here. |
| `api/src/main/resources/application.properties` | Backend configuration. | Uses env-overridable PostgreSQL settings and `spring.jpa.hibernate.ddl-auto=update`. |
| `api/src/test/` | Backend tests. | Includes auth slice tests plus snippets catalog, domain, and response tests. Coverage is still selective, not full-stack. |
| `web/` | Angular frontend application. | Package manager is Bun. Angular CLI project name is `code-snippets-web`. |
| `web/src/main.ts` | Frontend runtime bootstrap. | Bootstraps standalone `App` with `appConfig`. |
| `web/src/app/app.ts` | Frontend root component. | Minimal standalone root component hosting the router outlet. |
| `web/src/app/app.config.ts` | Angular app providers. | Provides router and globally picked Lucide icons. Add icons here or import them locally consistently. |
| `web/src/app/app.routes.ts` | Top-level frontend routes. | Lazy-loads landing, auth routes, and shell-wrapped snippets/user routes. |
| `web/src/app/domains/auth/` | Login, register, reset pages and auth routes. | Calls `Session` service. Login/register/reset/update/refresh hit `http://localhost:8080/api/auth/*` directly. |
| `web/src/app/domains/snippets/` | Feed, snippet detail, and snippet submit pages. | Uses `Catalog`, which now hydrates snippets and tags from backend and preserves some interaction state locally. |
| `web/src/app/domains/user/` | Profile and settings pages. | Uses frontend `Session` state; verify backend/profile integration before changing assumptions. |
| `web/src/app/domains/landing/` | Public landing page. | Contains imperative Three.js animation with cleanup in `ngOnDestroy`; higher risk than normal Angular templates. |
| `web/src/app/core/state/` | Angular signal-based app state services. | `Catalog`, `Session`, and `Workspace` are shared state hubs. Changes here affect many pages. |
| `web/src/app/core/state/catalog.service.ts` | Frontend snippet/tag state. | Fetches `/api/snippets` and `/api/tags`, merges local interaction fields, persists to `localStorage`, and falls back to local snippet creation on backend failure. |
| `web/src/app/core/state/session.service.ts` | Frontend auth/profile state. | Uses native `fetch` with hard-coded backend URLs and persists returned `User` to `localStorage`. |
| `web/src/app/core/storage.service.ts` | Local storage wrapper. | Centralizes `localStorage` reads/writes and app data clearing. |
| `web/src/app/shared/types.ts` | Frontend domain interfaces. | Must stay aligned with state services and backend DTOs. This file currently does not perfectly mirror snippet response field names from the backend. |
| `web/src/app/shared/seed-data.ts` | Initial user, tags, snippets, and solutions. | Still used for defaults, local fallback behavior, and tests. Large fixture edits can ripple widely. |
| `web/src/app/layout/` | Shell, header, sidebar, and mobile navigation. | Shell wraps authenticated app sections and modals. |
| `web/src/app/shared/modal/` | Docs, support, and trending-tag modals. | Controlled mostly through `Workspace` signals. |
| `web/src/styles.css` | Global Tailwind v4 import, theme tokens, base styles, utilities. | Main styling token source. Component templates use utility classes heavily. |
| `web/angular.json` | Angular build/test/serve configuration. | Uses `@angular/build:application` and `@angular/build:unit-test`. |
| `web/bun.lock` | Bun lockfile. | Do not edit manually. Only change when dependencies are intentionally updated. |
| `docker-compose.yml` | Local multi-service stack. | Runs PostgreSQL, backend, and frontend containers. Contains dev credentials only; do not treat as production config. |
| `api/Dockerfile` | Backend Docker build. | Multi-stage Maven build; skips tests during image build. |
| `web/Dockerfile` | Frontend Docker image. | Runs Angular dev server through Bun with host access. |
| `bin/dev.sh` | Local dev orchestration script. | Starts DB through Docker Compose if available, then API and web dev servers. |
| `bin/update.sh` | Dependency update helper. | Updates Maven and Bun/npm dependencies; use only when asked to update dependencies. |
| `docs/` | Planning and schema analysis docs. | Describes intended future API/database domains; parts of the docs now lag current auth/snippets code. |
| `trash/` | Ignored old/temporary material. | User requested this folder be ignored. Do not use it as source of truth. |

## Main Entrypoints

- Backend app: `api/src/main/java/com/example/api/ApiApplication.java`
- Backend seed routine: `api/src/main/java/com/example/api/DatabaseSeeder.java`
- Backend auth APIs: `api/src/main/java/com/example/api/domains/auth/RegistrationController.java`, `SessionController.java`, `ProfileController.java`, `RecoveryController.java`
- Backend snippets APIs: `api/src/main/java/com/example/api/domains/snippets/SnippetController.java`, `TagController.java`
- Backend config: `api/src/main/resources/application.properties`
- Frontend app bootstrap: `web/src/main.ts`
- Frontend root component: `web/src/app/app.ts`
- Frontend providers: `web/src/app/app.config.ts`
- Frontend top-level routes: `web/src/app/app.routes.ts`
- Frontend feature routes: `web/src/app/domains/auth/auth.routes.ts`, `web/src/app/domains/snippets/snippets.routes.ts`, `web/src/app/domains/user/user.routes.ts`
- Frontend shell: `web/src/app/layout/shell/shell.component.ts`
- Frontend state hubs: `web/src/app/core/state/catalog.service.ts`, `session.service.ts`, `workspace.service.ts`
- Local full-stack dev helper: `bin/dev.sh`
- Docker stack: `docker-compose.yml`

## Development Commands

```bash
# Install frontend dependencies
cd web && bun install

# Run frontend dev server
cd web && bun start

# Run frontend tests
cd web && bun run test

# Build frontend
cd web && bun run build

# Run backend dev server
cd api && ./mvnw spring-boot:run

# Run backend tests
cd api && ./mvnw test

# Build backend package
cd api && ./mvnw package

# Run local database + API + web dev servers
./bin/dev.sh

# Run Docker Compose stack
docker compose up --build

# Stop Docker Compose stack
docker compose down

# Update dependencies; only when explicitly requested
./bin/update.sh
```

No dedicated lint or format script was found. Prettier is present in `web/package.json`, but there is no discovered `format` script.

## Rust-Based Tooling Preference

Future agents should prefer Rust-based CLI tools when available:

| Task               | Preferred   | Fallback      |
| ------------------ | ----------- | ------------- |
| Search text        | `rg`        | `grep`        |
| Search files       | `fd`        | `find`        |
| View files         | `bat`       | `cat`         |
| List files         | `eza`       | `ls`          |
| Replace text       | `sd`        | `sed`         |
| Count code         | `tokei`     | `wc` / `cloc` |
| Disk usage         | `dust`      | `du`          |
| Benchmark commands | `hyperfine` | `time`        |

Agents must gracefully fallback when tools are unavailable. During this scan, `fd`, `rg`, and `bat` were available; `eza` and `tokei` were not.

## Coding Conventions

- Angular code uses standalone components with `imports` arrays in `@Component`; no NgModules were found.
- Angular routing uses `Routes` constants and `loadComponent` / `loadChildren` lazy loading.
- Frontend state uses Angular `signal` and `computed` in services and page components.
- Forms currently use template-driven `FormsModule` and signal-backed fields, not reactive forms.
- Shared mutable state is concentrated in root-provided services: `Catalog`, `Session`, `Workspace`, and `StorageService`.
- Frontend filenames follow domain/page/component naming such as `login.page.ts`, `sidebar.component.ts`, and `catalog.service.ts`.
- Templates use Tailwind utility classes directly. Global theme tokens and base styles live in `web/src/styles.css`.
- Lucide icons are used through `lucide-angular`; some icons are provided globally in `app.config.ts`, while landing imports icons locally.
- Frontend API calls currently use native `fetch` in `Session` and `Catalog` with hard-coded `http://localhost:8080` URLs. No Angular `HttpClient` wrapper or environment file was found.
- `Catalog` now loads snippets and tags from backend endpoints, then preserves frontend-local interaction fields such as likes, saves, votes, and some solution state via `localStorage`.
- Backend code uses package-by-domain and by role: controllers at the domain root, request records under `requests/`, response records under `responses/`, repositories under `repositories/`, and snippet JPA entities under `domains/snippets/domain/`.
- Auth business logic is split across stateless helper classes in `api/src/main/java/com/example/api/domains/auth/domain/`; there is no single service bean coordinating auth anymore.
- Snippet orchestration is handled by Spring `@Component` catalog classes in `api/src/main/java/com/example/api/domains/snippets/catalogs/`, not a `SnippetService`.
- Backend request and response DTOs are Java records.
- Backend entity behavior avoids general setters and exposes small affordances such as `praise`, `penalize`, `promote`, `reassign`, `update`, `like`, `unlike`, `solve`, `tag`, `upvote`, and `accept`.
- Backend request validation annotations were not found, despite docs mentioning validation as a planned dependency.
- Tests exist for auth workflow helpers, snippet catalogs, snippet domain affordances, snippet responses, frontend catalog state, and frontend session state, but routing/UI/database integration coverage is still thin.

## Skill-Resistance Complexity Map

| Area | Complexity | Why it is resistant | Agent guidance |
| ---- | ---------: | ------------------- | -------------- |
| Frontend `Catalog` state | High | It now merges backend-loaded snippets and tags with frontend-local likes, saves, solutions, votes, persistence, and local fallback behavior. Local storage shape is coupled to `types.ts`, `seed-data.ts`, and backend response fields. | Read `catalog.service.ts`, `types.ts`, `seed-data.ts`, relevant pages, and both backend controllers/responses before editing. Update `catalog.service.spec.ts` for behavior changes. |
| Frontend/backend auth contract | Critical | Frontend `Session` calls backend auth endpoints directly and persists returned `User` to `localStorage`. Backend auth behavior is now split across four controllers plus helper classes rather than one service. | Read `Session`, all auth controllers, `requests/`, `responses/`, `domain/`, and `User` together. Keep response shapes aligned with `web/src/app/shared/types.ts`. |
| Frontend/backend snippet contract | Critical | Backend snippet responses use fields like `tally`, `age`, `liked`, `saved`, and `author.owner`, while frontend types and pages still expect names like `solutionsCount`, `createdAt`, `isLikedByMe`, `isSavedByMe`, and `isAuthor`. | Read `web/src/app/shared/types.ts`, `catalog.service.ts`, `SnippetResponse`, `SolutionResponse`, `TagResponse`, and the affected page components together before changing integration behavior. |
| Password/auth security | Critical | Backend uses simple SHA-256 password hashing and wildcard CORS. This may be acceptable for a demo but is risky for production. | Do not present current auth as production-safe. Get explicit permission before changing auth/security behavior. Add tests if changing it. |
| Database schema management | High | JPA uses `ddl-auto=update`; no migrations were found. Schema can drift from docs and frontend models. | Avoid casual entity/column renames. If schema changes are requested, document migration implications and test against PostgreSQL. |
| Database seeding | High | `DatabaseSeeder` can clear and repopulate users, tags, snippets, and solutions on startup depending on current DB state. Seed IDs also influence frontend assumptions such as `current_user`. | Read `DatabaseSeeder.java` before changing IDs, default users, seed snippet data, or startup behavior. |
| Three.js landing page | High | Imperative animation manages renderer, buffers, events, resize observer, animation frame, and disposal manually. | Preserve cleanup paths in `ngOnDestroy`. Verify visually after edits and avoid leaking event listeners or WebGL resources. |
| Routing and shell layout | Medium | Shell wraps `/snippets` and `/user` sections and includes global navigation/modals; route changes can affect layout and modal availability. | Check `app.routes.ts`, domain route files, and `ShellComponent` before moving pages. |
| `Workspace` modal state | Medium | Docs/support/trending modal visibility is controlled through shared signals used by layout/nav components. | Keep signal names and modal interactions stable unless updating all callers. |
| Seed data | Medium | Large French-language fixture content still defines defaults, local fallback UX, and test assumptions even after backend snippet hydration was added. | Be careful with IDs and text used by routes/tests. Do not rewrite fixture content casually. |
| Docker/dev scripts | Medium | `bin/dev.sh` mixes Docker Compose DB startup with local Maven and Bun servers. Docker Compose can also run web/api containers. | Decide local vs container mode before changing ports or environment variables. |
| Generated/build artifacts | Low | Angular `dist`, `.angular`, Maven `target`, and `node_modules` are ignored. | Do not edit generated artifacts. |
| Tests | High | Coverage is broader than before but still mostly unit/slice-level. There are no strong controller HTTP integration tests or DB-backed contract tests. | Add or update focused tests for behavior changes, especially in auth flows, snippet contracts, catalog state, and routing-sensitive code. |

## Safe Change Rules

- Read related files before modifying shared behavior. For auth, read both frontend and backend contract files. For snippets, read frontend types/state plus backend controllers/catalogs/responses together.
- Keep diffs small and local to the requested change.
- Do not rewrite the application architecture unless explicitly asked.
- Keep public route paths and API response shapes stable unless the task requires changing them.
- Update or add tests when behavior changes, especially in `Catalog`, `Session`, backend auth helpers/controllers, or backend snippet catalogs/responses.
- Do not rename domain concepts such as `Snippet`, `Solution`, `Tag`, `User`, `Catalog`, `Session`, `Registration`, `Profile`, or `Recovery` without checking all references.
- Do not edit generated output, `node_modules`, `target`, `dist`, `.angular`, or files under `trash/`.
- Do not modify lockfiles unless dependencies intentionally change.
- Do not introduce a new package manager. The frontend declares `bun@1.3.13`.
- Respect existing Tailwind utility styling and theme tokens in `web/src/styles.css`.
- Be cautious with hard-coded ports: frontend expects API on `localhost:8080`, web runs on `4200`, and PostgreSQL runs on `5432`.
- Do not treat planning docs as implemented code. Verify with `rg --files`, source reads, and current tests.

## Testing Guidance

Frontend tests:

```bash
cd web && bun run test
```

Relevant frontend tests currently found:

- `web/src/app/app.spec.ts`: app creation smoke test.
- `web/src/app/core/state/catalog.service.spec.ts`: catalog local interaction behavior plus backend snippet/tag load behavior.
- `web/src/app/core/state/session.service.spec.ts`: login, register, update, refresh, reset, and logout behavior around the `Session` service.

Backend tests:

```bash
cd api && ./mvnw test
```

Relevant backend tests currently found:

- `api/src/test/java/com/example/api/ApiApplicationTests.java`: Spring context-load smoke test.
- `api/src/test/java/com/example/api/domains/auth/AuthSlicesTests.java`: auth workflow helper tests.
- `api/src/test/java/com/example/api/domains/snippets/catalogs/SnippetCatalogTests.java`: snippet catalog orchestration tests.
- `api/src/test/java/com/example/api/domains/snippets/catalogs/TagCatalogTests.java`: tag catalog summary tests.
- `api/src/test/java/com/example/api/domains/snippets/domain/SnippetTests.java`: snippet entity affordance tests.
- `api/src/test/java/com/example/api/domains/snippets/domain/SolutionTests.java`: solution entity affordance tests.
- `api/src/test/java/com/example/api/domains/snippets/responses/SnippetResponseTests.java`: snippet response builder and age-formatting tests.

For auth changes, update the auth helper tests at minimum and verify the controller contract manually or with new controller tests if you change HTTP behavior. For snippet API changes, update the relevant catalog/domain/response tests and manually verify frontend `Catalog` hydration because contract mismatches are a live risk. For landing-page changes, run the web app and visually verify the Three.js canvas and cleanup-sensitive navigation.

## Dependency and Package Rules

- Frontend package manager: Bun, declared in `web/package.json` as `bun@1.3.13`.
- Frontend lockfile: `web/bun.lock`.
- Backend package/build tool: Maven wrapper in `api/mvnw`.
- Backend runtime: Java 21.
- Do not mix npm/pnpm/yarn lockfiles into `web/`.
- Do not add dependencies unless needed. Prefer existing Angular, RxJS, Tailwind, Lucide, Three.js, Spring Boot, and JPA patterns.
- Use `bin/update.sh` only when the task is specifically to update dependencies; it can change many versions and lockfiles.

## Environment and Configuration

- `docker-compose.yml` defines `api`, `db`, and `web` services.
- PostgreSQL dev defaults:
  - database: `snippets_db`
  - user: `postgres`
  - password: `password`
  - host port: `5432`
- Backend environment variables:
  - `SPRING_PROFILES_ACTIVE=dev`
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
- `api/src/main/resources/application.properties` provides localhost fallbacks for database config and enables SQL logging.
- No `.env.example` file was found.
- `web/Dockerfile` runs the Angular dev server on `0.0.0.0:4200`.
- `api/Dockerfile` builds with Maven and skips tests during image build.

Do not expose or introduce real secrets. Current Docker credentials are development defaults.

## Agent Workflow

1. Scan with `rg --files -g '!trash/**'` or fallback.
2. Read `AGENTS.md`.
3. Read the relevant source files and route/config files.
4. Identify tests and commands for the affected area.
5. Make the smallest safe change.
6. Run the most relevant validation command.
7. Summarize changes, validation, and residual risks.

## Known Risks / Sharp Edges

- Frontend auth uses hard-coded API URLs instead of Angular environments/config.
- Frontend snippet state is hybrid now: backend-loaded snippets/tags are merged with frontend-local likes, saves, votes, and fallback creation behavior.
- Backend snippet response names do not currently line up cleanly with frontend snippet type names:
  - backend `tally` vs frontend `solutionsCount`
  - backend `age` vs frontend `createdAt`
  - backend `liked` / `saved` vs frontend `isLikedByMe` / `isSavedByMe`
  - backend `author.owner` vs frontend `author.isAuthor`
- Backend docs mention planned endpoints and schema that may not match the current auth/snippets implementation.
- Auth security is demo-grade: simple SHA-256 hashing and wildcard CORS.
- JPA auto-updates schema with no migration history.
- `DatabaseSeeder` can wipe and reseed data during startup depending on DB state and hard-coded IDs such as `current_user`.
- Backend test coverage is still mostly unit/slice-level rather than controller/database integration.
- Frontend test coverage does not cover routing, forms, most UI rendering, layout, or the Three.js landing page.
- `docs/api_development_plan.md` says `PUT /api/auth/profile`, but current code implements `PUT /api/auth/users/{id}`.
- `User`, snippet, and solution entities use "RULE: One English Word" timestamp field names; preserve those names unless you understand the convention and migration impact.

## Do Not Touch Without Permission

- `trash/`
- `web/bun.lock`
- `api/mvnw` and `api/mvnw.cmd`
- Docker and deployment config: `docker-compose.yml`, `api/Dockerfile`, `web/Dockerfile`
- Auth/security code: `api/src/main/java/com/example/api/domains/auth/`, `web/src/app/core/state/session.service.ts`
- Seed/reset behavior: `api/src/main/java/com/example/api/DatabaseSeeder.java`
- Database schema-impacting code: JPA entities and `application.properties`
- Large fixture data in `web/src/app/shared/seed-data.ts`
- Generated/build/dependency directories: `node_modules`, `dist`, `.angular`, `target`
