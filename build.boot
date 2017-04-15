(set-env! :dependencies
  '[[org.clojure/core.match "0.3.0-alpha4"]
    [boot/core "2.0.0-rc8"]
    [org.clojure/clojure "1.6.0"]]
  :source-paths #{"src"})

(task-options!
  pom {:project 'midiboot
       :version "0.1.0"}
  jar {:main 'midiboot.core}
  aot {:all true})

(deftask build "" []
  (comp (pom) (jar) (install)))
