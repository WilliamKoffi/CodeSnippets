# AGENTS.md

## Project Overview

This repository contains a small full-stack "Code Snippets" application. The frontend is an Angular 22 single-page app in `web/`, built with Bun, Tailwind CSS v4, Angular signals, standalone components, lazy routes, Lucide icons, and a Three.js landing-page animation. The backend is a Spring Boot 4.1 / Java 21 API in `api/`, built with Maven, Spring Web MVC, Spring Data JPA, and PostgreSQL.

The implemented backend currently covers user auth/profile APIs. Most snippet, solution, tag, like, save, and vote behavior is still frontend-local, seeded from `web/src/app/shared/seed-data.ts` and persisted through `localStorage`.

## Source Map

| Path | Purpose | Notes for agents |
|---|---|---|
| `api/` | Spring Boot backend application. | Java 21, Maven wrapper, Spring Boot 4.1. Auth domain is implemented; other domains are documented but not implemented. |
| `api/src/main/java/com/example/api/ApiApplication.java` | Backend runtime bootstrap. | Standard `@SpringBootApplication` entrypoint. |
| `api/src/main/java/com/example/api/HelloController.java` | Simple `/hello` endpoint. | Likely scaffolding/smoke-test code. Do not infer API conventions from this alone. |
| `api/src/main/java/com/example/api/domains/auth/` | Auth/profile domain. | Contains controller, service, JPA entity, repository, and DTOs. Read this whole folder before changing auth behavior. |
| `api/src/main/resources/application.properties` | Backend configuration. | Uses env-overridable PostgreSQL settings and `spring.jpa.hibernate.ddl-auto=update`. |
| `api/src/test/` | Backend tests. | Only default context-load test found; auth endpoints/services are not covered. |
| `web/` | Angular frontend application. | Package manager is Bun. Angular CLI project name is `code-snippets-web`. |
| `web/src/main.ts` | Frontend runtime bootstrap. | Bootstraps standalone Angular `App` with `appConfig`. |
| `web/src/app/app.config.ts` | Angular app providers. | Provides router and globally picked Lucide icons. Add icons here or import them locally consistently. |
| `web/src/app/app.routes.ts` | Top-level frontend routes. | Lazy-loads landing, auth routes, and shell-wrapped snippets/user routes. |
| `web/src/app/domains/auth/` | Login, register, reset pages and auth routes. | Calls `Session` service. Login/register hit `http://localhost:8080/api/auth/*` directly. |
| `web/src/app/domains/snippets/` | Feed, snippet detail, and snippet submit pages. | Uses local `Catalog` service; not backed by backend snippets APIs yet. |
| `web/src/app/domains/user/` | Profile and settings pages. | Uses frontend `Session` state; verify backend/profile integration before changing assumptions. |
| `web/src/app/domains/landing/` | Public landing page. | Contains imperative Three.js animation with cleanup in `ngOnDestroy`; higher risk than normal Angular templates. |
| `web/src/app/core/state/` | Angular signal-based app state services. | `Catalog`, `Session`, and `Workspace` are shared state hubs. Changes here affect many pages. |
| `web/src/app/core/storage.service.ts` | Local storage wrapper. | Centralizes `localStorage` reads/writes and app data clearing. |
| `web/src/app/shared/types.ts` | Frontend domain interfaces. | Must stay aligned with seed data, state services, and backend DTOs when integration changes. |
| `web/src/app/shared/seed-data.ts` | Initial user, tags, snippets, and solutions. | Large frontend fixture data; edits can affect tests and local app defaults. |
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
| `docs/` | Planning and schema analysis docs. | Describes intended future API/database domains; not all docs match current implementation exactly. |
| `trash/` | Ignored old/temporary material. | User requested this folder be ignored. Do not use it as source of truth. |

## Main Entrypoints

- Backend app: `api/src/main/java/com/example/api/ApiApplication.java`
- Backend auth API: `api/src/main/java/com/example/api/domains/auth/AuthController.java`
- Backend config: `api/src/main/resources/application.properties`
- Frontend app bootstrap: `web/src/main.ts`
- Frontend providers: `web/src/app/app.config.ts`
- Frontend top-level routes: `web/src/app/app.routes.ts`
- Frontend feature routes: `web/src/app/domains/auth/auth.routes.ts`, `web/src/app/domains/snippets/snippets.routes.ts`, `web/src/app/domains/user/user.routes.ts`
- Frontend shell: `web/src/app/layout/shell/shell.component.ts`
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
- Frontend API calls currently use native `fetch` in `Session` with hard-coded `http://localhost:8080` URLs. No Angular `HttpClient` wrapper or environment file was found.
- Backend code uses package-by-domain under `com.example.api.domains.auth`.
- Backend DTOs are Java records in `api/src/main/java/com/example/api/domains/auth/dto/`.
- Backend entity behavior avoids general setters and exposes small domain methods such as `praise`, `penalize`, `promote`, `reassign`, and `update`.
- Backend request validation annotations were not found, despite docs mentioning validation as a planned dependency.
- Tests are sparse. Do not assume behavior is protected unless you see a focused test.

## Skill-Resistance Complexity Map

| Area | Complexity | Why it is resistant | Agent guidance |
| ---- | ---------: | ------------------- | -------------- |
| Frontend `Catalog` state | High | Drives filtering, create, like/save, solution creation, vote toggling, persistence, and navigation from one service. Local storage data shape is coupled to `types.ts` and `seed-data.ts`. | Read `catalog.service.ts`, `types.ts`, `seed-data.ts`, and relevant pages before editing. Update `catalog.service.spec.ts` for behavior changes. |
| Frontend/backend auth contract | Critical | Frontend `Session` calls backend auth endpoints directly and persists returned `User` to `localStorage`. Backend `UserResponse` omits email/password and uses Java entity affordances. | Read `Session`, `AuthController`, `AuthService`, DTOs, and `User` together. Keep response shapes aligned with `web/src/app/shared/types.ts`. |
| Password/auth security | Critical | Backend uses simple SHA-256 password hashing and wildcard CORS. This may be acceptable for a demo but is risky for production. | Do not present current auth as production-safe. Get explicit permission before changing auth/security behavior. Add tests if changing it. |
| Database schema management | High | JPA uses `ddl-auto=update`; no migrations were found. Schema can drift from docs and frontend models. | Avoid casual entity/column renames. If schema changes are requested, document migration implications and test against PostgreSQL. |
| Snippets API migration | High | Docs plan persistent snippets/tags/solutions/interactions, but frontend currently handles them locally. | Treat docs as design intent, not implementation. Integration requires coordinated backend models, endpoints, frontend service changes, and tests. |
| Three.js landing page | High | Imperative animation manages renderer, buffers, events, resize observer, animation frame, and disposal manually. | Preserve cleanup paths in `ngOnDestroy`. Verify visually after edits and avoid leaking event listeners or WebGL resources. |
| Routing and shell layout | Medium | Shell wraps `/snippets` and `/user` sections and includes global navigation/modals; route changes can affect layout and modal availability. | Check `app.routes.ts`, domain route files, and `ShellComponent` before moving pages. |
| `Workspace` modal state | Medium | Docs/support/trending modal visibility is controlled through shared signals used by layout/nav components. | Keep signal names and modal interactions stable unless updating all callers. |
| Seed data | Medium | Large French-language fixture content defines default UX and test assumptions. | Be careful with IDs used by routes/tests. Do not rewrite fixture content casually. |
| Docker/dev scripts | Medium | `bin/dev.sh` mixes Docker Compose DB startup with local Maven and Bun servers. Docker Compose can also run web/api containers. | Decide local vs container mode before changing ports or environment variables. |
| Generated/build artifacts | Low | Angular `dist`, `.angular`, Maven `target`, and `node_modules` are ignored. | Do not edit generated artifacts. |
| Tests | High | Coverage is thin: frontend has app creation and catalog state tests; backend has only context-load. | Add or update focused tests for behavior changes, especially auth, state, and routing. |

## Safe Change Rules

- Read related files before modifying shared behavior. For auth, read both frontend and backend contract files.
- Keep diffs small and local to the requested change.
- Do not rewrite the application architecture unless explicitly asked.
- Keep public route paths and API response shapes stable unless the task requires changing them.
- Update or add tests when behavior changes, especially in `Catalog`, `Session`, backend auth, or routing.
- Do not rename domain concepts such as `Snippet`, `Solution`, `User`, `Catalog`, or `Session` without checking all references.
- Do not edit generated output, `node_modules`, `target`, `dist`, `.angular`, or files under `trash/`.
- Do not modify lockfiles unless dependencies intentionally change.
- Do not introduce a new package manager. The frontend declares `bun@1.3.13`.
- Respect existing Tailwind utility styling and theme tokens in `web/src/styles.css`.
- Be cautious with hard-coded ports: frontend expects API on `localhost:8080`, web runs on `4200`, and PostgreSQL runs on `5432`.
- Do not treat planning docs as implemented code. Verify with `rg --files` and source reads.

## Testing Guidance

Frontend tests:

```bash
cd web && bun run test
```

Relevant frontend tests currently found:

- `web/src/app/app.spec.ts`: app creation smoke test.
- `web/src/app/core/state/catalog.service.spec.ts`: catalog like/unlike and solution vote behavior.

Backend tests:

```bash
cd api && ./mvnw test
```

Relevant backend tests currently found:

- `api/src/test/java/com/example/api/ApiApplicationTests.java`: Spring context-load smoke test only.

For auth changes, add backend service/controller tests or verify endpoints manually against PostgreSQL. For frontend auth changes, test `Session` behavior and manually verify login/register flows because no existing `Session` specs were found. For landing-page changes, run the web app and visually verify the Three.js canvas and cleanup-sensitive navigation.

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
- Frontend snippet data is local-only while auth is backend-backed; this split can confuse integration work.
- Backend docs mention planned endpoints and schema that are not implemented yet.
- Auth security is demo-grade: simple SHA-256 hashing and wildcard CORS.
- JPA auto-updates schema with no migration history.
- Backend test coverage is minimal.
- Frontend test coverage does not cover routing, forms, auth, layout, or the Three.js landing page.
- `docs/api_development_plan.md` says `PUT /api/auth/profile`, but current code implements `PUT /api/auth/users/{id}`.
- `User` entity has comments about "RULE: One English Word" on timestamp field names; preserve these field names unless you understand the convention.
- `git status` could not be used in this environment because `.git` is not exposed as a normal repository.
- Angular best-practices MCP lookup returned an unexpected response during this scan; rely on local source and official Angular docs if needed.

## Do Not Touch Without Permission

- `trash/`
- `web/bun.lock`
- `api/mvnw` and `api/mvnw.cmd`
- Docker and deployment config: `docker-compose.yml`, `api/Dockerfile`, `web/Dockerfile`
- Auth/security code: `api/src/main/java/com/example/api/domains/auth/`, `web/src/app/core/state/session.service.ts`
- Database schema-impacting code: JPA entities and `application.properties`
- Large fixture data in `web/src/app/shared/seed-data.ts`
- Generated/build/dependency directories: `node_modules`, `dist`, `.angular`, `target`
