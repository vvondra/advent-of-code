import * as fs from "fs";
import Program from "./intcode";

const input = fs.readFileSync("15.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(Number);


type Coord = [number, number];
type Step = {
  destination: Coord,
  robot: Program,
  distance: number
}
const movements = {
  1: [1, 0],
  2: [-1, 0],
  3: [0, -1],
  4: [0, 1]
}
const toKey = (coord: Coord): string => coord.join(":");
enum Output {
  Wall = 0,
  Moved = 1,
  Done = 2
}

(async () => {
  let map: { [key: string]: number | Program } = { "0:0": 0 };
  const explore = function * (coord: Coord, prog: Program, distance: number): Generator<Step> {
    for (let dir = 1; dir < 5; dir++) {
      const destination = [coord[0] + movements[dir][0], coord[1] + movements[dir][1]] as Coord;
      if (!(toKey(destination) in map)) {
        const robot = prog.dup().addInput(dir);
        map[toKey(destination)] = robot;
        yield { destination, robot, distance };
      }
    }
  }

  // Run a BFS to find the oxygen system

  let queue: Step[] = [...explore([0, 0], new Program(input), 1)];
  let lastStep: Step;
  while (queue.length > 0) {
    const next = queue.shift();
    const out = await next.robot.nextVal();
    switch (out) {
      case Output.Wall:
        map[toKey(next.destination)] = Infinity;
        break;
      case Output.Moved:
        map[toKey(next.destination)] = next.distance;
        [...explore(next.destination, next.robot, next.distance+ 1)].forEach(n => {
          queue.push(n);
        });
        break;
      case Output.Done:
        queue = [];
        lastStep = next;
        break;
    }
  }
  console.log(lastStep.destination, lastStep.distance);

  // Re-run a very similar BFS but letting it finish until all places a reached
  // and keep track of max distance reached

  let maxDistance = 0;
  map = { [toKey(lastStep.destination)]: 0 };
  queue = [...explore(lastStep.destination, lastStep.robot, 1)];
  while (queue.length > 0) {
    const next = queue.shift();
    const out = await next.robot.nextVal();
    switch (out) {
      case Output.Wall:
        map[toKey(next.destination)] = Infinity;
        break;
      case Output.Moved:
      case Output.Done:
        map[toKey(next.destination)] = next.distance;
        maxDistance = Math.max(next.distance, maxDistance);
        [...explore(next.destination, next.robot, next.distance + 1)].forEach(n => {
          queue.push(n);
        });
        break;
    }
  }

  console.log(maxDistance);
})();
