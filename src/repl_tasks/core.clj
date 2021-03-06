(ns repl-tasks.core
  "Toutes les tâches définies ici, si elles sont appelées sans profil particulier,
  n'inclueront pas le profil 'local' et donc les dépendances tools, vinyasa, ..."
  (:require
    ;[cemerick.pomegranate :only [add-dependencies]]
    [clojure.java.browse :only [browse-url]]
    [clojure.pprint :only [pprint]]
    [clojure.tools.namespace.find]
    [io.aviso.ansi :as ansi]
    ;[leiningen.core.classpath :as lcc :refer [checkout-deps-paths resolve-dependencies]]
    [leiningen.core.main :only [leiningen-version]]
    [leiningen.core.project :only [read]]))

(set! *warn-on-reflection* true)

(defmacro with-full-print-length [& body]
  `(let [old-print-length# *print-length*]
     (set! *print-length* nil)
     (~@body)
     (set! *print-length* old-print-length#)))

(defn- cljs-project? []
  (some #{'org.clojure/clojurescript}
        (map first (:dependencies (leiningen.core.project/read)))))

(defn- merge-profiles
  "Si projet clojurescript, ajoute automatiquement le profile `cljs`
  si projet om, ajoute ce profile"
  [profiles]
  (->> (or profiles [])
       (into (if (cljs-project?)
               (if (some #{'om/om}
                         (map first (:dependencies (leiningen.core.project/read))))
                 ; om inclus cljs
                 [:leiningen/default :om]
                 [:leiningen/default :cljs])
               [:leiningen/default]))))

(defn project-with-adequate-profiles
  ([] (project-with-adequate-profiles []))
  ([profiles]
   (->> (merge-profiles profiles)
        (leiningen.core.project/read "project.clj"))))

(defn add-dep
  ([dependency version] (add-dep dependency version []))
  ([dependency version exclusions]
   (println "should add " [dependency version exclusions])
    #_(let [repos {"clojars" "http://clojars.org/repo"}]
        (cemerick.pomegranate/add-dependencies
          :coordinates
          [[dependency version :exclusions exclusions]] :repositories repos))))

(defn add-leiningen []
  (add-dep 'leiningen (leiningen.core.main/leiningen-version)
           '[[org.clojure/tools.nrepl]]))

;; ------------------------------------------

(defn dependencies []
  (add-dep 'dependencies "1.2.0")
  (eval '(do
           (require '[dependencies.core])
           (clojure.java.browse/browse-url (dependencies.core/gen-graph)))))

#_(defn lein-checks []
    (add-leiningen)
    ; (add-dep 'lein-ancient "0.5.5") faire ça en dehors du REPL,
    ; surtout s'il faut màj les deps de project.clj
    (add-dep 'org.apache.httpcomponents/httpclient "4.3.5")
    (add-dep 'jonase/eastwood "0.2.1")
    (add-dep 'lein-bikeshed "0.2.0")

    (eval '(do
             (require '[leiningen.check])
             (require '[leiningen.core.project])
             ; (require '[leiningen.ancient])
             ; sans le plugin qui wrappe eastwood, on sortirait du repl
             (require 'leiningen.eastwood)
             (require '[bikeshed.core])

             (let [proj (repl-tasks.core/project-with-adequate-profiles)]
               (leiningen.check/check proj)
               ;(leiningen.ancient/ancient proj)
               (bikeshed.core/bikeshed proj {:verbose true})
               ; pour l'instant pas d'options spécifiques
               (leiningen.eastwood/eastwood proj)))))

(defn lein-classpath []
  (let [tmp-file "/tmp/classpath.txt"]
    (spit tmp-file
          (with-out-str
            (println (str "--> Bien mettre à jour la version du jar du projet dans le lanceur shell, "
                          "car ce n'est pas inclus dans ce classpath!\n\n"))
            (->> (let [pmap (project-with-adequate-profiles)]
                   (leiningen.core.classpath/get-classpath
                     ; filter project's dependencies before getting classpath.
                     (update pmap
                             :dependencies
                             (fn [deps mdeps]
                               (filter (fn [dep]
                                         (as-> dep $$
                                               (drop-while #(not= :scope %) $$)
                                               (second $$)
                                               (or $$ "")
                                               (name $$)
                                               (not= "test" $$))) deps))
                             (:managed-dependencies pmap))))
                 (map #(clojure.string/replace % #"/home/olivier/\.m2/repository" "\\${M2_REPO}"))
                 (drop-while #(not (.contains ^String % "M2_REPO")))
                 (clojure.string/join ":")
                 (str "VERSION=xxx\n\nCP=\"${PROJECT_JAR}:")
                 print)
            (print "\"")))
    (clojure.java.browse/browse-url tmp-file)))

(declare lein-install)

(defn lein-deploy []
  (println (str ansi/yellow-font "lein-deploy: si erreur, peut etre redeploy de même version alors que non autorisé ou alors -SNAPSHOT sur repo en release only" ansi/reset-font))
  (lein-install)
  (eval '(do
           (require '[leiningen.deploy])
           (leiningen.deploy/deploy (repl-tasks.core/project-with-adequate-profiles) "releases"))))

(defn lein-deps
  "c'est space mais en gros, leiningen.deps/deps va pondre dans le repl les recommandations si conflits de version
  et deps-tree va générer l'arbre des dépendances."
  []
  (add-leiningen)
  (add-dep 'lein-deps-tree "0.1.2" ['com.cemerick/pomegranate])
  (eval '(do
           (require '[leiningen.deps])
           (require '[leiningen.deps-tree])
           (let [tmp-file "/tmp/dependencies.txt"
                 project (assoc (repl-tasks.core/project-with-adequate-profiles) :pedantic? :warn)]
             (leiningen.deps/deps project :tree)
             (spit tmp-file
                   (-> project
                       (#'leiningen.deps-tree/make-dependency-tree)
                       (#'leiningen.deps-tree/print-tree 4)
                       (with-out-str)))
             (clojure.java.browse/browse-url tmp-file)))))

(declare lein-midje)

(defn lein-install []
  (add-leiningen)
  (lein-midje)
  (eval '(do
           (require '[leiningen.install])
           (leiningen.install/install (repl-tasks.core/project-with-adequate-profiles)))))

(defn lein-midje
  "Si midje est présent dans les dependencies du projet, exécute les tests"
  []
  (if (some #{'midje/midje} (map first (:dependencies (project-with-adequate-profiles))))
    (eval '(do
             (require '[midje.repl])
             (midje.repl/load-facts)))
    (println (str ansi/yellow-font
                  "lein-midje: midje midje is not in the project's dependencies."
                  ansi/reset-font))))

(defn lein-midje-auto []
  (if (some #{'midje/midje} (map first (:dependencies (project-with-adequate-profiles))))
    (eval '(do
             (require '[midje.repl])
             (midje.repl/autotest)))
    (println (str ansi/yellow-font
                  "lein-midje-auto: midje is not in the project's dependencies."
                  ansi/reset-font))))

(defn lein-pprint
  "project map."
  ([] (lein-pprint []))
  ([profiles]
   (let [tmp-file "/tmp/pprint.txt"]
     (with-full-print-length spit tmp-file
                             (-> (project-with-adequate-profiles profiles)
                                 (clojure.pprint/pprint)
                                 (with-out-str)))
     (clojure.java.browse/browse-url tmp-file))))

(defn lein-release
  ":major, :minor, :patch, :alpha, :beta, or :rc"
  [level]
  (add-leiningen)
  (println (str "(require '[leiningen.release] '[leiningen.core.main] "
                "'[leiningen.core.project])\n"
                "(leiningen.release/release (repl-tasks.core/project-with-adequate-profiles) \"" (name level) "\")")))

(defn lein-run []
  (add-leiningen)
  (eval '(do
           (require '[leiningen.run])
           (leiningen.run/run (repl-tasks.core/project-with-adequate-profiles)))))

(defn lein-uberjar []
  (add-leiningen)
  (eval '(do
           (require '[leiningen.uberjar])
           (leiningen.uberjar/uberjar (repl-tasks.core/project-with-adequate-profiles)))))

(defn sdoc []
  (add-dep 'clj-ns-browser "1.3.1")
  (eval '(do
           (require '[clj-ns-browser.sdoc])
           (clj-ns-browser.sdoc/sdoc))))

(defn check-kibit []
  (when-not (some #{'jonase/kibit} (map first (:dependencies (project-with-adequate-profiles))))
    (println (str ansi/yellow-font
                  "∙ kibit [jonase/kibit \"0.1.2\"] is not in the project's "
                  "dependencies. ctrl-k will not allow linting."
                  ansi/reset-font))))

(defn check-cuttle []
  (when (cljs-project?)
    (println (str ansi/green-font "∙ cljs project. "
                  "Don't forget to compile with cuttle,"
                  "or lein with-profile +cljs,+local cljsbuild "
                  "auto dev (if clj1.7+)" ansi/reset-font))))

(defn goto
  [namesp]
  "Go to and reload the specified namespace, whose name ends with arg.
  reload also and its dependencies. Look in every source paths.
  FIXME: doesn't work for ClojureScript NS as tools.namespace not ok with that"
  (when-let [n (first
                 (filter
                   #(.endsWith (str %) (str namesp))
                   (->> (repl-tasks.core/project-with-adequate-profiles)
                        :source-paths
                        (map clojure.java.io/file)
                        (map clojure.tools.namespace.find/find-namespaces-in-dir)
                        (apply concat))))]
    (println (str ansi/green-font "reloading " n " and its dependencies." ansi/reset-font))
    (eval (do
            (require :reload-all [(symbol n)])
            (in-ns (symbol n))))))
