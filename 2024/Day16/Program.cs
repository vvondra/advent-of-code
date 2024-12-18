using System.Reflection;

var grid = File.ReadLines("input.txt")
    .SelectMany(
        (line, row) =>
            line.Select((ch, col) => new { Coordinate = new XY(row, col), Character = ch })
    )
    .ToDictionary(item => item.Coordinate, item => item.Character);

var start = grid.Keys.First(k => grid[k] == 'S');
var end = grid.Keys.First(k => grid[k] == 'E');

var result = FindShortestPath();
Console.WriteLine(result);

int FindShortestPath()
{
    var queue = new PriorityQueue<(XY Position, int Distance, XY PreviousDirection, (XY, XY)? prev), int>();
    var shortest = new Dictionary<(XY, XY), int>();
    var previous = new Dictionary<(XY, XY), HashSet<(XY, XY)?>>();
    queue.Enqueue((start, 0, new XY(0, 1), null), 0);

    var best = int.MaxValue;
    var paths = 0;

    while (queue.Count > 0)
    {
        var (current, distance, previousDirection, prev) = queue.Dequeue();

        if (!shortest.ContainsKey((current, previousDirection)))
        {
            shortest[(current, previousDirection)] = distance;
            previous[(current, previousDirection)] = [prev];
        } else if (shortest[(current, previousDirection)] == distance) {
            previous[(current, previousDirection)].Add(prev);
        }

        foreach (var dir in XY.Dirs.Where(dir => dir != previousDirection.Opposite))
        {
            var next = previousDirection != dir ? current : current + dir;

            if (grid.ContainsKey(next) && grid[next] != '#')
            {
                int newDistance = previousDirection != dir ? distance + 1000 : distance + 1;

                if (newDistance > best && queue.Peek().Distance >= newDistance)
                {
                    Console.WriteLine(DistinctXYs(previous));
                    return best;
                }

                if (next == end && newDistance <= best)
                {
                    best = newDistance;
                    paths++;
                }

                if (!previous.ContainsKey((next, dir.Opposite))) {
                    queue.Enqueue((next, newDistance, dir, (current, previousDirection)), newDistance);
                }
            }
        }
    }

    return best;
}

int DistinctXYs(Dictionary<(XY, XY), HashSet<(XY, XY)?>> previous)
{
    var queue = new Queue<(XY, XY)>();
    previous.Keys.Where(k => k.Item1 == end).ToList().ForEach(queue.Enqueue);

    var visited = new HashSet<XY> { end };

    while (queue.Count > 0)
    {
        var current = queue.Dequeue();

        foreach (var prev in previous[current])
        {
            if (prev.HasValue)
            {
                visited.Add(prev.Value.Item1);
                queue.Enqueue(prev.Value);
            }
        }
    }

    return visited.Count;
}

record XY(int Y, int X)
{
    public XY((int, int) XY)
        : this(XY.Item1, XY.Item2) { }

    public static XY operator +(XY me, XY other) => new(Y: me.Y + other.Y, X: me.X + other.X);

    public static XY operator -(XY me, XY other) => new(Y: me.Y - other.Y, X: me.X - other.X);

    public XY Opposite => new(-Y, -X);

    public static readonly XY[] Dirs = [new(-1, 0), new(0, -1), new(0, 1), new(1, 0)];

    public IEnumerable<XY> Adjacent() => Dirs.Select(dir => this + dir);

    public override string ToString() => $"({Y}, {X})";
}
