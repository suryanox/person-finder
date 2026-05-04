package com.persons.finder.infrastructure.sanitization

data class PromptSanitizerConfig(
    val maxLength: Int = 100,
    val rejectOnInjection: Boolean = true
)