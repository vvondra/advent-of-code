
var input = File.ReadLines("input.txt");

var grid = input
    .SelectMany((line, row) => line.Select((c, col) => (XY: new XY(row, col), Char: c)))
    .ToDictionary(x => x.XY, x => new Tile(x.XY, x.Char));

var start = grid.First(el => el.Value.Char == 'S').Value;

var maxY = grid.Keys.Max(xy => xy.Y);
var maxX = grid.Keys.Max(xy => xy.X);

var reachable = Enumerable.Range(0, 64)
    .Aggregate(new HashSet<Tile>([start]), (step, _) =>
        step.SelectMany(tile => tile.Adjacent(grid)).ToHashSet()
    );

Console.WriteLine(reachable.Count);

var reachable2 = Enumerable.Range(0, 500)
    .Aggregate(new HashSet<Tile>([start]), (step, _) =>
        step.SelectMany(tile => tile.InfiniteAdjacent(grid, NormalizeXY)).ToHashSet()
    );

Console.WriteLine(reachable2.Count);

Console.WriteLine(NormalizeXY(new XY(-3, 0)));
Console.WriteLine(NormalizeXY(new XY(-3, -3)));

XY NormalizeXY(XY xy)
{
    int normalizedX = xy.X % maxX;
    int normalizedY = xy.Y % maxY;

    if (normalizedX < 0)
    {
        normalizedX += maxX;
    }

    if (normalizedY < 0)
    {
        normalizedY += maxY;
    }

    return new XY(normalizedY, normalizedX);
}

record Tile(XY XY, char Char)
{
    public IEnumerable<Tile> Adjacent(Dictionary<XY, Tile> map) => XY
        .Adjacent()
        .Where(xy => map.ContainsKey(xy) && map[xy].Char != '#')
        .Select(xy => new Tile(xy, Char));

    public IEnumerable<Tile> InfiniteAdjacent(Dictionary<XY, Tile> map, Func<XY, XY> normalizer) => XY
        .Adjacent()
        .Where(xy => map[normalizer(xy)].Char != '#')
        .Select(xy => new Tile(xy, Char));
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