package com.ivy.math

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.parser.Parser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ExpressionParserTest {

    private lateinit var parser: Parser<TreeNode>

    @BeforeEach
    fun setUp() {
        parser = expressionParser()
    }

    @Test
    fun `Test expression Parser`() {
        val result = parser("9*(2+5)").firstOrNull()
        val actual = result?.value?.eval()
        assertThat(actual).isEqualTo(63.0)
    }

    /**Basic Cases
    "2 + 3 * 4" → Multiplication before Addition
    "10 / 2 + 5" → Division before Addition
    "8 - 3 + 2" → Left-to-right evaluation for Addition and Subtraction**/
    @ParameterizedTest
    @CsvSource("2+3*4, 14.0", "10/2+5, 10", "8-3+2, 7")
    fun `Test expressionParser with Basic Cases`(inputExpression: String, expectedOutput: Double) {
        val result = parser(inputExpression).first()
        val actual = result.value.eval()
        assertThat(actual).isEqualTo(expectedOutput)
    }

    /**
     * Brackets (B)
     * "(2 + 3) * 4" → Brackets take priority
     * "10 / (2 + 3)" → Brackets before Division
     * "((5 + 3) * 2) - 4" → Nested Brackets
     * **/
    @ParameterizedTest
    @CsvSource("(2+3)*4, 20", "10/(2+3), 2", "((5+3)*2)-4, 12")
    fun `Test expressionParser with Brackets (B)`(inputExpression: String, expectedOutput: Double) {
        val result = parser(inputExpression).first()
        val actual = result.value.eval()
        assertThat(actual).isEqualTo(expectedOutput)
    }

    /**
     * Division & Multiplication (DM)
     * "6 / 2 * 3" → Left-to-right evaluation
     * "10 * 2 / 5" → Left-to-right evaluation
     * "15 / (3 * 5)" → Brackets take priority
     * **/
    @ParameterizedTest
    @CsvSource("6/2*3, 1", "10*2/5, 4", "15/(3*5), 1")
    fun `Test expressionParser with Division & Multiplication (DM)`(
        inputExpression: String,
        expectedOutput: Double
    ) {
        val result = parser(inputExpression).first()
        val actual = result.value.eval()
        assertThat(actual).isEqualTo(expectedOutput)
    }

    /**
     * Addition & Subtraction (AS)
     * "20 - 5 + 3" → Left-to-right evaluation
     * "50 + 10 - 2" → Left-to-right evaluation
     * **/
    @ParameterizedTest
    @CsvSource("20-5+3, 18", "50+10-2, 58")
    fun `Test expressionParser with Addition & Subtraction (AS)`(
        inputExpression: String,
        expectedOutput: Double
    ) {
        val result = parser(inputExpression).first()
        val actual = result.value.eval()
        assertThat(actual).isEqualTo(expectedOutput)
    }

    /**
     * Mix of All BODMAS Cases
     * "10 + 2 * 3 - 4 / 2" → Multiplication & Division first, then Addition & Subtraction
     * "(8 + 2) * (6 / 3)" → Brackets first, then Multiplication & Division
     * "10 / (2 + 3) * 4" → Brackets first, then Division & Multiplication expected output is 8
     * **/
    @ParameterizedTest
    @CsvSource("10+2*3-4/2, 14", "(8+2)*(6/3), 20", "10/(2+3)*4, .5")
    fun `Test expressionParser with Mix of All BODMAS Cases`(
        inputExpression: String,
        expectedOutput: Double
    ) {
        val result = parser(inputExpression).first()
        val actual = result.value.eval()
        assertThat(actual).isEqualTo(expectedOutput)
    }

    /**
     * Edge Cases
     * "0 / 1" → Division with zero numerator
     * "1 / 0" → Division by zero (error case)
     * "((3 + 5) * 2) / (4 - 4)" → Division by zero (error case)
     * "(-3 + 5) * 2" → Negative numbers
     * "-3 * (-2)" → Negative multiplication
     * **/
    @ParameterizedTest
    @CsvSource("0/1, 0", "1/0, Infinity", "((3+5)*2)/(4-4), Infinity", "(-3+5)*2, 4", "-3*(-2), 6")
    fun `Test expressionParser with Edge Cases`(inputExpression: String, expectedOutput: Double) {
        val result = parser(inputExpression).first()
        val actual = result.value.eval()
        assertThat(actual).isEqualTo(expectedOutput)
    }

}