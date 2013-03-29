; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns flow-control-test
  (:use clojure.test))

(defn is-small1? [number]
  (if (< number 100) "yes"))
(is (= "yes" (is-small1? 50)))
(is (= nil (is-small1? 500)) "else 부분이 없다면 nil")

(defn is-small2? [number]
  (if (< number 100) "yes" "no"))
(is (= "no" (is-small2? 500)))

(def foo 10)
(defn is-small3? [number]
  (if (< number 100)
    "yes"
    (do (def foo number)
        "no")))
(is (= "no" (is-small3? 1000))
    "do문에서는 마지막 구문 평가값을 리턴")
(is (= 1000 foo)
    "do문에서 foo 심볼에 100을 할당했다. 부수효과(side effect)")

(is (=
     [5 4 3 2 1]
     (loop [result [] x 5]
       (if (zero? x)
         result
         (recur (conj result x) (dec x)))))
    "첫번째 루프에서 result에 빈 벡터[], x에는 5가 바인딩
    0이 될 때까지 result에 x를 더하고 x를 감소시킨다")

(defn countdown [result x]
  (if (zero? x)
    result
    (recur (conj result x) (dec x))))
(is (=
     [4 3 2 1]
     (countdown [4] 3))
    "인자를 넘겨주면 loop없이 recur만 사용해서 똑같이 구현할 수 있다")

(is (=
     [5 4 3 2 1]
     (into [] (take 5 (iterate dec 5))))
    "클로저 시퀀스 라이브러리를 사용해 반복작업 대부분을 더 간단히 처리할 수 있다")
(is (=
     [5 4 3 2 1]
     (into [] (drop-last (reverse (range 6))))))
(is (=
     [5 4 3 2 1]
     (vec (reverse (rest (range 6))))))

;-------------------------------------------------------------------------------
; for loop
; clojure에는 for loop가 없다. 
; for loop 처럼 동작하려면 다르게 생각하고 구현해야 한다.
; indexOfAny() 자바버전과 똑같이 동작하는 함수를 clojure로 만든다.

(defn indexed 
  "map은 로 f를 c1, c2 첫번째 아이템에 적용한다.
  vector는 e1 e2로 vector를 만든다"
  [coll] (map vector (iterate inc 0) coll))
(is (=
     [[0 \a] [1 \b] [2 \c] [3 \d] [4 \e]]
     (indexed "abcde")))

(defn index-filter 
  "for는 루프가 아니라 리스트 해석(list comprehension)을 위해 사용
  (indexed coll)리턴 값 중에서 (pred elt)가 참일 때만 각각 idx와 elt에 바인딩"
  [pred coll]
  (when pred
    (for [[idx elt] (indexed coll) :when (pred elt)] idx)))
(is (=
     [0 1 4 5 6]
     (index-filter #{\a \b} "abcdbbb"))
    "집합 그 자체가 원소의 포함 여부를 테스트할 수 있는 함수.
    그래서 #{\\a \\b} 집합을 predicate로 사용할 수 있다.")
(is (=
     []
     (index-filter #{\a \b} "xyz")))

(defn index-of-any [pred coll]
  (first (index-filter pred coll)))
(is (=
     0
     (index-of-any #{\a \b} "abcdbbb")))

