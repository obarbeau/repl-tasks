(ns leiningen.repl-tasks
  "Toutes les tâches définies ici, si elles sont appelées sans profil particulier,
  n'inclueront pas le profil 'local' et donc les dépendances tools, vinyasa, ..."
  (:require [bikeshed.core]
            [clojure.java.browse]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [leiningen.ancient]
            [leiningen.check]
            [leiningen.deps-tree]
            [leiningen.deploy]
            [leiningen.eastwood]
            [leiningen.install]
            [leiningen.run]
            [leiningen.uberjar]
            ;[midje.repl]
            ))

(set! *warn-on-reflection* true)

(defmacro with-full-print-length [& body]
  `(let [old-print-length# *print-length*]
     (set! *print-length* nil)
     (~@body)
     (set! *print-length* old-print-length#)))

(defn- merge-profiles
  "Si projet clojurescript, ajoute automatiquement le profile `cljs`
  TODO: si projet om, ..."
  [profiles]
  (->> (or profiles [])
       (into (if (some #{'org.clojure/clojurescript}
                       (map first (:dependencies (leiningen.core.project/read))))
               [:default :cljs]
               [:default]))))

(defn- project-with-adequate-profiles
  ([] (project-with-adequate-profiles []))
  ([profiles]
   (->> (merge-profiles profiles)
        (leiningen.core.project/read "project.clj"))))

;; ------------------------------------------

(defn lein-midje []
  #_(midje.repl/load-facts))

(defn lein-checks []
  (let [proj (project-with-adequate-profiles)]
    (leiningen.ancient/ancient proj)
    (leiningen.ancient/ancient proj "profiles")
    (bikeshed.core/bikeshed proj {:verbose true})
    (leiningen.check/check proj)
    (leiningen.eastwood/eastwood proj)
    ; kibit a un peu de mal...
    ))

(defn lein-deps
  ([] (lein-deps []))
  ([profiles]
   (spit "/tmp/dependencies.txt"
         (-> (project-with-adequate-profiles profiles)
             (#'leiningen.deps-tree/make-dependency-tree)
             (#'leiningen.deps-tree/print-tree 4)
             (with-out-str)))
   (clojure.java.browse/browse-url "/tmp/dependencies.txt")))

(defn lein-classpath []
  (spit "/tmp/classpath.txt"
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
  (clojure.java.browse/browse-url "/tmp/classpath.txt"))

(defn lein-pprint
  "project map."
  ([] (lein-pprint []))
  ([profiles]
   (with-full-print-length spit "/tmp/pprint.txt"
     (-> (project-with-adequate-profiles profiles)
         (clojure.pprint/pprint)
         (with-out-str)))
   (clojure.java.browse/browse-url "/tmp/pprint.txt")))

(defn lein-run []
  (leiningen.run/run (project-with-adequate-profiles)))

(defn lein-install []
  (lein-midje)
  (leiningen.install/install (project-with-adequate-profiles)))

(defn lein-deploy []
  (lein-install)
  (leiningen.deploy/deploy (project-with-adequate-profiles) "acs"))

(defn lein-uberjar []
  (leiningen.uberjar/uberjar (project-with-adequate-profiles)))

;;(sh/sh "ls")
