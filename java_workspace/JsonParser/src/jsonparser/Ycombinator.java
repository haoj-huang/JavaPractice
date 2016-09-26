package jsonparser;

import java.util.function.Function;

public class Ycombinator {
	public static Function<Function<Function<Integer, Integer>, Function<Integer, Integer>>, Function<Integer, Integer>> Y = 
			(Function<Function<Integer, Integer>, Function<Integer, Integer>> f)->{return f.apply((Integer x)->{return Ycombinator.Y.apply(f).apply(x);});};
			
	private static Function<Function<Integer, Integer>, Function<Integer, Integer>> F1 = 
			(Function<Integer, Integer> f)->{return (Integer n)->{if(n==0)return 1;else return n*Ycombinator.F1.apply(f).apply(n-1);};};
			
	private static Function<Function<Integer, Integer>, Function<Integer, Integer>> F2 = 
			(Function<Integer, Integer> f)->{return (Integer n)->{if(n>0&& n<=100){System.out.print(n+" ");return Ycombinator.F2.apply(f).apply(n+1);}else return 0;};};
			
	public static Function<Integer, Integer> factorial = Y.apply(F1);
	
	public static Function<Integer, Integer> accum = Y.apply(F2);
}
