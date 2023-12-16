var input = File.ReadLines("input.txt");

var grid = input
    .SelectMany((line, row) => line.Select((ch, col) => (Coordinate: (X: row, Y: col), Character: ch)))
    .ToDictionary(item => item.Coordinate, item => item.Character);


HashSet<(int X, int Y)> Explore((int X, int Y) coordinate, (int X, int Y) direction)
{
    var visited = new HashSet<(int X, int Y, int DX, int DY)>();
    void Explore2((int X, int Y) coordinate, (int X, int Y) direction)
    {
        var key = (coordinate.X, coordinate.Y, direction.X, direction.Y);
        if (visited.Contains(key) || !grid!.ContainsKey(coordinate))
        {
            return;
        }

        visited.Add(key);

        switch (grid[coordinate])
        {
            case '.':
                Explore2((coordinate.X + direction.X, coordinate.Y + direction.Y), direction);
                break;
            case '/':
                Explore2((coordinate.X - direction.Y, coordinate.Y - direction.X), (-direction.Y, -direction.X));
                break;
            case '\\':
                Explore2((coordinate.X + direction.Y, coordinate.Y + direction.X), (direction.Y, direction.X));
                break;
            case '|':
                if (direction.Y == 0)
                {
                    Explore2((coordinate.X + direction.X, coordinate.Y + direction.Y), direction);
                }
                else
                {
                    Explore2((coordinate.X + 1, coordinate.Y), (1, 0));
                    Explore2((coordinate.X - 1, coordinate.Y), (-1, 0));
                }
                break;
            case '-':
                if (direction.X == 0)
                {
                    Explore2((coordinate.X + direction.X, coordinate.Y + direction.Y), direction);
                }
                else
                {
                    Explore2((coordinate.X, coordinate.Y + 1), (0, 1));
                    Explore2((coordinate.X, coordinate.Y - 1), (0, -1));
                }
                break;
            default:
                throw new NotImplementedException();
        }
    }

    Explore2(coordinate, direction);

    return visited.Select(item => (item.X, item.Y)).ToHashSet();
}

int MaxExplore()
{
    var rows = grid.Keys.Select(item => item.X).Max();
    var cols = grid.Keys.Select(item => item.Y).Max();

    var max = 0;
    for (int i = 0; i <= cols; i++)
    {
        max = Math.Max(max, Explore((0, i), (1, 0)).Count);
        max = Math.Max(max, Explore((rows, i), (-1, 0)).Count);
    }

    for (int i = 0; i <= rows; i++)
    {
        max = Math.Max(max, Explore((i, 0), (0, 1)).Count);
        max = Math.Max(max, Explore((i, cols), (0, -1)).Count);
    }

    return max;
}

Console.WriteLine(Explore((0, 0), (0, 1)).Count);
Console.WriteLine(MaxExplore());