using Microsoft.Z3;

var input = File.ReadLines("input.txt")
    .Select(line =>
    {
        var parts = line.Split('@');
        var coordinates = parts[0].Split(',').Select(long.Parse).ToArray();
        var offsets = parts[1].Split(',').Select(long.Parse).ToArray();
        return new long[][] { coordinates, offsets };
    })
    .ToArray();


using (Context ctx = new Context())
{
    IntExpr x = ctx.MkIntConst("x");
    IntExpr y = ctx.MkIntConst("y");
    IntExpr z = ctx.MkIntConst("z");
    IntExpr vx = ctx.MkIntConst("vx");
    IntExpr vy = ctx.MkIntConst("vy");
    IntExpr vz = ctx.MkIntConst("vz");

    Solver s = ctx.MkSolver();

    foreach (var item in input)
    {
        var point = item[0];
        var vector = item[1];

        s.Add(ctx.MkEq(ctx.MkAdd(ctx.MkMul(ctx.MkInt(point[0]), vx), x), ctx.MkInt(vector[0])));
        s.Add(ctx.MkEq(ctx.MkAdd(ctx.MkMul(ctx.MkInt(point[1]), vy), y), ctx.MkInt(vector[1])));
        s.Add(ctx.MkEq(ctx.MkAdd(ctx.MkMul(ctx.MkInt(point[2]), vz), z), ctx.MkInt(vector[2])));
    }

    if (s.Check() == Status.SATISFIABLE)
    {
        Console.WriteLine("Found a solution:");
        Console.WriteLine("Point: ({0}, {1}, {2})", s.Model.Evaluate(x), s.Model.Evaluate(y), s.Model.Evaluate(z));
        Console.WriteLine("Vector: ({0}, {1}, {2})", s.Model.Evaluate(vx), s.Model.Evaluate(vy), s.Model.Evaluate(vz));
    }
    else
    {
        Console.WriteLine("No solution found.");
    }
}