## Overview  
This security MD explains my approach to handling GenAI calls securely in this service.

## Data sent to LLM  
I only pass the following fields to the LLM:
- jobTitle
- hobbies (list of strings)

I intentionally do NOT send:
- name
- location (lat/lon)
- any identifiers or metadata

This reduces exposure of PII and limits sensitive context being shared with a third-party model.

## Input expectations  
I assume inputs are plain user-provided data and enforce constraints on what is allowed:

- jobTitle should be a simple role (e.g. “Engineer”, “Doctor”)
- hobbies should be normal interest keywords

Neither field should contain:
- instructions or system-like text
- prompt injection attempts
- encoded or obfuscated payloads
- any PII (emails, phone numbers, addresses, etc.)

I treat all inputs strictly as data, not instructions.

## PromptSanitizer  
Before sending anything to the LLM, I pass inputs through `PromptSanitizer`, which:
- normalizes and trims input
- removes control characters and invisible Unicode
- strips known prompt formatting tokens
- detects common injection patterns and rejects high-risk inputs

This helps reduce obvious injection attempts before prompt construction.

## Prompt construction  
I build the final [System Prompt](src/main/resources/prompts/bio_system_prompt.txt) in a strict template where system instructions are fixed and user data is injected as structured fields only. I explicitly ensure user input is never interpreted as instructions.

## Why two-step approach  
I use a two-step approach because:
- Sanitization reduces malicious or malformed input early (FL instructions are detacted and ignored by prompt)
- Prompt structure ensures strict separation between instructions and data
- Detects any non english injections.

This defense-in-depth approach reduces prompt injection risk and improves safety when interacting with external LLMs.


## What I would do differently for a banking app

For a banking-grade system, I would never send raw PII (name, location, identifiers) to any external LLM. Instead, I would introduce a strict PII redaction and tokenization layer that replaces sensitive data with irreversible tokens before any AI call. All LLM interactions would go through an internal AI gateway enforcing policy checks, sanitization, and full audit logging. I would prefer self-hosted or VPC-isolated models instead of third-party APIs. User input would be heavily constrained to structured, non-identifiable features only. LLM outputs would be treated as untrusted and validated before use in business logic to ensure compliance and security.