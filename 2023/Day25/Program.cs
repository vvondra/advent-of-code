var input = File.ReadLines("input.txt");

Dictionary<string, List<string>> adjacencyList = [];

foreach (var line in input)
{
    var parts = line.Split(": ");
    var vertex = parts[0].Trim();
    var adjacentVertices = parts[1].Split(' ').Select(v => v.Trim()).ToList();

    if (!adjacencyList.ContainsKey(vertex))
    {
        adjacencyList[vertex] = [];
    }

    adjacencyList[vertex].AddRange(adjacentVertices);

    foreach (var adjacentVertex in adjacentVertices)
    {
        if (!adjacencyList.ContainsKey(adjacentVertex))
        {
            adjacencyList[adjacentVertex] = [vertex];
        }
        else
        {
            adjacencyList[adjacentVertex].Add(vertex);
        }
    }
}

List<(string, string)> minCut = [("chr", "zlx"), ("spk", "hqp"), ("hlx", "cpq")];

foreach (var (vertex1, vertex2) in minCut)
{
    adjacencyList[vertex1].Remove(vertex2);
    adjacencyList[vertex2].Remove(vertex1);
}

Console.WriteLine(CalculateComponentSize(adjacencyList, "chr") * CalculateComponentSize(adjacencyList, "zlx"));

int CalculateComponentSize(Dictionary<string, List<string>> adjacency, string startVertex)
{
    HashSet<string> visited = [];
    Queue<string> queue = new();
    int componentSize = 0;

    queue.Enqueue(startVertex);
    visited.Add(startVertex);

    while (queue.TryDequeue(out var currentVertex))
    {
        componentSize++;

        foreach (var adjacentVertex in adjacency[currentVertex])
        {
            if (!visited.Contains(adjacentVertex))
            {
                queue.Enqueue(adjacentVertex);
                visited.Add(adjacentVertex);
            }
        }
    }

    return componentSize;
}
