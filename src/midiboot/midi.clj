(ns midiboot.midi
  (:require [clojure.string :as string]
            [clojure.core.match :refer [match]]
            [net.tcp.server :as tcp]))

(defn status-byte? [byte]
  (>= byte 0x80))

(defn- skip-bytes-until [predicate stream lead-byte]
  (loop [byte lead-byte]
    (let [byte (or byte (.read stream))]
      (if (or (not byte) (predicate byte))
        byte
        (recur nil)))))

(defn- read-bytes-while [predicate stream]
  (loop [bytes []]
    (let [byte (.read stream)]
      (if (not (and byte (predicate byte)))
        [bytes byte]
        (recur (conj bytes byte))))))

(defn- messages-from-stream* [stream byte]
  (let [status-byte (skip-bytes-until status-byte? stream byte)]
    (if status-byte
      (let [[data-bytes next-byte]
            (read-bytes-while (complement status-byte?) stream)]
        (lazy-seq (cons (into [] (concat [status-byte] data-bytes))
                    (if next-byte
                      (messages-from-stream* stream next-byte)
                      nil)))))))

(defn messages-from-stream [stream]
  (messages-from-stream* stream nil))
