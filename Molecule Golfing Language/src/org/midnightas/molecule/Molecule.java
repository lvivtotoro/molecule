package org.midnightas.molecule;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.midnightas.chococompress.CCompress;

public class Molecule {

	public static final void main(String[] args) throws Exception {
		if(args.length == 0) {
			System.err.println("Enter the .mol file location.");
			System.exit(0);
		} else if(args.length == 1) {
			System.err.println("Please enter the file encoding.");
			System.exit(0);
		}
		Molecule molecule = new Molecule(new String(Files.readAllBytes(Paths.get(new File(args[0]).toURI())), args[1]));
		CCompress.update();
		molecule.run();
	}

	public List<Object> stack = new ArrayList<Object>();
	public List<Object> aStack = new ArrayList<Object>();
	public HashMap<Character, Object> vars = new HashMap<Character, Object>();
	public String content;
	public boolean wStatement = false;
	public int wIndex = 0;
	public boolean arrayMode = false;

	public Molecule(String content) throws UnsupportedEncodingException {
		this.content = content;
	}

	public void run() {
		addDefaultVariables(vars);
		for (int al = 0; al < content.length(); al++) {
			char atom = content.charAt(al);
			if (atom >= '0' && atom <= '9') {
				add(new Double(Double.parseDouble(atom + "")));
			} else if (atom == '"') {
				String text = "";
				for (int al0 = al + 1; al0 < content.length(); al0++) {
					char atom0 = content.charAt(al0);
					if (atom0 == '"') {
						al = al0;
						add(text);
						break;
					}
					text += atom0;
				}
			} else if (atom == '+') {
				Double item1 = (Double) getLatestItemInStack(stack, true);
				Double item0 = (Double) getLatestItemInStack(stack, true);
				add(new Double(item0 + item1));
			} else if (atom == '-') {
				Double item1 = (Double) getLatestItemInStack(stack, true);
				Double item0 = (Double) getLatestItemInStack(stack, true);
				add(new Double(item0 - item1));
			} else if (atom == '*') {
				Double item1 = (Double) getLatestItemInStack(stack, true);
				Double item0 = (Double) getLatestItemInStack(stack, true);
				add(new Double(item0 * item1));
			} else if (atom == '/') {
				Double item1 = (Double) getLatestItemInStack(stack, true);
				Double item0 = (Double) getLatestItemInStack(stack, true);
				add(new Double(item0 / item1));
			} else if (atom == '%') {
				Double item1 = (Double) getLatestItemInStack(stack, true);
				Double item0 = (Double) getLatestItemInStack(stack, true);
				add(new Double(item0 % item1));
			} else if (atom == 'M') {
				al++;
				char expression = content.charAt(al);
				if (expression == 'q') {
					Double dbl = (Double) getLatestItemInStack(stack, true);
					add(new Double(Math.sqrt(dbl)));
				} else if (expression == 'f') {
					Double dbl = (Double) getLatestItemInStack(stack, true);
					add(new Double(Math.floor(dbl)));
				} else if (expression == 's') {
					Double dbl = (Double) getLatestItemInStack(stack, true);
					add(new Double(Math.sin(dbl)));
				} else if (expression == 'c') {
					Double dbl = (Double) getLatestItemInStack(stack, true);
					add(new Double(Math.cos(dbl)));
				} else if (expression == 'p') {
					add(new Double(Math.PI));
				}
			} else if (atom == ':') {
				Object obj = getLatestItemInStack(stack, true);
				al++;
				char varChar = content.charAt(al);
				if (vars.containsKey(varChar))
					vars.remove(varChar);
				vars.put(varChar, obj);
			} else if (atom == ';') {
				al++;
				add(vars.get(content.charAt(al)));
			} else if (atom == 'V') {
				System.out.print("[");
				String toAdd = "";
				for (int i = 0; i < vars.size(); i++) {
					toAdd += vars.keySet().toArray()[i] + ":" + vars.values().toArray()[i] + ", ";
				}
				toAdd = toAdd.substring(0, toAdd.length() - 2);
				System.out.println(toAdd + "]");
			} else if (atom == 'c') {
				add(CCompress.compress(getLatestItemInStack(stack, true).toString()));
			} else if (atom == 'C') {
				add(CCompress.decompress(getLatestItemInStack(stack, true).toString()));
			} else if (atom == '~') {
				printStack(stack);
				System.out.println();
			} else if (atom == '.') {
				stack = new ArrayList<Object>();
			} else if (atom == '(') {
				wStatement = true;
				wIndex = al;
			} else if (atom == ')') {
				if (wStatement)
					al = wIndex;
			} else if (atom == '!') {
				wStatement = false;
				for (int al0 = al + 1; al0 < content.length(); al0++) {
					if (al0 == ')') {
						al = al0;
						break;
					}
				}
			} else if (atom == '[') {
				arrayMode = true;
			} else if (atom == ']') {
				stack.add(aStack.toArray());
				aStack = new ArrayList<Object>();
				arrayMode = false;
			} else if (atom == 'A') {
				al++;
				char expression = content.charAt(al);
				if (expression == 'a') {
					List<Object> newList = new ArrayList<Object>();
					for (Object o : (Object[]) getLatestItemInStack(stack, true))
						for (int i = 0; i < 2; i++)
							newList.add(o);
					add(newList.toArray());
				}
			} else if (atom == '\'') {
				al++;
				add(content.charAt(al));
			}
		}
		printStack(stack);
	}

	public void add(Object object) {
		if (!arrayMode)
			stack.add(object);
		else
			aStack.add(object);
	}

	public static void printStack(List<Object> stack) {
		if (stack.size() > 0) {
			for (Object cell : stack) {
				printObject(cell);
			}
		}
	}

	public static void printObject(Object cell) {
		if (cell instanceof Double) {
			Double dbl = (Double) cell;
			if (Double.toString(dbl).endsWith(".0"))
				System.out.print(dbl.intValue());
			else
				System.out.print(dbl);
		} else if (cell instanceof Object[]) {
			System.out.print("[");
			for (Object obj : (Object[]) cell) {
				printObject(obj);
			}
			System.out.print("]");
		} else
			System.out.print(cell);
	}

	public static void addDefaultVariables(HashMap<Character, Object> vars) {
		vars.put('M', "Molecule");
		vars.put('H', "Hello, world!");
		vars.put('K', "poopy poop");
		vars.put('I', CCompress.compress("youtube.com/lvivtotoro  "));
	}

	public static Object getLatestItemInStack(List<Object> stack, boolean remove) {
		if (remove)
			return stack.remove(stack.size() - 1);
		else
			return stack.get(stack.size() - 1);
	}

	static {
	}

}
