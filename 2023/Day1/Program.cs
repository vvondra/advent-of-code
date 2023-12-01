var input = Day1.Properties.Resources.input.Split(Environment.NewLine);

var result = input
    .Select(x => x.ToCharArray().Where(y => char.IsDigit(y)))
    .Select(x => $"{x.First()}{x.Last()}")
    .Select(int.Parse)
    .Sum();

Console.WriteLine(result);