package fr.cyu.chroma;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {

	private int windowWidth;                                                // window information needed to convert %age
	private int windowHeight;

	private final Map<String, String[]> keywords = new HashMap<>() {{		// create a map, with a regex expression to find keywords and isolate what is before and after, and the associated template
		put("CURSOR\\s+", new String[]{"Cursor", "= new Cursor();"});		// separate the template in several parts, to put the inputs afterward
		put("SELECT\\s+", new String[]{"currentCursor =", ";"});			// for commands that apply to the previously selected cursor, apply the change to the currentCursor instance
		put("REMOVE\\s+", new String[]{"", " = null;"});
		put("FWD\\s+", new String[]{"currentCursor.fwd(", ");"});
		put("BWD\\s+", new String[]{"currentCursor.bwd(", ");"});
		put("TURN\\s+", new String[]{"currentCursor.turn(", ");"});
		put("MOV\\s+", new String[]{"currentCursor.mov(", ");"});
		put("POS\\s+", new String[]{"currentCursor.setPosition(", ");"});
		put("HIDE\\s+", new String[]{"currentCursor.hide();",""});
		put("SHOW\\s+", new String[]{"currentCursor.show();",""});
		put("PRESS\\s+", new String[]{"currentCursor.setOpacity(",");"});
		put("THICK\\s+", new String[]{"currentCursor.setThickness(",");"});
		put("COLOR\\s+", new String[]{"currentCursor.setColor(", ");"});
		put("LOOKAT\\s+", new String[]{"currentCursor.setOrientation(",");"});
		put("NUM\\s+", new String[]{"double ",";"});
		put("STR\\s+", new String[]{"String ",";"});
		put("BOOL\\s+", new String[]{"boolean ",";"});
		put("DEL\\s+", new String[]{""," = null;"});
		put("IF\\s+", new String[]{"if(", "){"});
		put("WHILE\\s+", new String[]{"while(", "){"});
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+FROM\\s+(-?\\w+)\\s+TO\\s+(-?\\w+)\\s+STEP\\s+(-?\\w+)", new String[]{});
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+FROM\\s+(-?\\w+)\\s+TO\\s+(-?\\w+)", new String[]{});
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+TO\\s+(-?\\w+)\\s+STEP\\s+(-?\\w+)", new String[]{});
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+TO\\s+(-?\\w+)", new String[]{});

	}};



	public Interpreter(int windowWidth, int windowHeight) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}

	/**
	 * This function saves the size of the window, and affects the calculation of parameters in %age
	 *
	 * @param windowWidth width of the window containing the drawing
	 * @param windowHeight height of the window containing the drawing
	 */
	public void setSize(int windowWidth, int windowHeight) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}


	/**
	 *  This function translates cyCode into javaCode
	 *
	 * @param cyCode pseudocode written by the user
	 * @return String java code equivalent of cyCode
	 */
	public String decode(String cyCode) {
		String indentation = "\t\t";
		String javaCode = indentation + " Cursor currentCursor;";			// currentCursor must be declared because the user won't do it
		cyCode = cyCode.replaceAll("\\{", "");				// remove all the { (because it's easier to remove it and place it only where necessary than check if and where the user placed it)
		cyCode = cyCode.replaceAll("\\}", " }\n");			// skip line after } to prevent have code on the same line after a }
		List<String> cyLines = List.of(cyCode.split("\\r?\\n"));		// separate in a list of lines

		for (String cyLine : cyLines) {
			String newCyLine = "";
			String newJavaLine = indentation;
			boolean patternFound = false;
			boolean containsEnd = false;
			int indexComment = cyLine.indexOf("//");						// look for comment in the code and get everything but it, so it doesn't affect the java code
			if (indexComment != -1) {
				newCyLine = cyLine.substring(0, indexComment);
			} else {
				newCyLine = cyLine;
			}



			for (String key : keywords.keySet()) {
				Pattern pattern = Pattern.compile(key);
				Matcher matcher = pattern.matcher(newCyLine);				// for each keyword, check if one match the line
				if (matcher.find()) {

					if (newCyLine.contains("}")){							// check if the cyCode contains a }, and if so remove it, to replace it later at the right place
						newCyLine = newCyLine.replaceAll("}", "");
						containsEnd = true;
					}
					if (!key.contains("FOR")) {                             // the FOR syntax is peculiar and must be handled separately

						String[] inputs = newCyLine.split(matcher.group(0)); // if it matches, get the parts before and after the keyword
						int i = 0;
						String[] template = keywords.get(key);

						for (String input : inputs) {                       // for each part, put it in the equivalent java line, with the template
							input = convert(key, i, input);                 // convert percentage to px, and hexa color to rgb
							newJavaLine += input + " " + template[i] + " ";
							i++;
						}

						patternFound = true;

						break;
					}else{                                                  // the FOR command has several cases, and thus each of them must be able to be constructed
						int counter = matcher.groupCount();
						if (counter == 4){                                  // if the FOR command is the one with TO FROM and STEP
							newJavaLine = "for(int " + matcher.group(1) + "=" + matcher.group(2) + "; " + matcher.group(1) + "<=" + matcher.group(3) + "; " + matcher.group(1) + "+=" + matcher.group(4) +"){";
						}else{
							if (counter == 3 && key.contains("STEP")){      // if the FOR command is the one with TO and STEP
								newJavaLine = "for(int " + matcher.group(1) + "=0; " + matcher.group(1) + "<=" + matcher.group(2) + "; " + matcher.group(1) + "+=" + matcher.group(3) +"){";
							}else{
								if (counter == 3 && key.contains("FROM") && !newCyLine.contains("STEP")){ // if the FOR command is the one with TO and FROM
									newJavaLine = "for(int " + matcher.group(1) + "=" + matcher.group(2) + "; " + matcher.group(1) + "<=" + matcher.group(3) + "; " + matcher.group(1) + "++){";
								}else{
									if (counter == 2 && !newCyLine.contains("STEP")){ // if the FOR command is the one with just TO
										newJavaLine = "for(int " + matcher.group(1) + "=0; " + matcher.group(1) + "<=" + matcher.group(2) + "; " + matcher.group(1) + "++){";
									}
								}
							}
						}

						String[] inputs = newCyLine.split(matcher.group(0)); // add what was before and after the key
						if(inputs.length == 2){
							newJavaLine = indentation + inputs[0] + " " + newJavaLine + " " + inputs[1];
						}else{
							newJavaLine = indentation + " " + newJavaLine;
						}


						patternFound = true;

					}

				}else{
					if (newCyLine.contains("}")){							// if no matches, then check the } for end of loops
						containsEnd = true;
					}
				}
			}

			if (containsEnd){												// if the cyCode lines contained a }, then place it at the end of the java code
				if (patternFound){
					newJavaLine = newJavaLine + "\n" + indentation + " }";
				}else{
					newJavaLine = indentation + " " + newCyLine;						//  if the line contains a } and no pattern, just copy it, and do not add a ;
				}
			}else{
				if(!patternFound && !newCyLine.matches("[ \t]*")){	// if no pattern are found and the line contains other things that space or tabulation
					newJavaLine = indentation + " " + newCyLine + ";";
				}
			}

			javaCode += "\n" + newJavaLine;									// add the new line to the java code
		}

		return javaCode;
	}




	/**
	 *  This function return the absolute equivalent of the parameter in %age, depending on the set window size
	 *
	 * @param percentage value in %age
	 * @return absolute value of percentage
	 */
	private int getValue(int percentage){
		int fullSize = Math.max(this.windowHeight, this.windowWidth);                   // convert a percentage into its value depending on the window size
		return (fullSize/100) * percentage;
	}





	/**
	 *  This function converts content of a given input
	 *
	 * @param function cyCode function in which is the input
	 * @param i number of the current input of the function
	 * @param input parameter of the function
	 * @return the input with the parameter(s) converted from %age if needed, the undefined variable set to 0/empty_String/false, the parameter as a String for the COLOR function if it's a hexadecimal code
	 */
	private String convert(String function, int i, String input){

		if (i == 1 && !input.contains("=")){
			if (function.contains("NUM")){                                    // if a float wasn't initialized, put it to 0
				input = input + " = 0";
			}
			if (function.contains("STR")){                                    // if a String wasn't initialized, put it to ""
				input = input + " = 0";
			}
			if (function.contains("BOOL")){                                   // if a boolean wasn't initialized, put it to false
				input = input + " = 0";
			}
		}

		if (input.contains("%")){                                           // to convert the percentage in absolute value
			if (!function.contains("PRESS")){
				String pattern = "(\\d+)\\s*%";                             // make a pattern of a number with a %
				Pattern regex = Pattern.compile(pattern);
				Matcher matcher = regex.matcher(input);                     // look for the pattern in the string

				while (matcher.find()) {
					String stringNbr = matcher.group(1);
					int result = getValue(Integer.parseInt(stringNbr));     // convert the String in number and get its corresponding value depending on window  size
					input = matcher.replaceFirst(String.valueOf(result));   // replace it in the string
					matcher = regex.matcher(input);                         // look fot another
				}
			}else {                                                         // if the command is PRESS, then the percentage does not depend on the window size
				String pattern = "(\\d+)\\s*%";
				Pattern regex = Pattern.compile(pattern);
				Matcher matcher = regex.matcher(input);

				if (matcher.find()) {
					String stringNbr = matcher.group(1);
					float result = (float)Integer.parseInt(stringNbr)/100;  // convert the String in number
					input = matcher.replaceFirst(String.valueOf(result));   // replace it in the string
				}
			}
		}

		if (input.contains("#") && function.contains("COLOR")) {             // if the color command have a hexadecimal parameter, convert it to rgb
			input = "\"" + input + "\"";
		}

		return input;
	}
}
