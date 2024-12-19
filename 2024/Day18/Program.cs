var input = File.ReadLines("input.txt")
    .Select(x => new XY(x.Split(',').Select(int.Parse)))
    .ToList();

var start = new XY(0, 0);
var max = 70;
var end = new XY(max, max);

int Distance(int bytes) {
    var obstacles = input.Take(bytes).ToHashSet();
    var queue = new Queue<(XY, int)>();
    var visitied = new HashSet<XY>();
    queue.Enqueue((start, 0));
    visitied.Add(start);
    while (queue.TryDequeue(out var current))
    {
        var (pos, steps) = current;
        if (pos == end)
        {
            return steps;
        }
        foreach (var dir in XY.Dirs)
        {
            var next = pos + dir;
            if (next.X > max || next.Y > max || next.X < 0 || next.Y < 0 || obstacles.Contains(next)) {
                continue;
            }

            if (visitied.Contains(next)) {
                continue;
            }
            
            visitied.Add(next);
            queue.Enqueue((next, steps + 1));
        }
    }

    return -1;
}

Console.WriteLine(Distance(1024));

var first = Enumerable.Range(1024, input.Count).First(x => Distance(x) == -1);
Console.WriteLine(input[first - 1]);

record XY(int Y, int X)
{
    public XY((int, int) XY) : this(XY.Item1, XY.Item2) { }

    public XY(IEnumerable<int> strings) : this(strings.ElementAt(0), strings.ElementAt(1)) { }

    public static XY operator +(XY me, XY other) => new(Y: me.Y + other.Y, X: me.X + other.X);

    public static XY operator -(XY me, XY other) => new(Y: me.Y - other.Y, X: me.X - other.X);

    public XY Opposite => new(-Y, -X);

    public static readonly XY[] Dirs = [new(-1, 0), new(0, -1), new(0, 1), new(1, 0)];

    public IEnumerable<XY> Adjacent() => Dirs.Select(dir => this + dir);

    public override string ToString() => $"({Y}, {X})";
}
