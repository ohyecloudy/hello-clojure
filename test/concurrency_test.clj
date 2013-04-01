; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns concurrency-test
  (:use clojure.test))

;-------------------------------------------------------------------------------
; refs and software transactional memory (STM)

(def current-track (ref "song1"))
(is (= "song1" (deref current-track)))
(is (= "song1" @current-track))

(dosync (ref-set current-track "song2"))
(is (= "song2" @current-track))

; 클로저의 소프트웨어 트랜잭션 메모리는 ACI만 지원
; 영구성(durability)은 지원하지 않는다. 예) DB에서 지원

(def current-composer (ref "mozart"))
(dosync (ref-set current-track "song3")
        (ref-set current-composer "composer1"))
(is (= "song3" @current-track))
(is (= "composer1" @current-composer))

(defstruct message :sender :text)
(def messages (ref ()))

(defn naive-add-message 
  "좋지 않은 방식. 값을 읽고 갱신하는 함수가 있는데, 사용 안 함"
  [msg]
  (dosync (ref-set messages (cons msg @messages))))

(defn add-message
  "alter를 사용해서 갱신할 수 있다.
  ref로 가리키는 값을 update-fn 첫 인자로 넘기기 때문에,
  cons가 아닌 conj를 사용."
  [msg]
  (dosync (alter messages conj msg)))

(is (= '({:sender "user 1" :text "hello"})
       (add-message (struct message "user 1" "hello"))))
(is (= '({:sender "user 2" :text "howdy"} {:sender "user 1" :text "hello"})
       (add-message (struct message "user 2" "howdy"))))

