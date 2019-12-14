import * as fs from "fs";
import * as rd from "readline";

type Coord = { x: number; y: number; };
type Instruction = [string, number];
class Segment {
  a: Coord;
  b: Coord;
  distance: number;

  constructor(a: Coord, b: Coord, distance: number) {
    this.a = a;
    this.b = b;
    this.distance = distance;
  }

  intersects(other: Segment): boolean {
    if (Math.min(this.a.y, this.b.y) > Math.max(other.a.y, other.b.y)) {
      return false;
    }

    if (Math.min(this.a.x, this.b.x) > Math.max(other.a.x, other.b.x)) {
      return false;
    }

    if (Math.max(this.a.y, this.b.y) < Math.min(other.a.y, other.b.y)) {
      return false;
    }

    if (Math.max(this.a.x, this.b.x) < Math.min(other.a.x, other.b.x)) {
      return false;
    }

    return true;
  }

  // All lines perpendicular on the plane, this returns the constant dimension
  axis() {
    if (this.a.x === this.b.x) {
      return {x: this.a.x};
    } else {
      return {y: this.b.y};
    }
  }

  // Already assumes the segments intersect
  intersection(other: Segment): Coord {
    return { ...this.axis(), ...other.axis() } as Coord;
  }

  distanceTo(coord: Coord): number {
    return this.distance +  Math.abs(coord.x - this.a.x) + Math.abs(coord.y - this.a.y);
  }
}

const parse = (reader: rd.Interface): Promise<Segment[][]> => {
  const lines = [];
  return new Promise((resolve) => {
    reader
      .on("line", (line) => {
        const points: Array<[string, number]> = line
            .split(",")
            .map(s => s.trim())
            .map(s => [s.substring(0, 1), parseInt(s.substring(1), 10)]);

        const newSegments = points.reduce((acc: [Coord, number, Segment[]], next: Instruction) => {
          const [last, distanceToHere, segments] = acc;

          let nextLast: Coord;
          switch (next[0]) {
            case "U":
              nextLast = {x: last.x, y: last.y + next[1]};
              break;
            case "D":
              nextLast = {x: last.x, y: last.y - next[1]};
              break;
            case "L":
              nextLast = {x: last.x - next[1], y: last.y};
              break;
            case "R":
              nextLast = {x: last.x + next[1], y: last.y};
              break;
            default:
              throw new Error("Unknown dir: " + next[0]);
          }
          segments.push(new Segment(last, nextLast, distanceToHere));

          return [nextLast, distanceToHere + next[1], newSegments];
        }, [{x: 0, y: 0}, 0, []])[2];

        lines.push(newSegments);
      })
      .on("close", () => resolve(lines));
  });
};

parse(rd.createInterface(fs.createReadStream("3.input")))
  .then(lines => {
    let minDistance = Infinity;
    let minSignal = Infinity;
    lines[0].forEach(seg1 => {
      lines[1].forEach(seg2 => {
        if (seg1.intersects(seg2)) {
          const intersection = seg1.intersection(seg2);
          const distance = Math.abs(intersection.x) + Math.abs(intersection.y);
          const signal = seg1.distanceTo(intersection) + seg2.distanceTo(intersection);
          if (distance > 0 && distance < minDistance) {
            minDistance = distance;
          }

          if (signal > 0 && signal < minSignal) {
            minSignal = signal;
          }
        }
      });
    });

    console.log(minDistance);
    console.log(minSignal);
  });
