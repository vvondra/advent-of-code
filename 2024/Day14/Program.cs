var input = File.ReadLines("input.txt");


var robots = input.Select(x => {
    var parts = x
        .Split(" ")
        .SelectMany(x => x.Split("=").Skip(1).SelectMany(x => x.Split(",").Select(int.Parse)))
        .ToArray();

    return (PX: parts[0], PY: parts[1], VX: parts[2], VY: parts[3]);
});

static int Mod(int x, int m) => (x % m + m) % m;

var width = 101;
var height = 103;

HashSet<(int PX, int PY)> Steps(int steps) =>
    robots
        .Select(x =>
            (
                PX: Mod(x.PX + steps * x.VX, width),
                PY: Mod(x.PY + steps * x.VY, height)
            )
        )
        .ToHashSet();

var result = Steps(100)
    .Where(x => x.PX != width / 2 && x.PY != height / 2)
    .GroupBy(x => (x.PX < width / 2, x.PY < height / 2))
    .Select(x => x.Count())
    .Aggregate((a, b) => a * b);

Console.WriteLine(result);

for (var n = 7138; n <= 7138; n++) {
    var steps = Steps(n);

    for (var y = 0; y < height; y++) {
        for (var x = 0; x < width; x++) {
            Console.Write(steps.Contains((x, y)) ? '#' : '.');
        }
        Console.WriteLine();
    }

    Console.WriteLine(n);
}
