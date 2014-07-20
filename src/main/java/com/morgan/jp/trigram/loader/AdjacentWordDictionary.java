package com.morgan.jp.trigram.loader;

import java.io.File;
import java.util.List;

/**
 * @author toantran
 * 
 * 
 *         Interfaces that defines methods required to build an adjacent word
 *         based dictionary. Adjacents words are used as a key (ie. "this key"
 *         or "this is a key")
 * 
 */
public interface AdjacentWordDictionary {

	/**
	 * Loads a text file into adjacent word dictionary
	 * 
	 * @param input
	 *            Text File input
	 * @throws Exception
	 *             When the file loading fails either from giving a incorrect
	 *             file or otherwise corrupted file
	 */
	public void loadAdjWordDictionary(File input) throws Exception;

	/**
	 * Get a random key from available keys in the dictionary
	 * 
	 * @return
	 */
	public String getRandomKey();

	/**
	 * Checks if the dictionary contains the key using a String format
	 * 
	 * @param key
	 *            The key to check in the dictionary
	 * @return boolean of whether the key exists
	 */
	public boolean containsKey(String key);

	/**
	 * Checks if the dictionary contains the key using a String List format
	 * 
	 * @param key
	 *            The key to check in the dictionary
	 * @return boolean of whether the key exists
	 */
	public boolean containsKey(List<String> key);

	/**
	 * Gets the value associated with the String formatted key in the dictionary
	 * 
	 * @param key
	 *            The key used to lookup
	 * @return List of Strings associated with the key
	 */
	public List<String> get(String key);

	/**
	 * Gets the value associated with the String List formatted key in the
	 * dictionary
	 * 
	 * @param key
	 *            The key used to lookup
	 * @return List of Strings associated with the key
	 */
	public List<String> get(List<String> key);

	/**
	 * Get the size of the current dictionary
	 * 
	 * @return the size
	 */
	public int size();
}
