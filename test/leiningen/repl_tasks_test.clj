(ns leiningen.repl-tasks-test
  (:require [clojure.test :refer :all]
            [leiningen.repl-tasks :refer :all]
            [midje.repl :refer [facts]]))
;
; simplement pour que >lein-midje affiche des choses

(deftest a-test
  (testing "Simple test"
    (is (= 1 1))))

(facts "simple midje test"
       (= 1 1) => true)
