extern crate regex;

use std::io::BufReader;
use std::io::BufRead;
use std::fs::File;
use std::collections::HashMap;

use regex::Regex;

struct Wire {
    name: String,
    source: Source
}

#[derive(Debug)]
enum Source {
    Charge(u64),
    Wire(Box<String>),
    Not(Box<Source>),
    And(Box<Source>, Box<Source>),
    Or(Box<Source>, Box<Source>),
    Xor(Box<Source>, Box<Source>),
    Rshift(Box<Source>, Box<Source>),
    Lshift(Box<Source>, Box<Source>)
}

fn charge(source: &Source, wires: &HashMap<String, Wire>/*, charges: &mut HashMap<String, u64>*/) -> u64 {
    println!("Resolving charge for source {:?}", source);
    match *source {
        Source::Charge(charge) => charge,
        Source::Wire(ref wire_name) => {
            println!("Resolving charge for wire {}", wire_name);

            match wires.get(&wire_name.to_string()) {
                Some(ref wire) => charge(&wire.source, wires),
                _ => { panic!("Missing wire in map {}", wire_name); 0 },
            }
        },
        Source::Not(ref source) => !charge(source, wires),
        Source::And(ref source1, ref source2) => charge(source1, wires) & charge(source2, wires),
        Source::Or(ref source1, ref source2) => charge(source1, wires) | charge(source2, wires),
        Source::Xor(ref source1, ref source2) => charge(source1, wires) ^ charge(source2, wires),
        Source::Rshift(ref source1, ref source2) => charge(source1, wires) >> charge(source2, wires),
        Source::Lshift(ref source1, ref source2) => charge(source1, wires) << charge(source2, wires)
    }
}

fn get_source(wires: &HashMap<String, Wire>, source: &str) -> Source {
    let charge: Option<u64> = source.trim().parse().ok();
    if charge.is_some() {
        return Source::Charge(charge.unwrap());
    }

    let wire_pattern = Regex::new(r"^([a-z]+)$").unwrap();
    if wire_pattern.is_match(source) {
        return Source::Wire(Box::new(source.to_string()));
    }

    let not_pattern = Regex::new(r"NOT ([0-9a-z]+)").unwrap();
    for cap in not_pattern.captures_iter(source) {
        return Source::Not(
            Box::new(get_source(wires, cap.at(1).unwrap()))
        )
    }

    let op_pattern = Regex::new(r"([0-9a-z]+) (AND|OR|XOR|RSHIFT|LSHIFT) ([0-9a-z]+)").unwrap();

    for cap in op_pattern.captures_iter(source) {
        let left = Box::new(get_source(wires, cap.at(1).unwrap()));
        let right = Box::new(get_source(wires, cap.at(3).unwrap()));
        
        return match cap.at(2).unwrap() {
            "AND" => Source::And(left, right),
            "OR" => Source::Or(left, right),
            "XOR" => Source::Xor(left, right),
            "RSHIFT" => Source::Rshift(left, right),
            "LSHIFT" => Source::Lshift(left, right),
            _ => {
                println!("WARNING, unrecognized sources: {}", source);
                Source::Charge(0)
            }
        }
    }
    println!("WARNING, unrecognized sources: {}", source);
    Source::Charge(0)
}

fn add_wire(wires: &mut HashMap<String, Wire>, target_wire: &str, source: Source) {
    wires.insert(
        target_wire.to_string(),
        Wire {
            name: String::from(target_wire),
            source: source
        }
    );
}

fn main() {
    let f = File::open("input").unwrap();

    let file = BufReader::new(&f);

    let mut wires = HashMap::new();
    //let mut charges = HashMap::new();
    
    for line in file.lines() {
        let l = line.unwrap();

        let mut split = l.split(" -> ");
        
        let source = split.next().unwrap();

        let target_wire = split.next().unwrap();
        let source = get_source(&wires, source);

        add_wire(&mut wires, target_wire, source)
    }

    match wires.get("a") {
        Some(wire) => println!("{}: {}", wire.name, charge(&wire.source, &wires/*, &mut charges*/)),
        None => println!("a is not in the set.")
    }

}
