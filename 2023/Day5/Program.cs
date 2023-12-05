var input = File.ReadAllText("input.txt").Split(Environment.NewLine + Environment.NewLine, StringSplitOptions.TrimEntries).ToArray();

var seeds = input[0].Split(": ")[1].Split(" ", StringSplitOptions.TrimEntries).Select(int.Parse).ToArray();

var maps = input[1..].Select(block => {
    var split = block.Split(Environment.NewLine);
    var mapping = split[0][..(split[0].Length - 5)].Split("-to-");
    var mappings = split[1..].Select(line => line.Split(" ").Select(int.Parse).ToArray()).ToArray();

    return (mapping: (from: mapping[0], to: mapping[1]), mappings);
});

maps.Select(x => x.ToString()).ToList().ForEach(Console.WriteLine);