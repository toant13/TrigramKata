package com.morgan.jp.trigram.generate;


/**
 * 
 * @author toantran
 *
 * This interface establishes the necessary methods to generate some kind of text from
 */
public interface TextGenerator {
	
	
	/**
	 * Generates a piece of text
	 * @return The string the contains the piece of text
	 */
	public String generateNewText();
}
