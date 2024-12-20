var input = File.ReadLines("input.txt");

var grid = input
    .SelectMany(
        (line, row) =>
            line.Select((ch, col) => new { Coordinate = new XY(row, col), Character = ch })
    )
    .ToDictionary(item => item.Coordinate, item => item.Character);


var start = grid.First(x => x.Value == 'S').Key;
var end = grid.First(x => x.Value == 'E').Key;

int CheatingPass(int threshold, int cheatLength) {
    var distance = new Dictionary<XY, int> { [start] = 0 };

    int Pass() {
        var current = start;
        var steps = 0;
        
        while (current != end) {
            var next = current
                .Adjacent()
                .Where(x => grid[x] != '#' && !distance.ContainsKey(x))
                .First();

            distance[next] = ++steps;
            
            current = next;
        }

        return steps;
    }

    Pass();

    var cheats = distance
        .AsParallel()
        .SelectMany(
            pointSteps => pointSteps.Key
                .AdjacentWithinDistance(cheatLength)
                .Where(distance.ContainsKey)
                .Where(x => distance[x] - threshold >= pointSteps.Value + pointSteps.Key.ManhattanDistanceTo(x))
                .Select(_ => 1)
        )
        .Sum();

    return cheats;
}

Console.WriteLine(CheatingPass(100, 2));
Console.WriteLine(CheatingPass(100, 20));

record XY(int Y, int X)
{
    public XY((int, int) XY) : this(XY.Item1, XY.Item2) { }

    public XY(IEnumerable<int> strings) : this(strings.ElementAt(0), strings.ElementAt(1)) { }

    public static XY operator +(XY me, XY other) => new(Y: me.Y + other.Y, X: me.X + other.X);

    public static XY operator -(XY me, XY other) => new(Y: me.Y - other.Y, X: me.X - other.X);

    public XY Opposite => new(-Y, -X);

    public static readonly XY[] Dirs = [new(-1, 0), new(0, -1), new(0, 1), new(1, 0)];

    public IEnumerable<XY> Adjacent() => Dirs.Select(dir => this + dir);

    public IEnumerable<XY> AdjacentWithinDistance(int n) => 
        Enumerable.Range(-n, 2 * n + 1)
            .SelectMany(dy => Enumerable.Range(-n, 2 * n + 1)
                .Select(dx => new XY(Y + dy, X + dx)))
            .Where(xy => Math.Abs(xy.Y - Y) + Math.Abs(xy.X - X) <= n);

    public override string ToString() => $"({Y}, {X})";

    public int ManhattanDistanceTo(XY x) => Math.Abs(x.Y - Y) + Math.Abs(x.X - X);
}

