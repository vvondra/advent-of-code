using System.Xml.Linq;

var input = File.ReadLines("input.txt");

var grid = input
    .SelectMany((line, row) => line.Select((c, col) => (XY: new XY(row, col), Char: c)))
    .ToDictionary(x => x.XY, x => new Tile(x.XY, x.Char));

var start = grid.First(el => el.Value.Char == 'S').Value;

IEnumerable<Tile> Walk(Tile start, Dictionary<XY, Tile> map)
{
    var visited = new HashSet<XY>([start.XY]);
    var next = start.Adjacent(map).First();

    while (next != null && !visited.Contains(next))
    {
        visited.Add(next);
        yield return map[next];
        next = map[next].Adjacent(map).Where(xy => !visited.Contains(xy)).FirstOrDefault();
    }
}

Console.WriteLine((Walk(start, grid).Count() + 1) / 2);

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

    public static readonly XY[] Dirs = [new(-1, 0), new(0, -1), new(0, 1), new(1, 0)];

    public IEnumerable<XY> Adjacent() => Dirs.Select(dir => this + dir);
}