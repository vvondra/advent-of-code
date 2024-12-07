var input = File.ReadLines("input.txt");

var grid = input
    .SelectMany((line, row) => line.Select((ch, col) => new { Coordinate = (X: row, Y: col), Character = ch }))
    .ToDictionary(item => item.Coordinate, item => item.Character);

var start = grid.First(kvp => kvp.Value == '^').Key;

static int TraverseGrid(Dictionary<(int X, int Y), char> grid, (int X, int Y) start)
{
    var direction = (X: -1, Y: 0); // Initial direction is up
    var current = start;
    var visited = new HashSet<((int X, int Y) Position, (int X, int Y) Direction)>();

    while (grid.ContainsKey(current))
    {
        if (visited.Contains((current, direction)))
        {
            return 0; // Loop detected
        }

        visited.Add((current, direction));
        var next = (X: current.X + direction.X, Y: current.Y + direction.Y);

        if (!grid.TryGetValue(next, out var nextValue) || nextValue != '#')
        {
            current = next;
        }
        else
        {
            // Turn right
            direction = direction switch
            {
                (X: -1, Y: 0) => (X: 0, Y: 1),  // Up to Right
                (X: 0, Y: 1) => (X: 1, Y: 0),  // Right to Down
                (X: 1, Y: 0) => (X: 0, Y: -1), // Down to Left
                (X: 0, Y: -1) => (X: -1, Y: 0), // Left to Up
                _ => direction
            };
        }
    }

    return visited.Select(x => x.Position).Distinct().Count();
}

var result = TraverseGrid(grid, start);
Console.WriteLine(result);

var result2 = grid
    .Where(kvp => kvp.Value != '^')
    .Count(kvp =>
    {
        var modifiedGrid = new Dictionary<(int X, int Y), char>(grid)
        {
            [kvp.Key] = '#'
        };
        return TraverseGrid(modifiedGrid, start) == 0;
    });

Console.WriteLine(result2);