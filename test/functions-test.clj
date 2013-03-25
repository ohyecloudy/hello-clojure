; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns hello-clojure.functions-test
  (:use clojure.test))

;-------------------------------------------------------------------------------
; functions

; 함수 호출은 첫 번째 원소가 함수 이름인 리스트를 평가해서 이루어짐
(is (= "hello world" (str "hello" " " "world")))

; 함수가 서술식(predicate)인 경우 ? postfix를 붙이는 게 관례
(is (= true (string? "hello")))
(is (= true (keyword? :hello)))
(is (= true (symbol? 'hello)))

; defn으로 함수 정의
(defn greeting 
  "Returns a greeting of the form 'Hello, username."
  [username]
  (str "Hello, " username))
(is (= "Hello, world" (greeting "world")))

; TODO: expected exception 이런 게 있나?
; (greeting)
; 인자 개수가 맞아야 한다. 아니면 예외를 던진다.

; 다른 인자 개수에 대해 정의할 수 있다.
(defn greeting-2
  "Returns a greeting of the form 'Hello, username.'
  Default username is 'world'."
  ([] (greeting-2 "world"))
  ([username] (str "Hello, " username)))

(is (= "Hello, world" (greeting-2)))
(is (= "Hello, ohyecloudy" (greeting-2 "ohyecloudy")))

; &로 가변인자를 정의할 수 있다
(defn date [person-1 person-2 & chaperones]
  (str person-1 "," person-2 "," (count chaperones)))
(is (= "p1,p2,4" (date "p1" "p2" "p3" "p4" "p5" "p6")))

;-------------------------------------------------------------------------------
; anonymous functions

; TODO: 책에는 re-split을 사용했는데, 그냥 split 사용
(use '[clojure.string :only (split)])
(is 
  (= 
    ["fine" "day"] 
    (filter (fn [W] (> (count W) 2)) (split "A fine day" #"\W+")))
  "fn을 사용해 익명 함수를 정의할 수 있다.")

(is 
  (= 
    ["fine" "day"] 
    (filter #(> (count %) 2) (split "A fine day" #"\W+")))
  "%1, %2, %3... 인자에 간단히 접근. 하나만 있을 경우 %로 표현 가능하다. 
  #은 익명 함수 리더 매크로")

; let - 익명 함수를 lexical scope에 바인딩
(defn indexable-words [text]
  (let [indexable-word? #(> (count %) 2)]
    (filter indexable-word? (split text #"\W+"))))
(is (= ["very" "fine" "day"]
       (indexable-words "A very fine day")))

; 익명 함수를 사용해 동적 함수 생성
(defn make-greeter [greeting-prefix]
  "리턴하는 함수는 greeting-prefix에 대한 클로저(closure)"
  (fn [username] (str greeting-prefix ", " username)))
(def hello-greeting (make-greeter "Hello"))
(def aloha-greeting (make-greeter "Aloha"))
(is (= "Hello, world" (hello-greeting "world")))
(is (= "Aloha, world" (aloha-greeting "world")))
(is (= "Howdy, world" ((make-greeter "Howdy") "world"))
    "리턴한 익명 함수에 심볼을 할당하지 않고도 호출이 가능하다.")

