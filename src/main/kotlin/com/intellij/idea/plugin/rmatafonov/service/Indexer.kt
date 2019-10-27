package com.intellij.idea.plugin.rmatafonov.service

class Indexer {
    fun indexScenarios(featureLines: List<String>): List<String> {
        var i = 1

        return featureLines.map {
            return@map when {
                it.trim().matches(Regex("^Scenario: \\d+.*")) -> {
                    it.replace(Regex("Scenario: \\d+"), "Scenario: ${i++.padStart(2, '0')}")
                }
                it.trim().matches(Regex("^Scenario Outline: \\d+.*")) -> {
                    it.replace(Regex("Scenario Outline: \\d+"), "Scenario Outline: ${i++.padStart(2, '0')}")
                }
                it.trim().startsWith("Scenario:") -> {
                    it.replace("Scenario:", "Scenario: ${i++.padStart(2, '0')} -")
                }
                it.trim().startsWith("Scenario Outline:") -> {
                    it.replace("Scenario Outline:", "Scenario Outline: ${i++.padStart(2, '0')} -")
                }
                else -> it
            }
        }
    }

    private fun Int.padStart(length: Int, padChar: Char): String = "$this".padStart(length, padChar)
}