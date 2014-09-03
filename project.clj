(defproject repl-tasks "1.0.0"
  :description "PROJECTS|TASKS"

  :dependencies [;[clj-time                         "0.9.0-beta1"] ; sinon 0.6.0 buggé
                 [com.cemerick/pomegranate         "0.3.0"]
                 [jonase/eastwood                  "0.1.4"]
                 [lein-ancient                     "0.5.5"]
                 [lein-bikeshed                    "0.1.7"] ; 0.1.8-SNAPSHOT
                 [lein-deps-tree                   "0.1.2"
                  :exclusions [com.cemerick/pomegranate]]
                 [lein-kibit                       "0.0.8"]
                 [leiningen                                  ; nécessaire pour jar, inclus leiningen.core
                  #=(leiningen.core.main/leiningen-version)
                  :exclusions [org.clojure/tools.nrepl]]     ; on l'exclus car la version apportée est 0.2.0-beta5 qui bug
                 [midje                            "1.6.3"]
                 [org.apache.httpcomponents/httpclient "4.3.5"] ; pour overrider celui de `ancient`
                 [org.clojure/clojure              "1.6.0"]]

  :aot [leiningen.repl-tasks]
  ;:eval-in-leiningen true ne fonctionne pas avec lein 2.4.3
  :main leiningen.repl-tasks
  :omit-source true)
