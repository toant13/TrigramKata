package com.morgan.jp.trigram.generate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.morgan.jp.trigram.loader.TrigramMapAdjWordDictionary;

/**
 * @author toantran
 * 
 *         Performs units test for TrigramTextGenerator class.
 * 
 */
public class TrigramTextGeneratorTest {
	private TrigramMapAdjWordDictionary trigramDictionary;
	private TrigramTextGenerator generator;

	/**
	 * Sets up all the necessary components needed to run TrigramTextGenerator
	 * (ie loading dictionary)
	 * 
	 * @throws Exception
	 *             When error occurs during file loading
	 */
	@Before
	public void testSetup() throws Exception {
		URL inputURL = getClass().getResource("/three_word_input.txt");
		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);

		generator = new TrigramTextGenerator(trigramDictionary);
	}

	/**
	 * Tests exception thrown by constructor when incorrect dictionary argument
	 * given
	 * 
	 * @throws IOException
	 *             Dictionary is incorrect format or null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructorTrigramTextGenerator_null_IllegalArgumentException()
			throws IOException {
		new TrigramTextGenerator(null);
	}

	/**
	 * Test generateNewText method with a one key value pair dictionary. Only
	 * three possible output should be generated. One for each sentence ending
	 * punctuation.
	 */
	@Test
	public void generateNewText_loadedDictionary_generatedText() {
		String output = generator.generateNewText();
		boolean actual = output.equals("I wish I. ")
				|| output.equals("I wish I? ") || output.equals("I wish I! ");
		assertTrue("One of outputs generated does not match what is expected",
				actual);
	}

	/**
	 * Test quoteCheck method with a string that has an opening quote attached.
	 * Expected return is the same as the input. Nothing should change since the
	 * quoteFlag is false
	 */
	@Test
	public void quoteCheck_QuoteFirstCharString_SameAsInput() {
		String input = "\"hello";
		assertEquals(
				"The output for this case should be the same as the input",
				input, generator.quoteCheck(input));
	}

	// @Test
	// public void test() throws Exception {
	// TrigramTextGenerator trigramGenerator = new TrigramTextGenerator(
	// trigramDictionary);
	//
	// String s = trigramGenerator.generateNewText();
	// System.out.println(s);
	//
	// }

}
