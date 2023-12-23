var input = File.ReadLines("input.txt");

var grid = input.SelectMany((line, y) => line.Select((value, x) => (Coordinate: (x, y), Value: int.Parse(value.ToString()))))
                .ToDictionary(item => item.Coordinate, item => item.Value);

var start = (0, 0);
var end = (grid.Keys.Max(c => c.x), grid.Keys.Max(c => c.y));
var path = Dijkstra(start, end, false);
Console.WriteLine(path[end]);
var path2 = Dijkstra(start, end, true);
Console.WriteLine(path2[end]);

IEnumerable<(State, int)> GetAdjacent(State state)
{
    var (X, Y, DX, DY, Straight) = state;
    var directions = new List<(int X, int Y)>
    {
        (-1, 0), // Left
        (1, 0),  // Right
        (0, -1), // Up
        (0, 1)   // Down
    };
    return directions.Where(dir => dir != (-DX, -DY))
                     .Select(dir => new State(X + dir.X, Y + dir.Y, dir.X, dir.Y, (dir.X == DX && dir.Y == DY) ? Straight + 1 : 1))
                     .Where(next => next.DX != DX || next.DY != DY || next.Straight < 4)
                     .Where(next => grid.ContainsKey((next.X, next.Y)))
                     .Select(next => (next, grid[(next.X, next.Y)]));
}

IEnumerable<(State, int)> GetUltraAdjacent(State state)
{
    var (X, Y, DX, DY, Straight) = state;
    var directions = new List<(int X, int Y)>
    {
        (-1, 0), // Left
        (1, 0),  // Right
        (0, -1), // Up
        (0, 1)   // Down
    };

    var possible = directions.Where(dir => dir != (-DX, -DY));

    return possible
        .SelectMany(dir =>
        {
            if (dir == (DX, DY))
            {
                if (Straight < 10 && grid.ContainsKey((X + dir.X, Y + dir.Y)))
                {
                    return new List<(State, int)> { (new State(X + dir.X, Y + dir.Y, dir.X, dir.Y, Straight + 1), grid[(X + dir.X, Y + dir.Y)]) };
                }
                else
                {
                    return [];
                }
            }
            else
            {
                List<(State, int)> states = [];
                var distance = 0;
                var next = new State(X, Y, dir.X, dir.Y, 0);
                for (int i = 1; i <= 4; i++)
                {
                    next = new State(next.X + dir.X, next.Y + dir.Y, dir.X, dir.Y, next.Straight + 1);
                    if (!grid.ContainsKey((next.X, next.Y)))
                    {
                        return [];
                    }
                    distance += grid[(next.X, next.Y)];
                }

                states.Add((next, distance));
                return states;
            }
        })
        .Where(next => grid.ContainsKey((next.Item1.X, next.Item1.Y)));
}

Dictionary<(int X, int Y), int> Dijkstra((int X, int Y) start, (int X, int Y) end, bool ultra)
{
    var visited = new HashSet<State>();
    var queue = new PriorityQueue<State, int>();
    queue.Enqueue((start.X, start.Y, 0, 0, 0), 0);

    Dictionary<(int X, int Y), int> shortestDistances = new()
    {
        [start] = 0,
    };

    Func<State, IEnumerable<(State, int)>> adjacentFn = ultra ? GetUltraAdjacent : GetAdjacent;

    while (queue.TryDequeue(out State state, out int distance))
    {
        var (x, y, dx, dy, straight) = state;

        foreach (var (adjacent, additionalDistance) in adjacentFn(state))
        {
            if (grid.ContainsKey((adjacent.X, adjacent.Y)) && !visited.Contains(adjacent))
            {
                var newDistance = distance + additionalDistance;
                if (!shortestDistances.ContainsKey((adjacent.X, adjacent.Y)) || newDistance < shortestDistances[(adjacent.X, adjacent.Y)])
                {
                    shortestDistances[(adjacent.X, adjacent.Y)] = newDistance;
                }

                visited.Add(adjacent);
                queue.Enqueue(adjacent, newDistance);
            }
        }
    }

    return shortestDistances;
}

internal record struct State(int X, int Y, int DX, int DY, int Straight)
{
    public static implicit operator (int X, int Y, int DX, int DY, int Straight)(State value)
    {
        return (value.X, value.Y, value.DX, value.DY, value.Straight);
    }

    public static implicit operator State((int X, int Y, int DX, int DY, int Straight) value)
    {
        return new State(value.X, value.Y, value.DX, value.DY, value.Straight);
    }
}


