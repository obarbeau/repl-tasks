(ns repl-tasks.core
  "Toutes les tâches définies ici, si elles sont appelées sans profil particulier,
  n'inclueront pas le profil 'local' et donc les dépendances tools, vinyasa, ..."
  (:require [cemerick.pomegranate :only [add-dependencies]]
            [clojure.java.browse :only [browse-url]]
            [clojure.pprint :only [pprint]]
            [leiningen.core.main :only [leiningen-version]]
            [leiningen.core.project :only [read]]))

(set! *warn-on-reflection* true)

(defmacro with-full-print-length [& body]
  `(let [old-print-length# *print-length*]
     (set! *print-length* nil)
     (~@body)
     (set! *print-length* old-print-length#)))

(defn- merge-profiles
  "Si projet clojurescript, ajoute automatiquement le profile `cljs`
  si projet om, ajoute ce profile"
  [profiles]
  (->> (or profiles [])
       (into (if (some #{'org.clojure/clojurescript}
                       (map first (:dependencies (leiningen.core.project/read))))
               (if (some #{'om/om}
                         (map first (:dependencies (leiningen.core.project/read))))
                 [:default :om] ; om inclus cljs
                 [:default :cljs])
               [:default]))))

(defn- project-with-adequate-profiles
  ([] (project-with-adequate-profiles []))
  ([profiles]
   (->> (merge-profiles profiles)
        (leiningen.core.project/read "project.clj"))))

(defn add-dep
  ([dependency version] (add-dep dependency version []))
  ([dependency version exclusions]
   (let [repos {"clojars" "http://clojars.org/repo"}]
     (cemerick.pomegranate/add-dependencies :coordinates [[dependency version :exclusions exclusions]] :repositories repos))))

(defn add-leiningen []
  (add-dep 'leiningen (leiningen.core.main/leiningen-version) '[[org.clojure/tools.nrepl]]))

;; ------------------------------------------

(defn dependencies []
  (add-dep 'dependencies "1.2.0")
  (load-string "(require '[dependencies.core])
               (clojure.java.browse/browse-url (dependencies.core/gen-graph))"))

(defn lein-checks []
  (add-leiningen)
  (add-dep 'lein-ancient "0.5.5")
  (add-dep 'org.apache.httpcomponents/httpclient "4.3.5")
  (add-dep 'jonase/eastwood "0.1.4")
  (add-dep 'lein-bikeshed "0.1.7")

  (load-string "
               (require '[leiningen.check])
               (require '[leiningen.core.project])
               (require '[leiningen.ancient])
               ; sans le plugin qui wrappe eastwood, on sortirait du repl
               (require 'leiningen.eastwood)
               (require '[bikeshed.core])

               (let [proj (leiningen.core.project/read)]
               (leiningen.check/check proj)
               ;(leiningen.ancient/ancient proj)
               ; pour l'instant pas d'options spécifiques
               (leiningen.eastwood/eastwood proj)
               (bikeshed.core/bikeshed proj {:verbose true}))"))

(defn lein-classpath []
  (let [tmp-file "/tmp/classpath.txt"]
    (spit tmp-file
          (with-out-str
            (println (str "--> Bien mettre à jour la version du jar du projet dans le lanceur shell, "
                          "car ce n'est pas inclus dans ce classpath!\n\n"))
            (->> (leiningen.core.classpath/get-classpath
                  (project-with-adequate-profiles))
                 (map #(clojure.string/replace % #"/home/olivier/\.m2/repository" "\\${M2_REPO}"))
                 (drop-while #(not (.contains ^String % "M2_REPO")))
                 (clojure.string/join ":")
                 (str "VERSION=xxx\n\nCP=\"${PROJECT_JAR}:")
                 print)
            (print "\"")))
    (clojure.java.browse/browse-url tmp-file)))

(declare lein-install)

(defn lein-deploy []
  (lein-install)
  (load-string "(require '[leiningen.deploy]) (leiningen.deploy/deploy (project-with-adequate-profiles) \"releases\")"))

(defn lein-deps
  "c'est space mais en gros, leiningen.deps/deps va pondre dans le repl les recommandations si conflits de version
  et deps-tree va générer l'arbre des dépendances."
  []
  (add-leiningen)
  (add-dep 'lein-deps-tree "0.1.2" ['com.cemerick/pomegranate])
  (load-string "
               (require '[leiningen.deps])
               (require '[leiningen.deps-tree])
               (let [tmp-file \"/tmp/dependencies.txt\"
               project (assoc (project-with-adequate-profiles) :pedantic? :warn)]
               (leiningen.deps/deps project :tree)
               (spit tmp-file
               (-> project
               (#'leiningen.deps-tree/make-dependency-tree)
               (#'leiningen.deps-tree/print-tree 4)
               (with-out-str)))
               (clojure.java.browse/browse-url tmp-file))"))

(declare lein-midje)

(defn lein-install []
  (add-leiningen)
  (lein-midje)
  (load-string "(require '[leiningen.install]) (leiningen.install/install (project-with-adequate-profiles))"))

(defn lein-midje
  "Si midje est présent dans les dependencies du projet, exécute les tests"
  []
  (if (some #{'midje/midje} (map first (:dependencies (project-with-adequate-profiles))))
    (load-string "(require '[midje.repl]) (midje.repl/load-facts)")
    (println "lein-midje: midje n'est pas dans les dependencies de votre projet")))

(defn lein-midje-auto []
  (if (some #{'midje/midje} (map first (:dependencies (project-with-adequate-profiles))))
    (load-string "(require '[midje.repl]) (midje.repl/autotest)")
    (println "midje n'est pas dans les dependencies de votre projet.")))

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

(defn lein-release [level]
  (add-leiningen)
  (println (str "(require '[leiningen.release] '[leiningen.core.main] "
                    "'[leiningen.core.project])\n"
                    "(leiningen.release/release (project-with-adequate-profiles) \"" (name level) "\")")))

(defn lein-run []
  (add-leiningen)
  (load-string "(require '[leiningen.run]) (leiningen.run/run (project-with-adequate-profiles))"))

(defn lein-uberjar []
  (add-leiningen)
  (load-string "(require '[leiningen.uberjar]) (leiningen.uberjar/uberjar (project-with-adequate-profiles))"))

(defn sdoc []
  (add-dep 'clj-ns-browser "1.3.1")
  (load-string "(require '[clj-ns-browser.sdoc]) (clj-ns-browser.sdoc/sdoc)"))