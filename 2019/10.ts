import * as fs from "fs";

const input = fs.readFileSync("10.input", "utf-8")
  .trim()
  .split("\n")
  .map(s => s.trim().split(""));

const asteroids = new Set<string>();

input.forEach((row, i) => {
  row.forEach((col, j) => {
    if (input[i][j] === "#") {
      asteroids.add(`${j}:${i}`);
    }
  })
});

const toCoord = (a: string): [number, number] => a.split(":").map(c => parseInt(c, 10)) as [number, number];
const distance = (a: string, b: string): number => {
  const ca = toCoord(a);
  const cb = toCoord(b);

  return Math.abs(ca[0] - cb[0]) + Math.abs(ca[1] - cb[1]);
}
const vector = (a: string, b: string): [number, number] => {
  const ca = toCoord(a);
  const cb = toCoord(b);

  return [cb[0] - ca[0], cb[1] - ca[1]];
}
const isMultiple = (base: [number, number], a: [number, number]): boolean => {
  for (let c = 1; c < 33; c++) {
      if (c * base[0] === a[0] && c * base[1] === a[1]) {
        return true;
      }
  }

  return false;
}
const canonical = (base: [number, number]): [number, number] => {
  const divisor = gcd(base[0], base[1]);
  return [base[0] / divisor, base[1] / divisor];
}
const gcd = (x: number, y: number): number => {
  x = Math.abs(x);
  y = Math.abs(y);
  while (y) {
    var t = y;
    y = x % y;
    x = t;
  }
  return x;
}

const countDetected = (asteroid: string, asteroids: Set<string>): number => {
  const asteroidsCopy = new Set(asteroids);
  asteroidsCopy.delete(asteroid);
  let detectable = Array.from(asteroidsCopy)
  detectable.sort((a, b) => distance(a, asteroid) - distance(b, asteroid));

  const detected = new Set<string>();

  while (detectable.length > 0) {
    const test = detectable[0];
    detected.add(test);
    const base = canonical(vector(asteroid, test));
    detectable = detectable.filter(d => !isMultiple(base, vector(asteroid, d)));
  }

  return detected.size;
}

const max = Array.from(asteroids)
  .map(a => [a, countDetected(a, asteroids)])
  .reduce((prev, next) => {
    if (prev[1] < next[1]) {
      return next;
    }
    return prev;
  }, ["x", -Infinity]);

console.log(max);

const station = (max as [string, number])[0];
console.log(station);

const angle = (c: [number, number]): number => Math.atan2(c[0], c[1]);
const laser = (station: string, asteroids: string[]) => {
  let order = asteroids.reduce((groups, next) => {
    const g = angle(vector(station, next));
    if (!(g in groups)) {
      groups[g] = [];
    }

    groups[g].push(next);

    return groups;
  }, {});

  for (const k in order) {
    order[k].sort((a, b) => distance(a, station) - distance(b, station));
  }

  let angles = Object.keys(order).map(Number);
  angles.sort((b, a) => b - a).reverse();
  let current = 0;
  let count = 0;
  while (angles.length > 0) {
    const target = order[angles[current]][0];
    order[angles[current]] = order[angles[current]].slice(1);

    if (order[angles[current]].length === 0) {
      delete order[angles[current]];
      angles.splice(current, 1)
      current = current % angles.length;
    } else {
      current = (current + 1) % angles.length;
    }


    count++;

    if (count === 200) {
      console.log(count, target);
    }
  }

  return 0;
}

const remaining = new Set(asteroids);
remaining.delete(station);
laser(station, Array.from(remaining));
