; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns hello-clojure.sequences-test
  (:use clojure.test))

; 리스트를 시퀀스로 다루기
(is (= 1 (first '(1 2 3))))
(is (= '(2 3) (rest '(1 2 3))))
(is (= '(0 1 2 3) (cons 0 '(1 2 3))))

(is (= java.lang.Long (class (first '(1 2 3)))))
(is (= clojure.lang.PersistentList (class (rest '(1 2 3)))))
(is (= clojure.lang.Cons (class (cons 0 '(1 2 3)))))

; 벡터를 시퀀스로 다루기
(is (= 1 (first [1 2 3])))
(is (= '(2 3) (rest [1 2 3])))
(is (= '(0 1 2 3) (cons 0 [1 2 3])))

(is (= java.lang.Long (class (first [1 2 3]))))
(is (= clojure.lang.PersistentVector$ChunkedSeq (class (rest [1 2 3])))
    "벡터에 rest나 cons를 적용하면 시퀀스를 리턴한다.
    REPL에서 시퀀스는 리스트처럼 표현되기 때문에 헷갈리지 말 것.")
(is (= clojure.lang.Cons (class (cons 0 [1 2 3]))))

; 맵을 시퀀스로 다루기
; {} 리더 매크로를 사용해 map을 생성하면 순서를 보장하지 않는다.
; (is (= [:fname "Jongbin"]
;        (first {:fname "Jongbin" :lname "Oh"})))
; 이게 성공하리란 보장이 없음

(is (= [:fname "Jongbin"]
       (first (sorted-map :fname "Jongbin" :lname "Oh"))))
(is (= '([:lname "Oh"])
       (rest (sorted-map :fname "Jongbin" :lname "Oh"))))
(is (= '([:mname] [:fname "Jongbin"] [:lname "Oh"])
       (cons [:mname] (sorted-map :fname "Jongbin" :lname "Oh"))))

(is (= clojure.lang.PersistentTreeMap$BlackBranchVal
       (class (first (sorted-map :fname "Jongbin" :lname "Oh")))))
(is (= clojure.lang.PersistentTreeMap
       (class (sorted-map :fname "Jongbin" :lname "Oh"))))
(is (= clojure.lang.Cons
       (class (cons [:mname] (sorted-map :fname "Jongbin" :lname "Oh")))))

; 집합을 시퀀스로 다루기
; #{} 리더 매크로를 사용해 set을 생성하면 순서를 보장하지 않는다.
; (is (= '(:c :d) (rest #{:b :c :d})))
; 이게 보장하리란 보장이 없음

(is (= :b (first (sorted-set :b :c :d))))
(is (= '(:c :d) (rest (sorted-set :b :c :d))))
(is (= '(:a :b :c :d) (cons :a (sorted-set :b :c :d))))

(is (= clojure.lang.Keyword
       (class (first (sorted-set :b :c :d)))))
(is (= clojure.lang.APersistentMap$KeySeq
       (class (rest (sorted-set :b :c :d)))))
(is (= clojure.lang.Cons
       (class (cons :a (sorted-set :b :c :d)))))

(is (= '(:a 1 2 3)
       (conj '(1 2 3) :a))
    "conj는 컬렉션에 원소를 추가할 때 사용. 삽입 위치는 자료구조에 맞게 결정
    리스트는 맨 앞에 삽입")
(is (= [1 2 3 :a :b]
       (into [1 2 3] [:a :b]))
    "into는 컬렉션에 컬렉션 원소들을 추가. 삽입 위치는 자료구조에 맞게 결정
    벡터는 맨 뒤에 삽입")

