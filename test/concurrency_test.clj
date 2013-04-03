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

(defn add-message-commute 
  "병행적인 특성이 강하 alter.
  트랜잭션 순서가 교환 가능(commutative).
  STM은 트랜잭션 수행 순서를 마음대로 재배열한다."
  [msg]
  (dosync (commute messages conj msg)))

(def validate-message-list
  (partial every? #(and (:sender %) (:text %))))
(def messages 
  "validator keyword로 유효성 확인 함수를 정의할 수 있다."
  (ref () :validator validate-message-list))

; IllegalStateException. 
; (add-message "not a valid message")

(add-message (struct message "user 3" "legit message"))

;-------------------------------------------------------------------------------
; Use Atoms for Uncoordinated, Synchronous Updates

(def current-track (atom "song1"))
(is (= "song1" @current-track))
(is (= "song2"
       (reset! current-track "song2"))
    "트랜잭션 내에서 변경하는 게 아니기 때문에 dosync가 필요 없다. reset! 사용")

(def current-track (atom {:title "song1" :composer "composer1"}))
(is (= {:title "song2" :composer "composer2"}
       (reset! current-track {:title "song2" :composer "composer2"}))
    "current-track, current-composer를 atom을 사용해서 동시에 변경은 안된다.
    ref를 사용해야 한다. 
    아니면 atom을 사용하되 모델링 방식을 맵으로 바꾸면 된다.")
(is (= {:title "song2" :composer "composer2_2"}
       (swap! current-track assoc :composer "composer2_2"))
    "swap!에 함수(assoc)를 넘겨 composer만 변경할 수 있다.")

;-------------------------------------------------------------------------------
; Use Agents for Asynchronous Updates

(def counter (agent 0))
(is (not (= 1 
            (send counter inc)))
    "1을 바로 리턴하지 않고 에이전트 자체가 리턴")
; await, await-for 함수를 사용해 에이전트에 대한 작업이 완료될 때까지 블럭
(is (= nil (agent-errors counter)))

(def counter (agent 0 :validator number?))
; 숫자가 아닌 값을 넘겼지만 바로 에러가 나지 않는다.
(send counter (fn [_] "boo"))

(is (= 0 @counter))
(is (not (= nil (agent-errors counter)))
    "에러가 발생했다.")

; 트랜잭션에 에이전트를 포함하면 부수효과를 일으킬 수 있다.
; 트랜잭션이 성공한 경우에만 딱 한 번 에이전트로 보내지게 된다.
; 사용 예) 트랜잭션이 성공했을 때 파일에 로그를 남기기

; (def backup-agent (agent "output/messages-backup.clj"))
; (defn add-message-with-backup [msg]
;   (dosync
;     (let [snapshot (commute messages conj msg)]
;       (send-off backup-agent (fn [filename]
;                                (spit filename snapshot)
;                                filename))
;       snapshot)))
; 
; (add-message-with-backup (struct message "user1" "message backup"))

;-------------------------------------------------------------------------------
; Managing Per-Thread State with Vars

; def, defn을 호출하는 건 사실상 '동적 var'라는 특수 변수를 생성하는 것
; def에 초기 값을 넘긴 것은 사실상 그 var에 대한 '루트 바인딩'을 만든 것

; 10이라는 값을 가지는 루트 바인딩 foo 생성
(def foo 10)
(is (= 10 foo))

; 바인딩 foo는 모든 스레드에 의해 공유
(.start (Thread. (fn [] (is (= 10 foo)))))

(def ^:dynamic foo 10)
(binding [foo 42] foo)

; binding으로 스레드에 한정된 바인딩을 만들었기 때문에 
; 다른 스레드에서는 10을 리턴한다
(.start (Thread. (fn [] (is (= 10 foo)))))

; let은 let 구문 바깥에 있는 어떤 코드에도 영향을 주지 않는다.
; 반면 binding은 그 효과가 함수 호출을 따라 어디까지라도 내려간다.
(defn print-foo[] (println foo))
(let [foo "let foo"] (print-foo))
; 10 리턴
(binding [foo "bound foo"] (print-foo))
; bound foo 리턴

; set!으로 스레드 내 동적 바인딩을 설정할 수 있다.
; 자바 API 콜백 이벤트 핸들러를 구현하기 위해 보통 사용
; 동적 바인딩을 사용해 변수처럼 변경 가능한 레퍼런스를 사용
; 자바와 상호작용이 필요할 때만 이런 스타일을 어쩔 수 없이 사용
;
; 동적 바인딩을 가지고 사용되는 var를 '특수 변수'라고 부르고
; Lisp에서는 이름 앞뒤에 *를 붙이는 게 관례.
; *in*, *out*, ...
;
; (startElement
;   [uri local-name q-name ^Attributes atts]
;   (set! *stack* (conj *stack* *current*))
;   (set! *current* e)
;   (set! *state* :element)
;   nil)

