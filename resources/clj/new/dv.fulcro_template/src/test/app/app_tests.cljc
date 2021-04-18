(ns {{namespace}}.app-tests
  (:require
    [clojure.test :refer [deftest is testing]]))

; Tests for both client and server
(deftest sample-test
  (testing "addition computes addition correctly"
    (is (= (+ 1 5 3) 9) "with positive integers")
    (is (= (+ -1 -3 -5) -9) "with negative integers")
    (is (= (+ +5 -3) 2) "with a mix of signed integers")))
