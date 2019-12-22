import * as fs from "fs";

const input = fs.readFileSync("18.input", "utf-8")
  .trim()
  .split("\n")
  .map(s => s.trim().split(""));

type XY = [number, number];

console.log(input.map(e => e.join("")).join("\n"));

const toKey = (xy: XY) => xy.join(":");
const createMapping = (map: string[][]): { [key: string]: XY } => {
  const m = {};
  for (let i = 0; i < map.length; i++) {
    for (let j = 0; j < map[i].length; j++) {
      if (map[i][j] !== "." && map[i][j] !== "#") {
        m[map[i][j]] = [i, j];
      }
    }
  }
  return m;
}

const unvisited = (map: string[][]): string[] =>
  map.map(e => e.filter(f => f.match(/[^.#]/))).flat();

// BFS search giving options from current location, taking into account visited keys and doors
const options = (map: string[][], current: XY, visited: Set<string>, distance: number, mapping: { [key: string]: XY }) => {
  const history = new Set<string>([toKey(current)]);
  const options = [];

  const steps = (current: XY): XY[] => {
    const movements = [[1, 0], [-1, 0], [0, -1], [0, 1]];

    return movements
      .map(m => [current[0] + m[0], current[1] + m[1]] as XY)
      .filter(m => map[m[0]] && map[m[0]][m[1]]) // in bounds
      .filter(m => map[m[0]][m[1]] !== '#')
      .filter(m => !history.has(toKey(m)));
  }

  const queue = [...steps(current)].map(e => [e, distance + 1] as [XY, number]);

  while (queue.length > 0) {
    const [next, dist] = queue.pop();

    const cell = map[next[0]][next[1]];

    let through = false;
    let option = false;
    if (cell !== '.') {
      if (!visited.has(cell)) {
        if (cell.toUpperCase() === cell) { // Door
          if (visited.has(cell.toLowerCase())) { // We have the key
            option = true;
          }
        }

        if (cell.toLowerCase() === cell) { // Got key
          option = true;
        }

      } else {
        through = true;
      }
    } else {
      through = true;
    }

    if (through) {
      history.add(toKey(next));
      steps(next).forEach(s => queue.push([s, dist + 1]));
    }

    if (option) {
      options.push([next, dist, cell])
    }
  }

  return options;
}

const distances: { [key: string]: { [key: string]: [number, Set<string>] } } = {
  'A': {
    f: [0, new Set(['c', 'd'])]
  }
};

const options2 = (map: string[][], current: XY, visited: Set<string>, distance: number, mapping: { [key: string]: XY }) => {
  const cell = map[current[0]][current[1]];

  return Object.keys(distances[cell])
    .filter(
      f => [...distances[cell][f]].every(n => visited.has(n[1]))
    )
    .map(dest => {
      [mapping[dest], distance + distances[cell][dest][0], dest];
    });
};

const explore = (map: string[][]) => {
  const toVisit = unvisited(map).length;
  const mapping = createMapping(map);
  let minDistance = Infinity;
  const decide = (current: XY, visited: Set<string>, distance: number): number => {
    if (visited.size === toVisit) {
      console.log(distance)
      minDistance = Math.min(distance, minDistance);
      return distance;
    }

    const p = options(map, current, visited, distance, mapping)
      .filter(([_, distance]) => distance < minDistance)
      .map(([xy, newDistance, cell]) => {
        if (visited.size < 8) {
          console.log(visited);
        }
        return decide(xy, new Set([...visited, cell]), newDistance)
      });

    return Math.min(...p)
  };

  const paths = options(map, findStart(map), new Set(['@']), 0, mapping)
    .map(([xy, distance, cell]) => decide(xy, new Set(['@', cell]), distance + 1));

  return Math.min(...paths) - 1;
};

const res = explore(input);

console.log(res);

