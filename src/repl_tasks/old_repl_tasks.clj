(ns leiningen.old-repl-tasks
  "Toutes les tâches définies ici, si elles sont appelées sans profil particulier,
  n'inclueront pas le profil 'local' et donc les dépendances tools, vinyasa, ..."
  (:require [bikeshed.core]
            [clojure.java.browse]
            [clojure.java.io :as io]
            [clojure.java.browse]
            [clojure.java.shell :as sh]
            [dependencies.core]
            [leiningen.ancient]
            [leiningen.check]
            [leiningen.deps]
            [leiningen.deps-tree]
            [leiningen.deploy]
            [leiningen.eastwood]
            [leiningen.install]
            [leiningen.run]
            [leiningen.uberjar]))

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

;; ------------------------------------------

(defn dependencies []
  (clojure.java.browse/browse-url (dependencies.core/gen-graph)))

(defn lein-midje
  "Si midje est présent dans les dependencies du projet, exécute les tests"
  []
  (if (some #{'midje/midje} (map first (:dependencies (leiningen.core.project/read))))
    (load-string "(require '[midje.repl]) (midje.repl/load-facts)")
    (println "lein-midje: midje n'est pas dans les dependencies de votre projet")))

(defn lein-midje-auto []
  (if (some #{'midje/midje} (map first (:dependencies (leiningen.core.project/read))))
    (load-string "(require '[midje.repl]) (midje.repl/autotest)")
    (println "midje n'est pas dans les dependencies de votre projet.")))

; kibit ne fonctionne pas
(defn lein-checks []
  (let [proj (project-with-adequate-profiles)]
    (leiningen.ancient/ancient proj)
    (leiningen.ancient/ancient proj "profiles")
    (bikeshed.core/bikeshed proj {:verbose true})
    (leiningen.check/check proj)
    (leiningen.eastwood/eastwood proj)))

(defn lein-deps
  "c'est space mais en gros, leiningen.deps/deps va pondre dans le repl les recommandations si conflits de version
  (il ne le fait qu'une seule fois.
  et deps-tree va générer l'arbre des dépendances."
  ([] (lein-deps []))
  ([profiles]
   (let [tmp-file "/tmp/dependencies.txt"
         project (assoc (project-with-adequate-profiles profiles) :pedantic? :warn)]
     (leiningen.deps/deps project :tree)
     (spit tmp-file
           (-> project
               (#'leiningen.deps-tree/make-dependency-tree)
               (#'leiningen.deps-tree/print-tree 4)
               (with-out-str)))
     (clojure.java.browse/browse-url tmp-file))))

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

(defn lein-run []
  (leiningen.run/run (project-with-adequate-profiles)))

(defn lein-install []
  (lein-midje)
  (leiningen.install/install (project-with-adequate-profiles)))

(defn lein-deploy []
  (lein-install)
  (leiningen.deploy/deploy (project-with-adequate-profiles) "releases"))

(defn lein-uberjar []
  (leiningen.uberjar/uberjar (project-with-adequate-profiles)))

;;(sh/sh "ls")
