package com.persons.finder.infrastructure.sanitization

import org.springframework.stereotype.Component

@Component
class PromptSanitizer(
    private val config: PromptSanitizerConfig = PromptSanitizerConfig()
) {

    fun sanitize(input: String): String {
        var result = input

        if (result.length > config.maxLength) {
            result = result.take(config.maxLength)
        }

        result = result
            .replace(Regex("[\\p{Cntrl}&&[^\\t\\n\\r]]"), "")
            .replace(Regex("[\\u200B\\u200C\\u200D\\uFEFF]"), "")

        result = neutralizeDelimiters(result)

        if (config.rejectOnInjection && containsInjectionPattern(result)) {
            throw PromptInjectionException("Potential prompt injection detected")
        }

        result = result
            .replace(Regex("[ \\t]+"), " ")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()

        return result
    }

    private fun neutralizeDelimiters(input: String): String = input
        .replace(Regex("<\\|.*?\\|>"), "")
        .replace(Regex("\\[/?INST]", RegexOption.IGNORE_CASE), "")
        .replace(Regex("<</?SYS>>", RegexOption.IGNORE_CASE), "")
        .replace(Regex("^#{1,6}\\s*(system|user|assistant)\\s*$",
            setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)), "")

    private fun containsInjectionPattern(input: String): Boolean {
        val lower = input.lowercase()
        return INJECTION_PATTERNS.any { lower.contains(it) }
    }

    companion object {
        private val INJECTION_PATTERNS = listOf(
            "ignore previous instructions",
            "ignore all instructions",
            "ignore your instructions",
            "disregard previous",
            "forget your instructions",
            "you are now",
            "your new instructions",
            "system prompt:",
            "act as if",
            "pretend you are",
            "jailbreak",
            "do anything now",
            "dan mode",
        )
    }
}
