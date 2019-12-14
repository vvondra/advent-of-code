import * as fs from "fs";

type Coord = { x: number, y: number, z: number };

const input: Coord[] = fs.readFileSync("12.input", "utf-8")
  .trim()
  .split("\n")
  .map(s => {
    const matches = [...s.matchAll(/([a-z])=(-?\d+)/g)];

    return matches.reduce((coord, match) => {
      return { ...coord, [match[1]]: parseInt(match[2], 10) };
    }, {}) as Coord;
  });

const simulate = function * (initial: Coord[]): Generator<[Coord[], Coord[]]> {
  const stars = initial.map(x => ({ x: x.x, y: x.y, z: x.z }))
  const velocities = stars.map(x => ({ x: 0, y: 0, z: 0 }));

  while (true) {
    for (let star = 0; star < stars.length; star++) {
      const changes = { x: 0, y: 0, z: 0 };
      for (let other = 0; other < stars.length; other++) {
        for (const coord in changes) {
          if (stars[star][coord] > stars[other][coord]) {
            changes[coord]--;
          } else if (stars[star][coord] < stars[other][coord]) {
            changes[coord]++;
          }
        }
      }

      for (const coord in changes) {
        velocities[star][coord] += changes[coord];
      }
    }

    for (let star = 0; star < stars.length; star++) {
      for (const coord in velocities[star]) {
        stars[star][coord] += velocities[star][coord];
      }
    }

    yield [stars, velocities]
  }
}

const energy = (stars: Coord[], velocities: Coord[]) => {
  let total = 0;
  for (let star = 0; star < stars.length; star++) {
    let potential = 0;
    let kinetic = 0;
    for (const coord in stars[star]) {
      potential += Math.abs(stars[star][coord]);
      kinetic += Math.abs(velocities[star][coord]);
    }

    total += potential * kinetic;
  }

  return total;
}

function gcd(x: number, y: number) {
  let ay = Math.abs(y);
  let ax = Math.abs(x);
  while (ay) {
    const t = ay;
    ay = ax % y;
    ax = t;
  }

  return ax;
}

const cycle = (initial: Coord[], coord: string): number => {
  const visited = {};
  let counter = 0;
  const key = (s: Coord[], v: Coord[]) => s.map(s => s[coord]).join(":") + ":" + v.map(s => s[coord]).join(":");
  let simulation = simulate(initial);
  let next: [Coord[], Coord[]];
  while (true) {
    next = simulation.next().value
    const k = key(...next);
    if (k in visited) {
      return counter - visited[k];
    }

    visited[k] = counter;
    counter++;
  }
}

let simulation = simulate(input);
let counter = 1000;
let last: [Coord[], Coord[]];
while (counter > 0) {
  last = simulation.next().value
  counter--;
}

console.log(energy(...last));

const x = cycle(input, "x");
const y = cycle(input, "y");
const z = cycle(input, "z");

const xy = (x * y) / gcd(x, y);
const xyz = (xy * z) / gcd(xy, z);

console.log(xyz);
