var input = File.ReadLines("input.txt");

var galaxies = input
    .SelectMany((line, row) => line.Select((c, col) => c switch { '#' => new XY(row, col), _ => null }))
    .Where(x => x != null)
    .ToHashSet();

var rows = input.Count();
var cols = input.First().Length;

var emptyCols = Enumerable.Range(0, cols).Where(col => !galaxies.Any(xy => xy.X == col)).ToHashSet();
var emptyRows = Enumerable.Range(0, rows).Where(row => !galaxies.Any(xy => xy.Y == row)).ToHashSet();

IEnumerable<int> Range(int x, int y) => Enumerable.Range(Math.Min(x, y), Math.Abs(x - y));
long Distance(XY first, XY second, long modifier) {
    var distance = Math.Abs(first.Y - second.Y) + Math.Abs(first.X - second.X);

    return distance
        + Range(first.X, second.X).Count(emptyCols.Contains) * modifier
        + Range(first.Y, second.Y).Count(emptyRows.Contains) * modifier;
}

var distance = 0L;
var oldDistance = 0L;
foreach (var first in galaxies) {
    foreach (var second in galaxies) {
        distance += Distance(first, second, 1);
        oldDistance += Distance(first, second, 999_999);
    }
}

Console.WriteLine(distance / 2);
Console.WriteLine(oldDistance / 2);

record XY(int Y, int X)
{
}