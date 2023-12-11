var input = File.ReadLines("input.txt");

var grid = input
    .SelectMany((line, row) => line.Select((c, col) => (XY: new XY(row, col), Char: c)))
    .ToDictionary(x => x.XY, x => new Tile(x.XY, x.Char));

var start = grid.First(el => el.Value.Char == 'S').Value;

IEnumerable<Tile> Walk(Tile start, Dictionary<XY, Tile> map)
{
    var visited = new HashSet<XY>([start.XY]);
    var next = start.Adjacent(map).Last();
    yield return start;
    while (next != null && !visited.Contains(next))
    {
        visited.Add(next);
        yield return map[next];
        next = map[next].Adjacent(map).Where(xy => !visited.Contains(xy)).FirstOrDefault();
    }
}

Console.WriteLine(Walk(start, grid).Count() / 2);

var loop = Walk(start, grid).ToList();
var loopSet = loop.Select(tile => tile.XY).ToHashSet();
var looped = loop.Zip(loop.Skip(1)).Aggregate(new HashSet<XY>(), (inside, next) =>
{
    var prev = next.First;
    var current = next.Second;
    var offset = next.Second.XY - next.First.XY;

    IEnumerable<XY> Expand(IEnumerable<XY> xy)
    {
        var cleaned = xy.Where(xy => !loopSet.Contains(xy));
        Queue<XY> queue = new(cleaned);
        HashSet<XY> visited = [.. cleaned];

        while (queue.Count > 0)
        {
            XY currentState = queue.Dequeue();
            foreach (var nextState in from XY nextState in currentState.Adjacent()
                                      where !visited.Contains(nextState) && grid.ContainsKey(nextState) && !loopSet.Contains(nextState)
                                      select nextState)
            {
                visited.Add(nextState);
                queue.Enqueue(nextState);
            }
        }

        return visited;
    }

    List<XY> seed = current.Char switch
    {
        '-' => offset.X switch
        {
            1 => [new XY(1, 0)],
            -1 => [new XY(-1, 0)],
        },
        '|' => offset.Y switch
        {
            1 => [new XY(0, -1)],
            -1 => [new XY(0, 1)],
        },
        'F' => offset switch
        {
            XY(0, -1) => [new XY(-1, 0), new XY(0, -1), new XY(-1, -1)],
            XY(-1, 0) => [new XY(1, 1)]
        },
        'L' => offset switch
        {
            XY(0, -1) => [new XY(-1, 1)],
            XY(1, 0) => [new XY(0, -1), new XY(1, -1), new XY(1, 0)]
        },
        'J' => offset switch
        {
            XY(1, 0) => [new XY(-1, -1)],
            XY(0, 1) => [new XY(1, 0), new XY(1, 1), new XY(0, 1)]
        },
        '7' => offset switch
        {
            XY(-1, 0) => [new XY(0, 1), new XY(-1, 1), new XY(-1, 0)],
            XY(0, 1) => [new XY(1, -1)]
        },
        'S' => []
    };

    foreach (var visited in Expand(seed.Select(x => x + current.XY)))
    {
        inside.Add(visited);
    }

    return inside;
});

Console.WriteLine(looped.Count);

record Tile(XY XY, char Char)
{
    public IEnumerable<XY> Adjacent(Dictionary<XY, Tile> map)
    {
        var offsets = Char switch
        {
            '|' => [new XY(-1, 0), new XY(1, 0)],
            '-' => [new XY(0, -1), new XY(0, 1)],
            'F' => [new XY(1, 0), new XY(0, 1)],
            'L' => [new XY(-1, 0), new XY(0, 1)],
            'J' => [new XY(-1, 0), new XY(0, -1)],
            '7' => [new XY(1, 0), new XY(0, -1)],
            'S' => XY.Dirs.Where(x => map.ContainsKey(XY + x) && map[XY + x].Adjacent(map).Contains(XY)),
            '.' => [],
            _ => throw new NotImplementedException($"Unexpected char {Char} at {XY}"),
        };

        return offsets.Select(o => o + XY);
    }

    public override string ToString() => $"{XY}: {Char}";
}

record XY(int Y, int X)
{
    public XY((int, int) XY) : this(XY.Item1, XY.Item2)
    {

    }

    public static XY operator +(XY me, XY other) => new(Y: me.Y + other.Y, X: me.X + other.X);
    public static XY operator -(XY me, XY other) => new(Y: me.Y - other.Y, X: me.X - other.X);

    public static readonly XY[] Dirs = [new(-1, 0), new(0, -1), new(0, 1), new(1, 0)];

    public IEnumerable<XY> Adjacent() => Dirs.Select(dir => this + dir);
}