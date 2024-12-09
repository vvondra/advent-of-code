using System.Text;

var input = File.ReadAllText("input.txt").Trim().ToCharArray();

var (head, tail) = CreateList(input);

Console.WriteLine(Print(head));
head = Compress(head, tail);

Console.WriteLine(head.Checksum());

static Node Compress(Node head, Node tail)
{
    var empty = FirstEmpty(head);
    var full = LastFull(tail);

    var last = tail;
    while (true)
    {
        if (full == null || empty == null)
        {
            break;
        }

        if (empty.Size <= full.Size)
        {
            empty.Id = full.Id;
            full.Size -= empty.Size;
            empty = FirstEmpty(head);
            full = LastFull(tail);
        }
        else if (empty.Size > full.Size)
        {
            var node = new Node(full.Size, full.Id);
            empty.Previous.Next = node;
            node.Next = empty;
            node.Previous = empty.Previous;
            empty.Previous = node;

            empty.Size -= full.Size;
            full.Size = 0;
            full = LastFull(tail);
        }
    }

    return head;
}

static string Print(Node head)
{
    var current = head;
    var result = new StringBuilder();
    while (current != null)
    {
        result.Append(current);
        current = current.Next;
    }
    return result.ToString();
}

static Node FirstEmpty(Node head)
{
    var current = head;
    while (current != null)
    {
        if (current.IsFreeSpace && current.Size > 0)
        {
            return current;
        }
        current = current.Next;
    }
    return null;
}

static Node LastFull(Node tail)
{
    var current = tail;
    while (current != null)
    {
        if (!current.IsFreeSpace && current.Size > 0)
        {
            return current;
        }
        current = current.Previous;
    }
    return null;
}

static (Node head, Node tail) CreateList(char[] input)
{
    var head = new Node(0);
    var node = head;
    Node tail = null;
    var index = 0;

    foreach (var c in input)
    {
        var newNode = node.IsFreeSpace ? new Node(c - '0', index++) : new Node(c - '0');
        node.Next = newNode;
        newNode.Previous = node;
        node = newNode;
    }

    tail = node; // Set the tail to the last node

    return (head, tail);
}

public class Node
{
    public int Size { get; set; }
    public int? Id { get; set; }
    public Node Next { get; set; }
    public Node Previous { get; set; }

    public Node(int size, int? id = null)
    {
        Size = size;
        Id = id;
        Next = null;
        Previous = null;
    }

    public bool IsFreeSpace => Id == null;

    public long Checksum()
    {
        long checksum = 0;
        var current = this;
        int position = 0;

        while (current != null)
        {
            if (current.Size > 0 && current.Id.HasValue)
            {
                for (int i = 0; i < current.Size; i++)
                {
                    checksum += current.Id.Value * (position + i);
                }
                position += current.Size;
            }
            current = current.Next;
        }

        return checksum;
    }

    public override string ToString()
    {
        return IsFreeSpace ? new string('.', Size) : new string(Id.ToString()[0], Size);
    }
}
