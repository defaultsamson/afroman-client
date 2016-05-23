package ca.afroman.util;

import java.util.Comparator;
import java.util.List;

public class ListIO
{
	public static <T> void sort(List<T> list, Comparator<T> compare)
	{
		// https://en.wikipedia.org/wiki/Selection_sort
		
		int n = list.size();
		
		/* advance the position through the entire array */
		/* (could do j < n-1 because single element is also min element) */
		for (int j = 0; j < n - 1; j++)
		{
			/* find the min element in the unsorted a[j .. n-1] */
			
			/* assume the min is the first element */
			int iMin = j;
			/* test against elements after j to find the smallest */
			for (int i = j + 1; i < n; i++)
			{
				/* if this element is less, then it is the new minimum */
				if (compare.compare(list.get(iMin), list.get(i)) > 0)
				{
					/* found new minimum; remember its index */
					iMin = i;
				}
			}
			
			if (iMin != j)
			{
				T temp = list.get(j);
				list.set(j, list.get(iMin));
				list.set(iMin, temp);
				
				// swap(a[j], a[iMin]);
			}
		}
	}
}
