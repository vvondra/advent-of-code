var input = File.ReadLines("input.txt");

var grid = input
    .SelectMany((line, row) => line.Select((ch, col) => new { Coordinate = (X: row, Y: col), Character = ch }))
    .ToDictionary(item => item.Coordinate, item => item.Character);

int ScoreGrid(Dictionary<(int X, int Y), char> grid)
{
    var rows = grid.Keys.Max(x => x.X);
    return grid.Where(kvp => kvp.Value == 'O').Sum(kvp => rows - kvp.Key.X + 1);
}

Console.WriteLine(ScoreGrid(SettleGrid(grid)));
Console.WriteLine(ScoreGrid(CycleGrid(grid)));

Dictionary<(int X, int Y), char> SettleGrid(Dictionary<(int X, int Y), char> grid, int direction = 0)
{
    List<Func<(int X, int Y), (int X, int Y)>> modifiers =
[
    coordinate => (coordinate.X - 1, coordinate.Y),
            coordinate => (coordinate.X, coordinate.Y - 1),
            coordinate => (coordinate.X + 1, coordinate.Y),
            coordinate => (coordinate.X, coordinate.Y + 1)
];

    Dictionary<(int X, int Y), char> previousGrid = grid;
    Dictionary<(int X, int Y), char> candidate = grid;
    do
    {
        previousGrid = candidate;
        candidate = previousGrid.Select(kvp =>
            (kvp.Key, kvp.Value switch
            {
                'O' => (previousGrid.ContainsKey(modifiers[direction](kvp.Key)) && previousGrid[modifiers[direction](kvp.Key)] == '.') ? '.' : 'O',
                '.' => (previousGrid.ContainsKey(modifiers[(direction + 2) % 4](kvp.Key)) && previousGrid[modifiers[(direction + 2) % 4](kvp.Key)] == 'O') ? 'O' : '.',
                '#' => '#',

            })
        ).ToDictionary(x => x.Key, x => x.Item2);
    } while (IsGridChanged(previousGrid, candidate));

    return candidate;
}

Dictionary<(int X, int Y), char> CycleGrid(Dictionary<(int X, int Y), char> grid)
{
    Dictionary<(int X, int Y), char> next = grid;
    Dictionary<Dictionary<(int X, int Y), char>, int> memo = new(new DictionaryComparer());

    for (int i = 0; i < 1_000_000_000; i++)
    {
        if (memo.TryGetValue(next, out int value))
        {
            int cycleLength = i - value;
            int remainingIterations = 1_000_000_000 - i;
            int cycleOccurrences = remainingIterations / cycleLength;
        
            i += cycleLength * cycleOccurrences;
        }
        else
        {
            memo[next] = i;
        }

        for (var j = 0; j < 4; j++)
        {
            next = SettleGrid(next, j);
        }
    }

    return next;
}

static bool IsGridChanged(Dictionary<(int X, int Y), char> previousGrid, Dictionary<(int X, int Y), char> currentGrid)
{
    return previousGrid.Any(kvp => kvp.Value != currentGrid[kvp.Key]);
}

class DictionaryComparer : IEqualityComparer<Dictionary<(int X, int Y), char>>
{
    public bool Equals(Dictionary<(int X, int Y), char> x, Dictionary<(int X, int Y), char> y)
    {
        if (x.Count != y.Count)
            return false;

        foreach (var pair in x)
        {
            if (!y.TryGetValue(pair.Key, out var value) || !value.Equals(pair.Value))
                return false;
        }

        return true;
    }

    public int GetHashCode(Dictionary<(int X, int Y), char> obj)
    {
        int hash = 0;

        foreach (var pair in obj)
        {
            hash ^= pair.Key.GetHashCode() ^ pair.Value.GetHashCode();
        }

        return hash;
    }
}
