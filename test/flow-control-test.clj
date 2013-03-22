; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns hello-clojure.flow-control-test
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

