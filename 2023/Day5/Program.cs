var input = File.ReadAllText("input.txt").Split(Environment.NewLine + Environment.NewLine, StringSplitOptions.TrimEntries).ToArray();

var seeds = input[0].Split(": ")[1].Split(" ", StringSplitOptions.TrimEntries).Select(long.Parse).ToArray();

var maps = input[1..]
    .Select(block => {
        var split = block.Split(Environment.NewLine);
        var mapping = split[0][..(split[0].Length - 5)].Split("-to-");
        var mappings = split[1..].Select(line => line.Split(" ").Select(long.Parse).ToArray()).ToArray();

        return (mapping: (from: mapping[0], to: mapping[1]), mappings);
    })
    .ToDictionary(x => x.mapping.from, x => x);



var result = seeds
    .Select(seed => {
        var current = "seed";
        var num = seed;

        while (current != "location") {
            var mapping = maps[current];

            var range = mapping.mappings.FirstOrDefault(ranges => num >= ranges[1] && num < ranges[1] + ranges[2])
                ?? [1, 1, 1];

            num = num - range[1] + range[0];
            current = mapping.mapping.to;
        }

        return num;
    })
    .Min();

Console.WriteLine(result);