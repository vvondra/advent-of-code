import * as fs from "fs";

const input = fs.readFileSync("20.input", "utf-8")
  .split("\n")
  .slice(0, -1)
  .map(s => s.split(""));

type XY = [number, number];
type XYZ = [XY, number];
type Maze = { maze: string[][], portals: { [key: string]: { inner: XY, outer: XY } }, start: string, end: string }
const transpose = <T>(array: T[][]): T[][] => array[0].map((col, i) => array.map(row => row[i]));
const pad = <T>(arr: T[], len: number, fill: any): T[] => arr.concat(Array(len).fill(fill)).slice(0, len);
const toKey = (xy: XY, level: number) => xy.join(";") + ';' + level;
const isPortal = (cell: string) => cell.charCodeAt(0) > 96
const isOuter = (map: string[][], xy: XY) => {
  const [i, j] = xy;
  return i < 2 || j < 2 || i > map.length - 3 || j > map[0].length - 3;
}

const normalize = (map: string[][]): Maze => {
  let seq = 'a'.charCodeAt(0);
  let mapping = {};
  let xys = {};

  const rename = (row: string[]): string[] => {
    const r = [];
    for (let i = 0; i < row.length - 1; i++) {
      if (row[i].match(/[A-Z]/) && row[i + 1].match(/[A-Z]/)) {
        const portal = row[i] + row[i + 1];
        let remap = String.fromCharCode(seq);

        if (mapping[portal]) {
          remap = mapping[portal];
        } else {
          seq++;
          mapping[portal] = remap;
        }

        if (row[i - 1] == '.') {
          r.push(remap, ' ');
        }
        if (row[i + 2] == '.') {
          r.push(' ', remap);
        }
        i++;
      } else {
        r.push(row[i])
        if (i === row.length - 2) {
          r.push(row[i + 1])
        }
      }
    }
    return r;
  }

  const bounds = [input.length, Math.max(...input.map(l => l.length))]
  const padded = map.map(r => pad(r, bounds[1], " "));
  const first = padded.map(rename);
  const second = transpose(first);
  const third = second.map(rename);
  const final = transpose(third);

  for (let i = 0; i < final.length; i++) {
    for (let j = 0; j < final[i].length; j++) {
      if (isPortal(final[i][j])) {
        let label = "inner";
        if (isOuter(final, [i, j])) {
          label = "outer"
        }
        xys[final[i][j]] = { ...xys[final[i][j]], [label]: [i, j] }
      }
    }
  }

  return {
    maze: final,
    portals: xys,
    start: mapping["AA"],
    end: mapping["ZZ"]
  };
}

const explore = (map: Maze, recursive: boolean) => {
  const { maze, portals, start, end } = map;
  let current = portals[start].outer;
  let distance = 0;
  let level = 0;
  const visited = new Set([toKey(current, level)])
  const steps = (): XYZ[] => {
    const movements = [[1, 0], [-1, 0], [0, -1], [0, 1]];

    let candidates: XYZ[];
    const cell = maze[current[0]][current[1]];
    if (isPortal(cell) && isOuter(maze, current) && level == 0 && recursive && cell != end && cell != start) {
      return [];
    }

    if ((cell == start || cell == end ) && level > 0) {
      return [];
    }

    if (isPortal(cell) && cell != start) {
      const base = isOuter(maze, current) ? portals[cell].inner : portals[cell].outer;
      candidates = candidates = movements
        .map(m => [[base[0] + m[0], base[1] + m[1]], level + (isOuter(maze, current) ? -1 : 1)] as XYZ);
    } else {
      candidates = movements
        .map(m => [[current[0] + m[0], current[1] + m[1]], level] as XYZ);
    }

    return candidates
      .filter(m => maze[m[0][0]] && maze[m[0][0]][m[0][1]]) // in bounds
      .filter(m => maze[m[0][0]][m[0][1]] !== ' ' && maze[m[0][0]][m[0][1]] !== '#')
      .filter(e => !visited.has(toKey(e[0], e[1])))
      .filter(e => e[1] < 30) // short-circuit some
      .map(m => recursive ? m : [m[0], 0]);
  }
  const queue = [...steps().map(x => [x, 1] as [XYZ, number])];
  while (queue.length > 0) {
    [[current, level], distance] = queue.shift();

    visited.add(toKey(current, level));

    if (isPortal(maze[current[0]][current[1]])) {
      distance = distance - 1; // I forgot to read the instructions, portals are activated on dot
    }

    if (maze[current[0]][current[1]] === end && level === 0) {
      console.log(distance - 1);
      break;
    }

    steps().forEach(e => queue.push([e, distance + 1]));
  }
}

const normalized = normalize(input);
console.log(normalized.maze.map(e => e.join("")).join("\n"));

explore(normalized, false);
explore(normalized, true);
