(ns midiboot.music)

;; Pitches are MIDI pitches.

(def note-names ["C" "C#" "D" "D#" "E" "F" "F#" "G" "G#" "A" "A#" "B"])

(defn pitch-class [pitch] (mod pitch 12))

(defn note-name [pitch] (get note-names (pitch-class pitch)))
