var input = File.ReadLines("input.txt");

var grid = input
    .SelectMany(
        (line, row) =>
            line.Select((ch, col) => new { Coordinate = new XY(row, col), Character = ch - '0' })
    )
    .ToDictionary(item => item.Coordinate, item => item.Character);

var trailheads = grid.Where(item => item.Value == 0).Select(item => item.Key).ToList();

var result = trailheads.Select(x => Explore(x, false)).Sum();
Console.WriteLine(result);

var result2 = trailheads.Select(x => Explore(x, true)).Sum();
Console.WriteLine(result2);

int Explore(XY start, bool unique)
{
    var visited = new HashSet<XY>();

    int Explore2(XY next)
    {
        if (!unique && visited.Contains(next))
        {
            return 0;
        }
        visited.Add(next);

        if (grid[next] == 9)
        {
            return 1;
        }

        return next.Adjacent()
            .Where(xy => grid.ContainsKey(xy) && grid[xy] == grid[next] + 1)
            .Select(Explore2)
            .Sum();
    }

    return Explore2(start);
}

record XY(int Y, int X)
{
    public XY((int, int) XY)
        : this(XY.Item1, XY.Item2) { }

    public static XY operator +(XY me, XY other) => new(Y: me.Y + other.Y, X: me.X + other.X);

    public static XY operator -(XY me, XY other) => new(Y: me.Y - other.Y, X: me.X - other.X);

    public static readonly XY[] Dirs = [new(-1, 0), new(0, -1), new(0, 1), new(1, 0)];

    public IEnumerable<XY> Adjacent() => Dirs.Select(dir => this + dir);
}
