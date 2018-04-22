import java.util.ArrayList;
import java.util.Arrays;

/**
* A RPN calculator class that can compute the binary operations of
* addition, subtraction, multiplication and division, and the unary
* operation of square root.
*
* @author  Nicolas Klein
* @version 1.0
* @since   2018-04-21 
*/

public class RPNCalculator {
	public ArrayList<String> binaryOpList;
	public ArrayList<String> unaryOpList;
	
	public RPNCalculator() {
		this.binaryOpList = new ArrayList<String> ();
		this.binaryOpList.add("+");
		this.binaryOpList.add("-");
		this.binaryOpList.add("*");
		this.binaryOpList.add("/");

		this.unaryOpList = new ArrayList<String> ();
		this.unaryOpList.add("sqrt");
	}
	
	public String evalString(String str) {
		if(str.length() == 0) {
			return "0";
		}
		if(isInteger(str)) {
			return str;
		}
		try {
			checkExpr(str);
		} catch (InvalidRPNExpressionException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> als = listify(str);
		
		try {
			return evalArray(als);
		} catch (InvalidRPNExpressionException e) {
			e.printStackTrace();
		}
		return " ";
	}
	
	public String evalArray(ArrayList<String> als) throws InvalidRPNExpressionException {
		if(als.size() == 1) {
			return als.get(0);
		}else {
			for(int i = 0; i < als.size(); i++) {	
				if(checkUnary(als, i)) {
					double a = Double.parseDouble(als.get(i));
					String b = als.get(i+1);
					Double stack = evalUnary(a, b);
					
					als.set(i, stack.toString());
					als.remove(i+1);
					
					return evalArray(als);	
				}else if(checkBinary(als, i)) {
					double a = Double.parseDouble(als.get(i));
					double b = Double.parseDouble(als.get(i+1));
					String c = als.get(i+2);
					Double stack = evalBinary(a, b, c);
					
					als.set(i, stack.toString());
					als.remove(i+1);
					als.remove(i+1);
					
					return evalArray(als);				
				}
			}
			throw new InvalidRPNExpressionException();
		}
	}
	
	/**
	 * @param String s
	 * @return ArrayList of number strings and operators
	 * takes a string of valid RPN symbols and returns an
	 * array of those symbols
	 * 
	 */
	public static ArrayList<String> listify(String s){
		ArrayList<String> als = new ArrayList<String>();
		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(i) != ' ') {
				int j = 0;
				try {
					while(s.charAt(i + j) != ' ') {
						j++;
					}
					String str = s.substring(i, i+j);
					als.add(str);
					i = i+j;
				}catch(IndexOutOfBoundsException e) {
					String str = s.substring(i, s.length());
					als.add(str);
					break;
				}
				
			}
		}
		return als;
	}
	
	/**
	 * @param String s
	 * checks whether given string contains valid symbols
	 * 
	 */
	private boolean checkExpr(String s) throws InvalidRPNExpressionException {
		if(s.length() == 0) {
			return true;
		}
		
		if(isInteger(s)) {
			return true;
		}
		//strips the string of all binary and unary operations symbols
		//as well as space and period string
		//then checks if only integers or floats remain
		if(!isInteger(s)) {
			ArrayList<String> allowedChar = new ArrayList<String>();
			
			allowedChar.addAll(this.binaryOpList);
			allowedChar.addAll(this.unaryOpList);
			allowedChar.add(" ");
			allowedChar.add(".");

			for(String str:allowedChar) {
				while(s.indexOf(str) != -1) {
					int i = s.indexOf(str);
					s = s.substring(0, i) + s.substring(i+str.length(), s.length());
				}
			}
			
			if(!isInteger(s)) {
				throw new InvalidRPNExpressionException();
			}
		}
		return true;
	}
	
	public double evalBinary(double a, double b, String s) {
		if(s.equals(this.binaryOpList.get(0))) {
			return a + b;
		}
		
		if(s.equals(this.binaryOpList.get(1))) {
			return a - b;
		}
		
		if(s.equals(this.binaryOpList.get(2))) {
			return a * b;
		}
		
		if(s.equals(this.binaryOpList.get(3))) {
			return a / b;
		}
		return 0;
	}
	
	private double evalUnary(double a, String s) {
		if(s.equals(this.unaryOpList.get(0))) {
			return Math.sqrt(a);
		}
		return 0;
	}
	
	/**
	 * @param ArrayList of Strings als
	 * @param index i
	 * @return boolean
	 * checks whether the next three elements of array
	 * after index i constitute a valid RPN binary operation
	 */
	private boolean checkBinary(ArrayList<String> als, int i) {
		if(als.size() < i+3) return false;
		
		for(int j = i; j < i+2; j++) {
			if(!isInteger(als.get(j)) && !isFloat(als.get(j))) return false;
			else continue;
		}
		
		for(String str:this.binaryOpList) {
			if(str.equals(als.get(i+2))) return true;
			else continue;
		}
		return false;
	}
	
	
	/**
	 * @param ArrayList of Strings als
	 * @param index i
	 * @return boolean
	 * checks whether the next two elements of array
	 * after index i constitute a valid RPN unary operation
	 */
	private boolean checkUnary(ArrayList<String> als, int i) {
		if(als.size() < i+2) return false;
		if(!isInteger(als.get(i)) && !isFloat(als.get(i))) return false;
		for(String str:this.unaryOpList) {
			if(str.equals(als.get(i+1))) return true;
		}
		return false;
	}
	
	private static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	private static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public static boolean isFloat(String s) {
	    return isFloat(s,10);
	}

	private static boolean isFloat(String s, int radix) {
	    if(s.isEmpty()) return false;
		if(countSubString(s, ".") != 1) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0 && s.charAt(i) != '.') return false;
	    }
	    return true;
	}
	
	private static int countSubString(String str, String subStr) {
		int lastIndex = 0;
		int count = 0;

		while(lastIndex != -1){

		    lastIndex = str.indexOf(subStr,lastIndex);

		    if(lastIndex != -1){
		        count ++;
		        lastIndex += subStr.length();
		    }
		}
		return count;
	}
}
