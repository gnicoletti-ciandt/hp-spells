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

Written as acceptance criteria in Gherkin, following the journey of a person using the app.
No technical knowledge is needed to read them.

### Journey 1: Opening the app

```gherkin
Feature: Opening the app
  As a Harry Potter fan
  I want to see a spell as soon as I open the page
  So that I know the app works and what it offers

  Acceptance criteria:
  - When I open the page, I see the spell "Expelliarmus" and its description without doing anything.
  - I see a list called "Valid spells" on the left side, sorted from A to Z.
  - Each spell appears only once in the list, no matter how it is capitalized.
  - When I hover over a spell in the list, I see its description.
  - If the list cannot be loaded, I see the message "Could not load the spell list."
    and I can still search for spells by typing.
```

### Journey 2: Choosing a spell from the list

```gherkin
Feature: Choosing a spell from the list
  As a Harry Potter fan
  I want to click on a spell
  So that I can read about it without typing

  Acceptance criteria:
  - When I click a spell in the list, its name appears in the search box.
  - The spell's name and description are shown right away.
  - The spell I am reading about is highlighted in the list.
```

### Journey 3: Searching for a spell

```gherkin
Feature: Searching for a spell
  As a Harry Potter fan
  I want to type the name of a spell
  So that I can find out what it does

  Acceptance criteria:
  - When I type a spell name and submit, I see that spell's name (with a ✨ before it) and its description.
  - Capital letters and extra spaces around my words do not matter ("  lumos " finds "Lumos").
  - If several spells match what I typed, I get the one that matches exactly.
    Typing "Lumos" shows "Lumos", even though "Lumos Maxima" also exists.
  - If nothing matches exactly, I get the first similar spell. Typing "Lum" shows the first spell starting that way.
  - Only one spell is ever shown at a time.
  - While a new search is running, the previous result and any previous error disappear.
```

### Journey 4: Something goes wrong

```gherkin
Feature: Clear messages when a search does not work
  As a Harry Potter fan
  I want to understand what happened when I do not get a spell
  So that I know what to do next

  Acceptance criteria:
  - If I submit an empty search, I am told that a spell name is required.
  - If the spell does not exist, I am told that no spell was found for what I typed (for example "xyz").
  - If the spell information source is down, slow or sends something unreadable,
    I am told there was a problem getting spells from it.
  - If the app cannot reach the spell service at all, I see
    "Could not reach the spell service. Please try again."
  - In every case I see no spell, only the message.
```

### Journey 5: What the spell service guarantees

These are the promises behind the screen, described in terms of what a person would see.

```gherkin
Feature: Spell information
  Acceptance criteria:
  - Every spell has a name and a description, with no stray spaces around them.
  - A spell with a missing description is shown with an empty description, never as an error.
  - Spells without a name never appear in the list.
  - Nothing can be changed by users: the app only lets people read spells.
  - Spell information is provided in English only.
```

## Scope and challenge

This app currently has unit tests only. It is used as an interview challenge: the
challenger is expected to implement the missing test layers for the business rules above.

1. **End-to-end tests:** drive the real frontend in a browser against the backend, covering the frontend scenarios.
2. **Integration tests:** exercise the backend HTTP API (and the frontend server) with potterapi stubbed, covering the backend scenarios, including upstream failures.
3. **Contract tests:** verify the contract between the frontend and the backend (`/api/spell`, `/api/spells`), and between the backend and potterapi.
