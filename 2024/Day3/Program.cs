using System.Text.RegularExpressions;

var input = File.ReadAllText("input.txt");

var matches = Regex.Matches(input, @"mul\((\d+),\s*(\d+)\)");
var results = new List<(int, int)>();

var sum = matches
    .Select(m => (int.Parse(m.Groups[1].Value), int.Parse(m.Groups[2].Value)))
    .Select(t => t.Item1 * t.Item2)
    .Sum();


Console.WriteLine(sum);


var splitByMultipleStrings = Regex.Split(input, @"(mul\(\d+,\s*\d+\)|do\(\)|don't\(\))")
    .Where(s => !string.IsNullOrEmpty(s))
    .ToList();

var enabled = true;
var sum2 = 0;
foreach (var part in splitByMultipleStrings)
{
    if (part == "do()")
    {
        enabled = true;
    }
    else if (part == "don't()")
    {
        enabled = false;
    }
    else if (enabled)
    {
        var match = Regex.Match(part, @"mul\((\d+),\s*(\d+)\)");
        if (match.Success)
        {
            var a = int.Parse(match.Groups[1].Value);
            var b = int.Parse(match.Groups[2].Value);
            sum2 += a * b;
        }
    }
}

Console.WriteLine(sum2);