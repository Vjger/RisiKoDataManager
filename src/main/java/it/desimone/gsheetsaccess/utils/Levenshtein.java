package it.desimone.gsheetsaccess.utils;

import java.util.Arrays;

public class Levenshtein {

	public static int calculateSlow(String x, String y) {
		if (x.isEmpty()) {
			return y.length();
		}

		if (y.isEmpty()) {
			return x.length();
		} 

		int substitution = calculateSlow(x.substring(1), y.substring(1)) 
				+ costOfSubstitution(x.charAt(0), y.charAt(0));
		int insertion = calculateSlow(x, y.substring(1)) + 1;
		int deletion = calculateSlow(x.substring(1), y) + 1;

		return min(substitution, insertion, deletion);
	}

	public static int costOfSubstitution(char a, char b) {
		return a == b ? 0 : 1;
	}

	public static int min(int... numbers) {
		return Arrays.stream(numbers)
				.min().orElse(Integer.MAX_VALUE);
	}

	public static int calculateFast(String x, String y) {
		int[][] dp = new int[x.length() + 1][y.length() + 1];

		for (int i = 0; i <= x.length(); i++) {
			for (int j = 0; j <= y.length(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				}
				else if (j == 0) {
					dp[i][j] = i;
				}
				else {
					dp[i][j] = min(dp[i - 1][j - 1] 
							+ costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)), 
							dp[i - 1][j] + 1, 
							dp[i][j - 1] + 1);
				}
			}
		}

		return dp[x.length()][y.length()];
	}
	

    /**
     * Restituisce il massimo tra |i| e |j|
     */
    static int max( int i, int j )
    {
        return (i>j ? i : j);
    }

    /**
     * Restituisce il minimo tra |i|, |j| e |k|,
     */
    static int min3( int i, int j, int k )
    {
        int result = i;
        if (j < result) result = j;
        if (k < result) result = k;
        return result;
    }

    /**
     * Restituisce la distanza di Levenshtein tra due stringhe |S| e
     * |T|.  La distanza e' un intero compreso tra 0 e la massima
     * lunghezza delle due stringhe. Si presti attenzione al fatto che
     * le posizioni dei caratteri delle stringhe in Java iniziano da 0
     * e non da 1 come nello pseudocodice usato nei lucidi. 
     */     
    public static int levDistance( String S, String T )
    {
        int i, j;
        final int n = S.length(), m = T.length();
        int L[][] = new int[n+1][m+1];
        for ( i=0; i<n+1; i++ ) {
            for ( j=0; j<m+1; j++ ) {
                if ( i==0 || j==0 ) {
                    L[i][j] = max(i, j);
                } else {
                    L[i][j] = min3(L[i-1][j] + 1, L[i][j-1] + 1,
                                   L[i-1][j-1] + (S.charAt(i-1) != T.charAt(j-1) ? 1 : 0) );
                }
            }
        }

        /* Stampa la matrice L */
//        for (i=0; i<n+1; i++) {
//            for (j=0; j<m+1; j++) {
//                System.out.print(L[i][j]);
//            }
//            System.out.println();
//        }
        return L[n][m];
    }

	public static void main(String[] args){
		String a = "Massimiliano Tresoldi";
		String b = "Massimiliano Tressoldi";
		//System.out.println(calculateSlow(a, b));
		System.out.println(calculateFast(a, b));
		System.out.println(levDistance(a, b));
	}
}
