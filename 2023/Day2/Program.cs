var input = Day2.Properties.Resources.input.Split(Environment.NewLine);

var games = input
    .Select(x =>
    {
        var parts = x.Split(":");
        var id = int.Parse(parts[0].Split(" ").Last().Trim());
        var draws = parts[1].Split(";").Select(draw =>
        {
            draw.Split(", ").Select(ball =>
            {
                return ball.Split(" ")
                    .Select(x => x.Trim())
                    .ToList()
                    .ToDictionary(
                    y => y[1],
                    y => int.Parse(y[0])
            });
        });

        return new Game(id, draws);
    })



public record Game(int Id, List<Dictionary<string, int>> Draws);