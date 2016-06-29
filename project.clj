(defproject repl-tasks "3.3.0-SNAPSHOT"
  :description (str "PROJECTS|REPL-TASKS"
                    "Call lein tasks (and some lein plugins),"
                    "as well as utility functions"
                    "directly from the REPL")

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[com.cemerick/pomegranate         "0.3.1"]
                 [io.aviso/pretty                 "0.1.26"]
                 [leiningen-core
                  #=(leiningen.core.main/leiningen-version)]
                 [org.clojure/clojure              "1.8.0"]
                 [org.clojure/tools.namespace     "0.2.11"]]

  :aot [repl-tasks.core]
  ;:eval-in-leiningen true does not work with lein 2.4.3
  :omit-source true
  :url "https://github.com/obarbeau/repl-tasks.git")
