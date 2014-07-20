package com.morgan.jp.trigram.store;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.morgan.jp.trigram.loader.TrigramMapAdjWordDictionary;

/**
 * @author toantran
 * 
 *         This class tests the TrigramMapAdjWordDictionary class
 * 
 */
public class TrigramMapAdjWordDictionaryTest {

	/**
	 * Tests loadAdjWordDictionary with a single three word input file. Check if
	 * dictionary is created
	 * 
	 * @throws Exception
	 *             If there is a file loading error
	 */
	@Test
	public void loadAdjWordDictionary_ThreeWordInputFile_DictionaryCreated()
			throws Exception {
		URL inputURL = getClass().getResource(
				File.separator + "three_word_input.txt");

		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);

		assertEquals("Dictionary should have been created with one input", 1,
				trigramDictionary.size());
	}

	/**
	 * Tests loadAdjWordDictionary method with two word input file. The file
	 * does not have enough words and an exception should be thrown
	 * 
	 * @throws Exception
	 *             Exception throw for the input file word amount
	 */
	@Test(expected = IllegalArgumentException.class)
	public void loadAdjWordDictionary_TwoWordInputFile_IllegalArgumentException()
			throws Exception {
		URL inputURL = getClass().getResource(
				File.separator + "two_word_input.txt");
		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);
	}

	/**
	 * Tests loadAdjWordDictionary method with incorrect file path.
	 * FileNotFoundException should be thrown
	 * 
	 * @throws Exception
	 *             File not found
	 */
	@Test(expected = FileNotFoundException.class)
	public void loadAdjWordDictionary_IncorrectFilePath_FileNotFoundException()
			throws Exception {
		File inputFile = new File("");
		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);
	}

	/**
	 * Tests getRandomKey method when dictionary is not loaded. Illegal
	 * exception should be thrown because no key can be retrieved
	 */
	@Test(expected = IllegalStateException.class)
	public void getRandomKey_DictionaryNotLoaded_IllegalStateException() {
		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.getRandomKey();
	}

	/**
	 * 
	 * Tests getRandomKey method with a three word input. Only one key value
	 * pair is possible and should be returned
	 * 
	 * @throws Exception
	 *             If file doesn't exist
	 */
	@Test
	public void getRandomKey_ThreeWordInputFile_Null() throws Exception {
		URL inputURL = getClass().getResource(
				File.separator + "three_word_input.txt");
		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);
		assertEquals(
				"Returned random key incorrect, only 'I wish' should be returned",
				"I wish", trigramDictionary.getRandomKey());
	}

	/**
	 * Test size method with three word input file. Only one key value pair is
	 * possible.
	 * 
	 * @throws Exception
	 *             File not loaded
	 */
	@Test
	public void size_ThreeWordInputFile_OneSizedList() throws Exception {
		URL inputURL = getClass().getResource(
				File.separator + "three_word_input.txt");
		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);
		assertEquals(
				"Test failed. Dictionary should contain exactly one key value pair",
				1, trigramDictionary.size());
	}

	/**
	 * Test get method with string input. Only one key value pair is possible.
	 * 
	 * @throws Exception
	 *             File not loaded
	 */
	@Test
	public void get_StringKey_OneSizedList() throws Exception {
		List<String> expected = new ArrayList<String>();
		expected.add("I");

		URL inputURL = getClass().getResource(
				File.separator + "three_word_input.txt");
		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);

		List<String> actual = trigramDictionary.get("I wish");
		assertEquals("Test failed. Dictionary should only return one value",
				expected, actual);
	}

	/**
	 * Test get method with string array input. Only one key value pair is
	 * possible.
	 * 
	 * @throws Exception
	 *             File not loaded
	 */
	@Test
	public void get_StringArrayKey_OneSizedList() throws Exception {
		List<String> expected = new ArrayList<String>();
		expected.add("I");

		URL inputURL = getClass().getResource(
				File.separator + "three_word_input.txt");
		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);

		List<String> input = new ArrayList<String>();
		input.add("I");
		input.add("wish");

		List<String> actual = trigramDictionary.get(input);
		assertEquals("Test failed. Dictionary should only return one value",
				expected, actual);
	}

	/**
	 * Test containsKey method with string array input. True should be result
	 * 
	 * @throws Exception
	 *             File not loaded
	 */
	@Test
	public void containsKey_StringArrayKey_True() throws Exception {
		URL inputURL = getClass().getResource(
				File.separator + "three_word_input.txt");
		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);

		List<String> input = new ArrayList<String>();
		input.add("I");
		input.add("wish");

		assertEquals("Test failed. Should only return true",
				true, trigramDictionary.containsKey(input));
	}
	
	/**
	 * Test containsKey method with string input. True should be result
	 * 
	 * @throws Exception
	 *             File not loaded
	 */
	@Test
	public void containsKey_StringKey_True() throws Exception {
		URL inputURL = getClass().getResource(
				File.separator + "three_word_input.txt");
		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);


		assertEquals("Test failed. Should return true",
				true, trigramDictionary.containsKey("I wish"));
	}

}
