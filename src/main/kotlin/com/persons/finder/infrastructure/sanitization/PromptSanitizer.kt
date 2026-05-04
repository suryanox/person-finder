package com.persons.finder.infrastructure.sanitization

import org.springframework.stereotype.Component

@Component
class PromptSanitizer {

    fun sanitize(input: String): String = input
        .replace(Regex("[\\p{Cntrl}&&[^\\t\\n\\r]]"), "")
        .replace(Regex("[\\p{Cf}]"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
}
