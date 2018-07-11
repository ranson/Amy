/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package de.unistuttgart.iaas.amyassist.amy.core.natlang.nl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Lists;

/**
 * Test for NL Lexer
 * 
 * @author Felix Burk
 */
public class NLLexerTest {

	/**
	 * test normal words and numbers
	 */
	@Test
	public static Stream<List<String>> testWords() {
		return Stream.of(Arrays.asList("what", "the", "99", "this", "is", "a", "test"));
	}

	/**
	 * tests bad chars
	 */
	@ParameterizedTest
	@MethodSource("badCharacters")
	public void testBadChars(Character badCharacter) {
		NLLexer lexer = new NLLexer();
		assertThrows(NLLexerException.class, () -> lexer.tokenize(badCharacter.toString()));
	}

	public static Stream<Character> badCharacters() {
		// every ascii character except numbers of letters
		return IntStream.range(0, 128).mapToObj(i -> (char) i)
				.filter(c -> (c != 32 && c < 48) || (57 < c && c < 65) || (90 < c && c < 97) || 122 < c);
	}

	/**
	 * standard test for the simple NL Lexer class
	 * 
	 * @param input
	 */
	@ParameterizedTest
	@MethodSource("testWords")
	public void checkInput(List<String> input) {
		NLLexer lexer = new NLLexer();
		List<WordToken> tokenize = lexer.tokenize(String.join(" ", input));
		assertThat(Lists.transform(tokenize, WordToken::getContent), is(input));
	}

	/**
	 * standard test, more to come
	 */
	@Test
	public void testTypes() {
		NLLexer lex = new NLLexer();
		List<WordToken> tokenize = lex.tokenize("wajjo 9 0 oh 99");
		assertThat(Lists.transform(tokenize, WordToken::getType), contains(WordTokenType.WORD, WordTokenType.NUMBER,
				WordTokenType.NUMBER, WordTokenType.WORD, WordTokenType.NUMBER));
	}
}