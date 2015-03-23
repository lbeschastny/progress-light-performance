(ns progress-light-performance.core
  (:require [progress-light.core :as progress-light]
            [clj-progress.core :as progress])
  (:gen-class))

(defn test1 [n]
  (progress/init n)
  (dotimes [i n] (progress/tick))
  (progress/done))

(defn test2 [n]
  (let [p-light (progress-light/progress-light)]
    (progress-light/monitor-progress p-light n)
    (dotimes [i n] (progress-light/tick p-light))
    (progress-light/done p-light)))

(defn -main
  "Benchmark performance of clj-progress versus progress-light"
  [& args]
  (let [n 1000000]
    (time (test2 n))
    (time (test1 n))))
