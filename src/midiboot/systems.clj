(ns midiboot.systems
  (:require [system.core :refer [defsystem]]
            (system.components
              [repl-server :refer [new-repl-server]])
            ;;[environ.core :refer [env]]
            [example.core :refer [system-main]]))

(defsystem dev-system
  [:web (system-main)])

;; (defsystem prod-system
;;   [:web (new-web-server (Integer. (env :http-port)) app)
;;    :repl-server (new-repl-server (Integer. (env :repl-port)))])
