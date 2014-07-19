package com.morgan.jp.trigram.store;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import org.junit.Test;

import com.morgan.jp.trigram.loader.TrigramMapAdjWordDictionary;

public class TrigramMapAdjWordDictionaryTest {


	
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

	@Test(expected = FileNotFoundException.class)
	public void loadAdjWordDictionary_IncorrectFilePath_FileNotFoundException()
			throws Exception {
		File inputFile = new File("");
		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);
	}

	@Test(expected = IllegalStateException.class)
	public void getRandomKey_DictionaryNotLoaded_IllegalStateException() {
		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.getRandomKey();
	}

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
	
	//TODO: random punc tests

}
