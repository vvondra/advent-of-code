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
    .ToHashSet();

Console.WriteLine(result.Count);

var result2 = pairs.Values
    .SelectMany(pairList => pairList.SelectMany(pair =>
    {
        var start = pair.Item1;
        var end = pair.Item2;
        var vector = start - end;

        var points = new List<XY> { start, end };
        var current = start + vector;

        while (current.X >= 0 && current.X < maxX && current.Y >= 0 && current.Y < maxY)
        {
            points.Add(current);
            current += vector;
        }

        return points;
    }))
    .ToHashSet();

Console.WriteLine(result2.Count);

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