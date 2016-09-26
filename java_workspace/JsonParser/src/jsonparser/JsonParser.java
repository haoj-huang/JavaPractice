package jsonparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Pair;


public class JsonParser {
	
	private static String num = "0123456789";
	private static String positive_num = "123456789";
	private static String hex = "0123456789abcdefABCDEF";
	private static String charater = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_\b\f\n\r\t/";
	
	private static Parser<Void> digit = Scanners.among(num);
	private static Parser<String> digits = Scanners.quoted(digit,digit.optional(),digit.many());
	private static Parser<Void> positive = Scanners.among(positive_num);
	private static Parser<String> decimal = Scanners.quoted(positive,digit.optional(),digit.many());
	private static Parser<Void> e = Parsers.or(Parsers.sequence(Scanners.isChar('e'),Scanners.among("+-").optional()),Parsers.sequence(Scanners.isChar('E'),Scanners.among("+-").optional()));
	private static Parser<String> exp = Scanners.quoted(e,digit.optional(),digits);
	private static Parser<String> integer = Scanners.quoted(Scanners.isChar('-').optional(),digit.optional(),decimal);
	private static Parser<String> frac = Scanners.quoted(Scanners.isChar('.'),digit.skipMany(),digit);
	private static Parser<String> Boolean = Scanners.quoted(Parsers.or(Scanners.isChar('t'),Scanners.isChar('f')),Scanners.isChar('e'),Parsers.or(Scanners.string("ru"),Scanners.string("als")));
	private static Parser<String> Null = Scanners.quoted(Scanners.isChar('n'),Scanners.isChar('l'),Scanners.string("ul"));
	private static Parser<String> unicode = Scanners.quoted(Scanners.isChar('\\'),Scanners.among(hex).skipTimes(4),Scanners.isChar('u'));
	
	private static Parser<String> string = Scanners.quoted(Scanners.isChar('\"'), Scanners.isChar('\"'),Parsers.or(unicode,Scanners.isChar(' '),Scanners.among(charater)).many());
	private static Parser<String> number = Scanners.quoted(Parsers.sequence(Scanners.isChar('-').optional(),digit.optional(),positive.many()).cast(),exp.optional().cast(),frac.optional());
	
	private static Pair<String,String> spliter(String str,char c){
		int nos = 0, nob = 0;
		String left="", right="";
		if(str=="") return new Pair<String, String>(left, right);
		int i = 0, sz = str.length();
		boolean condition = true;
		while(condition){
			if(str.charAt(i)=='[') ++nos;
			else if(str.charAt(i)==']') --nos;
			else if(str.charAt(i)=='{') ++nob;
			else if(str.charAt(i)=='}') --nob;
			
			left += str.charAt(i); ++i;
			
			if(i >= sz||(nos == 0 && nob == 0 && str.charAt(i) == c))
				condition = false;
		}
		if(i+1 < sz) right = str.substring(i+1);
		left = left.trim();
		right = right.trim();
		Pair<String,String> pair = new Pair<String,String>(left,right);
		return pair;
	}

	private static Function<String,Integer> Case = (String s)->{
		if(s.charAt(0)=='\"') {
			try{string.parse(s);return 1;}
			catch(Exception e){return 0;}
		}
		else if(s.charAt(0)=='-'||(s.charAt(0)>='0'&&s.charAt(0)<='9')){
			try{number.parse(s);return 2;}
			catch(Exception e){return 0;}
		}
		else if(s.charAt(0)=='{'&&s.charAt(s.length()-1)=='}') return 3;
		else if(s.charAt(0)=='['&&s.charAt(s.length()-1)==']') return 4;
		else if(s=="true") return 5;
		else if(s=="false") return 6;
		else if(s=="null") return 7;
		else return 0;
	};

	private static ArrayList<Object> parseArr(String b) throws FormatWrongException, NotJsonStringException{
		ArrayList<Object> arr = new ArrayList<Object>();
		b = b.substring(1, b.length()-1);
		Pair<String,String> pair = spliter(b,',');
		while(pair.a!=""){
			switch(Case.apply(pair.a)){
				case 1:arr.add(pair.a.substring(1, pair.a.length()-1));break;
				case 2:arr.add(Double.parseDouble(pair.a));break;
				case 3:arr.add(parse(pair.a));break;
				case 4:arr.add(parseArr(pair.a));break;
				case 5:arr.add(true);break;
				case 6:arr.add(false);break;
				case 7:arr.add(null);break;
				default:throw new FormatWrongException("parseArr");
			}
			pair = spliter(pair.b,',');
		}
		return arr;
	}
	
	private static Pair<String, Object> parsePair(String str) throws FormatWrongException, NotJsonStringException{
		Pair<String,String> pair = spliter(str,':');
		try{string.parse(pair.a);}
		catch(Exception e){throw new FormatWrongException("parsePair");}
		String name = pair.a.substring(1, pair.a.length()-1);
		Pair<String, Object> p;
		switch(Case.apply(pair.b)){
			case 1:p=new Pair<String, Object>(name,pair.b.substring(1, pair.b.length()-1));break;
			case 2:p=new Pair<String, Object>(name,(Double)Double.parseDouble(pair.b));break;
			case 3:p=new Pair<String, Object>(name,parse(pair.b));break;
			case 4:p=new Pair<String, Object>(name,parseArr(pair.b));break;
			case 5:p=new Pair<String, Object>(name,(Boolean)true);break;
			case 6:p=new Pair<String, Object>(name,(Boolean)false);break;
			case 7:p=new Pair<String, Object>(name,(Void)null);break;
			default:throw new FormatWrongException("parsePair");
		}
		return p;
	}
	
	public static Map<String,Object> parse(String str) throws NotJsonStringException{
		Map<String,Object> map = new HashMap<String,Object>();
		int sz=str.length();
		if(sz==0) throw new NotJsonStringException();
		else if(str.charAt(0)!='{'||str.charAt(sz-1)!='}')
			throw new NotJsonStringException();
		else{
			str = str.substring(1, sz-1);
			while(str!=""){
				Pair<String,String> ps = spliter(str,',');
				str = ps.b;
				try {
					Pair<String,Object> p = parsePair(ps.a);
					map.put(p.a, p.b);
				} catch (FormatWrongException e) {}
			}
			return map;
		}
	}

	public static void main(String[] args) {
		Ycombinator.accum.apply(1);
		System.out.println();
		System.out.println(Ycombinator.factorial.apply(10));
		
		System.out.println(unicode.many().parse(new StringBuffer("\\u3201\\u5200")));
		System.out.println(digits.parse(new StringBuffer("100")));
		System.out.println(integer.parse(new StringBuffer("-10")));
		System.out.println(frac.parse(new StringBuffer(".12")));
		System.out.println(number.parse(new StringBuffer("-0.22e+5")));
		String str = "\"  a e\\u3201\\u5200abcf\"";
		String sn = "-0.22e+5";
		try{
			System.out.println(string.parse(new StringBuffer(str)));
			System.out.println(string.parse(new StringBuffer(str)).equals(str));
			System.out.println(number.parse(new StringBuffer(sn)));
			System.out.println(number.parse(new StringBuffer(sn)).equals(sn));
		}catch(Exception e){
			System.out.println("~~~~~~~~~Wrong Input, Cannot Parse~~~~~~~~~");
		}
		System.out.println(Null.parse(new StringBuffer("null")));
		System.out.println(Boolean.parse(new StringBuffer("true")));
		System.out.println("abcde".substring(1, 4));
		Map<String,String> map = new HashMap<String,String>();
		System.out.println(map.isEmpty());
		ArrayList<Object> arr = new ArrayList<Object>();
		ArrayList<Integer> is = new ArrayList<Integer>();
		is.add(1);
		is.add(2);
		arr.add("aaa");
		arr.add(null);
		arr.add(true);
		arr.add(is);
		System.out.println(arr.size());
		for(Object o:arr){
			System.out.println(o);
		}
	}
}
