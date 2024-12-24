var input = File
    .ReadLines("input.txt")
    .Select(l => l.Split('-').ToArray());


var vertices = input.SelectMany(l => l).ToHashSet();
var edges = input
    .SelectMany(l => new[] { (l[0], l[1]), (l[1], l[0]) })
    .GroupBy(l => l.Item1)
    .ToDictionary(l => l.Key, l => l.Select(l => l.Item2).Order().ToList());


var unvisited = vertices.ToHashSet();

static HashSet<HashSet<string>> FindTriangles(HashSet<string> vertices, Dictionary<string, List<string>> edges)
{
    var triangles = new HashSet<HashSet<string>>(HashSet<string>.CreateSetComparer());

    foreach (var node in vertices)
    {
        if (!edges.ContainsKey(node)) continue;

        var neighbors = edges[node];
        for (int i = 0; i < neighbors.Count; i++)
        {
            for (int j = i + 1; j < neighbors.Count; j++)
            {
                string neighbor1 = neighbors[i];
                string neighbor2 = neighbors[j];

                if (edges.ContainsKey(neighbor1) && edges[neighbor1].Contains(neighbor2))
                {
                    var triangle = new HashSet<string> { node, neighbor1, neighbor2 };
                    triangles.Add(triangle);
                }
            }
        }
    }

    return triangles;
}

var triangles = FindTriangles(vertices, edges);

Console.WriteLine(triangles.Count(x => x.Any(y => y.StartsWith('t'))));

void BronKerbosch(HashSet<string> R, HashSet<string> P, HashSet<string> X, List<HashSet<string>> cliques)
{
    if (!P.Any() && !X.Any())
    {
        cliques.Add([.. R]);
        return;
    }

    var P1 = new HashSet<string>(P);
    foreach (var v in P1)
    {
        var neighbors = edges.ContainsKey(v) ? new HashSet<string>(edges[v]) : [];
        BronKerbosch([.. R, v], [.. P.Intersect(neighbors)], [.. X.Intersect(neighbors)], cliques);
        P.Remove(v);
        X.Add(v);
    }
}

var cliques = new List<HashSet<string>>();
BronKerbosch([], [.. vertices], [], cliques);

Console.WriteLine(string.Join(',', cliques.MaxBy(x => x.Count).Order()));