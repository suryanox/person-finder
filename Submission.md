# Submission

## Overview
This project is a location-based person finder system built using **Domain Driven Design (DDD)** and a **spec-driven development approach**.

The system allows:
- Creating persons with location data
- Updating user location
- Finding nearby users within a radius
- Generating AI-powered bios using an OpenAI-compatible chat API

The implementation strictly follows:
- `spec.md` for spec-driven development approac
- `ai_log.md` for genAI assisted development logs
- `security.md` for input validation, sanitization, and prompt safety controls

Architecture is layered:
- Domain layer (business logic)
- Application/service layer (use cases)
- Infrastructure layer (AI + persistence integration)
- Presentation layer (REST APIs)

## How to Run

```bash
export OPENAI_BASE_URL=http://localhost:11434/api
export OPENAI_API_KEY=dummy
```

OR

set secrets in [Resource](src/main/resources/application.properties)

```bash
make start
```

