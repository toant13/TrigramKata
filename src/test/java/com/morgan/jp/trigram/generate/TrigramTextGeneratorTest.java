package com.morgan.jp.trigram.generate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * Test quoteCheck method with a string that has an closing quote attached.
	 * Expected return is the same as the input, but with quote removed.
	 */
	@Test
	public void quoteCheck_QuoteLastCharString_InputWithNoQuote() {
		String input = "hello\"";
		String expected = "hello";

		assertEquals(
				"The output for this case should be the same as the input",
				expected, generator.quoteCheck(input));
	}

	/**
	 * Loads an input file with "the punctuation. the punctuation special".
	 * Since the key is has a sentence ending punctuation the return should be
	 * capitalized. This method in test should remove the punctuation and use
	 * that as a key (ie "This key." is equal to "this key") with the two words
	 * 
	 * @throws Exception
	 *             Input file not found
	 */
	@Test
	public void getWord_EndPunctuationInput_EitherOfValuesForKey()
			throws Exception {
		URL inputURL = getClass().getResource("/endpunctuation_input.txt");
		String inputSearchkey = "the punctuation.";
		List<String> inputSearchList = new ArrayList<String>();
		inputSearchList.add("the");
		inputSearchList.add("punctuation.");

		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary endPuncDictionary = new TrigramMapAdjWordDictionary();
		endPuncDictionary.loadAdjWordDictionary(inputFile);

		TrigramTextGenerator gen = new TrigramTextGenerator(endPuncDictionary);

		String actual = gen.getWord(inputSearchkey, inputSearchList);
		boolean actualBool = actual.equals("The") || actual.equals("Special");

		assertTrue("Actual results should be only one of two values. Actual: "
				+ actual + " Expected: \"The\" or \"Special\"", actualBool);
	}

	/**
	 * Loads an input file with "the. Punctuation the punctuation special". This
	 * method in test should remove the punctuation and use that as a key (ie
	 * "This key." is equal to "this key") to find all possible values with the
	 * two words
	 * 
	 * @throws Exception
	 *             Input file not found
	 */
	@Test
	public void getWord_MiddlePunctuationInput_EitherOfValuesForKey()
			throws Exception {
		URL inputURL = getClass().getResource("/middlepunctuation_input.txt");
		String inputSearchkey = "the. Punctuation";
		List<String> inputSearchList = new ArrayList<String>();
		inputSearchList.add("the.");
		inputSearchList.add("Punctuation");

		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary endPuncDictionary = new TrigramMapAdjWordDictionary();
		endPuncDictionary.loadAdjWordDictionary(inputFile);

		TrigramTextGenerator gen = new TrigramTextGenerator(endPuncDictionary);

		String actual = gen.getWord(inputSearchkey, inputSearchList);
		boolean actualBool = actual.equals("the") || actual.equals("special");

		assertTrue("Actual results should be only one of two values. Actual: "
				+ actual + " Expected: \"the\" or \"special\"", actualBool);
	}

	/**
	 * Loads an input file with "This key this key special". This method should
	 * be able to find all keys for those words combinations regardless of case.
	 * (ie "This key" == "this key")
	 * 
	 * @throws Exception
	 *             Input file not found
	 */
	@Test
	public void getWord_CapitalizationInput_EitherOfValuesForKey()
			throws Exception {
		URL inputURL = getClass().getResource("/capitalization_input.txt");
		String inputSearchkey = "This key";
		List<String> inputSearchList = new ArrayList<String>();
		inputSearchList.add("This");
		inputSearchList.add("key");

		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary endPuncDictionary = new TrigramMapAdjWordDictionary();
		endPuncDictionary.loadAdjWordDictionary(inputFile);

		TrigramTextGenerator gen = new TrigramTextGenerator(endPuncDictionary);

		String actual = gen.getWord(inputSearchkey, inputSearchList);
		boolean actualBool = actual.equals("this") || actual.equals("special");

		assertTrue("Actual results should be only one of two values Actual: "
				+ actual + " Expected: \"this\" or \"special\"", actualBool);
	}

	/**
	 * Loads an input file with "this is one this is two". This method should
	 * randomly select one of the possible values from the search key
	 * 
	 * @throws Exception
	 *             Input file not found
	 */
	@Test
	public void getWord_MultiValueInput_EitherOfValuesForKey() throws Exception {
		URL inputURL = getClass().getResource("/multivalue_input.txt");
		String inputSearchkey = "this is";
		List<String> inputSearchList = new ArrayList<String>();
		inputSearchList.add("this");
		inputSearchList.add("is");

		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary endPuncDictionary = new TrigramMapAdjWordDictionary();
		endPuncDictionary.loadAdjWordDictionary(inputFile);

		TrigramTextGenerator gen = new TrigramTextGenerator(endPuncDictionary);

		String actual = gen.getWord(inputSearchkey, inputSearchList);
		boolean actualBool = actual.equals("one") || actual.equals("two");

		assertTrue("Actual results should be only one of two values Actual: "
				+ actual + " Expected: \"one\" or \"two\"", actualBool);
	}
}
