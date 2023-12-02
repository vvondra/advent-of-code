var input = File.ReadLines("input.txt");

var games = input
    .Where(row => !string.IsNullOrWhiteSpace(row))
    .Select(row =>
    {
        var parts = row.Split(":");
        var id = int.Parse(parts[0].Split(" ").Last().Trim());
        var draws = parts[1].Split(";").Select(draw =>
            draw.Split(", ")
                .Select(x => x.Trim().Split(" "))
                .ToDictionary(
                    x => x[1],
                    x => int.Parse(x[0])
                )
        ).ToList();

        return new Game(id, draws);
    })
    .ToList();

var criteria = new Dictionary<string, int> {
    {"red", 12 },
    {"green", 13 },
    {"blue", 14 }
};

var result = games
    .Where(game => game.Draws.All(draw => draw.All(ball => criteria[ball.Key] >= ball.Value)))
    .Select(game => game.Id)
    .Sum();

Console.WriteLine(result);

var result2 = games
    .Select(game => game.Draws.Aggregate(new Dictionary<string, int>(), (acc, draw) =>
    {
        foreach (var ball in draw)
        {
            acc[ball.Key] = int.Max(acc.GetValueOrDefault(ball.Key), ball.Value);
        }
        return acc;
    }))
    .Select(agg => agg.Aggregate(1, (acc, set) => acc * set.Value))
    .Sum();

    Console.WriteLine(result2);

public record Game(int Id, IEnumerable<Dictionary<string, int>> Draws);