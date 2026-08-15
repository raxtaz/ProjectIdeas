import java.util.Collections;
import java.util.PriorityQueue;

public class PriorityQueueDemo
{
	public static void main(String[] args)
	{
		//1. Default min-heap | natural order: smallest element has highest priority
		PriorityQueue<Integer> minHeap = new PriorityQueue<>();

		// Add elements in arbitary order
		minHeap.offer(40);
		minHeap.offer(10);
		minHeap.offer(30);
		minHeap.offer(20);

		System.out.println("---Min-Heap (Ascending Retreival)---");
		// peek() inspects highest-priority element w/o removal
		System.out.println("Head element (peak): " + minHeap.peek());

		// poll() removes & returns elements in priority order
		while(!minHeap.isEmpty())
		{
			System.out.println(minHeap.poll());
		}
		System.out.println("\n");

		//2. Custom Max-Heap (Reverse order: Largest element has highest priority
		PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

		maxHeap.offer(40);
		maxHeap.offer(10);
		maxHeap.offer(30);
		maxHeap.offer(20);

		System.out.println("---Max-Heap (Descending Retreival)---");
		System.out.println("Head element (peak): " + maxHeap.peek());

		while(!maxHeap.isEmpty())
		{
			System.out.println(maxHeap.poll() + " ");
		}
	}
}