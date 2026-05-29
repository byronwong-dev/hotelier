# hotelier

A Ktor-based hotel data aggregation service. Fetches hotel listings from multiple supplier APIs concurrently, merges and deduplicates them, and exposes a single unified `GET /hotels` endpoint.

---

## Thoughts 
Definitely there are a lot more can be done, like cache handling, better search queries, pagination, sorting, circuit breaker at the retrieval side, better error handling like maybe 3/5 supplier is available we will display, and so much more.
But for the aim of the assignment, I just focus on the merging and delivery of the data, keeping the changes simple

You can review commit-by-commit to see the incremental changes and the thought process behind it.

The reason for using Ktor is also that it is a very lightweight framework just for the assignment and I wanted to try it out for fun.
However when it comes to more complex deployment I think in some instance a more complete like SpringBoot or Micronaut for JVM stack is desired

## Requirements

| Tool | Version |
|------|---------|
| Java | 21 |
| Kotlin | 2.3.21 |
| Ktor | 3.5.0 |
| Gradle | 9.5.0 (via wrapper) |

### Installing Java 21

**sdkman** (recommended on macOS/Linux):
```bash
sdk install java 21.0.7-tem
sdk use java 21.0.7-tem
```

**asdf**:
```bash
asdf plugin add java
asdf install java temurin-21.0.7+7
asdf local java temurin-21.0.7+7
```

**Homebrew** (macOS):
```bash
brew install --cask temurin@21
```

Verify the installation:
```bash
java -version
# openjdk version "21.x.x" ...
```

> The Gradle build is configured with `jvmToolchain(21)` and will automatically use the JDK 21 toolchain if it is on your `PATH`.

---

## Building

```bash
./gradlew build
```

Runs compilation, ktlint checks, and all tests.

To auto-fix code style issues before building:
```bash
./gradlew ktlintFormat
./gradlew build
```

---

## Running

```bash
./gradlew run
```

The server starts on port **8080**:
```
Application started in 0.1 seconds.
Responding at http://0.0.0.0:8080
```

---

## API

### `GET /hotels`

Returns a merged list of hotels aggregated from all configured suppliers.

```bash
curl http://localhost:8080/hotels
```

Response shape:
```json
[
  {
    "id": "iJhz",
    "destination_id": 5432,
    "name": "Beach Villas Singapore",
    "location": {
      "lat": 1.264751,
      "lng": 103.824006,
      "address": "8 Sentosa Gateway, Beach Villas, 098269",
      "city": "Singapore",
      "country": "Singapore"
    },
    "description": "...",
    "amenities": {
      "general": ["outdoor pool", "indoor pool", "wifi"],
      "room": ["tv", "coffee machine", "hair dryer"]
    },
    "images": {
      "rooms": [{ "link": "https://...", "description": "Double room" }],
      "site": [],
      "amenities": []
    },
    "booking_conditions": []
  }
]
```

---

## Configuration

Edit `src/main/resources/application.yaml` to control which suppliers are active and how many times each fetch is retried on failure:

```yaml
hotelier:
  suppliers:
    maxRetries: 3       # retries per supplier on HTTP failure
    enabled:
      - ACME
      - PAPERFLIES
      - PATAGONIA
```

Removing an entry from `enabled` disables that supplier without any code changes. The default (if the key is absent) is all three suppliers with 3 retries.

---

## Testing

```bash
./gradlew test
```

Tests use real JSON fixture files under `src/test/resources/` and exercise the full merge and deduplication logic for all three suppliers.

## Linting
```bash
./gradlew ktlintFormat # for applying formatting fixes
./gradlew ktlintCheck  # for checking code style without fixing
```
