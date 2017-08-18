(ns midiboot.core
  (:gen-class)
  (:require [clojure.string :as string]
            [clojure.core.match :refer [match]]
            [net.tcp.server :as tcp]
            [midiboot.midi :as midi]))

(def note-names ["C" "C#" "D" "D#" "E" "F" "F#" "G" "G#" "A" "A#" "B"])

(defn pitch-class [pitch] (mod pitch 12))

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

(defn handler [input output]
  (doall (map handle-midi-message (midi/messages-from-stream input))))

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
