(ns midiboot.core
  (:gen-class)
  (:require [clojure.string :as string]
            [clojure.core.match :refer [match]]
            [net.tcp.server :as tcp]
            [midiboot.midi :as midi]
            [midiboot.music :as music]))

(def pitches-on (atom (sorted-set)))

(defn notes-on []
  (map music/note-name @pitches-on))

(defn show-notes [notes]
  (println (if (empty? notes) "(none)" (string/join " " notes))))

(defn note-on [pitch]
  (swap! pitches-on conj pitch)
  (show-notes (notes-on)))

(defn note-off [pitch]
  (swap! pitches-on disj pitch)
  (show-notes (notes-on)))

(defn handle-midi-message [message]
  (match message
    [:note-on pitch _] (note-on pitch)
    [:note-off pitch _] (note-off pitch)))

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
