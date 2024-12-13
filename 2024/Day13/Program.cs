using Microsoft.Z3;

var input = File.ReadAllText("input.txt").Split("\n\n");

var machines = input.Select(x =>
{
    var lines = x.Split('\n');
    var buttonA = lines[0].Split(["X+", ", Y+"], StringSplitOptions.None).Skip(1).Select(int.Parse).ToArray();
    var buttonB = lines[1].Split(["X+", ", Y+"], StringSplitOptions.None).Skip(1).Select(int.Parse).ToArray();
    var prize = lines[2].Split(["X=", ", Y="], StringSplitOptions.None).Skip(1).Select(int.Parse).ToArray();
    return (AX: buttonA[0], AY: buttonA[1], BX: buttonB[0], BY: buttonB[1], PX: prize[0], PY: prize[1]);

});

var sum = 0L;
var modifier = 10000000000000;

sum = machines
    .Select(machine =>
    {
        using Context ctx = new();

        IntExpr a = ctx.MkIntConst("a");
        IntExpr b = ctx.MkIntConst("b");
        IntExpr cost = ctx.MkIntConst("cost");

        using Solver s = ctx.MkSolver();

        s.Add(
            ctx.MkEq(
                ctx.MkInt(machine.PX + modifier),
                ctx.MkAdd(ctx.MkMul(ctx.MkInt(machine.AX), a), ctx.MkMul(ctx.MkInt(machine.BX), b))
            )
        );

        s.Add(
            ctx.MkEq(
                ctx.MkInt(machine.PY + modifier),
                ctx.MkAdd(ctx.MkMul(ctx.MkInt(machine.AY), a), ctx.MkMul(ctx.MkInt(machine.BY), b))
            )
        );

        s.Add(
            ctx.MkEq(
                cost,
                ctx.MkAdd(ctx.MkMul(ctx.MkInt(3), a), b)
            )
        );

        using Optimize opt = ctx.MkOptimize();
        opt.Add(s.Assertions);
        opt.MkMinimize(cost);

        if (opt.Check() == Status.SATISFIABLE)
        {
            Model model = opt.Model;
            return ((IntNum)model.Evaluate(cost)).Int64;
        }

        return 0L;
    })
    .Sum();

Console.WriteLine(sum);