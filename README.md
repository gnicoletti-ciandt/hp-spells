# Harry Potter App

A spell lookup app built on the public [potterapi](https://github.com/fedeperin/potterapi)
(`https://potterapi-fedeperin.vercel.app`).

| Module | Tech | Port | Purpose |
|---|---|---|---|
| [potter-consumer-service](./potter-consumer-service) | Java 17, Maven (plain JDK `HttpServer`) | 4100 | Wraps potterapi and exposes a simplified spell endpoint |
| [potter-frontend](./potter-frontend) | Node.js (no dependencies) | 5100 | Static page that displays a spell |

## Prerequisites

- JDK 17+
- Maven 3.x
- Node.js 18+ (uses the built-in test runner)
- Internet access (the backend calls potterapi)

## Install and run

### Backend

```bash
cd potter-consumer-service
mvn -s settings-local.xml clean package      # compiles, runs unit tests, builds target/potter-consumer-service.jar
java -jar target/potter-consumer-service.jar
```

Environment variables:

| Variable | Default | Description |
|---|---|---|
| `PORT` | `4100` | Port the service listens on |
| `POTTER_API_BASE_URL` | `https://potterapi-fedeperin.vercel.app` | Upstream API base URL |

### Frontend

No install step is needed (there are no npm dependencies).

```bash
cd potter-frontend
npm start        # http://localhost:5100
```

`PORT` overrides the port. The backend URL can be overridden per page load with
`http://localhost:5100/?apiBase=http://localhost:9999`. It defaults to `http://localhost:4100`.

Open http://localhost:5100 once both are running.

### Unit tests

```bash
cd potter-consumer-service && mvn -s settings-local.xml test
cd potter-frontend && npm test
```

## API

`GET /api/spell?name=<spell name>`

| Status | Body |
|---|---|
| 200 | `{"name":"Expelliarmus","description":"Disarming Charm"}` |
| 400 | `{"error":"missing required query parameter 'name'"}` |
| 404 | `{"error":"no spell found for name 'xyz'"}` |
| 405 | `{"error":"method not allowed"}` (any method other than GET) |
| 502 | `{"error":"upstream potterapi error: ..."}` |

`GET /api/spells`

| Status | Body |
|---|---|
| 200 | `[{"name":"Accio","description":"Summoning Charm"}, ...]` (sorted A-Z) |
| 405 | `{"error":"method not allowed"}` |
| 502 | `{"error":"upstream potterapi error: ..."}` |

`GET /health` returns `{"status":"UP"}`. CORS is open (`Access-Control-Allow-Origin: *`).

## Business rules

### Backend

1. **A spell name is required.** A missing or blank `name` returns 400. Surrounding whitespace is trimmed before the lookup.
2. **Search is delegated to potterapi** (`/en/spells?search=<name>`, English only). The name is URL-encoded, with spaces as `%20`.
3. **One spell is returned per request.** potterapi's search returns a list of best matches, which is narrowed to one as follows:
   - An exact match wins, ignoring case and surrounding whitespace. So `Lumos` returns "Lumos" even if "Lumos Maxima" is listed first.
   - Otherwise the first result is returned, so a partial name like `Lum` returns the first match.
4. **No results means 404.** potterapi answers 200 with an empty list in that case, and the service turns it into a 404.
5. **Upstream failures mean 502.** This covers a non-200 status, an unparsable body, a network error, or a timeout (5s connect, 10s request).
6. **Response shape is our own.** potterapi's `spell` becomes `name` and `use` becomes `description`. Both are trimmed, and null values become empty strings. The upstream `index` field is dropped.
7. **Spell list.** `/api/spells` returns every spell from potterapi, with the same name/description mapping as rule 6. Entries without a name are dropped, duplicates (ignoring case) are removed keeping the first, and the list is sorted A-Z ignoring case. These are the "valid" spells, i.e. the ones a lookup can succeed for.
8. **Read-only.** Only GET is supported.

### Frontend

1. A "Valid spells" list is shown on the left, loaded from `/api/spells`. Hovering a spell shows its description as a tooltip. If the list can't be loaded, the sidebar shows "Could not load the spell list." and manual lookup still works.
2. Clicking a spell fills the input and looks it up. The currently displayed spell is highlighted in the list.
3. On load, the page automatically looks up the default spell, "Expelliarmus".
4. Submitting the form looks up the entered name (trimmed).
5. A successful result is shown as the spell name prefixed with ✨, followed by its description.
6. Errors are shown in an alert area, and the previous result is hidden while a new lookup runs.
   - If the backend returns an `error` message, that message is shown.
   - Otherwise the page shows `Request failed with status <code>`.
   - If the backend can't be reached, it shows "Could not reach the spell service. Please try again."

## Scope

This app has unit tests only. There are no contract or e2e tests.
