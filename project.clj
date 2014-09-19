(defproject repl-tasks "0.0.9"
  :description "PROJECTS|REPL-TASKS"

  :dependencies [[com.cemerick/pomegranate         "0.3.0"]
                 [dependencies                     "1.2.0"]
                 [jonase/eastwood                  "0.1.4"]
                 [lein-ancient                     "0.5.5"]
                 [lein-bikeshed                    "0.1.7"] ; 0.1.8-SNAPSHOT
                 [lein-deps-tree                   "0.1.2"
                  :exclusions [com.cemerick/pomegranate]]
                 ;[jonase/kibit                     "0.0.8"]
                 [leiningen                                  ; nécessaire pour jar, inclus leiningen.core
                  #=(leiningen.core.main/leiningen-version)
                  :exclusions [org.clojure/tools.nrepl]]     ; on l'exclus car la version apportée est 0.2.0-beta5 qui bug
                 [org.apache.httpcomponents/httpclient "4.3.5"] ; pour overrider celui de `ancient`
                 [org.clojure/clojure              "1.6.0"]
                 [org.clojure/tools.reader         "0.8.5"] ; pour overrider celui de ancient
                 ]

  :aot [leiningen.repl-tasks]
  ;:eval-in-leiningen true ne fonctionne pas avec lein 2.4.3
  :main leiningen.repl-tasks
  :omit-source true
  :url "http://none")
