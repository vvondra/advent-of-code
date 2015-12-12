#!/bin/bash


BYTES_WO_WS=$(echo "$(wc -c < input ) - $(wc -l < input)" | bc)

ESCAPE_COUNT=$(grep -o '\\x[a-z0-9][a-z0-9]' input | wc -l)
SLASH_COUNT=$(grep -o '\\\\' input | wc -l)
QUOTE_COUNT=$(grep -o '\\"' input | wc -l)
LINE_COUNT=$(wc -l < input)


echo "Bytes: $BYTES_WO_WS"
echo "Escapes: $ESCAPE_COUNT"
echo "Slashes: $SLASH_COUNT"
echo "Quotes: $QUOTE_COUNT"
echo "Lines: $LINE_COUNT"

echo $((3 * $ESCAPE_COUNT + $SLASH_COUNT + $QUOTE_COUNT + 2 * ($LINE_COUNT + 1)))