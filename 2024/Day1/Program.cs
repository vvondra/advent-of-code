var input = File.ReadLines("input.txt");

var parsedInput = input
    .Select(l => l.Split(new[] { ' ' }, StringSplitOptions.RemoveEmptyEntries).Select(int.Parse).ToArray());

var first = parsedInput.Select(l => l[0]).OrderBy(x => x);
var second = parsedInput.Select(l => l[1]).OrderBy(x => x);
var result = first.Zip(second, (f, s) => Math.Abs(f - s)).Sum();
Console.WriteLine(result);

var secondCount = second.GroupBy(s => s).ToDictionary(g => g.Key, g => g.Count());
var sum = first.Sum(f => f * secondCount.GetValueOrDefault(f, 0));

Console.WriteLine(sum);