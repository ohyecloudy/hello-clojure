; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns functional-programming-test
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

; 컬렉션의 헤드를 참조하는 코드 (이렇게 하지 말 것!)
; (def head-fibo
;   (lazy-cat [0M 1M]
;             (map + head-fibo (rest head-fibo))))
; (nth head-fibo 1000000M)
;
; 최상위 var인 head-fibo가 컬렉션의 '헤드를 참조'
; 피보나치 수열을 만드는 데 사용한 앞쪽 원소를 
; 가비지 콜렉터가 수거하는 것이 불가능
;
; java.lang.OutOfMemoryError 발생. 

(defn count-heads-pairs [coll]
  (loop [cnt 0 coll coll]
    (if (empty? coll)
      cnt
      (recur (if (= :h (first coll) (second coll))
               (inc cnt)
               cnt)
             (rest coll)))))
(is (= 2 (count-heads-pairs [:h :h :h :t :h])))
; '연속 두 번 등장한다'는 개념이 분명히 표현X
; - loop/recur 속에 뒤섞여 있기 때문

; 복잡한 버전
(defn by-pairs [coll]
  (let [take-pair (fn [c]
                    (when (next c) (take 2 c)))]
    (lazy-seq
      (when-let [pair (seq (take-pair coll))]
        (cons pair (by-pairs (rest coll)))))))
(is (= nil (next [1])))
(is (= '(2) (next [1 2])))

(is (= '((:h :t) (:t :t) (:t :h) (:h :h) (:h :h))
       (by-pairs [:h :t :t :h :h :h])))

(defn count-heads-pairs-2 [coll]
  (count (filter (fn [pair] (every? #(= :h %) pair))
                 (by-pairs coll))))
(is (= 2 (count-heads-pairs-2 [:h :t :t :h :h :h])))

; 시퀀스 함수 사용
(is (= '((:h :t) (:t :h) (:h :h)) 
       (partition 2 [:h :t :t :h :h :h])))
(is (= '((:h :t) (:t :t) (:t :h) (:h :h) (:h :h))
       (partition 2 1 [:h :t :t :h :h :h])))
(is (= (partition 2 1 [:h :t :t :h :h :h])
       (by-pairs [:h :t :t :h :h :h]))
    "구현한 by-pairs는 (partition 2 1 coll)과 같다")

; TODO: 책과 다름. 1.3 이후에는 defvar 대신에 def를 사용
(def count-if (comp count filter))
(is (= 3 (count-if odd? [1 2 3 4 5])))

; count-if와 partition을 이용해 보편적인 함수를 정의
(defn count-runs
  "Count runs of length n where pred is true in coll."
  [n pred coll]
  (count-if #(every? pred %) (partition n 1 coll)))

(is (= (count-heads-pairs-2 [:h :t :t :h :h :h])
       (count-runs 2 #(= % :h) [:h :t :t :h :h :h])))

(def count-heads-pairs-3 
  "partial은 함수 f와 함수 인자 목록 중 일부를 받아
  f를 부분적으로만 적용.
  나머지 인자들은 partial이 만든 함수에 넘긴다"
  (partial count-runs 2 #(= % :h)))
(is (= (count-heads-pairs-2 [:h :t :t :h :h :h])
       (count-heads-pairs-3 [:h :t :t :h :h :h])))

; (partial count-runs 2 #(= % :h)) 은 
; (fn [coll] (count-runs 2 #(= % :h) coll))
; 코드의 좀 더 효과적인 표현

;-------------------------------------------------------------------------------
; recursion

(declare my-odd? my-even?)

(defn my-odd? [n]
  (if (= n 0) 
    false
    (my-even? (dec n))))

(defn my-even? [n]
  (if (= n 0)
    true
    (my-odd? (dec n))))

(is (= '(true false true false)
       (map my-even? (range 4))))
(is (= '(false true false true)
       (map my-odd? (range 4))))

; StackOverflowError
; (my-even? 1000000M)

