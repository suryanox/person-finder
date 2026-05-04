# Person Finder — Spec

## Stack

Spring Boot (already set up), Kotlin, Spring Data JPA, PostgreSQL via Docker Compose, Spring's RestTemplate for the OpenAI HTTP call.

---

## Phase 1 — Database Setup

Add a docker-compose.yml at the project root that runs PostgreSQL 16. The database name, user, and password can all just be "personfinder". Expose the default port 5432.

Update application.properties to point at this Postgres instance instead of H2. We also need to add two config keys for OpenAI: openai.api-key and openai.base-url. These will just be placeholder values in the file and the developer fills them in locally.

Add the postgres JDBC driver dependency to build.gradle.kts and remove the H2 one. Set spring.jpa.hibernate.ddl-auto to update so Hibernate manages the schema automatically.

Two tables get created from the JPA entities:

The persons table needs id (auto-generated), name, job_title, hobbies (stored as plain text, comma-separated), and bio (nullable text, populated by the AI call).

The locations table uses reference_id as both the primary key and logical foreign key back to persons. It has latitude and longitude as doubles. Put a composite index on latitude and longitude — this is the column pair we filter on in the nearby query and it matters a lot at scale.

---

## Phase 2 — Domain Model

Expand the Person data class to include jobTitle, hobbies as a List of strings, and a nullable bio. Annotate it as a JPA entity. The hobbies list gets stored as a comma-separated string in the DB and mapped back to a list in code.

The Location data class is fine structurally, just needs the JPA annotations and the composite index annotation mentioned above.

Add a PersonNotFoundException in a domain/exceptions package. It just takes an id and produces a readable message.

Create an AiService interface in domain/ports with a single method: generateBio that takes jobTitle and hobbies as strings and returns a string. This keeps the domain clean and the OpenAI implementation behind an interface.

Create a PersonRepository and LocationRepository interface in domain/ports as well. These define the data access contract the services depend on. LocationRepository needs a findWithinBoundingBox method that takes min/max lat and lon and returns a list of locations — that's the indexed query that backs the nearby search.

---

## Phase 3 — Infrastructure

The Spring Data JPA repositories just extend JpaRepository. The LocationRepository one needs a custom JPQL query for the bounding-box lookup.

Write adapter classes that implement the domain port interfaces and delegate to the JPA repositories. The services will depend on the domain ports, not Spring Data directly.

For the OpenAI integration, create an OpenAiService class in infrastructure/ai that implements AiService. It reads the api key and base url from application.properties via @Value. Use Spring's RestTemplate to POST to /chat/completions. The model to use is gpt-4o-mini.

The prompt template should have a system message telling the model to generate short quirky bios under 280 characters and to treat user_input XML tags as strict content boundaries. The user message then wraps the sanitized job title and hobbies in those tags.

Write a PromptSanitizer component in infrastructure/sanitization. It does two things: first a deny-list regex pass that strips known injection phrases like "ignore all instructions", "you are now", "act as", "system prompt", and "override instructions". Then the calling code wraps whatever survives that pass inside the XML delimiter tags in the prompt. Both layers together make it hard to smuggle instructions through.

Write a HaversineCalculator as a plain Kotlin object (no Spring) in domain/services. It takes two lat/lon pairs and returns the distance in km. The formula is 2R * asin(sqrt(sin²(Δlat/2) + cos(lat1)*cos(lat2)*sin²(Δlon/2))) with R = 6371.0.

---

## Phase 4 — Services

PersonsServiceImpl handles three operations.

Creating a person: sanitize jobTitle and hobbies through PromptSanitizer, call generateBio, build and persist the Person entity with the returned bio, then persist the Location separately. Return the saved person.

Updating a location: check the person exists first and throw PersonNotFoundException if not, then upsert the location row.

Finding nearby: compute a bounding box from the query lat/lon and radius (lat ± radius/111, lon ± radius/(111 * cos(lat)) as a rough approximation). Use that to call findWithinBoundingBox which does the cheap indexed SQL query. Then post-filter the results using exact Haversine distance to drop anything outside the true radius. Fetch the matching Person records by their ids. Return them paired with their distances, sorted ascending.

---

## Phase 5 — APIs and Error Handling

All routes live under /api/v1/persons.

POST /api/v1/persons takes a JSON body with name, jobTitle, hobbies (array), latitude, and longitude. On success return 201 with the full person response including the generated bio. Return 400 if name, jobTitle, or hobbies are blank, or if lat/lon are out of valid range. Return 502 if the OpenAI call fails.

PUT /api/v1/persons/{id}/location takes a JSON body with latitude and longitude. Return 204 on success. Return 404 if the person doesn't exist. Return 400 if lat/lon are invalid or id is not a valid number.

GET /api/v1/persons/nearby takes lat, lon, and radius as query params. Return 200 with a list of nearby persons each paired with their distance in km, sorted nearest first. Return an empty list if nobody is within range. Return 400 if any param is missing, non-numeric, or if radius is zero or negative.

For error handling use a @ControllerAdvice class. It maps PersonNotFoundException to 404, IllegalArgumentException to 400 (using the exception message directly), any exception coming from the OpenAI call to 502, and everything else to 500. All error responses use the same shape: a JSON object with a single "error" field containing a human-readable message.

Input validation happens in the controller before calling the service. Invalid inputs throw IllegalArgumentException which the advice picks up.

---

## Phase 6 — Tests

For HaversineCalculator, test a few known coordinate pairs with expected distances, verify it's symmetric (A to B equals B to A), and that same-point returns zero.

For PromptSanitizer, test that known injection strings get stripped, that a clean input comes through unchanged, and that an empty string doesn't blow up.

For PersonsServiceImpl, mock the repository, AI service, and sanitizer. Verify the sanitizer is called before generateBio, that the returned bio ends up on the persisted person, and that getById throws PersonNotFoundException for an unknown id.

For LocationsServiceImpl, mock the repository with a fixed set of locations. Verify only locations within the radius come back, they're sorted by distance, and a location exactly on the boundary is included.

For the OpenAI service, use a WireMock or MockRestServiceServer stub rather than hitting the real API. Verify the response gets parsed into a non-null non-blank string, that it's under 280 characters, and that the HTTP request was formed correctly (right URL, auth header, model field).

For controller integration tests use @WebMvcTest. Cover the happy path for each endpoint, missing required fields returning 400, unknown person id returning 404 on the PUT, and missing query params returning 400 on the GET.
