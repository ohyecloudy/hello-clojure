; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns hello-clojure.functional-programming-test
  (:use clojure.test))

(defn stack-consuming-fibo [n]
  (cond
    (= n 0) 0
    (= n 1) 1
    :else (+ (stack-consuming-fibo (- n 1))
             (stack-consuming-fibo (- n 2)))))
(is (= 34 (stack-consuming-fibo 9)))
(is (= true
       (try stack-consuming-fibo 1000000 true
         (catch StackOverflowError _ false))))

(defn tail-fibo [n]
  (letfn [(fib
            [current next n]
            (if (zero? n)
              current
              (fib next (+ current next) (dec n))))]
    (fib (bigint 0) (bigint 1) n)))

(is (= 34 (tail-fibo 9)))

(is (= true
       (try tail-fibo (bigint 1000000) true
         (catch StackOverflowError _ false)))
    "jvm문제. 자동적인 꼬리 재귀 최적화를 수행하지 못한다")

(defn recur-fibo [n]
  "recur를 이용하는 자체 재귀 함수"
  (letfn [(fib
            [current next n]
            (if (zero? n)
              current
              (recur next (+ current next) (dec n))))]
    (fib (bigint 0) (bigint 1) n)))
; 가능! 
; (recur-fibo 1000000)

(defn lazy-seq-fibo
  "lazy-seq를 사용해 재귀적 호출을 지연"
  ([]
   (concat [0 1] (lazy-seq-fibo 0 1)))
  ([a b]
   (let [n (+ a b)]
     (lazy-seq
       (cons n (lazy-seq-fibo b n))))))
(is (= '(0 1 1 2 3 5 8 13 21 34)
       (take 10 (lazy-seq-fibo))))

(take 5 (iterate (fn [[a b]] [b (+ a b)]) [0 1]))

(defn fibo []
  (map first (iterate (fn [[a b]] [b (+ a b)]) [0 1])))

(is (= 34 (nth (fibo) 9))
    "시퀀스 라이브러리를 사용")

