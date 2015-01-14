(defproject repl-tasks "2.2.0"
  :description "PROJECTS|REPL-TASKS"

  :dependencies [[com.cemerick/pomegranate         "0.3.0"]
                 [io.aviso/pretty                  "0.1.8"]
                 [leiningen-core
                  #=(leiningen.core.main/leiningen-version)
                  ; tools.nrepl is excluded 'cos version 0.2.0-beta5 has a bug
                  :exclusions [org.clojure/tools.nrepl]]
                 [org.clojure/clojure              "1.6.0"]]

  :aot [repl-tasks.core]
  ;:eval-in-leiningen true does not work with lein 2.4.3
  :main repl-tasks.core
  :omit-source true
  :url "https://github.com/obarbeau/repl-tasks.git")

; màj 150114 v2.3.0 : ajout message d'info pour utiliser cljsbuild-ui
