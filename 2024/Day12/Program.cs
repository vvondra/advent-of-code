﻿var input = File.ReadLines("input.txt");

var grid = input
    .SelectMany(
        (line, row) =>
            line.Select((ch, col) => new { Coordinate = new XY(row, col), Character = ch - '0' })
    )
    .ToDictionary(item => item.Coordinate, item => item.Character);

List<ISet<XY>> Explore()
{
    var visited = new HashSet<XY>();
    var unvisited = new HashSet<XY>(grid.Keys);
    var queue = new Queue<XY>();
    var regions = new List<ISet<XY>>();

    while (unvisited.Count > 0)
    {
        var start = unvisited.First();
        unvisited.Remove(start);
        queue.Enqueue(start);
        visited.Add(start);

        var group = new HashSet<XY> { start };

        while (queue.Count > 0)
        {
            var next = queue.Dequeue();

            foreach (var adj in next.Adjacent())
            {
                if (grid.ContainsKey(adj) && grid[adj] == grid[next] && visited.Add(adj))
                {
                    queue.Enqueue(adj);
                    group.Add(adj);
                    unvisited.Remove(adj);
                }
            }
        }

        regions.Add(group);
    }

    return regions;
}

int Perimeter(ISet<XY> region) =>
    region.Count * 4 - region.Sum(xy => xy.Adjacent().Count(region.Contains));

var result = Explore().Select(x => x.Count * Perimeter(x)).Sum();
Console.WriteLine(result);

var result2 = Explore().Select(x => x.Count * PerimeterSides(x)).Sum();
Console.WriteLine(result2);

int PerimeterSides(ISet<XY> region)
{
    var perimeterXY = region
        .SelectMany(xy => xy.Adjacent())
        .Where(xy => !region.Contains(xy))
        .ToHashSet();

    List<ISet<XY>> sides = [];

    while (perimeterXY.Count > 0)
    {
        var start = perimeterXY.First();
        perimeterXY.Remove(start);

        var queue = new Queue<XY>();
        queue.Enqueue(start);

        var side = new HashSet<XY> { start };
        XY direction = null;

        while (queue.Count > 0)
        {
            var next = queue.Dequeue();

            foreach (var adj in next.Adjacent())
            {
                if (perimeterXY.Contains(adj))
                {
                    if (direction == null)
                    {
                        direction = adj - next;
                    }
                    else if (direction != adj - next)
                    {
                        continue;
                    }

                    queue.Enqueue(adj);
                    side.Add(adj);
                    perimeterXY.Remove(adj);
                }
            }
        }

        sides.Add(side);
    }

    return sides.Count;
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
