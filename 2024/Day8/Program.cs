var input = File.ReadLines("input.txt");

var maxX = input.Count();
var maxY = input.First().Length;

var grid = input
    .SelectMany((line, row) => line.Select((ch, col) => new { Coordinate = (X: row, Y: col), Character = ch }))
    .Where(item => item.Character != '.')
    .ToDictionary(item => item.Coordinate, item => item.Character);

var pairs = grid.GroupBy(item => item.Value)
    .ToDictionary(
        group => group.Key,
        group => group
            .SelectMany(g => group.Where(gg => gg.Key != g.Key).Select(gg => (gg.Key, g.Key)).ToList())
            .ToList()
    );
    

static (int X, int Y) Subtract((int X, int Y) a, (int X, int Y) b)
{
    return (a.X - b.X, a.Y - b.Y);
}

static (int X, int Y) Add((int X, int Y) a, (int X, int Y) b)
{
    return (a.X + b.X, a.Y + b.Y);
}


var result = pairs
    .Values
    .SelectMany(p => p)
    .Select(p => {
        var vector = Subtract(p.Item1, p.Item2);
        return Add(p.Item1, vector);
    })
    .Where(xy => xy.X >= 0 && xy.X < maxX && xy.Y >= 0 && xy.Y < maxY)
    .Distinct();

Console.WriteLine(result.Count());


for (var x = 0; x < maxX; x++)
{
    for (var y = 0; y < maxY; y++)
    {
        if (result.Contains((x, y)))
        {
            Console.Write('#');
        }
        else if (grid.ContainsKey((x, y)))
        {
            Console.Write(grid[(x, y)]);
        }
        else
        {
            Console.Write('.');
        }
    }
    Console.WriteLine();
}