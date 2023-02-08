package com.amica.billing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import com.amica.billing.parse.CSVParser;
import com.amica.billing.parse.FlatParser;
import com.amica.billing.parse.QuotedCSVParser;
import com.amica.billing.parse.Parser;

import com.amica.esa.componentconfiguration.manager.ComponentConfigurationManager;
import com.amica.escm.configuration.api.Configuration;
import lombok.extern.java.Log;

/**
 * A factory for parsers that determines which type of parser to create
 * based on the extension of given filenames.
 * 
 * @author Will Provost
 */
 @Log
public class ParserFactory {

	private static Map<String,Supplier<Parser>> parsers = new HashMap<>();
	static {
		resetParsers();
	}
	
	/**
	 * Helper function to use the Java Reflection API to create a class
	 * of the requested name, and to handle any exceptions.
	 */
	public static Parser createParserWithClassName(String className) {
		if (className.equals(CSVParser.class.getName())) {
			return new CSVParser();
		} else if (className.equals(FlatParser.class.getName())) {
			return new FlatParser();
		} else if (className.equals(QuotedCSVParser.class.getName())) {
			return new QuotedCSVParser();
		}
		
		return null;
	}
	
	public static void resetParsers() {
		parsers.put("csv", CSVParser::new);
		parsers.put("flat", FlatParser::new);
		parsers.put(null, CSVParser::new);

		if (System.getProperty("env.name") != null) {
			ComponentConfigurationManager manager = ComponentConfigurationManager.getInstance();
			Configuration config = manager.getConfiguration("Billing");

			Iterator<String> keys = config.getKeys();
			while (keys.hasNext()) {
				String key = keys.next();
				if (key.startsWith("ParserFactory")) {
					if (key.equals("ParserFactory")) {
						parsers.put(null, () -> createParserWithClassName(config.getString(key)));
					} else {
						int index = key.indexOf(".");
						String extension = key.substring(index + 1);
						parsers.put(extension, () -> createParserWithClassName(config.getString(key)));
					}
				}
			}

		}
	}

	public static void addParser(String extension, Supplier<Parser> factory) {
		if (!parsers.containsKey(extension)) {
			parsers.put(extension.toLowerCase(), factory);
		} else {
			throw new IllegalArgumentException
				("There is already a parser for extension " + extension + 
					"; use replaceParser() to replace it.");
		}
	}
	
	public static void replaceParser(String extension, Supplier<Parser> factory) {
		if (parsers.containsKey(extension)) {
			parsers.put(extension.toLowerCase(), factory);
		} else {
			throw new IllegalArgumentException
				("There is no parser for extension " + extension + 
					"; use addParser() to add one.");
		}
	}
	
	public static void replaceDefaultParser(Supplier<Parser> factory) {
		parsers.put(null, factory);
	}
	
	/**
	 * Looks up the file extension to find a 
	 * <code>Supplier&lt;Parser&gt;</code>, invokes it, and returns the result. 
	 */
	public static Parser createParser(String filename) {
		if (filename != null) {
			int index = filename.indexOf(".");
			if (index  != -1 && index != filename.length() - 1) {
				String extension = filename.substring(index + 1).toLowerCase();
				if (parsers.containsKey(extension)) {
					return parsers.get(extension).get();
				}
			}
		}
		return parsers.get(null).get();
	}
}
