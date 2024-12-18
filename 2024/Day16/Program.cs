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
    var queue = new PriorityQueue<(XY Position, int Distance, XY PreviousDirection), int>();
    var visited = new Dictionary<XY, int>();
    queue.Enqueue((start, 0, new XY(0, 1)), 0);

    var best = int.MaxValue;
    var paths = 0;

    while (queue.Count > 0)
    {
        var (current, distance, previousDirection) = queue.Dequeue();

        foreach (var dir in XY.Dirs)
        {
            var next = current + dir;
            if (grid.ContainsKey(next) && grid[next] != '#')
            {
                int newDistance = distance + 1;
                if (previousDirection != dir)
                {
                    newDistance += 1000; // Add turning cost
                }

                if (newDistance > best)
                {
                    Console.WriteLine($"Paths: {paths}");
                    return best;
                }

                if (next == end)
                {
                    best = newDistance;
                    paths++;
                }

                queue.Enqueue((next, newDistance, dir), newDistance);
            }
        }
    }

    return -1;
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
