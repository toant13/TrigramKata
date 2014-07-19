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

import com.morgan.jp.trigram.loader.AdjacentWordDictionary;

public class TrigramTextGenerator implements TextGenerator {
	private AdjacentWordDictionary adjWordDictionary;
	private List<Character> punctuationList = Collections.emptyList();
	private boolean quoteFlag = false;
	private Properties prop = new Properties();

	public TrigramTextGenerator(AdjacentWordDictionary adjWordDictionary)
			throws IOException {
		prop.load(getClass().getResourceAsStream(
				File.separator + "trigram.properties"));

		this.adjWordDictionary = adjWordDictionary;
		punctuationList = new ArrayList<Character>();
		punctuationList.add('?');
		punctuationList.add('.');
		punctuationList.add('!');
	}

	public int getRandomParagraphSize() {
		Random random = new Random();
		return random.nextInt(Integer.parseInt(prop
				.getProperty("paragraph_range"))) + 2;
	}

	public String generateNewText(int maxLineLength) {
		int lineCnt = 1;
		int paragraphSize = getRandomParagraphSize();

		StringBuilder outputText = new StringBuilder();
		List<String> ouputList = new LinkedList<String>();

		/********** Fence post case for initial text input ************/

		String searchKey = adjWordDictionary.getRandomKey();
		
		//First word must contain no puntuations
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

		outputText.append(quoteCheck(outputWord));
		outputText.append(" ");
		outputText.append(quoteCheck(lastKey.get(1)));
		int wrapCnt = outputText.length();

		int sentenceCnt = outputText.toString().length()
				- outputText.toString().replace(".", "").length();
		/***************************************************************/

		while (adjWordDictionary.containsKey(searchKey)
				&& lineCnt < maxLineLength) {
			String keyWord = getWord(searchKey, lastKey);
			outputWord = quoteCheck(keyWord);

//			wrapCnt += outputWord.length();
//			
//			if (wrapCnt >= Integer
//					.parseInt(prop.getProperty("wordwrap_length"))) {
//				outputText.append("\n");
//				wrapCnt = 0;
//				lineCnt++;
//			} else {
//				outputText.append(" ");
//			}
//			
//			if (outputWord.contains(".")) {
//				sentenceCnt++;
//			} 
//			
//			if (sentenceCnt >= paragraphSize) {
//				outputText.append("\n\n");
//				wrapCnt = 0;
//				sentenceCnt = 0;
//				paragraphSize = getRandomParagraphSize();
//			}
			outputText.append(" ");
			outputText.append(outputWord);
			lastKey.remove(0);
			lastKey.add(keyWord);

			searchKey = lastKey.get(0) + " " + lastKey.get(1);

		}

		char lastChar = outputText.toString().charAt(
				outputText.toString().length() - 1);

		if (!punctuationList.contains(lastChar)) {
			outputText.append(getRandomPunctuation());
		}
		if (lastChar != '"' && quoteFlag) {
			outputText.append("\"");
		}
		return outputText.toString();

	}

	private Character getRandomPunctuation() {
		if (punctuationList.size() > 0) {
			Random random = new Random();
			return punctuationList.get(random.nextInt(punctuationList.size()));
		} else {
			throw new IllegalStateException(
					"Punctuation list was never created");
		}
	}

	private String quoteCheck(String word) {
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

	private String getWord(String searchKey, List<String> keySplit) {

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

			return getWordFromSpecialCaseKey(searchKey, keySplit.get(1),
					specialCaseList, regularCaseList);

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

	private String getWordFromSpecialCaseKey(String searchKey,
			String nextKeyStart, List<String> specialCaseList,
			List<String> regularCaseList) {
		if (regularCaseList != null) {

			// consolidate all available words two one list
			for (String s : regularCaseList) {
				specialCaseList.add(s);
			}

			String newWord = getRandomString(specialCaseList);

			List<String> nextKey = new ArrayList<String>();
			nextKey.add(nextKeyStart);
			nextKey.add(newWord);

			// Make sure the new key doesn't terminate the program.
			if (adjWordDictionary.containsKey(nextKey)) {
				return newWord;
			} else {
				return getRandomString(adjWordDictionary.get(searchKey));
			}
		} else {
			return getRandomString(adjWordDictionary.get(searchKey));
		}
	}

	private String getRandomString(List<String> list) {
		if (list.size() > 0) {
			Random random = new Random();
			return list.get(random.nextInt(list.size()));
		} else {
			throw new IllegalArgumentException(
					"List to find random string was empty");
		}
	}

}
