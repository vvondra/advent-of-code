var input = File.ReadLines("input.txt").ToList();

var grid = input
    .SelectMany((line, row) => line.Select((c, col) => (new XY(row, col), c)))
    .ToDictionary(
        x => x.Item1,
        x => x.Item2
    );

var numbers = new List<Number>();
for (int row = 0; row < input.Count; row++)
{
    for (int col = 0; col < input.First().Length; col++)
    {
        if (char.IsDigit(grid[new XY(row, col)]))
        {
            var num = string.Concat(input[row][col..].TakeWhile(char.IsDigit));
            numbers.Add(new Number(new XY(row, col), num));
            col += num.Length;
        }
    }
}

var result = numbers
    .Where(number => number.Adjacent().Where(c => grid.ContainsKey(c)).Any(c => grid[c] != '.' && !char.IsDigit(grid[c])))
    .Select(number => int.Parse(number.Digits))
    .Sum();

Console.WriteLine(result);

var result2 = numbers
    .Select(number => (number, number.Adjacent().Distinct().Where(c => grid.ContainsKey(c) && grid[c] == '*')))
    .Where(number => number.Item2.Any())
    .Select(number => (number.number, number.Item2.Single()))
    .GroupBy(x => x.Item2)
    .Where(c => c.Count() == 2)
    .Select(c => c.Select(x => int.Parse(x.number.Digits)).Aggregate(1, (acc, y) => acc * y))
    .Sum();

Console.WriteLine(result2);

record Number(XY XY, string Digits)
{
    public IEnumerable<XY> Adjacent() => Enumerable.Range(0, Digits.Length)
                                            .Select(y => XY + new XY(0, y))
                                            .SelectMany(y => y.Adjacent());
}
record XY(int Y, int X)
{
    public static XY operator +(XY me, XY other) => new(Y: me.Y + other.Y, X: me.X + other.X);

    static readonly XY[] Dirs =
    [
        new(-1, -1),
        new(-1, 0),
        new(-1, 1),
        new(0, -1),
        new(0, 1),
        new(1, -1),
        new(1, 0),
        new(1, 1),
    ];

    public IEnumerable<XY> Adjacent() => Dirs.Select(dir => this + dir);
}