package com.persons.finder.infrastructure.sanitization

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PromptSanitizerTest {

    private val sanitizer = PromptSanitizer()

    @Test
    fun `clean input passes through unchanged`() {
        val input = "Software Engineer who loves hiking and cooking"
        assertEquals(input, sanitizer.sanitize(input))
    }

    @Test
    fun `collapses multiple spaces into one`() {
        assertEquals("hello world", sanitizer.sanitize("hello   world"))
    }

    @Test
    fun `collapses tabs and newlines into single space`() {
        assertEquals("hello world", sanitizer.sanitize("hello\t\nworld"))
    }

    @Test
    fun `strips non-printable control characters`() {
        val result = sanitizer.sanitize("hello\u0000world\u001F")
        assertEquals("helloworld", result)
    }

    @Test
    fun `strips unicode format characters`() {
        val result = sanitizer.sanitize("hello\u200Bworld")
        assertEquals("helloworld", result)
    }

    @Test
    fun `trims leading and trailing whitespace`() {
        assertEquals("hello", sanitizer.sanitize("  hello  "))
    }

    @Test
    fun `empty string returns empty string`() {
        assertEquals("", sanitizer.sanitize(""))
    }
}
