package org.midnightas.molecule;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Molecule {

	public static final void main(String[] args) throws Exception {
		String oldContent = new String(Files.readAllBytes(Paths.get(new File(args[0]).toURI())));
		String content = oldContent + "";
		List<Object> stack = new ArrayList<Object>();
		HashMap<Character, Object> vars = new HashMap<Character, Object>();
		addDefaultVariables(vars);
		for (int al = 0; al < content.length(); al++) {
			char atom = content.charAt(al);
			if (atom >= '0' && atom <= '9') {
				stack.add(new Double(Double.parseDouble(atom + "")));
			} else if (atom == '"') {
				String text = "";
				for (int al0 = al + 1; al0 < content.length(); al0++) {
					char atom0 = content.charAt(al0);
					if (atom0 == '"') {
						al = al0;
						stack.add(text);
						break;
					}
					text += atom0;
				}
			} else if (atom == '+') {
				Double item1 = (Double) getLatestItemInStack(stack, true);
				Double item0 = (Double) getLatestItemInStack(stack, true);
				stack.add(new Double(item0 + item1));
			} else if (atom == '-') {
				Double item1 = (Double) getLatestItemInStack(stack, true);
				Double item0 = (Double) getLatestItemInStack(stack, true);
				stack.add(new Double(item0 - item1));
			} else if (atom == '*') {
				Double item1 = (Double) getLatestItemInStack(stack, true);
				Double item0 = (Double) getLatestItemInStack(stack, true);
				stack.add(new Double(item0 * item1));
			} else if (atom == '/') {
				Double item1 = (Double) getLatestItemInStack(stack, true);
				Double item0 = (Double) getLatestItemInStack(stack, true);
				stack.add(new Double(item0 / item1));
			} else if (atom == '%') {
				Double item1 = (Double) getLatestItemInStack(stack, true);
				Double item0 = (Double) getLatestItemInStack(stack, true);
				stack.add(new Double(item0 % item1));
			} else if (atom == 'M') {
				al++;
				char expression = content.charAt(al);
				if (expression == 'q') {
					Double dbl = (Double) getLatestItemInStack(stack, true);
					stack.add(new Double(Math.sqrt(dbl)));
				} else if (expression == 'f') {
					Double dbl = (Double) getLatestItemInStack(stack, true);
					stack.add(new Double(Math.floor(dbl)));
				} else if (expression == 's') {
					Double dbl = (Double) getLatestItemInStack(stack, true);
					stack.add(new Double(Math.sin(dbl)));
				} else if (expression == 'c') {
					Double dbl = (Double) getLatestItemInStack(stack, true);
					stack.add(new Double(Math.cos(dbl)));
				} else if(expression == 'p') {
					stack.add(new Double(Math.PI));
				}
			} else if(atom == ':') {
				Object obj = getLatestItemInStack(stack, true);
				al++;
				char varChar = content.charAt(al);
				if(vars.containsKey(varChar))
					vars.remove(varChar);
				vars.put(varChar, obj);
			} else if(atom == ';') {
				al++;
				stack.add(vars.get(content.charAt(al)));
			} else if(atom == 'V') {
				System.out.print("[");
				String toAdd = "";
				for(int i = 0; i < vars.size(); i++) {
					toAdd += vars.keySet().toArray()[i] + ":" + vars.values().toArray()[i] + ", ";
				}
				toAdd = toAdd.substring(0, toAdd.length() - 2);
				System.out.println(toAdd + "]");
			}
		}
		if (stack.size() > 0) {
			for (Object cell : stack) {
				if (cell instanceof Double) {
					Double dbl = (Double) cell;
					if (Double.toString(dbl).endsWith(".0"))
						System.out.print(dbl.intValue());
					else
						System.out.print(dbl);
				} else
					System.out.print(cell);
			}
		}
	}

	public static void addDefaultVariables(HashMap<Character, Object> vars) {
		vars.put('M', "Molecule");
		vars.put('H', "Hello, world!");
		vars.put('K', "poopy-poop");
	}

	public static Object getLatestItemInStack(List<Object> stack, boolean remove) {
		if (remove)
			return stack.remove(stack.size() - 1);
		else
			return stack.get(stack.size() - 1);
	}

}
