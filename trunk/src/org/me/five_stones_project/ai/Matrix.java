package org.me.five_stones_project.ai;

/**
 *
 * @author Tangl Andras
 */

public class Matrix {
	//I used float instead of double because SharedPreferences 
	//cannot handle double numbers

	public static float[][] invert(float[][] A) {
		if(A.length != A[0].length)
			return null;
		
		//if(determinant(A) == 0)
			//return null;
		
		float[][] upper = A;
		float[][] copy = new float[A.length][A.length];
		float[][] lower = new float[A.length][A.length];
		for(int k = 0; k < A.length; ++k) {
			lower[k][k] = 1;
			for(int i = 0; i < A.length; ++i) {
				if(i <= k - 1)
					lower[k][i] = 0;
				if(i >= k + 1)
					lower[k][i] = upper[k][i] / upper[k][k];
				for(int j = 0; j < A.length; ++j) {
					if(i <= k)
						copy[j][i] = upper[j][i];
					if(i > k && j <= k)
						copy[j][i] = 0;
					if(i > k && j > k)
						copy[j][i] = upper[j][i] - lower[k][i] * upper[j][k];
				}
			}
			upper = copy;
		}
		
		//invert lower triangle matrix
		float[][] inverseLower = createIdentityMatrix(A.length);
		for(int k = 1; k < A.length; ++k)
			for(int i = 0; i < k; ++i)
				inverseLower[i][k] -= lower[i][k];
							
		//invert upper triangle matrix
		float[][] inverseUpper = createIdentityMatrix(A.length);
		for(int k = A.length - 1; k >= 0; --k) {
			for(int i = k + 1; i < A.length; ++i)
				inverseUpper[i][k] -= upper[i][k];
			for(int i = k; i < A.length; ++i)
			inverseUpper[i][k] /= upper[k][k];
		}
		
		//calculate inverse A as inverseUpper * inverseLower
		float[][] inverse = new float[A.length][A.length];
		for(int i = 0; i < A.length; ++i)
			for(int j = 0; j < A.length; ++j)
				for(int k = 0; k < A.length; ++k)
					inverse[i][j] += inverseUpper[k][j] * inverseLower[i][k];
		
		return inverse;
	}
	
	public static float determinant(float[][] mat) { 
		float result = 0; 

		if(mat.length == 1) { 
			result = mat[0][0]; 
			return result; 
		} 

		if(mat.length == 2) { 
			result = mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0]; 
			return result; 
		} 

		for(int i = 0; i < mat[0].length; i++) { 
			float temp[][] = new float[mat.length - 1][mat[0].length - 1]; 
	
			for(int j = 1; j < mat.length; j++) { 
				System.arraycopy(mat[j], 0, temp[j - 1], 0, i); 
				System.arraycopy(mat[j], i + 1, temp[j - 1], i, mat[0].length - i - 1); 
			} 
	
			result += mat[0][i] * Math.pow(-1, i) * determinant(temp); 
		} 

		return result; 

	}
	
	public static float[][] createIdentityMatrix(int size) {
		float[][] matrix = new float[size][size];
		for(int i = 0; i < size; ++i)
			matrix[i][i] = 1;
		return matrix;
	}
}
