package main

import (
    "bufio"
    "fmt"
    "log"
    "os"
    "strings"
)

func main() {
    file, err := os.Open("input")
    if err != nil {
        log.Fatal(err)
    }
    defer file.Close()

    count := 0

    scanner := bufio.NewScanner(file)
    for scanner.Scan() {
        line := scanner.Text()
        vowels := (strings.Count(line, "a") +
            strings.Count(line, "e") +
            strings.Count(line, "i") +
            strings.Count(line, "o") +
            strings.Count(line, "u"))

        nice := vowels >= 3 &&
            !strings.Contains(line, "ab") &&
            !strings.Contains(line, "cd") &&
            !strings.Contains(line, "pq") &&
            !strings.Contains(line, "xy")

        hasDouble := false
        for i := 1; i < len(line); i++ {
            if line[i] == line[i-1] {
                hasDouble = true
                break
            }
        }
        nice = nice && hasDouble

        if nice {
            count++
        }
    }
 
    fmt.Println(count)
    

    if err := scanner.Err(); err != nil {
        log.Fatal(err)
    }
}