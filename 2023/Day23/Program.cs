var input = File.ReadLines("input.txt");

var grid = input
    .SelectMany((line, row) => line.Select((ch, col) => new { Coordinate = new XY(row, col), Character = ch }))
    .ToDictionary(item => item.Coordinate, item => item.Character);

var maxX = grid.Max(x => x.Key.Y);
var start = grid.First(el => el.Key.Y == 0 && el.Value == '.').Key;
var end = grid.First(el => el.Key.Y == maxX && el.Value == '.').Key;

var slipperyEdges = ExploreMaze(start, end);
Console.WriteLine(LongestPath(start, end, slipperyEdges));


int LongestPath(XY from, XY to, Dictionary<(XY, XY), int> edges)
{
    var topologicalOrder = TopologicalSort(from, edges);

    var longestPath = new Dictionary<XY, int>
    {
        [from] = 0
    };

    foreach (var vertex in topologicalOrder)
    {
        if (longestPath.ContainsKey(vertex))
        {
            foreach (var neighbor in GetNeighbors(vertex, edges))
            {
                var weight = longestPath[vertex] + edges[(vertex, neighbor)];
                if (!longestPath.ContainsKey(neighbor) || weight > longestPath[neighbor])
                {
                    longestPath[neighbor] = weight;
                }
            }
        }
    }

    return longestPath[to];
}

List<XY> TopologicalSort(XY start, Dictionary<(XY, XY), int> edges)
{
    var topologicalOrder = new List<XY>();

    void TopologicalSort2(XY vertex)
    {
        var visited = new HashSet<XY>
        {
            vertex
        };

        foreach (var neighbor in GetNeighbors(vertex, edges))
        {
            if (!visited.Contains(neighbor))
            {
                TopologicalSort2(neighbor);
            }
        }

        topologicalOrder.Insert(0, vertex);
    }

    TopologicalSort2(start);

    return topologicalOrder;
}


IEnumerable<XY> GetNeighbors(XY vertex, Dictionary<(XY, XY), int> edges) => edges.Keys.Where(edge => edge.Item1 == vertex).Select(edge => edge.Item2);

Dictionary<(XY, XY), int> ExploreMaze(XY start, XY end)
{
    var queue = new Queue<(XY point, XY lastJunction, int distance, XY previous)>();
    var edges = new Dictionary<(XY, XY), int>();

    queue.Enqueue((start, start, 0, new XY(start.Y, start.X - 1)));
    edges.Add((start, start), 0);

    var allowedDirs = new Dictionary<char, XY>
    {
        { '>', new XY(0, 1) },
        { '<', new XY(0, -1) },
        { 'v', new XY(1, 0) },
        { '^', new XY(-1, 0) },
    };

    while (queue.Count > 0)
    {
        var (current, lastJunction, distance, previous) = queue.Dequeue();

        var adjacentPoints = current.Adjacent()
            .Where(xy => grid!.ContainsKey(xy.next) && grid[xy.next] != '#' && xy.next != previous);

        var isJunction = adjacentPoints.Count() > 1;

        if (isJunction || current == end)
        {
            if (edges.ContainsKey((lastJunction, current)))
            {
                edges[(lastJunction, current)] = Math.Max(edges[(lastJunction, current)], distance);
            }
            else
            {
                edges.Add((lastJunction, current), distance);
            }
        }

        foreach (var (next, dir) in adjacentPoints.Where(xy => grid![xy.next] == '.' || allowedDirs[grid[xy.next]] == xy.dir))
        {
            if (isJunction)
            {
                queue.Enqueue((next, current, 1, current));
            }
            else
            {
                queue.Enqueue((next, lastJunction, distance + 1, current));
            }
        }
    }

    return edges;
}



record XY(int Y, int X)
{
    public XY((int, int) XY) : this(XY.Item1, XY.Item2)
    {

    }

    public static XY operator +(XY me, XY other) => new(Y: me.Y + other.Y, X: me.X + other.X);
    public static XY operator -(XY me, XY other) => new(Y: me.Y - other.Y, X: me.X - other.X);

    private static readonly XY[] Dirs = [new(-1, 0), new(0, -1), new(0, 1), new(1, 0)];

    public IEnumerable<(XY next, XY dir)> Adjacent() => Dirs.Select(dir => (next: this + dir, dir));
}