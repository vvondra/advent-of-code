var input = File.ReadLines("input.txt")
    .Select(line =>
        line
            .Split(":", StringSplitOptions.TrimEntries)[1]
            .Split(" ", StringSplitOptions.TrimEntries | StringSplitOptions.RemoveEmptyEntries)
            .Select(int.Parse).ToArray()
    )
    .ToArray();

var times = input[0];
var distances = input[1];

var result = Enumerable.Range(0, times.Length)
    .Select(i => Enumerable.Range(0, times[i]).Count(time => (times[i] - time) * time > distances[i]))
    .Aggregate(1, (acc, val) => acc * val);

Console.WriteLine(result);

var input2 = File.ReadLines("input.txt")
    .Select(line =>
        line
            .Split(":", StringSplitOptions.TrimEntries)[1]
    )
    .Select(num => num.Replace(" ", ""))
    .ToArray();

var time = int.Parse(input2[0]);
var distance = long.Parse(input2[1]);

var result2 = Enumerable.Range(0, time).Count(i => ((long)time - i) * i > distance);

Console.WriteLine(result2);