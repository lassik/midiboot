(set-env! :dependencies
  '[[org.clojure/core.match "0.3.0-alpha4"]
    [boot/core "2.0.0-rc8"]
    [org.clojure/clojure "1.6.0"]
    [boot-fmt/boot-fmt "0.1.6"]
    [org.danielsz/system "0.4.0"]]
  :source-paths #{"src"})

(require
  '[boot-fmt.core :refer [fmt]]
  ;;'[midiboot.systems :refer [dev-system]]
  '[system.boot :refer [system run]])

(task-options!
  pom {:project 'midiboot
       :version "0.1.0"}
  jar {:main 'midiboot.core}
  aot {:all true})

;; (deftask dev
;;   "Run a restartable system in the Repl"
;;   []
;;   (comp
;;     ;;(environ :env {:http-port "3000"})
;;     (watch :verbose true)
;;     (system :sys #'dev-system :auto true :files ["core.clj"])
;;     (repl :server true)))

(deftask build "" []
  (comp (pom) (jar) (install)))
