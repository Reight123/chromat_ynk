package fr.cyu.chroma;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {

	private int maxWindowWidth;                                             // window information needed to convert %age

	private final Map<String, String[]> keywords = new HashMap<>() {{		// create a map, with a regex expression to find keywords and isolate what is before and after, and the associated template
		put("CURSOR\\s+([a-zA-Z_]\\w*)\\s*", new String[]{});				// separate the template in several parts, to put the inputs afterward
		put("SELECT\\s+", new String[]{"currentPointer =", ";"});			// for commands that apply to the previously selected pointer, apply the change to the currentPointer instance
		put("REMOVE\\s+", new String[]{"", " = null;"});
		put("FWD\\s+", new String[]{"currentPointer.fwd(", ");"});
		put("BWD\\s+", new String[]{"currentPointer.bwd(", ");"});
		put("TURN\\s+", new String[]{"currentPointer.turnRight(", ");"});
		put("TURNR\\s+", new String[]{"currentPointer.turnRight(", ");"});
		put("TURNL\\s+", new String[]{"currentPointer.turnLeft(", ");"});
		put("MOV\\s+", new String[]{"currentPointer.move(", ");"});
		put("POS\\s+", new String[]{"currentPointer.pos(", ");"});
		put("HIDE\\s+", new String[]{"currentPointer.hide();",""});
		put("SHOW\\s+", new String[]{"currentPointer.show();",""});
		put("PRESS\\s+", new String[]{"currentPointer.setOpacity(",");"});
		put("THICK\\s+", new String[]{"currentPointer.setThickness(",");"});
		put("COLOR\\s+", new String[]{"currentPointer.setColor(", ");"});
		put("LOOKAT\\s+", new String[]{"currentPointer.lookat(",");"});
		put("NUM\\s+", new String[]{"double ",";"});
		put("INT\\s+", new String[]{"int ",";"});
		put("STR\\s+", new String[]{"String ",";"});
		put("BOOL\\s+", new String[]{"boolean ",";"});
		put("DEL\\s+", new String[]{""," = null;"});
		put("IF\\s+", new String[]{"if(", "){"});
		put("WHILE\\s+", new String[]{"while(", "){"});
		put("MIRROR\\s+", new String[]{"mirror(", "){ // temporary "});
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+FROM\\s+(-?\\w+)\\s+TO\\s+(-?\\w+)\\s+STEP\\s+(-?\\w+)", new String[]{}); // the FOR is peculiar and is handled separately, thus the empty String
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+FROM\\s+(-?\\w+)\\s+TO\\s+(-?\\w+)", new String[]{});
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+TO\\s+(-?\\w+)\\s+STEP\\s+(-?\\w+)", new String[]{});
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+TO\\s+(-?\\w+)", new String[]{});
		put("MIMIC\\s+([a-zA-Z_]\\w*)\\s*", new String[]{});
		put("MIMICEND\\s+([a-zA-Z_]\\w*)\\s*", new String[]{});

	}};



	public Interpreter(int windowWidth, int windowHeight) {
		this.maxWindowWidth = Math.max(windowHeight, windowWidth);
	}

	/**
	 * This function saves the size of the window, and affects the calculation of parameters in %age
	 *
	 * @param windowWidth width of the window containing the drawing
	 * @param windowHeight height of the window containing the drawing
	 */
	public void setSize(int windowWidth, int windowHeight) {
		this.maxWindowWidth = Math.max(windowHeight, windowWidth);
	}


	/**
	 *  This function translates cyCode into javaCode
	 *
	 * @param cyCode pseudocode written by the user
	 * @return String java code equivalent of cyCode
	 */
	public String decode(String cyCode) {
		String indentation = "\t\t";
		String javaCode = " Pointer currentPointer;";			// currentPointer must be declared because the user won't do it
		int preventSELECT = 0;
		cyCode = cyCode.replaceAll("\\{", "");				        // remove all the { (because it's easier to remove it and place it only where necessary than check if and where the user placed it)
		cyCode = cyCode.replaceAll("\\}", " }\n");		    	    // skip line after } to prevent have code on the same line after a }
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

					if (!key.contains("FOR")  && !key.startsWith("MIMIC") && !key.contains("CURSOR")) {     // the FOR and MIMIC syntax are peculiar and must be handled separately

						if (!(preventSELECT != 0 && key.contains("SELECT"))) {
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
						}else{
							System.out.println("SELECT INTERDIT");
							patternFound = true;
							break;
							// TODO exit the program and tell the user the operation is forbidden
						}

					}else if(key.contains("FOR")) {                            // the FOR command has several cases, and thus each of them must be able to be constructed
						int counter = matcher.groupCount();

						if (counter == 4) {                                  // if the FOR command is the one with TO FROM and STEP
							newJavaLine = "\t\tfor(double " + matcher.group(1) + "=" + matcher.group(2) + "; " + matcher.group(1) + "<=" + matcher.group(3) + "; " + matcher.group(1) + "+=" + matcher.group(4) + "){";
							patternFound = true;
							break;

						} else if (counter == 3 && key.contains("STEP")) {      // if the FOR command is the one with TO and STEP
							newJavaLine = "\t\tfor(double " + matcher.group(1) + "=0; " + matcher.group(1) + "<=" + matcher.group(2) + "; " + matcher.group(1) + "+=" + matcher.group(3) + "){";
							patternFound = true;
							break;

						} else if (counter == 3 && key.contains("FROM") && !newCyLine.contains("STEP")) { // if the FOR command is the one with TO and FROM
							newJavaLine = "\t\tfor(double " + matcher.group(1) + "=" + matcher.group(2) + "; " + matcher.group(1) + "<=" + matcher.group(3) + "; " + matcher.group(1) + "++){";
							patternFound = true;
							break;

						} else if (counter == 2 && !newCyLine.contains("STEP")) { // if the FOR command is the one with just TO
							newJavaLine = "\t\tfor(double " + matcher.group(1) + "=0; " + matcher.group(1) + "<=" + matcher.group(2) + "; " + matcher.group(1) + "++){";
							patternFound = true;
							break;
						}
					}else if (key.startsWith("MIMIC") && !key.contains("END")){ // if the match if for MIMIC (and isn't for MIMICEND) write the necessary things to do the mimic
						//System.out.println("'" + key + "'");
						newJavaLine = "targetStart = " + matcher.group(1) +";\n\t\t" +
								"k++;\n\t\t" +
								"oldliste.add(new ArrayList<>(liste));\n\t\t" +
								"tempPointer = new Pointer(gc);\n\t\t" +
								"tempPointer.pos(currentPointer.getPos_x(),currentPointer.getPos_y());\n\t\t" +
								"temp.add(tempPointer);\n\t\t" +
								"targetPointer = new Pointer(gc);\n\t\t" +
								"targetPointer.pos(targetStart.getPos_x(),targetStart.getPos_y());\n\t\t" +
								"target.add(targetPointer);" +
								"liste.add(temp.get(k));\n\t\t" +
								"liste.add(target.get(k));\n\t\t" +
								"for(; " + matcher.group(1) + "Index <2;"+matcher.group(1) + "Index++){\n\t\t\t" +
								"currentPointer = liste.get(liste.size()-1 -" + matcher.group(1) + "Index);\n\t\t";
						preventSELECT++;									// increment to know if code is in a mimic loop
						patternFound = true;
						break;

					}else if (key.contains("END")) {
						newJavaLine = "\n\t\t"+ matcher.group(1) +"Index = 0;\n\t\tliste=oldliste.get(oldliste.size() - 1);" +
								"\n\n\t\toldliste.remove(oldliste.size() - 1);\n";
						preventSELECT--;									// decrement to know if code is out of a mimic loop
						patternFound = true;
						break;

					}else {
						newJavaLine = "Pointer "+ matcher.group(1) +" = new Pointer(gc);\n" +
								"\t\tint " + matcher.group(1) + "Index = 0;";
						patternFound = true;
						break;
					}

						String[] inputs = newCyLine.split(matcher.group(0)); // add what was before and after the key
						if(inputs.length == 2){
							newJavaLine = indentation + inputs[0] + " " + newJavaLine + " " + inputs[1];
						}else{
							newJavaLine = indentation + " " + newJavaLine;
						}



				}else{
					if (newCyLine.contains("}")){							// if no matches, then check the } for end of loops
						containsEnd = true;
					}
				}

				if(preventSELECT < 0){
					// TODO tell the user something is wrong, as there is more ending of mimic/mirror than opening
				}

			}

			if (containsEnd){												// if the cyCode lines contained a }, then place it at the end of the java code
				if (patternFound){
					newJavaLine = newJavaLine + "\n" + indentation + " }";
				}else{
					newJavaLine = indentation + " " + newCyLine;            //  if the line contains a } and no pattern, just copy it, and do not add a ;
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
	 *  This function converts content of a given input
	 *
	 * @param function cyCode function in which is the input
	 * @param i number of the current input of the function
	 * @param input parameter of the function
	 * @return the input with the parameter(s) converted from %age if needed, the undefined variable set to 0/empty_String/false, the parameter as a String for the COLOR function if it's a hexadecimal code
	 */
	private String convert(String function, int i, String input){

		if (i == 1 && !input.contains("=")){
			if (function.contains("NUM")){                                    // if a double wasn't initialized, put it to 0
				input = input + " = 0.0";
			}
			if (function.contains("INT")){                                    // if a int wasn't initialized, put it to 0
				input = input + " = 0";
			}
			if (function.contains("STR")){                                    // if a String wasn't initialized, put it to ""
				input = input + " = \"\"";
			}
			if (function.contains("BOOL")){                                   // if a boolean wasn't initialized, put it to false
				input = input + " = false";
			}
		}

		if (input.contains("%")){                                           // to convert the percentage in absolute value
			if (!function.contains("PRESS")){
				String pattern = "(\\w+\\.?\\d*)\\s*%";                     // make a pattern of a number (int double or variableName) with a %
				Pattern regex = Pattern.compile(pattern);
				Matcher matcher = regex.matcher(input);                     // look for the pattern in the string

				while (matcher.find()) {
					String[] str = input.split(matcher.group(0), 2);    	// split the string just for the first occurrence
					if (str.length == 2){									// if the pattern is included in the input
						input = str[0] + "(int) (" + matcher.group(1) + "*(" + this.maxWindowWidth + "/100))" + str[1];
					} else {                                                 // if the input is the matcher.group(0), meaning the pattern is the whole input
						input = "(int) (" + matcher.group(1) + "*(" + this.maxWindowWidth + "/100))";
					}
					matcher = regex.matcher(input);                         // look fot another match in the input
				}
			}else {                                                         // if the command is PRESS, then the percentage does not depend on the window size, it just needs to be divided by 100
				String pattern = "(\\w+\\.?\\d*)\\s*%";
				Pattern regex = Pattern.compile(pattern);
				Matcher matcher = regex.matcher(input);

				if (matcher.find()) {
					input = input.replaceAll(matcher.group(0), "(double) (" + matcher.group(1) + "/100)");   // replace it in the string
				}
			}
		}

		if (input.contains("#") && function.contains("COLOR")) {             // if the color command have a hexadecimal parameter, put it as a String
			input = "\"" + input + "\"";
		}

		return input;
	}
}
