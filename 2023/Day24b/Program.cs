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


using (Context ctx = new())
{
    IntExpr x = ctx.MkIntConst("x");
    IntExpr y = ctx.MkIntConst("y");
    IntExpr z = ctx.MkIntConst("z");
    IntExpr vx = ctx.MkIntConst("vx");
    IntExpr vy = ctx.MkIntConst("vy");
    IntExpr vz = ctx.MkIntConst("vz");

    Solver s = ctx.MkSolver();

    int index = 0;
    foreach (var item in input)
    {
        IntExpr t = ctx.MkIntConst("t" + index++);
        s.Add(ctx.MkGe(t, ctx.MkInt(0)));
        var point = item[0];
        var vector = item[1];

        s.Add(
            ctx.MkEq(
                ctx.MkAdd(ctx.MkMul(t, vx), x),
                ctx.MkAdd(ctx.MkMul(t, ctx.MkInt(vector[0])), ctx.MkInt(point[0]))
            )
        );
        s.Add(
            ctx.MkEq(
                ctx.MkAdd(ctx.MkMul(t, vy), y),
                ctx.MkAdd(ctx.MkMul(t, ctx.MkInt(vector[1])), ctx.MkInt(point[1]))
            )
        );
        s.Add(
            ctx.MkEq(
                ctx.MkAdd(ctx.MkMul(t, vz), z),
                ctx.MkAdd(ctx.MkMul(t, ctx.MkInt(vector[2])), ctx.MkInt(point[2]))
            )
        );
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