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

public class TrigramMapAdjWordDictionary implements AdjacentWordDictionary {
	private final static int ADJACENT_WORD_MAX = 2;
	private Map<String, List<String>> dictionary = Collections.emptyMap();
	

	public void loadAdjWordDictionary(File input) throws Exception {
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
				List<String> valueList = dictionary.get(key);

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
	}

	public String getRandomKey() {
		if (dictionary.size() > 0) {
			Random random = new Random();
			List<String> keys = new ArrayList<String>(dictionary.keySet());
			return keys.get(random.nextInt(keys.size()));
		} else {
			throw new IllegalStateException("Dictionary has not been created. Cannot get random key.");
		}
	}

	public int size() {
		return dictionary.size();
	}

	public boolean containsKey(String key) {
		return dictionary.containsKey(key);
	}

	public boolean containsKey(List<String> key) {
		if (key.size() == ADJACENT_WORD_MAX) {
			StringBuilder sbKey = new StringBuilder();
			for (int i = 0; i < ADJACENT_WORD_MAX - 1; i++) {
				sbKey.append(key.get(i));
				sbKey.append(" ");
			}
			sbKey.append(ADJACENT_WORD_MAX - 1);
			return dictionary.containsKey(sbKey.toString());
		} else {
			throw new IllegalArgumentException("Size of key array must equal two.");
		}
	}
	
	public List<String> get(List<String> key) {
		if (key.size() == ADJACENT_WORD_MAX) {
			StringBuilder sbKey = new StringBuilder();
			for (int i = 0; i < ADJACENT_WORD_MAX - 1; i++) {
				sbKey.append(key.get(i));
				sbKey.append(" ");
			}
			sbKey.append(ADJACENT_WORD_MAX - 1);
			return dictionary.get(sbKey.toString());
		} else {
			throw new IllegalArgumentException("Size of key array must equal two.");
		}
	}

	public List<String> get(String key) {
		return dictionary.get(key);
	}

	private String[] loadTextInput(File input) throws Exception {
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
			throw new IllegalArgumentException(
					"Input file does not have enough words to form at least one trigram");
		} else {
			return wordArray;
		}
	}

}
