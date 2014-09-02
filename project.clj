(defproject repl-tasks "1.0.0"
  :description "PROJECTS|TASKS"

  :dependencies [[camel-snake-kebab                "0.1.5"] ; juste pour tester ancient
                 ;[com.cemerick/pomegranate         "0.3.0"]
                 [jonase/eastwood                  "0.1.4"]
                 [lein-ancient                     "0.5.5"]
                 [lein-bikeshed                    "0.1.8-SNAPSHOT"]
                 [lein-deps-tree                   "0.1.2"
                  :exclusions [com.cemerick/pomegranate]]
                 [lein-kibit                       "0.0.8"]
                 [leiningen-core
                  #=(leiningen.core.main/leiningen-version)]
                 [midje                            "1.6.3"]
                 [org.apache.httpcomponents/httpclient "4.3.5"] ; pour overrider celui de `ancient`
                 [org.clojure/clojure              "1.6.0"]
                 ]

  ;:eval-in-leiningen true
  :main leiningen.repl-tasks
  :omit-source true)
