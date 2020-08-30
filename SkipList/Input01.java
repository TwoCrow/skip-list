// This file inputs a lot of random values, then searches for one of them.

import java.io.*;
import java.util.*;

public class Input01
{
	public static void main(String [] args)
	{
		SkipList<Integer> skiplist = new SkipList<Integer>();

		// Insert many elements, which will take a while.
		for (int i = 0; i < 1000000; i++)
		{	
            skiplist.insert(RNG.getRandomInteger());
        }

		long totalTime = 0, start, end, total;

		int used = RNG.getRandomUsedInteger();

        System.out.println("Currently searching for " + used);

		boolean foundTarget = skiplist.contains(used);

        if (foundTarget)
        {
            System.out.println("Found " + used + "!");
        }
	}
}
