## Overview  
I followed a spec-driven development approach for this project using `spec.md` as the source of truth. AI tools were used throughout for scaffolding, boilerplate generation, and implementation acceleration, while core architectural decisions and fixes were handled manually.

---

## Phase 1 — Database Setup

- AI generated initial PostgreSQL Docker setup and Spring Boot configuration.
- AI suggested schema for `persons` and `locations` tables.

Fixes:
- Corrected table naming from plural (`persons`, `locations`) to singular (`person`, `location`) for better domain consistency and cleaner JPA entity mapping alignment.
- Fixed nullability issues where AI defaulted all columns as nullable; enforced NOT NULL constraints for core domain fields (name, jobTitle, latitude, longitude) to ensure data integrity.
- Added missing composite index on (latitude, longitude) required for efficient bounding-box queries in nearby search, avoiding full table scans at scale.

---

## Phase 2 — Domain Model

- AI generated initial JPA entities, repositories, and port interfaces.
- AI partially aligned with domain structure but missed proper separation of domain concerns.

Fixes:
- Added missing `PersonNotFoundException` manually after AI missed clear domain exception handling.
- Added missing `PromptInjectionException` manually after AI missed clear domain exception handling
- Refined repository contract for bounding-box queries to properly support spatial lookup requirements in nearby search.

---

## Phase 3 — Infrastructure

- AI provided a basic PromptSanitizer using simple regex-based filtering.
- AI generated a minimal one-line prompt structure for LLM calls.
- AI assisted in OpenAI integration via RestTemplate.

Fixes:
- Enhanced PromptSanitizer significantly with broader injection detection patterns and improved handling of real-world prompt injection edge cases.
- Redesigned prompt structure from a simple one-liner into a strict system + data separation format, explicitly enforcing instruction/data boundaries.
- Rewrote delimiter handling regex logic after edge cases were discovered where structured prompt tokens were not properly neutralized.

---

## Phase 4 — Services

- AI generated initial service layer with mixed responsibilities between person and location logic.

Fixes:
- Separated concerns by decoupling person management and location handling, improving service clarity and maintainability.
- Extracted Haversine distance calculation into a dedicated utility class to improve testability and avoid embedding mathematical logic inside service flow.
- Introduced a global exception handler using `@ControllerAdvice` for consistent error handling across the API.
- Standardized error responses by mapping exceptions into a consistent API format suitable for backend-for-frontend (BFF) usage, instead of exposing raw application errors.

---

## Summary

AI was used effectively for scaffolding and accelerating initial development, but multiple areas required manual correction, particularly around:
- domain modeling clarity
- prompt injection safety
- service separation of concerns
- database integrity constraints
- production-grade error handling
