//Defining a node
class Node
{
int data;
Node next;

Node(int data)
{
	this.data = data;
	this.next = null;
}
}

public class LinkedListL1
{
static void main(String[] args)
{
	//Manually linking nodes
	Node head = new Node(10);
	Node nodeB = new Node(20);
	Node nodeC = new Node(30);

	head.next = nodeB;
	nodeB.next = nodeC;

	//Traversing the linked list
	Node current = head;
	while(current != null)
	{
		System.out.print(current.data + " -> ");
		current = current.next;
	}
	System.out.println("null");
}
}
