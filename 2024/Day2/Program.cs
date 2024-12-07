var input = File.ReadLines("input.txt");

var reports = input.Select(x => x.Split(' ').Select(int.Parse).ToList()).ToList();

var monotonousCount = reports.Count(report =>
    report.Zip(report.Skip(1), (a, b) => a <= b && b - a >= 1 && b - a <= 3).All(x => x) ||
    report.Zip(report.Skip(1), (a, b) => a >= b && a - b >= 1 && a - b <= 3).All(x => x)
);

Console.WriteLine(monotonousCount);

var skipCount = reports.Count(report =>
    report.Select((_, index) => report.Where((_, i) => i != index)).Any(skipReport =>
        skipReport.Zip(skipReport.Skip(1), (a, b) => a <= b && b - a >= 1 && b - a <= 3).All(x => x) ||
        skipReport.Zip(skipReport.Skip(1), (a, b) => a >= b && a - b >= 1 && a - b <= 3).All(x => x)
    )
);

Console.WriteLine(skipCount);