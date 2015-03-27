(ns progress-light-performance.core
  (:require [progress-light.core :as progress-light]
            [clj-progress.core :as progress])
  (:gen-class))

(progress/set-throttle! 0)

(defn test1
  "no throttling"
  [n]
  (progress/init n)
  (dotimes [i n] (progress/tick))
  (progress/done))

(defn test2
  "async throttling implementation"
  [n]
  (let [p-light (progress-light/progress-light)]
    (progress-light/monitor-progress p-light n)
    (dotimes [i n] (progress-light/tick p-light))
    (progress-light/done p-light)))

(defn test3
  "updating once per seconds"
  [n]
  (progress/with-throttle 1000
    (progress/init n)
    (dotimes [i n] (progress/tick))
    (progress/done)))

(defn test4
  "updating once per 20 milliseconds"
  [n]
  (progress/with-throttle 20
    (progress/init n)
    (dotimes [i n] (progress/tick))
    (progress/done)))

(defmacro run
  [handler]
  `(progress/with-progress
    (println "Running" ~(name handler)
             "(" (-> ~handler var meta :doc) "):")
    (time (~handler 1000000))))

(defn -main
  "Benchmark performance of clj-progress versus progress-light"
  [& args]
  (run test4)
  (run test3)
  (run test2)
  (run test1))
