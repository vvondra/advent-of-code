using System.Text.RegularExpressions;

var input = File.ReadLines("input.txt");
var grid = input.Select(x => x.ToCharArray()).ToArray();

IEnumerable<string> GetAllLines()
{
    int rows = grid.Length;
    int cols = grid[0].Length;

    // Horizontal lines
    for (int i = 0; i < rows; i++)
    {
        yield return new string(grid[i]);
        yield return new string(grid[i].Reverse().ToArray());
    }

    // Vertical lines
    for (int j = 0; j < cols; j++)
    {
        char[] column = new char[rows];
        for (int i = 0; i < rows; i++)
        {
            column[i] = grid[i][j];
        }
        yield return new string(column);
        yield return new string(column.Reverse().ToArray());
    }

    // Diagonal lines (top-left to bottom-right)
    for (int k = 0; k < rows + cols - 1; k++)
    {
        List<char> diagonal = [];
        for (int j = 0; j <= k; j++)
        {
            int i = k - j;
            if (i < rows && j < cols)
            {
                diagonal.Add(grid[i][j]);
            }
        }
        if (diagonal.Count > 0)
        {
            yield return new string(diagonal.ToArray());
            yield return new string(diagonal.AsEnumerable().Reverse().ToArray());
        }
    }

    // Diagonal lines (bottom-left to top-right)
    for (int k = 0; k < rows + cols - 1; k++)
    {
        List<char> diagonal = [];
        for (int j = 0; j <= k; j++)
        {
            int i = rows - 1 - k + j;
            if (i >= 0 && i < rows && j < cols)
            {
                diagonal.Add(grid[i][j]);
            }
        }
        if (diagonal.Count > 0)
        {
            yield return new string(diagonal.ToArray());
            yield return new string(diagonal.AsEnumerable().Reverse().ToArray());
        }
    }
}


var count = GetAllLines().Sum(line => Regex.Matches(line, "XMAS").Count);
Console.WriteLine(count);

var count2 = 0;
for (int i = 1; i < grid.Length - 1; i++)
{
    for (int j = 1; j < grid[0].Length - 1; j++)
    {
        if (grid[i][j] != 'A') {
            continue;
        }

        var diagonal1 = new string([grid[i - 1][j - 1], grid[i][j], grid[i + 1][j + 1]]);
        var diagonal2 = new string([grid[i - 1][j + 1], grid[i][j], grid[i + 1][j - 1]]);

        if ((diagonal1 == "MAS" || diagonal1 == "SAM") && (diagonal2 == "MAS" || diagonal2 == "SAM"))
        {
            count2++;
        }
    }
}
Console.WriteLine(count2);