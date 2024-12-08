var input = File.ReadLines("input.txt");

var maxX = input.Count();
var maxY = input.First().Length;

var grid = input
    .SelectMany((line, row) => line.Select((ch, col) => new { Coordinate = new XY(row, col), Character = ch }))
    .Where(item => item.Character != '.')
    .ToDictionary(item => item.Coordinate, item => item.Character);

var pairs = grid.GroupBy(item => item.Value)
    .ToDictionary(
        group => group.Key,
        group => group
            .SelectMany(g => group.Where(gg => gg.Key != g.Key).Select(gg => (gg.Key, g.Key)).ToList())
            .ToList()
    );

var result = pairs
    .Values
    .SelectMany(p => p)
    .Select(p => p.Item1 + (p.Item1 - p.Item2))
    .Where(xy => xy.X >= 0 && xy.X < maxX && xy.Y >= 0 && xy.Y < maxY)
    .Distinct()
    .ToHashSet();

Console.WriteLine(result.Count());


for (var x = 0; x < maxX; x++)
{
    for (var y = 0; y < maxY; y++)
    {
        if (result.Contains(new XY(x, y)))
        {
            Console.Write('#');
        }
        else if (grid.ContainsKey(new XY(x, y)))
        {
            Console.Write(grid[new XY(x, y)]);
        }
        else
        {
            Console.Write('.');
        }
    }
    Console.WriteLine();
}

record XY(int X, int Y)
{
    public static XY operator -(XY a, XY b)
    {
        return new XY(a.X - b.X, a.Y - b.Y);
    }

    public static XY operator +(XY a, XY b)
    {
        return new XY(a.X + b.X, a.Y + b.Y);
    }
}