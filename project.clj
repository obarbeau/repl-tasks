(defproject repl-tasks "2.3.0"
  :description (str "PROJECTS|REPL-TASKS"
                    "Call lein tasks (and some lein plugins)"
                    "directly from the REPL")

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

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

; new 140922 v1.1.0 : initial version (almost))
; màj 150114 v2.3.0 : info message for prj `cljsbuild-ui`
