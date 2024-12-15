var parts = File.ReadAllText("input.txt").Split("\n\n");

var grid = parts[0].Split("\n")
    .SelectMany(
        (line, row) =>
            line.Select((ch, col) => new { Coordinate = new XY(row, col), Character = ch })
    )
    .ToDictionary(item => item.Coordinate, item => item.Character);

var moves = string.Concat(parts[1].Where(c => !char.IsWhiteSpace(c)));

foreach (var move in moves)
{
    var control = grid.Count(kvp => kvp.Value == 'O');
    var gridCopy = grid.ToDictionary(kvp => kvp.Key, kvp => kvp.Value);

    var start = grid.Keys.First(k => grid[k] == '@');

    var dir = move switch
    {
        '^' => new XY(-1, 0),
        'v' => new XY(1, 0),
        '<' => new XY(0, -1),
        '>' => new XY(0, 1),
        _ => throw new Exception("Invalid move")
    };

    var toCheck = start;
    var toMove = new List<XY>();
    while (true)
    {
        var next = toCheck + dir;
        if (grid.TryGetValue(next, out var nextChar))
        {
            if (nextChar == '.')
            {
                toMove.Add(toCheck);
                toMove.Add(next);
                break;
            }
            else if (nextChar == 'O')
            {
                toMove.Add(toCheck);
            }
            else if (nextChar == '#')
            {
                toMove.Clear();
                break;
            }
        }
        
        toCheck = next;
    }

    toMove.Reverse();
    foreach (var moved in toMove) {
        var prev = moved - dir;
        if (moved == start) {
            grid[moved] = '.';
        } else {
            grid[moved] = grid[prev];
        }
    }

    var control2 = grid.Count(kvp => kvp.Value == 'O');
    if (control != control2) {
        Render(gridCopy);
        Console.WriteLine(dir);
        Console.WriteLine();
        Render(grid);
        throw new Exception("Control mismatch");
    }

}

var result = grid.Aggregate(0L, (acc, kvp) => acc + kvp.Value switch
{
    'O' => kvp.Key.X + (100 * kvp.Key.Y),
    _ => 0
});

Console.WriteLine(result);

void Render(Dictionary<XY, char> map) {
    foreach (var row in Enumerable.Range(0, map.Keys.Max(k => k.Y) + 1))
    {
        foreach (var col in Enumerable.Range(0, map.Keys.Max(k => k.X) + 1))
        {
            Console.Write(map.TryGetValue(new XY(row, col), out var ch) ? ch : ' ');
        }
        Console.WriteLine();
    }
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
