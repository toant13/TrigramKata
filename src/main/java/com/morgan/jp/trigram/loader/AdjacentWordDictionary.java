package com.morgan.jp.trigram.loader;

import java.io.File;
import java.util.List;

public interface AdjacentWordDictionary {
	public void loadAdjWordDictionary(File input) throws Exception;
	public String getRandomKey();
	public boolean containsKey(String key);
	public boolean containsKey(List<String> key);
	public List<String> get(String key);
	public List<String> get(List<String> key);
	
	public int size();
}
