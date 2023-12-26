var input = File.ReadLines("input.txt")
    .Select(line => line.Split(" "))
    .Select(line => (dir: line[0][0], steps: long.Parse(line[1]), color: line[2][1..]));

var next = new Dictionary<char, (int X, int Y)>() {
    { 'R', (1, 0) },
    { 'D', (0, 1) },
    { 'L', (-1, 0) },
    { 'U', (0, -1) }
};

var loop = Walk(input.Select(x => (x.dir, x.steps)).ToList(), (0, 0)).ToList();
Console.WriteLine(CalculateSurfaceArea(loop));

var loop2 = Walk(input.Select(x => {
    var dir = x.color[6] switch
    {
        '0' => 'R',
        '1' => 'D',
        '2' => 'L',
        '3' => 'U',
    };
    var steps = Convert.ToInt64(x.color[1..6], 16);
    return (dir, steps);
}).ToList(), (0, 0)).ToList();
Console.WriteLine(CalculateSurfaceArea(loop2));

double CalculateSurfaceArea(List<(long X, long Y)> vertices)
{
    int n = vertices.Count;
    double area = 0;

    for (int i = 0; i < n; i++)
    {
        var currentVertex = vertices[i];
        var nextVertex = vertices[(i + 1) % n];

        area += (currentVertex.X * nextVertex.Y) - (nextVertex.X * currentVertex.Y);
    }

    return Math.Abs(area / 2);
}

IEnumerable<(long X, long Y)> Walk(List<(char dir, long steps)> values, (long, long) start)
{
    var (x, y) = start;
    var n = values.Count;
    for (int i = 0; i < n; i++)
    {
        var (dir, steps) = values[i];
        yield return (x, y);

        var (dx, dy) = next[dir];

        x += dx * steps;
        y += dy * steps;

        x = dir switch {
            'L' => values[Mod(i - 1, n)].dir == 'D' && values[Mod(i + 1, n)].dir == 'U' ? x - 1 : (values[Mod(i - 1, n)].dir == 'U' && values[Mod(i + 1, n)].dir == 'D' ? x + 1 : x),
            'R' => values[Mod(i - 1, n)].dir == 'U' && values[Mod(i + 1, n)].dir == 'D' ? x + 1 : (values[Mod(i - 1, n)].dir == 'D' && values[Mod(i + 1, n)].dir == 'U' ? x - 1 : x),
            _ => x
        };

        y = dir switch {
            'U' => values[Mod(i - 1, n)].dir == 'L' && values[Mod(i + 1, n)].dir == 'R' ? y - 1 : (values[Mod(i - 1, n)].dir == 'R' && values[Mod(i + 1, n)].dir == 'L' ? y + 1 : y),
            'D' => values[Mod(i - 1, n)].dir == 'R' && values[Mod(i + 1, n)].dir == 'L' ? y + 1 : (values[Mod(i - 1, n)].dir == 'L' && values[Mod(i + 1, n)].dir == 'R' ? y - 1 : y),
            _ => y
        };
    }
}

int Mod(int x, int m) => (x % m + m) % m;
