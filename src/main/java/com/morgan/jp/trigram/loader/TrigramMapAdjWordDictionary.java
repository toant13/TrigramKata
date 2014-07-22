package com.morgan.jp.trigram.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author toantran
 * 
 *         Trigram based (Two word) key format implementation of an
 *         AdjacentWordDictionary
 * 
 */
public class TrigramMapAdjWordDictionary implements AdjacentWordDictionary {
	private static final Logger log = LoggerFactory.getLogger(TrigramMapAdjWordDictionary.class);
	
	private final static int ADJACENT_WORD_MAX = 2;
	private Map<String, List<String>> dictionary = Collections.emptyMap();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.morgan.jp.trigram.loader.AdjacentWordDictionary#loadAdjWordDictionary
	 * (java.io.File)
	 */
	public void loadAdjWordDictionary(File input) throws Exception {
		log.info("loadAdjWordDictionary method");
		String[] wordArray = loadTextInput(input);
		dictionary = new HashMap<String, List<String>>();
		for (int i = 0; i < wordArray.length; i++) {
			if ((i + ADJACENT_WORD_MAX) < wordArray.length) {
				StringBuilder key = new StringBuilder();
				for (int j = i; j < i + ADJACENT_WORD_MAX - 1; j++) {
					key.append(wordArray[j]);
					key.append(" ");
				}
				key.append(wordArray[i + (ADJACENT_WORD_MAX - 1)]);
				List<String> valueList = dictionary.get(key.toString());

				String value = wordArray[i + ADJACENT_WORD_MAX];
				if (valueList == null) {
					valueList = new ArrayList<String>();
					valueList.add(value);
					dictionary.put(key.toString(), valueList);
				} else {
					valueList.add(value);
				}
			} else { // no more available adjacent words
				break;
			}
		}
		log.info("Successfully loaded keys and values into dictionary");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.morgan.jp.trigram.loader.AdjacentWordDictionary#getRandomKey()
	 */
	public String getRandomKey() {
		if (dictionary.size() > 0) {
			Random random = new Random();
			List<String> keys = new ArrayList<String>(dictionary.keySet());
			return keys.get(random.nextInt(keys.size()));
		} else {
			log.error("Dictionary has not been created. Cannot get random key.");
			throw new IllegalStateException(
					"Dictionary has not been created. Cannot get random key.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.morgan.jp.trigram.loader.AdjacentWordDictionary#size()
	 */
	public int size() {
		return dictionary.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.morgan.jp.trigram.loader.AdjacentWordDictionary#containsKey(java.
	 * lang.String)
	 */
	public boolean containsKey(String key) {
		return dictionary.containsKey(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.morgan.jp.trigram.loader.AdjacentWordDictionary#containsKey(java.
	 * util.List)
	 */
	public boolean containsKey(List<String> key) {
		if (key.size() == ADJACENT_WORD_MAX) {
			String sKey = StringUtils.join(key, " ");
			return dictionary.containsKey(sKey);
		} else {
			log.error("Size of key array must equal two.");
			throw new IllegalArgumentException(
					"Size of key array must equal two.");
		}
	}

	/* (non-Javadoc)
	 * @see com.morgan.jp.trigram.loader.AdjacentWordDictionary#get(java.util.List)
	 */
	public List<String> get(List<String> key) {
		if (key.size() == ADJACENT_WORD_MAX) {
			String sKey = StringUtils.join(key, " ");
			return dictionary.get(sKey);
		} else {
			log.error("Size of key array must equal two.");
			throw new IllegalArgumentException(
					"Size of key array must equal two.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.morgan.jp.trigram.loader.AdjacentWordDictionary#get(java.lang.String)
	 */
	public List<String> get(String key) {
		return dictionary.get(key);
	}

	/**
	 * Reads a text file and puts it into a String Array.
	 * 
	 * @param input
	 *            The input text File
	 * @return The String Array form of the input file
	 * @throws Exception
	 *             Thrown when the file does not contain enough words to
	 *             generate a trigram
	 */
	private String[] loadTextInput(File input) throws Exception {
		log.info("Reading in input text file");
		BufferedReader br = new BufferedReader(new FileReader(input));

		StringBuilder sBuilder = new StringBuilder();
		String line = "";

		// Fence post case. There are one more number of lines than the number
		// of carriage returns. Get first line and append space for each
		// carriage return to the rest of
		// the lines
		if ((line = br.readLine()) != null) {
			sBuilder.append(line);
		}

		while ((line = br.readLine()) != null) {
			// Do not add blank lines
			if (line.length() == 0) {
				continue;
			} else {
				sBuilder.append(" " + line);
			}
		}
		br.close();

		String[] wordArray = StringUtils.split(sBuilder.toString());

		if (wordArray.length < ADJACENT_WORD_MAX + 1) {
			log.error("Input file does not have enough words to form at least one trigram");
			throw new IllegalArgumentException(
					"Input file does not have enough words to form at least one trigram");
		} else {
			return wordArray;
		}
	}

}
