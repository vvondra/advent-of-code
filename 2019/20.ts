import * as fs from "fs";

const input = fs.readFileSync("20.input", "utf-8")
  .split("\n")
  .slice(0, -1)
  .map(s => s.split(""));

type XY = [number, number];
type Maze = { maze: string[][], portals: { [key: string]: XY[] }, start: string, end: string }
const transpose = <T>(array: T[][]): T[][] => array[0].map((col, i) => array.map(row => row[i]));
const pad = <T>(arr: T[], len: number, fill: any): T[] => arr.concat(Array(len).fill(fill)).slice(0, len);
const toKey = (xy: XY) => xy.join(";");
const isPortal = (cell: string) => cell.charCodeAt(0) > 96

const normalize = (map: string[][]): Maze => {
  let seq = 'a'.charCodeAt(0);
  let mapping = {};
  let xys = {};

  const rename = (row: string[], j: number): string[] => {
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
        if (!xys[final[i][j]]) {
          xys[final[i][j]] = [];
        }
        xys[final[i][j]].push([i, j]);
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



const explore = (map: Maze) => {
  const { maze, portals, start, end } = map;
  let current = portals[start][0];
  let distance = 0;
  let target = portals[end][0];
  const visited = new Set([toKey(current)])
  const steps = (): XY[] => {
    const movements = [[1, 0], [-1, 0], [0, -1], [0, 1]];

    let candidates: XY[];
    const cell = maze[current[0]][current[1]];
    if (isPortal(cell) && cell != start) {
      candidates = portals[cell]
        .map(c => {
          return movements
            .map(m => [c[0] + m[0], c[1] + m[1]] as XY);
        })
        .flat();
    } else {
      candidates = movements
        .map(m => [current[0] + m[0], current[1] + m[1]] as XY);
    }

    return candidates
      .filter(m => maze[m[0]] && maze[m[0]][m[1]]) // in bounds
      .filter(m => maze[m[0]][m[1]] !== ' ' && maze[m[0]][m[1]] !== '#')
      .filter(e => !visited.has(toKey(e)));
  }

  const queue = [...steps().map(x => [x, 1] as [XY, number])];
  while (queue.length > 0) {
    [current, distance] = queue.shift();
    visited.add(toKey(current));
    if (isPortal(maze[current[0]][current[1]])) {
      distance = distance - 1;
    }
    if (maze[current[0]][current[1]] === end) {
      console.log(distance - 1);
      break;
    }

    steps()
      .forEach(e => queue.push([e, distance + 1]));
  }
}

const normalized = normalize(input);
console.log(normalized.maze.map(e => e.join("")).join("\n"));

explore(normalized);
