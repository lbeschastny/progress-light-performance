(ns progress-light-performance.core
  (:require [progress-light.core :as progress-light]
            [clj-progress.core :as progress])
  (:gen-class))

(defn throttle
  [func wait]
  (let [prev  (atom 0)
        delta (* wait 1000000)] ; milliseconds
    (fn [& args]
      (let [now (. System (nanoTime))]
        (when (> (- now @prev) delta)
          (reset! prev now)
          (apply func args))))))

(defmacro with-throttle [wait & body]
  `(binding [progress/*progress-handler* (update-in progress/*progress-handler*
                                                    [:tick]
                                                    throttle ~wait)]
    ~@body))

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
  (with-throttle 1000
    (progress/init n)
    (dotimes [i n] (progress/tick))
    (progress/done)))

(defn test4
  "updating once per millisecond"
  [n]
  (with-throttle 1
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
