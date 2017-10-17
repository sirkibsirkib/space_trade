package abstracts;

import io.ScreenPoint;

/*
 * This class contains a number of general, convenient
 * mathematical static functions for other classes to use.
 */
public abstract class Fun {
	
	public static double rdg(double range){
		return Math.random()*range;
	}
	
	public static int rng(int range){
		return (int) (Math.random()*range);
	}

	public static double sqr(double d) {
		return d*d;
	}
	
	/*
	 * given an array of whole numbers, will return a randomly-selected index within the array.
	 * the probability of an index being chosen is proportional to the ratio of that
	 * whole number, to the sum of all the whole numbers in the array.
	 * eg:		given [1,3] will return "0" 25% of the time, and "1" 75% of the time.
	 */
	public static int weightedCaseRng(int i0, int... iN){
		if(i0 < 0){
			throw new Error();
		}
		int total = i0;
		for(int x : iN){
			if(x < 0){
				throw new Error();
			}
			total += x;
		}
		int choice = rng(total+1);
		if(choice <= i0){
			return 0;
		}
		choice -= i0;
		for(int i = 0; i < iN.length; i++){
			if(choice <= iN[i]){
				return i+1;
			}
			choice -= iN[i];
		}
		return 0;
	}

	/*
	 * returns true if a die with "sides" sides was thrown to reveal 1, false otherwise
	 */
	public static boolean chance(int sides) {
		return rng(sides) == 0;
	}
	
	/*
	 * returns true with probability p, false otherwise
	 */
	public static boolean prob(double p){
		return Math.random() < p;
	}

	public static double max(double a, double b) {
		return a > b ? a : b;
	}
	
	public static int max(int a, int b) {
		return a > b ? a : b;
	}

	public static int min(int a, int b) {
		return a < b ? a : b;
	}
	
	public static double min(double a, double b) {
		return a < b ? a : b;
	}

	/*
	 * returns a random value on the interval (0, range)
	 * the probabilities of return values are equivalent to
	 * an exponential function with exponent "exponent"
	 */
	public static double skewRdg(double range, double exponent) {
		return Math.pow(rdg(Math.pow(range, exponent)), 1/exponent);
	}
}
