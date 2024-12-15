var parts = File.ReadAllText("input.txt").Split("\n\n");

var grid = parts[0].Split("\n")
    .SelectMany(
        (line, row) =>
            line.Select((ch, col) => new { Coordinate = new XY(row, col), Character = ch })
    )
    .ToDictionary(item => item.Coordinate, item => item.Character);
var originalGrid = grid.ToDictionary(kvp => kvp.Key, kvp => kvp.Value);

var moves = string.Concat(parts[1].Where(c => !char.IsWhiteSpace(c)));

foreach (var move in moves)
{
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
}

var result = grid.Aggregate(0L, (acc, kvp) => acc + kvp.Value switch
{
    'O' => kvp.Key.X + (100 * kvp.Key.Y),
    _ => 0
});

Console.WriteLine(result);

// Part 2
var doubleGrid = originalGrid
    .SelectMany(kvp => new [] {
        new KeyValuePair<XY, char>(
            new XY(kvp.Key.Y, kvp.Key.X * 2),
             kvp.Value switch {
                '.' => '.',
                '#' => '#',
                'O' => '[',
                '@' => '@',
                _ => throw new Exception("Invalid character")
            }
        ),
        new KeyValuePair<XY, char>(
            new XY(kvp.Key.Y, kvp.Key.X * 2 + 1),
            kvp.Value switch {
                '.' => '.',
                '#' => '#',
                'O' => ']',
                '@' => '.',
                _ => throw new Exception("Invalid character")
            }
        )
    })
    .ToDictionary(kvp => kvp.Key, kvp => kvp.Value);

foreach (var move in moves)
{
    var start = doubleGrid.Keys.First(k => doubleGrid[k] == '@');

    var dir = move switch
    {
        '^' => new XY(-1, 0),
        'v' => new XY(1, 0),
        '<' => new XY(0, -1),
        '>' => new XY(0, 1),
        _ => throw new Exception("Invalid move")
    };

    if (dir.Y == 0) {
        var toCheck = start;
        var toMove = new List<XY>();

        // Horizontal move
        while (true)
        {
            var next = toCheck + dir;
            if (doubleGrid.TryGetValue(next, out var nextChar))
            {
                if (nextChar == '.')
                {
                    toMove.Add(toCheck);
                    toMove.Add(next);
                    break;
                }
                else if (nextChar == '[' || nextChar == ']')
                {
                    toMove.Add(toCheck);
                    toMove.Add(next);
                    toCheck = next;
                    next = toCheck + dir;
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
                doubleGrid[moved] = '.';
            } else {
                doubleGrid[moved] = doubleGrid[prev];
            }
        }
    } else {
        // Vertical move
        var toCheck = new Queue<XY>();
        var toMove = new List<XY>();
        toCheck.Enqueue(start);
        while (toCheck.Count > 0)
        {
            var next = toCheck.Dequeue();
            var nextChar = doubleGrid[next];
            if (nextChar == '.')
            {
                continue;
            }
            else if (nextChar == '[')
            {
                if (doubleGrid[next + dir] != '#' && doubleGrid[next + dir + new XY(0, 1)] != '#') {
                    toMove.Add(next);
                    toMove.Add(next + new XY(0, 1));
                    toCheck.Enqueue(next + dir);
                    toCheck.Enqueue(next + dir + new XY(0, 1));
                } else {
                    toMove.Clear();
                    break;
                }
            }
            else if (nextChar == ']')
            {
                if (doubleGrid[next + dir] != '#' && doubleGrid[next + dir + new XY(0, -1)] != '#') {
                    toMove.Add(next);
                    toMove.Add(next + new XY(0, -1));
                    toCheck.Enqueue(next + dir);
                    toCheck.Enqueue(next + dir + new XY(0, -1));
                } else {
                    toMove.Clear();
                    break;
                }
            }
            else if (nextChar == '@')
            {
                if (doubleGrid[next + dir] != '#') {
                    toMove.Add(next);
                    toCheck.Enqueue(next + dir);
                } else {
                    toMove.Clear();
                    break;
                }
            }
        }
        toMove = toMove.Distinct().Reverse().ToList();
        foreach (var moved in toMove) {
            var next = moved + dir;
            doubleGrid[next] = doubleGrid[moved];
            doubleGrid[moved] = '.';
        }
    }
}

var result2 = doubleGrid.Aggregate(0L, (acc, kvp) => acc + kvp.Value switch
{
    '[' => kvp.Key.X + (100 * kvp.Key.Y),
    _ => 0
});

Console.WriteLine(result2);

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
