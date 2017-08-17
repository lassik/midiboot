(ns midiboot.core
  (:gen-class)
  (:require [clojure.string :as string]
            [clojure.core.match :refer [match]]
            [net.tcp.server :as tcp]))

(def note-names ["C" "C#" "D" "D#" "E" "F" "F#" "G" "G#" "A" "A#" "B"])

(def midi-middle-c 60)

(defn pitch-class [pitch] (mod (+ midi-middle-c pitch) 12))

(defn note-name [pitch] (get note-names (pitch-class pitch)))

(def pitches-on (atom (sorted-set)))

(defn notes-on []
  (map note-name @pitches-on))

(defn show-notes [notes]
  (println (if (empty? notes) "(none)" (string/join " " notes))))

(defn note-on [pitch]
  (swap! pitches-on conj pitch)
  (show-notes (notes-on)))

(defn note-off [pitch]
  (swap! pitches-on disj pitch)
  (show-notes (notes-on)))

(defn handle-midi-message [message]
  (let [[status a b] message]
    (match [status a b]
      [0x80 pitch velocity] (note-off pitch)
      [0x90 pitch velocity] ((if (= 0 velocity) note-off note-on) pitch)
      :else (println "Got MIDI message" status))))

(defn status-byte? [byte]
  (>= byte 0x80))

(defn skip-bytes-until [predicate stream lead-byte]
  (loop [byte lead-byte]
    (let [byte (or byte (.read stream))]
      (if (or (not byte) (predicate byte))
        byte
        (recur nil)))))

(defn read-bytes-while [predicate stream]
  (loop [bytes []]
    (let [byte (.read stream)]
      (if (not (and byte (predicate byte)))
        [bytes byte]
        (recur (conj bytes byte))))))

(defn midi-messages [stream byte]
  (let [status-byte (skip-bytes-until status-byte? stream byte)]
    (if status-byte
      (let [[data-bytes next-byte]
            (read-bytes-while (complement status-byte?) stream)]
        (lazy-seq (cons (into [] (concat [status-byte] data-bytes))
                    (if next-byte
                      (midi-messages stream next-byte)
                      nil)))))))

(defn handler [reader writer]
  (doall (map handle-midi-message (midi-messages reader nil))))

(def server (atom nil))

(defn serve [port]
  (swap! server
    (fn [server]
      (if server
        (tcp/stop server))
      (let [server (tcp/tcp-server
                     :port    port
                     :handler (tcp/wrap-streams handler))]
        (tcp/start server)
        server))))
