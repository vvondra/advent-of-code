var input = File.ReadLines("input.txt")
    .Select((line, idx) =>
    {
        var parts = line.Split('~');

        return new Brick(idx, parts.Select(part => part.Split(',').Select(int.Parse).ToArray()).ToArray());
    })
    .ToList();


var safe = FindSafeToRemove(Settle(input));

Console.WriteLine(safe.Count(x => x.Item2 == 0));
Console.WriteLine(safe.Sum(x => x.Item2));

static List<(Brick, int)> FindSafeToRemove(List<Brick> input)
{
    var supportedBricks = new List<(Brick, int)>();

    foreach (var element in input)
    {
        var modifiedInput = new List<Brick>(input);
        modifiedInput.Remove(element);

        var settled = Settle(modifiedInput);

        var supported = 0;
        foreach (var settledElement in settled)
        {
            var reference = modifiedInput.Find(x => x.name == settledElement.name);

            if (!settledElement.SameAs(reference))
            {
                supported++;
            }
        }

        supportedBricks.Add((element, supported));
    }

    return supportedBricks;
}

static List<Brick> Settle(List<Brick> input)
{
    var settled = new List<Brick>();

    var workingSet = new List<Brick>(input.OrderBy(x => x.MinZ));

    while (workingSet.Count > 0)
    {
        var current = workingSet[0];
        workingSet.RemoveAt(0);

        var minZ = current.MinZ;

        while (minZ > 1 && !IsOverlapping(current, settled))
        {
            current = current.ReduceZ(1);
            minZ--;
        }

        settled.Add(current);
    }

    return settled;
}

static bool IsOverlapping(Brick element, List<Brick> settled)
{
    foreach (var settledElement in settled)
    {
        if (settledElement.MaxZ != element.MinZ - 1)
        {
            continue;
        }

        foreach (var (x, y) in GetFootprint(element.ends))
        {
            foreach (var (x2, y2) in GetFootprint(settledElement.ends))
            {
                if (x == x2 && y == y2)
                {
                    return true;
                }
            }
        }
    }

    return false;
}
static IEnumerable<(int x, int y)> GetFootprint(int[][] ends)
{
    var (x1, y1) = (ends[0][0], ends[0][1]);
    var (x2, y2) = (ends[1][0], ends[1][1]);

    if (x1 == x2)
    {
        var y = y1 < y2 ? y1 : y2;

        for (var i = 0; i <= Math.Abs(y1 - y2); i++)
        {
            yield return (x1, y + i);
        }
    }
    else
    {
        var x = x1 < x2 ? x1 : x2;

        for (var i = 0; i <= Math.Abs(x1 - x2); i++)
        {
            yield return (x + i, y1);
        }
    }
}

internal record struct Brick(int name, int[][] ends)
{
    public static implicit operator (int name, int[][] ends)(Brick value)
    {
        return (value.name, value.ends);
    }

    public static implicit operator Brick((int name, int[][] ends) value)
    {
        return new Brick(value.name, value.ends);
    }

    public readonly Brick ReduceZ(int dz)
    {
        return new Brick(name, ends.Select(end => new[] { end[0], end[1], end[2] - dz }).ToArray());
    }

    public readonly int MaxZ => ends.Max(x => x[2]);
    public readonly int MinZ => ends.Min(x => x[2]);

    public readonly bool SameAs(Brick other)
    {
        return ends[0][0] == other.ends[0][0]
            && ends[0][1] == other.ends[0][1]
            && ends[0][2] == other.ends[0][2]
            && ends[1][0] == other.ends[1][0]
            && ends[1][1] == other.ends[1][1]
            && ends[1][2] == other.ends[1][2];
    }
}