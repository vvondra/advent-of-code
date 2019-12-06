import * as fs from "fs";
import * as rd from "readline";

const reader = rd.createInterface(fs.createReadStream("6.input"));

const orbits = {
  COM: null,
};

reader
  .on("line", (input: string) => {
    const rel = input.split(")");
    orbits[rel[1]] = rel[0];
  })
  .on("close", () => {
    const total = Object.values(orbits).reduce((acc, planet) => {
      let p = orbits[planet];
      let sum = 1;
      while (parent != null) {
        sum += 1;
        p = orbits[p];
      }

      return acc + sum;
    }, 0) - 1;

    console.log(total);

    const markDepth = (path: string[]) => {
      return path.reverse().map((s, i) => {
        return [s, i];
      }).reduce((acc, e) => {
        acc[e[0]] = e[1];
        return acc;
      }, {});
    };

    const santaPath = ["SAN"];
    const youPath = ["YOU"];
    let parent = orbits["SAN"];
    while (parent != null) {
      santaPath.push(parent);
      parent = orbits[parent];
    }

    parent = orbits["YOU"];
    while (parent != null) {
      youPath.push(parent);
      parent = orbits[parent];
    }

    const santaPathWithDepth = markDepth(santaPath);
    const youPathWithDepth = markDepth(youPath);

    let pathLength = 0;
    let commonParent = null;
    parent = orbits["SAN"];
    while (parent != null) {
      pathLength++;
      if (santaPathWithDepth[parent] === youPathWithDepth[parent]) {
        commonParent = parent;
        break;
      }
      parent = orbits[parent];
    }

    parent = orbits["YOU"];
    while (parent != null) {
      pathLength++;
      if (parent === commonParent) {
        break;
      }
      parent = orbits[parent];
    }

    console.log(pathLength - 2);
  });
