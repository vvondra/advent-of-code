(ns day10)

(use '[clojure.string :only (join)])

(defn look-n-say
  "docstring"
  [string]
  (join (mapcat (juxt count first) (partition-by identity string)))
  )


(println (count (nth (iterate look-n-say "3113322113") 50)))

