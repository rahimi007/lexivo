package com.example.lexicon.domain

import com.example.lexicon.data.WordEntity

object VocabularyParser {
    fun parse(text: String): WordEntity? {
        val word = extractSection(text, "[001:WORD]")?.trim() ?: return null
        if (word.isEmpty()) return null

        val quizSection = extractSection(text, "[017:QUIZ]")
        var question: String? = null
        var optionA: String? = null
        var optionB: String? = null
        var optionC: String? = null
        var optionD: String? = null
        var correct: String? = null
        
        if (quizSection != null) {
            question = extractQuizPart(quizSection, "QUESTION:")
            optionA = extractQuizPart(quizSection, "OPTION_A:")
            optionB = extractQuizPart(quizSection, "OPTION_B:")
            optionC = extractQuizPart(quizSection, "OPTION_C:")
            optionD = extractQuizPart(quizSection, "OPTION_D:")
            correct = extractQuizPart(quizSection, "CORRECT:")
        }

        return WordEntity(
            word = word,
            pronunciation = extractSection(text, "[002:PRONUNCIATION]")?.trim(),
            partOfSpeech = extractSection(text, "[003:PART_OF_SPEECH]")?.trim(),
            meaning = extractSection(text, "[004:MEANING]")?.trim(),
            commonness = extractSection(text, "[005:COMMONNESS]")?.trim()?.toIntOrNull()?.coerceIn(1, 5) ?: 1,
            translations = extractSection(text, "[006:TRANSLATIONS]")?.trim(),
            examples = extractSection(text, "[007:EXAMPLES]")?.trim(),
            usage = extractSection(text, "[008:USAGE]")?.trim(),
            collocations = extractSection(text, "[009:COLLOCATIONS]")?.trim(),
            relatedWords = extractSection(text, "[010:RELATED]")?.trim(),
            register = extractSection(text, "[011:REGISTER]")?.trim(),
            learnerNote = extractSection(text, "[012:LEARNER_NOTE]")?.trim(),
            context = extractSection(text, "[013:CONTEXT]")?.trim(),
            memory = extractSection(text, "[014:MEMORY]")?.trim(),
            source = extractSection(text, "[015:SOURCE]")?.trim(),
            shortDefinition = extractSection(text, "[016:SHORT_DEFINITION]")?.trim(),
            quizQuestion = question,
            quizOptionA = optionA,
            quizOptionB = optionB,
            quizOptionC = optionC,
            quizOptionD = optionD,
            quizCorrectOption = correct
        )
    }

    private fun extractSection(text: String, header: String): String? {
        val startIndex = text.indexOf(header)
        if (startIndex == -1) return null
        
        val startOfContent = startIndex + header.length
        
        val nextSectionRegex = Regex("""\[\d{3}:[A-Z_]+\]""")
        val match = nextSectionRegex.find(text, startOfContent)
        
        val endOfContent = match?.range?.first ?: text.length
        return text.substring(startOfContent, endOfContent).trim()
    }

    private fun extractQuizPart(quizText: String, partLabel: String): String? {
        val lines = quizText.lines()
        val startIndex = lines.indexOfFirst { it.trim().startsWith(partLabel) }
        if (startIndex == -1) return null
        
        val contentLines = mutableListOf<String>()
        val startLine = lines[startIndex].substringAfter(partLabel).trim()
        if (startLine.isNotEmpty()) contentLines.add(startLine)
        
        for (i in (startIndex + 1) until lines.size) {
            val line = lines[i].trim()
            if (line.endsWith(":") && line.matches(Regex("""[A-Z_]+:"""))) break
            contentLines.add(line)
        }
        
        return contentLines.joinToString("\n").trim().takeIf { it.isNotEmpty() }
    }
}
