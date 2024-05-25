package fr.cyu.chroma;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {

	private int maxWindowWidth;                                             // window information needed to convert %age
	private static final Set<String> COLOR_NAMES = new HashSet<>(); 		// will contain the list of allowed color names

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
		put("MATH\\s+", new String[]{"",";"});
		put("NUM\\s+", new String[]{"double ",";"});
		put("INT\\s+", new String[]{"int ",";"});
		put("STR\\s+", new String[]{"String ",";"});
		put("BOOL\\s+", new String[]{"boolean ",";"});
		put("DEL\\s+", new String[]{"// "," = null;"}); // primitive type cannot point on null, so it cannot be "deleted" at all, even by simulating it
		put("IF\\s+", new String[]{"if(", "){"});
		put("WHILE\\s+", new String[]{"while(", "){"});
		put("MIRROR\\s+", new String[]{"mirror(", "){ // temporary "});
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+FROM\\s+(-?\\w+)\\s+TO\\s+(-?\\w+)\\s+STEP\\s+(-?\\w+)", new String[]{}); // the FOR is peculiar and is handled separately, thus the empty String
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+FROM\\s+(-?\\w+)\\s+TO\\s+(-?\\w+)", new String[]{});
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+TO\\s+(-?\\w+)\\s+STEP\\s+(-?\\w+)", new String[]{});
		put("FOR\\s+([a-zA-Z_]\\w*)\\s+TO\\s+(-?\\w+)", new String[]{});
		put("MIMIC\\s+([a-zA-Z_]\\w*)\\s*", new String[]{});
		put("MIMICEND\\s*", new String[]{});

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
	 *  This function translates the cyCode pseudo code into javaCode
	 *
	 * @param cyCode pseudocode written by the user
	 * @param ignoreError A boolean indicating whether errors should be thrown or ignored.
	 * @return String java code equivalent of cyCode
	 * @throws IllegalArgumentException If there is an illegal argument passed to the function
	 */
	public String decode(String cyCode, boolean ignoreError) throws IllegalArgumentException, IllegalStateException{
		String indentation = "\t\t";
		String javaCode = " Pointer currentPointer";						// currentPointer must be declared because the user won't do it
		if (ignoreError){javaCode+=" = null;";}else{javaCode+=";";}			// if the instruction will be surrounded by try catch, add "= null" to detect the error, if not don't add it, or it will crash
		int preventSELECT = 0;
		String removed = "";
		List<String> functionPile = new ArrayList<>();
		List<String> mimicPile = new ArrayList<>();
		Set<String> nullVariables = new HashSet<>();						// to store the "removed" variable (that point to null instead), to allow to just assign them a new value if used again, and not redeclare them (which would do an error)
		cyCode = cyCode.replaceAll("\\{", "");				        // remove all the { (because it's easier to remove it and place it only where necessary than check if and where the user placed it)
		cyCode = cyCode.replaceAll("\\}", "\n" + indentation + "}\n");		// skip line next to } to prevent having code on the same line as a }
		List<String> cyLines = List.of(cyCode.split("\\r?\\n"));			// separate in a list of lines

		for (String cyLine : cyLines) {
			String newCyLine = "";
			String newJavaLine = indentation;
			boolean doUnknownCommands = false;
			boolean patternFound = false;
			int indexComment = cyLine.indexOf("//");						// look for comment in the code and get everything but it, so it doesn't affect the java code
			if (indexComment != -1) {
				newCyLine = cyLine.substring(0, indexComment);
			} else {
				newCyLine = cyLine;
			}


			String key2 = "";
			for (String key : keywords.keySet()) {
				Pattern pattern = Pattern.compile(key);
				Matcher matcher = pattern.matcher(newCyLine);				// for each keyword, check if one match the line
				if (matcher.find()) {

					if (!key.contains("FOR")  && !key.startsWith("MIMIC") && !key.contains("CURSOR")) {     // the FOR and MIMIC syntax are peculiar and must be handled separately
						if (key.contains("WHILE")){
							functionPile.add("WHILE");
							if (ignoreError){
								newJavaLine += "try{\n" + indentation;
							}
						}
						if (key.contains("IF")){
							functionPile.add("IF");
							if (ignoreError){
								newJavaLine += "try{\n" + indentation;
							}
						}
													// loop if is for all cases except FOR MIMIC ENDMIMIC MIRROR ENDMIRROR and CURSOR
						if (!(preventSELECT != 0 && key.contains("SELECT"))) {    // if the usage of select is allowed
							String[] inputs = newCyLine.split(matcher.group(0)); // if it matches, get the parts before and after the keyword
							int i = 0;
							String[] template = keywords.get(key);

							boolean isVariable = key.contains("NUM") || key.contains("STR") || key.contains("INT") || key.contains("BOOL");
							String variableName = " ";

							if (isVariable) { 									// if the command is a variable declaration, we need the variable name to see if it is already declared or not (because the input can be '<variableName>' but also '<variableName> = 15.4')
								Pattern patternInput = Pattern.compile("\\s*([a-zA-Z_]\\w*)\\s+.+"); // get the name if the input is the name plus an assignation
								Matcher matcherInput = patternInput.matcher(inputs[1]);
								if (matcherInput.find() && matcherInput.groupCount() == 1) {
									variableName = matcherInput.group(1);
								} else {
									patternInput = Pattern.compile("\\s*([a-zA-Z_]\\w*)\\s*"); // get the name if the input is just the name
									matcherInput = patternInput.matcher(inputs[1]);
									if (matcherInput.find() && matcherInput.groupCount() == 1) {
										variableName = matcherInput.group(1);
									} else {
										variableName = inputs[1];
									}
								}
							}


							if (!isVariable || !nullVariables.contains(variableName)) { // if the command is the declaration of a new variable or another command
								for (String input : inputs) {                       // for each part, put it in the equivalent java line, with the template
									input = convert(key, i, input, ignoreError);    // convert percentage to px, set undefined variable to 0/""/false , put hexadecimal code in a String, ...
									newJavaLine += input + " " + template[i] + " ";
									i++;
								}
								if (isVariable) {
									nullVariables.add(variableName);
								}
							} else { // if the command is the declaration of a variable that has already been declared but "removed" (i.e. it now points on null, and must only be assigned)
								newJavaLine = indentation + inputs[0] + convert(key, 1, inputs[1], ignoreError) + template[1];
								nullVariables.remove(variableName);
							}

							if (key.contains("REMOVE") || key.contains("DEL")){
								nullVariables.add(inputs[1]);
							}

							key2 = key;
							patternFound = true;
							break;
						}else{													// if there is a select inside a mimic or a mirror
							if (!ignoreError) { 								// if errors are not ignored, throw an error
								throw new IllegalArgumentException("Usage of select is restricted inside mimic and mirror loop");
							}
							patternFound = true;								// if errors are ignored, the pattern has been found nonetheless
							break;
						}

					}else if(key.contains("FOR")) {                           	// the FOR command has several cases, and thus each of them must be able to be constructed
						int counter = matcher.groupCount();
						key2 = key;
						if (counter == 4) {                                  	// if the FOR command is the one with TO FROM and STEP
							if (ignoreError){
								newJavaLine = indentation + "try{\n" + indentation;
							} else {
								newJavaLine = indentation;
							}
							functionPile.add("FOR");
							newJavaLine += "for(double " + matcher.group(1) + "=" + matcher.group(2) + "; " + matcher.group(1) + "<=" + matcher.group(3) + "; " + matcher.group(1) + "+=" + matcher.group(4) + "){";
							patternFound = true;
							break;

						} else if (counter == 3 && key.contains("STEP")) {      // if the FOR command is the one with TO and STEP
							if (ignoreError){
								newJavaLine = indentation + "try{\n" + indentation;
							} else {
								newJavaLine = indentation;
							}
							functionPile.add("FOR");
							newJavaLine += "for(double " + matcher.group(1) + "=0; " + matcher.group(1) + "<=" + matcher.group(2) + "; " + matcher.group(1) + "+=" + matcher.group(3) + "){";
							patternFound = true;
							break;

						} else if (counter == 3 && key.contains("FROM") && !newCyLine.contains("STEP")) { // if the FOR command is the one with TO and FROM
							if (ignoreError){
								newJavaLine = indentation + "try{\n" + indentation;
							} else {
								newJavaLine = indentation;
							}
							functionPile.add("FOR");
							newJavaLine += "for(double " + matcher.group(1) + "=" + matcher.group(2) + "; " + matcher.group(1) + "<=" + matcher.group(3) + "; " + matcher.group(1) + "++){";
							patternFound = true;
							break;

						} else if (counter == 2 && !newCyLine.contains("STEP")) { // if the FOR command is the one with just TO
							if (ignoreError){
								newJavaLine = indentation + "try{\n" + indentation;
							} else {
								newJavaLine = indentation;
							}
							functionPile.add("FOR");
							newJavaLine += "for(double " + matcher.group(1) + "=0; " + matcher.group(1) + "<=" + matcher.group(2) + "; " + matcher.group(1) + "++){";
							patternFound = true;
							break;
						}

					}else if (key.startsWith("MIMIC") && !key.contains("END")){ // if the match if for MIMIC (and isn't for MIMICEND) write the necessary things to do the mimic
						key2 = key;
						functionPile.add("MIMIC");
						mimicPile.add(matcher.group(1));
						if (ignoreError){
							newJavaLine = indentation + "try{\n" + indentation; // if error must be ignored, add try at start of the function
						}
						newJavaLine += "targetStart = " + matcher.group(1) +";\n" + indentation +
								"k++;\n" + indentation +
								"oldliste.add(new ArrayList<>(liste));\n" + indentation +
								"tempPointer = new Pointer(gc);\n" + indentation +
								"tempPointer.pos(currentPointer.getPos_x(),currentPointer.getPos_y());\n" + indentation +
								"temp.add(tempPointer);\n" + indentation +
								"targetPointer = new Pointer(gc);\n" + indentation +
								"targetPointer.pos(targetStart.getPos_x(),targetStart.getPos_y());\n" + indentation +
								"target.add(targetPointer);" +
								"liste.add(temp.get(k));\n" + indentation +
								"liste.add(target.get(k));\n" + indentation +
								"for(; " + matcher.group(1) + "Index <2;"+matcher.group(1) + "Index++){\n\t" + indentation +
								"currentPointer = liste.get(liste.size()-1 -" + matcher.group(1) + "Index);\n" + indentation;
						preventSELECT++;									// increment to know if code is in a mimic loop
						patternFound = true;
						break;

					}else if (key.contains("END")) {
                        if (!removed.equals("MIMIC") && !ignoreError) {
                            throw new IllegalStateException("Closing MIMIC but function " + removed + " is still opened"); // throw an error to tell the user that there is something wrong
                        }

                        String lastMimic = "";
                        if (!mimicPile.isEmpty()) {
                            lastMimic = mimicPile.remove(mimicPile.size() - 1);
                        } else if (!ignoreError) {
                            throw new IllegalStateException("Closing mimic, but no mimic are still open");
                        } else {
                            break;
                        }
                        key2 = key;
                        newJavaLine = "\n" + indentation + lastMimic + "Index = 0;\n" + indentation + "liste=oldliste.get(oldliste.size() - 1);" +
                                "\n\n" + indentation + "oldliste.remove(oldliste.size() - 1);\n";
                        preventSELECT--;                                    // decrement to know if code is out of a mimic loop
                        patternFound = true;
                        if (ignoreError) {
                            newJavaLine += indentation + "} catch (Exception ignored){}"; // if error must be ignored, add catch at end of function
                        }
                        break;

                    }else {
						key2 = key;
						if (!nullVariables.contains(matcher.group(1))) { 		// if the cursor has never been used, declare it
							newJavaLine = indentation + "Pointer " + matcher.group(1) + " = new Pointer(gc);\n" +
									indentation + "int " + matcher.group(1) + "Index = 0;";
						} else {												// if the cursor have already been used, but was "removed" and point on null, just assign it a new value
							newJavaLine = indentation + matcher.group(1) + " = new Pointer(gc);\n" +
									indentation + matcher.group(1) + "Index = 0;";
							nullVariables.remove(matcher.group(1));
						}
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
					if (newCyLine.contains("}")){							// if no matches, then check the } for end of loops (if there is one, the only case is that this is the only thing on the line except tabulations
						patternFound = true;
						if (!functionPile.isEmpty()) {
							removed = functionPile.remove(functionPile.size() - 1); // delete the last function opened
						}else if(!ignoreError){
							throw new IllegalStateException("Closing loop, but no loop are opened");
						} else {
							break;
						}

						newJavaLine = indentation + "}";
						if (ignoreError && !removed.equals("MIMIC")){
							newJavaLine +="\n" + indentation + "} catch (Exception ignored){}"; // if error must be ignored, add catch at end of function
						}
						break;
					}
				}

				if(preventSELECT < 0){
					if (!ignoreError) {
						throw new IllegalStateException("Missing end of loop after mimic or mirror"); // throw an error to tell the user that there is more start of mimic and mirror than end
					}
				}

			}

			if(!patternFound && !newCyLine.matches("[ \t]*")){	// if no pattern are found and the line contains other things that space or tabulation
				if (!ignoreError) {
					throw new IllegalArgumentException("Unknown instruction : " + newCyLine);
				} else {
					if (doUnknownCommands) {
						if (ignoreError) {
							newJavaLine = indentation + "try{\n" + indentation + "\t" + newCyLine + ";\n" + indentation + "} catch (Exception ignored){}";
						} else {
							newJavaLine = indentation + " " + newCyLine + ";";
						}
					}
				}															// if error are ignored and the command is on only a line, and not a variable declaration (because variable declaration cannot be accessed outside try catch blocs)
			}else if (ignoreError && !newCyLine.matches("[ \t]*") && !newJavaLine.contains("}") && !key2.contains("CURSOR") && !key2.contains("SELECT") && !key2.contains("REMOVE") && !key2.contains("IF") && !key2.contains("FOR") && !key2.contains("WHILE") && !key2.contains("MIMIC") && !key2.contains("MIRROR") && !key2.contains("NUM") && !key2.contains("INT") && !key2.contains("STR") && !key2.contains("BOOL")){
				newJavaLine = indentation + "try{\n\t"  + newJavaLine + "\n" + indentation + "} catch (Exception ignored){}";
			}

			javaCode += "\n" + newJavaLine;									// add the new line to the java code

		}

		if(!functionPile.isEmpty() && !ignoreError){
			throw new IllegalStateException(functionPile.toString() + "the number of end of loop doesn't match the number of start"); // throw an error to tell the user that there is not the same amount of start of if/for/while than end
		}

		if(preventSELECT != 0 && !ignoreError){
			throw new IllegalStateException("the number of end of mimic or mirror doesn't match the number of start"); // throw an error to tell the user that there is not the same amount of start of mimic and mirror than end
		}

		return javaCode;
	}







	/**
	 * This function converts the content of the given input to its absolute value from a percentage. Undefined variables are set to 0/empty_String/false. The input is converted as a String for the COLOR function if it's a hexadecimal code or a constant if a color name.
	 *
	 * @param function The cyCode function in which the input resides.
	 * @param i The number of the current input of the function.
	 * @param input The input of the function.
	 * @param ignoreError A boolean indicating whether errors should be thrown or ignored.
	 * @return The input converted.
	 * @throws IllegalArgumentException If there is an illegal argument passed to the function.
	 */
	private String convert(String function, int i, String input, boolean ignoreError) throws IllegalArgumentException{

		if (i == 1 && !input.contains("=")){
			if (function.contains("NUM")){                                 	// if a double wasn't initialized, put it to 0
				input = input + " = 0.0";
			}
			if (function.contains("INT")){                                 	// if an int wasn't initialized, put it to 0
				input = input + " = 0";
			}
			if (function.contains("STR")){                                 	// if a String wasn't initialized, put it to ""
				input = input + " = \"\"";
			}
			if (function.contains("BOOL")){                                	// if a boolean wasn't initialized, put it to false
				input = input + " = false";
			}
		}

		if (input.contains("%")){                                           // to convert the percentage in absolute value
			if (!function.contains("PRESS")){
				String pattern = "(\\w+\\.?\\d*)\\s*%";                     // make a pattern of a number (int double or variableName) with a %
				Pattern regex = Pattern.compile(pattern);
				Matcher matcher = regex.matcher(input);                     // look for the pattern in the string

				while (matcher.find()) {									// while there is another % in the line
					String[] str = input.split(matcher.group(0), 2);    	// split the string just for the first occurrence
					if (str.length == 2){									// if the pattern is included in the input
						input = str[0] + "(int) (" + matcher.group(1) + "*(" + this.maxWindowWidth + "/100))" + str[1];
					} else {                                                // if the input is the matcher.group(0), meaning the pattern is the whole input
						input = "(int) (" + matcher.group(1) + "*(" + this.maxWindowWidth + "/100))";
					}
					matcher = regex.matcher(input);                         // look fot another match in the input
				}
			} else {                                                        // if the command is PRESS, then the percentage does not depend on the window size, it just needs to be divided by 100
				String pattern = "(\\w+\\.?\\d*)\\s*%";
				Pattern regex = Pattern.compile(pattern);
				Matcher matcher = regex.matcher(input);

				if (matcher.find()) {
					input = input.replaceAll(matcher.group(0), "(double) (" + matcher.group(1) + "/100)");   // replace it in the string
				} else {
					if (!ignoreError) {
						throw new IllegalArgumentException("unrecognized pattern in function PRESS with a percentage : " + input);
					}
				}
			}
		}

		if (function.contains("COLOR")) {
			if (input.contains("#")){
				String pattern = "\\s*(#[0-9A-F][0-9A-F][0-9A-F][0-9A-F][0-9A-F][0-9A-F])\\s*"; // check that the hexadecimal code has the correct format
				Pattern regex = Pattern.compile(pattern);
				Matcher matcher = regex.matcher(input.toUpperCase());
				if (matcher.find()) {
					input = "\"" + matcher.group(1) + "\""; 				// remove the spaces or tabulations and put it as a String
				} else {
					if (!ignoreError) {
						throw new IllegalArgumentException("unrecognized pattern in function COLOR with a # without a hexadecimal code : " + input); // if the errors aren't ignored, throw an error
					}
					input = "\"" + input + "\""; 							// else, put is as a string and let the try catch handle the error
				}
			} else {
				String pattern = "\\s*([A-Za-z]+)\\s*";						// check if the input is the name of a colour
				Pattern regex = Pattern.compile(pattern);
				Matcher matcher = regex.matcher(input.toUpperCase());
				if (matcher.find()) {
					if (isColorName(matcher.group(1))) {
						input = "Color." + matcher.group(1);				// if so then write the constant corresponding to teh colour
					} else if (!ignoreError) {								// if there is only one argument, then there is an error
						throw new IllegalArgumentException("unrecognized argument in function COLOR : " + input);
					} else {
						input = "-1,-1,-1"; 								// else put something that can be handled by the try catch
					}
				}
			}

		}

		return input;
	}




	/**
	 * Checks if a given string is a valid color name.
	 *
	 * @param input The string to check as a color name.
	 * @return boolean true if the input is a valid color name, false otherwise.
	 */
	private boolean isColorName(String input) {
		if (COLOR_NAMES.isEmpty()){
			setColorNames();
		}
		return COLOR_NAMES.contains(input);
	}




	/**
	 * Set the set<String> with all possible colour name.
	 */
	private static void setColorNames(){
		COLOR_NAMES.add("ALICEBLUE");
		COLOR_NAMES.add("ANTIQUEWHITE");
		COLOR_NAMES.add("AQUA");
		COLOR_NAMES.add("AQUAMARINE");
		COLOR_NAMES.add("AZURE");
		COLOR_NAMES.add("BEIGE");
		COLOR_NAMES.add("BISQUE");
		COLOR_NAMES.add("BLACK");
		COLOR_NAMES.add("BLANCHEDALMOND");
		COLOR_NAMES.add("BLUE");
		COLOR_NAMES.add("BLUEVIOLET");
		COLOR_NAMES.add("BROWN");
		COLOR_NAMES.add("BURLYWOOD");
		COLOR_NAMES.add("CADETBLUE");
		COLOR_NAMES.add("CHARTREUSE");
		COLOR_NAMES.add("CHOCOLATE");
		COLOR_NAMES.add("CORAL");
		COLOR_NAMES.add("CORNFLOWERBLUE");
		COLOR_NAMES.add("CORNSILK");
		COLOR_NAMES.add("CRIMSON");
		COLOR_NAMES.add("CYAN");
		COLOR_NAMES.add("DARKBLUE");
		COLOR_NAMES.add("DARKCYAN");
		COLOR_NAMES.add("DARKGOLDENROD");
		COLOR_NAMES.add("DARKGRAY");
		COLOR_NAMES.add("DARKGREEN");
		COLOR_NAMES.add("DARKGREY");
		COLOR_NAMES.add("DARKKHAKI");
		COLOR_NAMES.add("DARKMAGENTA");
		COLOR_NAMES.add("DARKOLIVEGREEN");
		COLOR_NAMES.add("DARKORANGE");
		COLOR_NAMES.add("DARKORCHID");
		COLOR_NAMES.add("DARKRED");
		COLOR_NAMES.add("DARKSALMON");
		COLOR_NAMES.add("DARKSEAGREEN");
		COLOR_NAMES.add("DARKSLATEBLUE");
		COLOR_NAMES.add("DARKSLATEGRAY");
		COLOR_NAMES.add("DARKSLATEGREY");
		COLOR_NAMES.add("DARKTURQUOISE");
		COLOR_NAMES.add("DARKVIOLET");
		COLOR_NAMES.add("DEEPPINK");
		COLOR_NAMES.add("DEEPSKYBLUE");
		COLOR_NAMES.add("DIMGRAY");
		COLOR_NAMES.add("DIMGREY");
		COLOR_NAMES.add("DODGERBLUE");
		COLOR_NAMES.add("FIREBRICK");
		COLOR_NAMES.add("FLORALWHITE");
		COLOR_NAMES.add("FORESTGREEN");
		COLOR_NAMES.add("FUCHSIA");
		COLOR_NAMES.add("GAINSBORO");
		COLOR_NAMES.add("GHOSTWHITE");
		COLOR_NAMES.add("GOLD");
		COLOR_NAMES.add("GOLDENROD");
		COLOR_NAMES.add("GRAY");
		COLOR_NAMES.add("GREEN");
		COLOR_NAMES.add("GREENYELLOW");
		COLOR_NAMES.add("GREY");
		COLOR_NAMES.add("HONEYDEW");
		COLOR_NAMES.add("HOTPINK");
		COLOR_NAMES.add("INDIANRED");
		COLOR_NAMES.add("INDIGO");
		COLOR_NAMES.add("IVORY");
		COLOR_NAMES.add("KHAKI");
		COLOR_NAMES.add("LAVENDER");
		COLOR_NAMES.add("LAVENDERBLUSH");
		COLOR_NAMES.add("LAWNGREEN");
		COLOR_NAMES.add("LEMONCHIFFON");
		COLOR_NAMES.add("LIGHTBLUE");
		COLOR_NAMES.add("LIGHTCORAL");
		COLOR_NAMES.add("LIGHTCYAN");
		COLOR_NAMES.add("LIGHTGOLDENRODYELLOW");
		COLOR_NAMES.add("LIGHTGRAY");
		COLOR_NAMES.add("LIGHTGREEN");
		COLOR_NAMES.add("LIGHTGREY");
		COLOR_NAMES.add("LIGHTPINK");
		COLOR_NAMES.add("LIGHTSALMON");
		COLOR_NAMES.add("LIGHTSEAGREEN");
		COLOR_NAMES.add("LIGHTSKYBLUE");
		COLOR_NAMES.add("LIGHTSLATEGRAY");
		COLOR_NAMES.add("LIGHTSLATEGREY");
		COLOR_NAMES.add("LIGHTSTEELBLUE");
		COLOR_NAMES.add("LIGHTYELLOW");
		COLOR_NAMES.add("LIME");
		COLOR_NAMES.add("LIMEGREEN");
		COLOR_NAMES.add("LINEN");
		COLOR_NAMES.add("MAGENTA");
		COLOR_NAMES.add("MAROON");
		COLOR_NAMES.add("MEDIUMAQUAMARINE");
		COLOR_NAMES.add("MEDIUMBLUE");
		COLOR_NAMES.add("MEDIUMORCHID");
		COLOR_NAMES.add("MEDIUMPURPLE");
		COLOR_NAMES.add("MEDIUMSEAGREEN");
		COLOR_NAMES.add("MEDIUMSLATEBLUE");
		COLOR_NAMES.add("MEDIUMSPRINGGREEN");
		COLOR_NAMES.add("MEDIUMTURQUOISE");
		COLOR_NAMES.add("MEDIUMVIOLETRED");
		COLOR_NAMES.add("MIDNIGHTBLUE");
		COLOR_NAMES.add("MINTCREAM");
		COLOR_NAMES.add("MISTYROSE");
		COLOR_NAMES.add("MOCCASIN");
		COLOR_NAMES.add("NAVAJOWHITE");
		COLOR_NAMES.add("NAVY");
		COLOR_NAMES.add("OLDLACE");
		COLOR_NAMES.add("OLIVE");
		COLOR_NAMES.add("OLIVEDRAB");
		COLOR_NAMES.add("ORANGE");
		COLOR_NAMES.add("ORANGERED");
		COLOR_NAMES.add("ORCHID");
		COLOR_NAMES.add("PALEGOLDENROD");
		COLOR_NAMES.add("PALEGREEN");
		COLOR_NAMES.add("PALETURQUOISE");
		COLOR_NAMES.add("PALEVIOLETRED");
		COLOR_NAMES.add("PAPAYAWHIP");
		COLOR_NAMES.add("PEACHPUFF");
		COLOR_NAMES.add("PERU");
		COLOR_NAMES.add("PINK");
		COLOR_NAMES.add("PLUM");
		COLOR_NAMES.add("POWDERBLUE");
		COLOR_NAMES.add("PURPLE");
		COLOR_NAMES.add("RED");
		COLOR_NAMES.add("ROSYBROWN");
		COLOR_NAMES.add("ROYALBLUE");
		COLOR_NAMES.add("SADDLEBROWN");
		COLOR_NAMES.add("SALMON");
		COLOR_NAMES.add("SANDYBROWN");
		COLOR_NAMES.add("SEAGREEN");
		COLOR_NAMES.add("SEASHELL");
		COLOR_NAMES.add("SIENNA");
		COLOR_NAMES.add("SILVER");
		COLOR_NAMES.add("SKYBLUE");
		COLOR_NAMES.add("SLATEBLUE");
		COLOR_NAMES.add("SLATEGRAY");
		COLOR_NAMES.add("SLATEGREY");
		COLOR_NAMES.add("SNOW");
		COLOR_NAMES.add("SPRINGGREEN");
		COLOR_NAMES.add("STEELBLUE");
		COLOR_NAMES.add("TAN");
		COLOR_NAMES.add("TEAL");
		COLOR_NAMES.add("THISTLE");
		COLOR_NAMES.add("TOMATO");
		COLOR_NAMES.add("TURQUOISE");
		COLOR_NAMES.add("VIOLET");
		COLOR_NAMES.add("WHEAT");
		COLOR_NAMES.add("WHITE");
		COLOR_NAMES.add("WHITESMOKE");
		COLOR_NAMES.add("YELLOW");
		COLOR_NAMES.add("YELLOWGREEN");
	}
}
