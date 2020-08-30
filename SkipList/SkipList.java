// Patrick Sherbondy
// COP 3503C, Fall 2019
// NID: pa867244

import java.util.ArrayList;

// The Node class can hold any comparable data type. Each node can hold one piece of data, and will
// have a randomly generated height. Nodes are placed within the skip list within sorted order.
class Node<AnyType>
{
	AnyType data;
	int height;
	// Each entry in the ArrayList points to whichever node it first encounters at a given level.
	ArrayList<Node<AnyType>> next;
	// The prev pointer holds a reference to the node that comes before this given node, but only
	// at the most bottom level. This is useful for deleting duplicates in the skip list.
	Node<AnyType> prev;

	// This constructor creates a new node with a designated height. This constructor does not handle
	// assigning data to a newly-created node.
	Node(int height)
	{
		this.height = height;
		this.next = new ArrayList<>(height);

		// Initialize the array list pointers.
		for (int i = 0; i < height; i++)
		{
			this.next.add(null);
		}

		prev = null;
	}

	// This constructor creates a new node by calling the previous constructor, and then assigning
	// the node with the data passed to it.
	Node(AnyType data, int height)
	{
		this.data = data;
		this.height = height;
		this.next = new ArrayList<>(height);

		// Initialize the array list pointers.
		for (int i = 0; i < height; i++)
		{
			this.next.add(null);
		}

		prev = null;
	}

	// This is a getter method that returns the data stored in the given node.
	public AnyType value()
	{
		return data;
	}

	// This is a getter method that returns the height of the given node.
	public int height()
	{
		return height;
	}

	// This method returns the reference to the next node at the given level.
	// If the level is less than 0, or greater than the height of the given node, it returns null.
	// Node levels are numbered 0 through height - 1, from bottom to top.
	public Node<AnyType> next(int level)
	{
		if (level < 0 || level > height - 1)
		{
			return null;
		}

		return next.get(level);
	}

	// AUXILIARY METHODS (Suggested)

	// This method sets the next node for a given node at a given level.
	public void setNext(int level, Node<AnyType> node)
	{
		next.set(level, node);
	}

	// This method grows a given node by a height of one, effectively adding a null reference
	// on top of the next array list.
	public void grow()
	{
		next.add(null);
		this.height++;
	}

	// This method performs a virtual coin flip. It is used to see if a given node will grow by a
	// height of one. It grows the node by one and returns true. Otherwise it returns false.
	public boolean maybeGrow()
	{
		if (Math.random() < 0.5)
		{
			grow();
			return true;
		}

		return false;
	}

	// This method trims the given node by one, and decreases its height accordingly.
	public void trim()
	{
		next.remove(this.height - 1);
		this.height--;
	}

	// This method is similar to the trim() method, but instead of removing only the topmost node,
	// it removes all nodes starting from the top until the targetHeight is reached.
	public void trim(int targetHeight)
	{
		for (int i = this.height - 1; i >= targetHeight; i--)
		{
			next.remove(i);
			this.height--;
		}
	}
}

// This class creates and manages the skip list itself. The skip list is composed of multiple
// objects from the Node class.
public class SkipList<AnyType extends Comparable<AnyType>>
{
	int size;
	int height;
	Node<AnyType> head;

	// This constructor creates an entirely new SkipList, initializing size and height to 0.
	SkipList()
	{
		size = 0;
		height = 1;
		head = new Node<>(this.height);
	}

	// This constructor creates an entirely new SkipList, but with a head node starting with a
	// specified height.
	SkipList(int height)
	{
		size = 0;

		// Ensure the height passed to the method is legal. Nodes can have a minimum height of one,
		// and the height of the skip list is the height of tallest node.
		if (height <= 1)
		{
			height = 1;
		}
		else
		{
			this.height = height;
		}

		head = new Node<>(height);
	}

	// This is a getter method that returns the total number of nodes in the SkipList.
	public int size()
	{
		return size;
	}

	// This is a getter method that returns the height of the tallest node in the SkipList.
	public int height()
	{
		return height;
	}

	// This is a getter method that returns a reference to the head of the SkipList.
	public Node<AnyType> head()
	{
		return head;
	}

	// This method inserts a piece of data, with the height of the node left up to random chance.
	public void insert(AnyType data)
	{
		int height = generateRandomHeight(this.height);
		insert(data, height);
	}

	// Thsi method inserts a piece of data with a determined height for the node being inserted.
	public void insert(AnyType data, int height)
	{
		Node<AnyType> temp = head;
		Node<AnyType> node = new Node<>(data, height);

		// Increase the size of the skip list first, since there is never a case where we won't be
		// able to insert an element into the skip list.
		this.size++;
		int maxHeight = getMaxHeight(this.size);

		// Check to see if the increase in the size of the skip list causes us to increase the max
		// height any given node can be. If so, visit each node that was maximally tall before and
		// flip a coin to see if it will grow.
		if (maxHeight > this.height)
		{
			this.height = maxHeight;
			growSkipList();
		}

		int level = this.height - 1;

		// Travel through the skip list to find the insertion point of the new node, starting at the
		// head and the highest level.
		while (level >= 0)
		{
			// If the next node we're looking at is null or is greater than or equal to the data we
			// want to insert, we need to start thinking about insertion.
			if (temp.next.get(level) == null || temp.next.get(level).data.compareTo(data) >= 0)
			{
				// If height of the to-be-inserted node is less than or equal to the level we're operating
				// on, we definitely need to start inserting the new node, starting from the topmost
				// level of the new node.
				if (level <= height - 1)
				{
					node.next.set(level, temp.next.get(level));
					temp.next.set(level, node);
					node.prev = temp;
				}

				// Move down a level to start a new evaluation.
				level--;
			}
			// If the node we're looking at it less than the data we want to insert, we move to that
			// next node to try to find the correct spot.
			else if (temp.next.get(level).data.compareTo(data) < 0)
			{
				temp = temp.next.get(level);
			}
		}

		// Set the node directly after the inserted node's previous pointer to the new node itself,
		// so long as there is a node after the new node.
		if (node.next.get(0) != null)
		{
			node.next.get(0).prev = node;
		}
	}

	// This method searches through the skip list for a node to delete with the same value as what
	// is passed to this method. If there does not exist a node in the skip list with the data
	// we want to delete, this method returns without changing any aspect of the skip list.
	// This method will ONLY delete the first instance it encounters of the target data.
	public void delete(AnyType data)
	{
		Node<AnyType> temp = head;
		Node<AnyType> tempNext = null;
		boolean nodeTerminated = false;
		int level = this.height - 1;

		// Start at the highest level in the skip list, and comb through it searching for the node
		// to be deleted.
		while (level >= 0)
		{
			// If the next node is null or greater than the target data, decrease a level.
			if (temp.next.get(level) == null || temp.next.get(level).data.compareTo(data) > 0)
			{
				level--;
			}
			// If the next node is less than the target data, move to that next node.
			else if (temp.next.get(level).data.compareTo(data) < 0)
			{
				temp = temp.next.get(level);
			}
			// If the next node contains the target, start thinking destructive thoughts of deletion!
			else if (temp.next.get(level).data.compareTo(data) == 0)
			{
				// Declare this to make lines less long. :)
				Node<AnyType> prev = temp.next.get(level).prev;

				// If the data in the previous node is not null and is equal to the target data,
				// move down a level. This means we've found a duplicate, and this method ONLY deletes
				// the first duplicate in the skip list.
				if (prev.data != null && prev.data.compareTo(data) == 0)
				{
					level--;
				}
				// Otherwise, start chipping away at the node to be deleted starting from the top level,
				// and ensuring its references are moved safely around to perserve the structure of the
				// skip list.
				else
				{
					tempNext = temp.next.get(level).next.get(level);
					temp.next.get(level).trim();
					temp.next.set(level, tempNext);

					nodeTerminated = true;
					level--;
				}
			}
		}

		// If a node was indeed terminated, then we need to evaluate the height of the skip list to see
		// if the heights need to be trimmed at all.
		if (nodeTerminated)
		{
			this.size--;
			int maxHeight = 0;

			if (this.size <= 1)
			{
				maxHeight = 1;
			}
			else
			{
				maxHeight = getMaxHeight(this.size);
			}

			if (maxHeight < this.height)
			{
				trimSkipList(maxHeight);
				this.height = maxHeight;
			}
		}

		return;
	}

	// This method searches for an instance of a given piece of data, and returns true if it finds
	// at least one instance of that data. Otherwise, it returns false.
	public boolean contains(AnyType data)
	{
		if (get(data) != null)
		{
			return true;
		}

		return false;
	}

	// This method returns the reference to the first node it finds matching the given data. It
	// returns null if it is unable to find a matching node.
	public Node<AnyType> get(AnyType data)
	{
		Node<AnyType> temp = head;
		int level = this.height - 1;

		// Starting at the highest level, search through the skip list for a node with the target data.
		while (level >= 0)
		{
			// If the next node is null or is greater than the target data, move down a level.
			if (temp.next.get(level) == null || temp.next.get(level).data.compareTo(data) > 0)
			{
				level--;
			}
			// If the next node is less than the target data, move to it.
			else if (temp.next.get(level).data.compareTo(data) < 0)
			{
				temp = temp.next.get(level);
			}
			// If the next node is equal to the target data, return its reference.
			else if (temp.next.get(level).data.compareTo(data) == 0)
			{
				return temp;
			}
		}

		// If we exit the loop without returning, we did not find the target data.
		return null;
	}

	// This method returns a double indicative of how difficult this assigment was for me.
	// 1.0 = stupid easy, 5.0 = AHHHHHHHHH
	public static double difficultyRating()
	{
		return 4.0;
	}

	// This method returns a double indicative of the number of hours (roughly) spent working
	// on this assignment.
	public static double hoursSpent()
	{
		return 18.0;
	}
	// AUXILIARY METHODS (Suggested)

	// This method returns the calculated maximum height the skip list can have based on the number
	// of nodes within the skip list. The formula is the ceiling of log2(n).
	private static int getMaxHeight(int n)
	{
		// If there is only one node in the skip list, return 1 since the following formula returns 0
		// when given 1, which is incorrect.
		if (n == 1)
		{
			return 1;
		}

		return (int)Math.ceil(Math.log(n) / Math.log(2));
	}

	// This method generates a random height by doing a virtual coin toss, given the maximum height
	// of the skip list. It will only perform the coin toss at most maxHeight time.
	// It returns the generated height after at most maxHeight coin tosses.
	private static int generateRandomHeight(int maxHeight)
	{
		int height = 1;

		// Perform the coin toss until we flip tails or reach the maxHeight.
		while (height <= maxHeight)
		{
			if (Math.random() < 0.5)
			{
				height++;
			}
			else
			{
				return height;
			}
		}

		return height;
	}

	// This method grows the skip list along the highest level of the nodes. At each node of the
	// (previously) maximum height, it performs a coin toss to see if the node gets to grow by a
	// height of one.
	private void growSkipList()
	{
		// When growing the skip list, we must always grow the head by one. The head node is always
		// maximally tall.
		head.grow();

		Node<AnyType> temp = head.next.get(this.height - 2);
		ArrayList<Node<AnyType>> tempNext = head.next;

		// Skim along the (previously) highest level, looking for the maximally tall nodes to increase
		// in height.
		while (temp != null)
		{
			if (temp.maybeGrow())
			{
				tempNext.set(this.height - 1, temp);
				tempNext = temp.next;
			}

			temp = temp.next.get(this.height - 2);
		}
	}

	// This method trims the skip list after a deletion drops the maximum height of the skip list.
	private void trimSkipList(int targetHeight)
	{
		int level = targetHeight - 1;

		// Ensure targetHeight is a realistic number. The smallest height a skip list can have is 1.
		if (targetHeight <= 1)
		{
			targetHeight = 1;
			level = 0;
		}

		Node<AnyType> temp = head.next.get(targetHeight - 1);

		// Skim across the highest level of the skip list, trimming down each maximally tall node to
		// the new maximum height.
		while (temp != null)
		{
			if (temp.height() >= targetHeight)
			{
				temp.trim(targetHeight);
			}

			temp = temp.next(targetHeight - 1);
		}

		// Trim down the head node to the targetHeight.
		head.trim(targetHeight);
	}
}
