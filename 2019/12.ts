import * as fs from "fs";

type Coord = { x: number, y: number, z: number };

const stars: Coord[] = fs.readFileSync("12.input", "utf-8")
  .trim()
  .split("\n")
  .map(s => {
    const matches = [...s.matchAll(/([a-z])=(-?\d+)/g)];

    return matches.reduce((coord, match) => {
      return { ...coord, [match[1]]: parseInt(match[2], 10) };
    }, {}) as Coord;
  });

const velocities = stars.map(x => ({ x: 0, y: 0, z: 0 }));

let last = 0;
for (let i = 0; i < 10000000; i++) {
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

  if (i === 999) {
    let energy = 0;
    for (let star = 0; star < stars.length; star++) {
      let potential = 0;
      let kinetic = 0;
      for (const coord in stars[star]) {
        potential += Math.abs(stars[star][coord]);
        kinetic += Math.abs(velocities[star][coord]);
      }

      energy += potential * kinetic;
    }

    console.log(energy);
  }

  if (stars[0].y === 2) {
    console.log(i, i - last);
    last = i;
  }

  if (i % 100000 === 0) {
    console.log(i);
  }
}
