(defproject repl-tasks "2.1.0"
  :description "PROJECTS|REPL-TASKS"

  :dependencies [[com.cemerick/pomegranate         "0.3.0"]
                 [leiningen-core
                  #=(leiningen.core.main/leiningen-version)
                  :exclusions [org.clojure/tools.nrepl]] ; on exclus tools.nrepl car la version apportée est 0.2.0-beta5 qui bug
                 ;[org.apache.httpcomponents/httpclient "4.3.5"] ; pour overrider celui de `ancient`
                 [org.clojure/clojure              "1.6.0"]
                 ;[org.clojure/tools.reader         "0.8.5"] ; pour overrider celui de ancient
                 ]

  :aot [repl-tasks.core]
  ;:eval-in-leiningen true ne fonctionne pas avec lein 2.4.3
  :main repl-tasks.core
  :omit-source true
  :url "http://none")
