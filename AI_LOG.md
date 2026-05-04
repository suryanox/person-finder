## Overview

This project was built using a spec-driven development approach, with `spec.md` serving as the single source of truth. AI tools were used extensively for scaffolding, boilerplate generation, and accelerating implementation and adding unit tests. However, all architectural decisions, validations, and production-grade fixes were handled manually to ensure correctness, security, and maintainability.

---

## Phase 1 — Database Setup

### AI Contributions
- Generated initial PostgreSQL Docker configuration.
- Suggested baseline schema for `person` and `location` tables.
- Provided initial Spring Boot datasource configuration.

### Manual Fixes & Improvements
- Standardized table naming from plural (`persons`, `locations`) to singular (`person`, `location`) to align with domain modeling and JPA conventions.
- Enforced strict `NOT NULL` constraints on core fields:
  - `name`
  - `jobTitle`
  - `latitude`
  - `longitude`
  This ensured data integrity at the persistence layer.
- Introduced composite index on `(latitude, longitude)` to support efficient geo-based bounding box queries and prevent full table scans under load.

---

## Phase 2 — Domain Model

### AI Contributions
- Generated initial JPA entities and repository interfaces.
- Created basic domain structure for person and location management.

### Manual Fixes & Improvements
- Introduced missing domain exceptions:
  - `PersonNotFoundException`
  - `PromptInjectionException`
- Improved domain boundary separation between infrastructure and business logic.
- Refined repository contracts to properly support geospatial querying for nearby-person lookups.

---

## Phase 3 — Infrastructure Layer

### AI Contributions
- Implemented initial `PromptSanitizer` using regex-based filtering.
- Generated basic LLM prompt structure (single-string format).
- Assisted in OpenAI-style integration using `RestTemplate`.

### Manual Fixes & Improvements
- Hardened `PromptSanitizer` with expanded injection detection patterns covering real-world adversarial prompts.
- Replaced weak single-string prompt design with structured system/user separation to enforce instruction–data isolation.
- Improved delimiter sanitization logic to handle encoded prompt injection attempts and structured token bypasses.
- Strengthened resilience against prompt injection edge cases not covered by initial AI implementation.

---

## Phase 4 — Service Layer

### AI Contributions
- Generated initial service layer combining person and location logic.
- Provided basic implementation for nearby search and bio generation flow.

### Manual Fixes & Improvements
- Separated responsibilities into distinct services:
  - `PersonsService`
  - `LocationsService`
- Extracted Haversine distance calculation into a dedicated utility class for reuse and testability.
- Introduced global exception handling using `@ControllerAdvice`.
- Standardized API error responses for consistent BFF consumption instead of exposing raw stack traces.
- Improved logging strategy for AI-related failures to ensure traceability without leaking sensitive prompt data.
- Fixed missing failure propagation in PromptSanitizer where sanitization violations were silently ignored instead of throwing proper exceptions.
- Corrected AI-generated persistence logic where `Person` entity was incorrectly used for inserts; replaced with proper `InsertPersonRow` to avoid unintended ID handling and JPA entity misuse.


- Refactoring with small functions and proper concerns.

---

## Summary

AI was effective in accelerating initial scaffolding and reducing boilerplate effort. However, significant manual intervention was required in the following areas:

- Domain modeling correctness and consistency
- Prompt injection security hardening
- Service boundary separation
- Database schema integrity and indexing strategy
- Production-grade error handling and API consistency

Overall, AI acted as a productivity multiplier, while architectural correctness and security were enforced manually.