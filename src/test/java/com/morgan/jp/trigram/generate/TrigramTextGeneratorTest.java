package com.morgan.jp.trigram.generate;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.morgan.jp.trigram.loader.TrigramMapAdjWordDictionary;

public class TrigramTextGeneratorTest {

	@Test
	public void test() throws Exception {	
//		URL inputURL = getClass().getResource(
//				File.separator + "one_page_input.txt");
		
		URL inputURL = getClass().getResource(
				File.separator + "Tom_Swift.txt");


		String inputLocation = inputURL.getPath();
		File inputFile = new File(inputLocation);

		TrigramMapAdjWordDictionary trigramDictionary = new TrigramMapAdjWordDictionary();
		trigramDictionary.loadAdjWordDictionary(inputFile);
		
		TrigramTextGenerator trigramGenerator = new TrigramTextGenerator(trigramDictionary);
		
		
		
		String s = trigramGenerator.generateNewText();
		System.out.println(s);

	}

}
