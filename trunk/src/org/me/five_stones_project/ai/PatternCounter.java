package org.me.five_stones_project.ai;

import android.graphics.Point;
import android.util.Pair;

/**
 * 
 * @author Tangl Andras
 */

public class PatternCounter {

	public static int countPattern(int[][] matrix, int[] pattern, int shift) {
		int count = 0;

		for(int w = 0; w < matrix.length; ++w)
			f: for(int h = 0; h < matrix[0].length - pattern.length; ++h) {
				for(int i = 0; i < pattern.length; ++i)
					if((matrix[w][h + i] << shift) != pattern[i])
						continue f;
				count++;
			}

		for(int h = 0; h < matrix[0].length; ++h)
			f: for(int w = 0; w < matrix.length - pattern.length; ++w) {
				for(int i = 0; i < pattern.length; ++i)
					if((matrix[w + i][h] << shift) != pattern[i])
						continue f;
				count++;
			}
		
		for(int s = 0; s < matrix.length + matrix[0].length - 1; ++s) {
			int from = Math.max(0, s - matrix[0].length + 1);
			int to = Math.min(matrix.length - 1, s);
			if(to - from >= pattern.length - 1)
				f: for(int c = from; c <= to - pattern.length + 1; ++c) {
					for(int i = 0; i < pattern.length; ++i)
						if((matrix[c + i][s - c - i] << shift) != pattern[i])
							continue f;
					count++;
				}
		}
	
		for(int s = 0; s < matrix.length + matrix[0].length - 1; ++s) {
			int from = Math.max(0, s - matrix[0].length + 1);
			int to = Math.min(matrix.length - 1, s);
			if(to - from >= pattern.length - 1)
				f: for(int c = from; c <= to - pattern.length + 1; ++c) {
					for(int i = 0; i < pattern.length; ++i)
						if((matrix[matrix.length - 1 - c - i][s - c - i] << shift) != pattern[i])
							continue f;
					count++;					
				}
		}

		return count;
	}
	
	public static int countPattern(int[][] matrix, int[] pattern, Point midPoint, int shift) {
		int count = 0;

		f: for(int w = Math.max(0, midPoint.x - pattern.length - 1); w <= midPoint.x; ++w)
			if(matrix.length >= w + pattern.length) {
				for(int j = 0; j < pattern.length; ++j)
					if((matrix[w + j][midPoint.y] << shift) != pattern[j])
						continue f;
				count++;
			}

		f: for(int h = Math.max(0, midPoint.y - pattern.length - 1); h <= midPoint.y; ++h)
			if(matrix[0].length >= h + pattern.length) {
				for(int j = 0; j < pattern.length; ++j)
					if((matrix[midPoint.x][h + j] << shift) != pattern[j])
						continue f;
				count++;
			}
				
			int maxMinus = Math.min(midPoint.x, midPoint.y) < pattern.length ? 
					Math.min(midPoint.x, midPoint.y) : pattern.length - 1;
			int min = Math.min(matrix.length - midPoint.x - 1, matrix[0].length - midPoint.y - 1);
			int maxPlus = min < pattern.length ? min : pattern.length - 1;
			
			if(maxPlus + maxMinus >= pattern.length - 1)
				f: for(int i = maxMinus; i >= 0; --i) 
					if(midPoint.x + pattern.length - i <= matrix.length
							&& midPoint.y + pattern.length - i <= matrix[0].length){						
						for(int j = 0; j < pattern.length; ++j)
							if((matrix[midPoint.x - i + j][midPoint.y - i + j] << shift) != pattern[j])
								continue f;
						count++;
				}
			
			maxMinus = Math.min(matrix.length - midPoint.x - 1, midPoint.y) < pattern.length ? 
					Math.min(matrix.length - midPoint.x - 1, midPoint.y) : pattern.length - 1;				
			maxPlus = Math.min(midPoint.x, matrix[0].length - midPoint.y - 1) < pattern.length ? 
					Math.min(midPoint.x, matrix[0].length - midPoint.y - 1) : pattern.length - 1;
			
			if(maxPlus + maxMinus >= pattern.length - 1)
				f: for(int i = maxMinus; i >= 0; --i) 
					if(midPoint.x - pattern.length + i + 1 >= 0
							&& midPoint.y + pattern.length - i <= matrix[0].length) {		
						for(int j = 0; j < pattern.length; ++j)
							if((matrix[midPoint.x + i - j][midPoint.y - i + j] << shift) != pattern[j])
								continue f;
						count++;
				}
			
		return count;
	}
	
	public static Pair<Point, Point> searchForFive(int[][] matrix, Point midPoint, int shift) {
		int[] pattern = Patterns.FIVE;
		f: for(int w = Math.max(0, midPoint.x - pattern.length - 1); w <= midPoint.x; ++w)
			if(matrix.length >= w + pattern.length) {
				for(int j = 0; j < pattern.length; ++j)
					if((matrix[w + j][midPoint.y] << shift) != pattern[j])
						continue f;
				return new Pair<Point, Point>(
						new Point(w, midPoint.y),
						new Point(w + 4, midPoint.y));
			}
		
		f: for(int h = Math.max(0, midPoint.y - pattern.length - 1); h <= midPoint.y; ++h)
			if(matrix[0].length >= h + pattern.length) {
				for(int j = 0; j < pattern.length; ++j)
					if((matrix[midPoint.x][h + j] << shift) != pattern[j])
						continue f;
				return new Pair<Point, Point>(
						new Point(midPoint.x, h),
						new Point(midPoint.x, h + 4));
			}
				
		int maxMinus = Math.min(midPoint.x, midPoint.y) < pattern.length ? 
				Math.min(midPoint.x, midPoint.y) : pattern.length - 1;
		int min = Math.min(matrix.length - midPoint.x - 1, matrix[0].length - midPoint.y - 1);
		int maxPlus = min < pattern.length ? min : pattern.length - 1;
		
		if(maxPlus + maxMinus >= pattern.length - 1)
			f: for(int i = maxMinus; i >= 0; --i) 
				if(midPoint.x + pattern.length - i <= matrix.length
						&& midPoint.y + pattern.length - i <= matrix[0].length){						
					for(int j = 0; j < pattern.length; ++j)
						if((matrix[midPoint.x - i + j][midPoint.y - i + j] << shift) != pattern[j])
							continue f;
					return new Pair<Point, Point>(
							new Point(midPoint.x - i, midPoint.y - i),
							new Point(midPoint.x - i + 4, midPoint.y - i + 4));
			}
		
		maxMinus = Math.min(matrix.length - midPoint.x - 1, midPoint.y) < pattern.length ? 
				Math.min(matrix.length - midPoint.x - 1, midPoint.y) : pattern.length - 1;				
		maxPlus = Math.min(midPoint.x, matrix[0].length - midPoint.y - 1) < pattern.length ? 
				Math.min(midPoint.x, matrix[0].length - midPoint.y - 1) : pattern.length - 1;
		
		if(maxPlus + maxMinus >= pattern.length - 1)
			f: for(int i = maxMinus; i >= 0; --i) 
				if(midPoint.x - pattern.length + i + 1 >= 0
						&& midPoint.y + pattern.length - i <= matrix[0].length) {		
					for(int j = 0; j < pattern.length; ++j)
						if((matrix[midPoint.x + i - j][midPoint.y - i + j] << shift) != pattern[j])
							continue f;
					return new Pair<Point, Point>(
							new Point(midPoint.x + i, midPoint.y - i),
							new Point(midPoint.x + i - 4, midPoint.y - i + 4));
			}
		return null;
	}
}
