(defproject repl-tasks "2.5.0-SNAPSHOT"
  :description (str "PROJECTS|REPL-TASKS"
                    "Call lein tasks (and some lein plugins)"
                    "directly from the REPL")

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[com.cemerick/pomegranate         "0.3.0"]
                 [io.aviso/pretty                  "0.1.14"]
                 [leiningen-core
                  #=(leiningen.core.main/leiningen-version)
                  ; tools.nrepl is excluded 'cos version 0.2.0-beta5 has a bug
                  :exclusions [org.clojure/tools.nrepl]]
                 [org.clojure/clojure              "1.6.0"]]

  :aot [repl-tasks.core]
  ;:eval-in-leiningen true does not work with lein 2.4.3
  :omit-source true
  :url "https://github.com/obarbeau/repl-tasks.git")

; new 140902 v1.0.0 : initial version
; new 140922 v1.1.0
; new 140925 v2.0.0
; new 140926 v2.1.0
; new 141001 v2.2.0
; upd 150114 v2.3.0 : info message for prj `cljsbuild-ui`;
;                     add license and publish on GitHub
; upd 150122 v2.4.0 : use forms instead of strings for load-string
; upd 150130 v2.4.1 : update `io.aviso/pretty`
; upd 150130 v2.5.0 : add Build & Dependency Status
