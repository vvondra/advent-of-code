using System.Text;

var input = File.ReadAllText("input.txt").Trim().ToCharArray();

var (head, tail) = CreateList(input);
head = CompressBlocks(head, tail);
Console.WriteLine(head.Checksum());

var (head2, tail2) = CreateList(input);

head2 = CompressFiles(head2, tail2);
Console.WriteLine(head2.Checksum());

static Node CompressBlocks(Node head, Node tail)
{
    var empty = head.FirstEmpty();
    var full = tail.LastFull();
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
            empty = head.FirstEmpty();
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
        }

        full = tail.LastFull();
    }

    return head;
}

static Node CompressFiles(Node head, Node tail)
{
    var full = tail.LastFullNotMoved();
    var empty = head.FirstEmpty();

    while (full != null)
    {
        if (empty == null)
        {
            empty = head.FirstEmptyBeforeFull(full);
            full = full?.Previous?.LastFullNotMoved();

        }
        else if (empty.Size < full.Size)
        {
            empty = empty?.Next?.FirstEmptyBeforeFull(full);
        }
        else if (empty.Size >= full.Size)
        {
            var node = new Node(full.Size, full.Id);
            node.Moved = true;
            empty.Previous.Next = node;
            node.Next = empty;
            node.Previous = empty.Previous;
            empty.Previous = node;

            empty.Size -= full.Size;
            full.Id = null;
            full = full.LastFullNotMoved();
            empty = head.FirstEmptyBeforeFull(full);
        }
    }

    return head;
}

static (Node head, Node tail) CreateList(char[] input)
{
    var head = new Node(0);
    var node = head;
    Node? tail = null;
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
    public Node? Next { get; set; }
    public Node? Previous { get; set; }

    public bool Moved { get; set; } = false;

    public Node(int size, int? id = null)
    {
        Size = size;
        Id = id;
        Next = null;
        Previous = null;
    }

    public bool IsFreeSpace => Id == null;

    public Node? FirstEmpty()
    {
        var current = this;
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

    public Node? FirstEmptyBeforeFull(Node full)
    {
        var current = this;
        while (current != null && current != full)
        {
            if (current.IsFreeSpace && current.Size > 0)
            {
                return current;
            }
            current = current.Next;
        }
        return null;
    }

    public Node? LastFull()
    {
        var current = this;
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

    public Node? LastFullNotMoved()
    {
        var current = this;
        while (current != null)
        {
            if (!current.IsFreeSpace && current.Size > 0 && !current.Moved)
            {
                return current;
            }
            current = current.Previous;
        }
        return null;
    }

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
            if (current.IsFreeSpace)
            {
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
