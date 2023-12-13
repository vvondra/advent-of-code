var input = File.ReadAllText("input.txt").Split(Environment.NewLine + Environment.NewLine, StringSplitOptions.TrimEntries)
                .Select(StringTo2DArray);

// too low 23970

var result = input
    .Select(grid => (grid, score: SymmetryScore(grid).First()))
    .Select(scored => {
        var second = GenerateFlippedGrids(scored.grid).SelectMany(SymmetryScore).First(score => score > 0 && score != scored.score);

        return (scored.score, second);
    })
    .Aggregate((0, 0), (acc, next) => (acc.Item1 + next.score, acc.Item2 + next.second));

Console.WriteLine(result.Item1);
Console.WriteLine(result.Item2);

static char[,] StringTo2DArray(string input)
{
    var lines = input.Split('\n');
    int rows = lines.Length;
    int cols = lines[0].Length;

    char[,] grid = new char[rows, cols];

    foreach (var (line, rowIndex) in lines.Select((line, index) => (line, index)))
    {
        foreach (var (ch, colIndex) in line.Select((ch, index) => (ch, index)))
        {
            grid[rowIndex, colIndex] = ch;
        }
    }

    return grid;
}

static IEnumerable<char[,]> GenerateFlippedGrids(char[,] originalGrid)
{
    int rows = originalGrid.GetLength(0);
    int cols = originalGrid.GetLength(1);

    for (int i = 0; i < rows; i++)
    {
        for (int j = 0; j < cols; j++)
        {
            char[,] newGrid = (char[,])originalGrid.Clone();

            // Flip the character
            newGrid[i, j] = newGrid[i, j] == '#' ? '.' : '#';

            yield return newGrid;
        }
    }
}

static IEnumerable<int> SymmetryScore(char[,] grid)
{
    int rows = grid.GetLength(0);
    int cols = grid.GetLength(1);

    // Check rows for symmetry
    for (int i = 0; i < rows - 1; i++)
    {
        if (GetRow(grid, i).SequenceEqual(GetRow(grid, i + 1)))
        {
            if (IsSymmetric(grid, i, true))
            {
                yield return 100 * (i + 1);
            }
        }
    }

    // Check columns for symmetry
    for (int j = 0; j < cols - 1; j++)
    {
        if (GetColumn(grid, j).SequenceEqual(GetColumn(grid, j + 1)))
        {
            if (IsSymmetric(grid, j, false))
            {
                yield return j + 1;
            }
        }
    }
}
static bool IsSymmetric(char[,] grid, int index, bool isRow)
{
    int rows = grid.GetLength(0);
    int cols = grid.GetLength(1);
    int length = isRow ? rows : cols;

    // Start comparing from the rows or columns next to the center of symmetry
    for (int offset = 1; index + offset < length && index - offset >= 0; offset++)
    {
        int firstIndex = index - offset;
        int secondIndex = index + offset + 1; // Skip over the second row/column that co-defines the center

        if (isRow)
        {
            if (firstIndex < 0 || secondIndex >= rows) break; // Bounds checking
            if (!GetRow(grid, firstIndex).SequenceEqual(GetRow(grid, secondIndex)))
                return false;
        }
        else
        {
            if (firstIndex < 0 || secondIndex >= cols) break; // Bounds checking
            if (!GetColumn(grid, firstIndex).SequenceEqual(GetColumn(grid, secondIndex)))
                return false;
        }
    }

    return true;
}


static IEnumerable<char> GetRow(char[,] grid, int rowIndex)
{
    int cols = grid.GetLength(1);
    for (int j = 0; j < cols; j++)
    {
        yield return grid[rowIndex, j];
    }
}

static IEnumerable<char> GetColumn(char[,] grid, int colIndex)
{
    int rows = grid.GetLength(0);
    for (int i = 0; i < rows; i++)
    {
        yield return grid[i, colIndex];
    }
}
