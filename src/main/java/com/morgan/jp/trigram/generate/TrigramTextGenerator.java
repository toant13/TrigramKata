package com.morgan.jp.trigram.generate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.morgan.jp.trigram.loader.AdjacentWordDictionary;
import com.morgan.jp.trigram.loader.TrigramMapAdjWordDictionary;

/**
 * @author toantran
 * 
 *         Generates new text from given adjacent word dictionary.
 * 
 */
public class TrigramTextGenerator implements TextGenerator {
	private static final Logger log = LoggerFactory
			.getLogger(TrigramMapAdjWordDictionary.class);
	private Properties prop = new Properties();

	// End sentence punctuation list
	private List<Character> punctuationList = Collections.emptyList();
	private AdjacentWordDictionary adjWordDictionary;
	private boolean quoteFlag = false;

	/**
	 * Constructs object and assigns dictionary to generate text from
	 * 
	 * @param adjWordDictionary
	 *            The dictionary to generate text
	 * @throws IOException
	 *             Error occurred loading properties file
	 */
	public TrigramTextGenerator(AdjacentWordDictionary adjWordDictionary)
			throws IOException {

		if(adjWordDictionary == null || adjWordDictionary.size() ==0){
			log.error("The given dictionary is null or is empty");
			throw new IllegalArgumentException("The given dictionary is null or is empty");
		}else{
			this.adjWordDictionary = adjWordDictionary;
		}

		prop.load(getClass().getResourceAsStream("/trigram.properties"));

		punctuationList = new ArrayList<Character>();
		punctuationList.add('?');
		punctuationList.add('.');
		punctuationList.add('!');
	}

	/**
	 * Generates a random paragraph size from a specific range for number
	 * sentences. For example, if a range of paragraphs is set between three and
	 * seven sentences long. This method will return a number between three and
	 * seven.
	 * 
	 * @return integer of random paragraph size
	 */
	private int getRandomParagraphSize() {
		log.debug("getRandomParagraphSize method");
		Random random = new Random();
		return random.nextInt(Integer.parseInt(prop
				.getProperty("paragraph_range")))
				+ Integer.parseInt(prop.getProperty("paragraph_min"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.morgan.jp.trigram.generate.TextGenerator#generateNewText()
	 */
	public String generateNewText() {
		List<String> outputList = new LinkedList<String>();

		/********** Fence post case for initial text input ************/

		String searchKey = adjWordDictionary.getRandomKey();

		// First word must contain no puntuations
		int searchCnt = 0;
		while (StringUtils.isAlphanumeric(searchKey)
				&& searchCnt <= Integer
						.parseInt(prop.getProperty("search_max"))) {
			searchKey = adjWordDictionary.getRandomKey();
			searchCnt++;
		}

		List<String> lastKey = new ArrayList<String>();
		String[] keyStringArray = searchKey.split(" ");
		lastKey.add(keyStringArray[0]);
		lastKey.add(keyStringArray[1]);

		// Capitalize the first letter of the string since this is the start of
		// the output text. Only when character is a letter
		String outputWord = WordUtils.capitalize(lastKey.get(0));

		outputList.add(quoteCheck(outputWord));
		outputList.add(quoteCheck(lastKey.get(1)));

		/***************************************************************/

		while (adjWordDictionary.containsKey(searchKey)
				&& outputList.size() < Integer.parseInt(prop
						.getProperty("max_word_size"))) {
			String keyWord = getWord(searchKey, lastKey);

			outputWord = quoteCheck(keyWord);
			outputList.add(outputWord);

			lastKey.remove(0);
			lastKey.add(keyWord);

			searchKey = lastKey.get(0) + " " + lastKey.get(1);

		}

		// Handle case for last word in output text (ie. adding punctuations,
		// closing quotes)
		StringBuilder lastWord = new StringBuilder(outputList.get(outputList
				.size() - 1));
		char lastChar = lastWord.charAt(lastWord.length() - 1);

		if (!punctuationList.contains(lastChar)) {
			lastWord.append(getRandomPunctuation());
			outputList.remove(outputList.size() - 1);
			outputList.add(lastWord.toString());
		}
		if (lastChar != '"' && quoteFlag) {
			lastWord.append("\"");
			outputList.remove(outputList.size() - 1);
			outputList.add(lastWord.toString());
		}
		return formatOutput(outputList);

	}

	/**
	 * Format the given string list with: correct word wrapping length,
	 * paragraphs sizes, and correct new line feed.
	 * 
	 * @param outputList
	 *            List for strings to format.
	 * @return Correctly formatted string
	 */
	private String formatOutput(List<String> outputList) {
		log.debug("formatOutput method");
		String eol = System.getProperty("line.separator");
		int paragraphSize = getRandomParagraphSize();

		int wrapCnt = 0;
		int sentenceCnt = 0;
		StringBuilder sb = new StringBuilder();
		for (String s : outputList) {
			wrapCnt++;
			if (punctuationList.contains(s.charAt(s.length() - 1))
					&& s.equals(s.toLowerCase())) {
				sentenceCnt++;
			}

			sb.append(s);
			if (wrapCnt >= Integer
					.parseInt(prop.getProperty("wordwrap_length"))) {
				sb.append(eol);
				wrapCnt = 0;
			} else {
				sb.append(" ");
			}

			if (sentenceCnt >= paragraphSize) {
				sentenceCnt = 0;
				paragraphSize = getRandomParagraphSize();
				sb.append(eol);
				if (wrapCnt != 0) {
					sb.append(eol);
				}
				wrapCnt = 0;
			}

		}
		return sb.toString();
	}

	/**
	 * Return a random end of sentence punctuation.
	 * 
	 * @return character primitive of end of sentence punctuation mark
	 */
	private char getRandomPunctuation() {
		log.debug("getRandomPunctuation method");
		if (punctuationList.size() > 0) {
			Random random = new Random();
			return punctuationList.get(random.nextInt(punctuationList.size()));
		} else {
			log.error("Punctuation list was never created");
			throw new IllegalStateException(
					"Punctuation list was never created");
		}
	}

	/**
	 * Formats the given word to remove incorrect quotes according to the state
	 * of the generating output text. For example, sentences can not begin with
	 * a word that has a quote at the end of it (ie: green", word") unless there
	 * was an opening quote.
	 * 
	 * @param word
	 *            String to execute a quote check on
	 * @return Newly formatted string incorrect format
	 */
	public String quoteCheck(String word) {
		log.debug("quoteCheck method");

		if (word.contains("\"")) {
			if (word.indexOf("\"") == 0) {
				if (quoteFlag) {
					return word.substring(1, word.length());
				} else {
					quoteFlag = true;
					return word;
				}

			}
			if (word.indexOf("\"") == (word.length() - 1)) {
				if (quoteFlag) {
					quoteFlag = false;
					return word;
				} else {
					return word.substring(0, word.length() - 1);
				}
			}
		}
		return word;
	}

	/**
	 * Gets the next word to add to the output of the generated text from a
	 * dictionary search key. This method removes special cases for keys and
	 * make them equivalent to their nonpunctuation version keys.
	 * 
	 * There are three special cases for the key: case 1: Keys that end with a
	 * punctuation (ex: this example.) case 2: Keys that has a punctuation in
	 * the middle (ex: this. Example) case 3: Keys that start with capital
	 * letter (ex: This example)
	 * 
	 * 
	 * @param searchKey
	 *            The key to be search in the dictionary
	 * @param keySplit
	 *            The key in a list form
	 * @return String to be used for generated text
	 */
	public String getWord(String searchKey, List<String> keySplit) {
		log.debug("getWord method");

		// Check if search key ends with a sentence ending punctuation (ie:
		// period, question mark, exclamation mark) only when not an
		// abbreviation like Mrs., Mr., etc
		if (punctuationList.contains(keySplit.get(1).charAt(
				keySplit.get(1).length() - 1))
				&& keySplit.get(1).equals(keySplit.get(1).toLowerCase())) {

			List<String> specialCaseList = adjWordDictionary.get(searchKey);

			// return values from the key without the punctuation.
			List<String> regularCaseList = adjWordDictionary.get(searchKey
					.substring(0, searchKey.length() - 1));

			return WordUtils.capitalize(getWordFromSpecialCaseKey(searchKey,
					keySplit.get(1), specialCaseList, regularCaseList));

		}
		// check for punctuation in middle of key and that it's not an
		// abbreviation or a one word question or exclamation
		else if (keySplit.get(0).equals(keySplit.get(0).toLowerCase())
				&& punctuationList.contains(keySplit.get(0).charAt(
						keySplit.get(0).length() - 1))) {
			List<String> specialCaseList = adjWordDictionary.get(searchKey);

			List<String> keyArray = new ArrayList<String>();
			keyArray.add(keySplit.get(0).substring(0,
					keySplit.get(0).length() - 1));
			keyArray.add(keySplit.get(1).toLowerCase());
			List<String> regularCaseList = adjWordDictionary.get(keyArray);

			return getWordFromSpecialCaseKey(searchKey, keySplit.get(1),
					specialCaseList, regularCaseList);
		}
		// check for capitalized keys (ex: "We jump" is equivalent to "we jump")
		else if ((!searchKey.contains(".")) && (!searchKey.contains("!"))
				&& (!searchKey.contains("?"))
				&& (!searchKey.equals(searchKey.toLowerCase()))) {
			List<String> specialCaseList = adjWordDictionary.get(searchKey);

			List<String> regularCaseList = adjWordDictionary.get(searchKey
					.toLowerCase());

			return getWordFromSpecialCaseKey(searchKey, keySplit.get(1),
					specialCaseList, regularCaseList);
		} else {
			List<String> keyValue = adjWordDictionary.get(searchKey);
			String newWord = getRandomString(keyValue);
			return newWord;
		}
	}

	/**
	 * Combines lists from special keys (ie keys with punctuations) and
	 * non-specials keys and returns random string from the consolidated list
	 * 
	 * @param searchKey
	 *            Original unaltered search key
	 * @param nextKeyStart
	 *            First string of next key
	 * @param specialCaseList
	 *            List of string from special keys
	 * @param regularCaseList
	 *            List of strings from non-special keys
	 * @return random words form the two lists
	 */
	public String getWordFromSpecialCaseKey(String searchKey,
			String nextKeyStart, List<String> specialCaseList,
			List<String> regularCaseList) {
		log.debug("getWordFromSpecialCaseKey method");

		if (regularCaseList != null) {

			// consolidate all available words two one list
			for (String s : regularCaseList) {
				specialCaseList.add(s);
			}

			String newWord = getRandomString(specialCaseList);

			List<String> nextKey = new ArrayList<String>();
			nextKey.add(nextKeyStart);
			nextKey.add(newWord);

			// Make sure the new special case key doesn't terminate the program.
			if (adjWordDictionary.containsKey(nextKey)) {
				return newWord;
			} else {
				return getRandomString(adjWordDictionary.get(searchKey));
			}
		} else {
			return getRandomString(adjWordDictionary.get(searchKey));
		}
	}

	/**
	 * Picks random string from list and return it
	 * 
	 * @param list
	 *            List to get random strings from
	 * @return random string
	 */
	private String getRandomString(List<String> list) {
		log.debug("getRandomString method");

		if (list.size() > 0) {
			Random random = new Random();
			return list.get(random.nextInt(list.size()));
		} else {
			log.error("List to find random string was empty");
			throw new IllegalArgumentException(
					"List to find random string was empty");
		}
	}

}
