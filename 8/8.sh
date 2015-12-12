#!/bin/bash


BYTES_WO_WS=$(echo "$(wc -c < input ) - $(wc -l < input)" | bc)
CHAR_COUNT=0
while IFS='' read -r LINE || [[ -n "$LINE" ]]; do
	#echo $LINE
	LINE=$(sed "s/\x\([0-9][0-9]\)/\0\1/" <<< $LINE)
	echo $LINE
	CMD="INTERPOL=\$'${LINE:1:$(expr ${#LINE} - 2)}'"
	#echo $CMD
	#echo $INTERPOL
	eval $CMD
    CHAR_COUNT=$((CHAR_COUNT+${#INTERPOL}))
    #echo $CHAR_COUNT
    #echo "---"
done < input

echo "Bytes: $BYTES_WO_WS"
echo "Chars: $CHAR_COUNT"

echo $(($BYTES_WO_WS - $CHAR_COUNT))