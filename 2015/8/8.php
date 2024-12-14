<?php

$lines = file('input');

$rawChars = 0;
$memoryChars = 0;
$extraChars = 0;
foreach ($lines as $line) {
    $rawChars += strlen($line);
    $chars = str_split($line);

    $inEscape = false;
    for ($i = 1; $i < count($chars) - 1; $i++) {
        if ($chars[$i] == '\\') {
            $i++;
            if ($chars[$i] == '\\' || $chars[$i] == '"') {
                $memoryChars++;
                continue;
            }

            if ($chars[$i] == 'x') {
                $i += 2;
                $memoryChars++;
                continue;
            }
        } else {
            $memoryChars++;
        }
    }

    $extraChars += 4 + substr_count($line, '\\x') + preg_match_all('/\\\[^x]/', $line) * 2;
}

echo $rawChars - $memoryChars . "\n";
echo $extraChars . "\n";
