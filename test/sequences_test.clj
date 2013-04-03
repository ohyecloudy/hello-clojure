; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns sequences-test
  (:use clojure.test)
  (:require clojure.string))

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

;-------------------------------------------------------------------------------
; creating a sequence

(is (= '(0 1 2 3 4 5) (range 6)))
(is (= '(5 6 7 8 9) (range 5 10)))
(is (= '(1 3 5 7 9) (range 1 10 2)))

(is (= '(1 1 1 1 1) (repeat 5 1)))
(is (= '("x" "x" "x") (repeat 3 "x")))

(is (= '(1 2 3 4 5 6 7 8 9 10)
       (take 10 (iterate inc 1)))
    "iterate은 무한히 반복. take는 sequence의 처음 n개 리턴")
(is (= '(1 1 1 1 1) (take 5 (repeat 1)))
    "repeat에 인자 하나만 넣으면 무한 개 sequence를 만든다")

(is (= '(0 1 2 0 1 2 0 1 2 0) (take 10 (cycle (range 3)))))

(is (= '(1 "a" 2 "b" 3 "c" 4 "d" 5 "e")
       (interleave (iterate inc 1)
                   ["a" "b" "c" "d" "e"])))

(is (= '("a" "," "b" "," "c")
       (interpose "," ["a" "b" "c"])))
(is (= "a,b,c"
       (apply str (interpose "," ["a" "b" "c"]))))
(is (= "a,b,c"
       (clojure.string/join \, ["a" "b" "c"])))

(is (= #{1 2 3} (set [1 2 3])))
(is (= #{1 2 3} (hash-set 1 2 3)))
(is (= [0 1 2] (vec (range 3))))

;-------------------------------------------------------------------------------
; filtering a sequence

(is (= '(2 4 6 8 10) (take 5 (filter even? (iterate inc 1)))))
(is (= '(1 3 5 7 9) (take 5 (filter odd? (iterate inc 1)))))

(is (= '(\t \h)
       (take-while (complement #{\a\e\i\o\u}) "the-quick-brown-fox"))
    "집합은 그 자체로 함수가 될 수 있다. 
    #{}는 인자가 집합 원소인지 판단하는 함수
    complement는 다른 서술식 값을 반대로 뒤집는다")

(is (= '(\c \d \e \f)
       (drop-while #{\a\b\f} "abcdef")))

(is (= ['(0 1 2 3 4) '(5 6 7 8 9)] 
       (split-at 5 (range 10))))
(is (= ['(0 2 4 6 8 10) '(12 14 16 18)]
       (split-with #(<= % 10) (range 0 20 2))))

;-------------------------------------------------------------------------------
; a sequence predicate

(is (= true (every? odd? [1 3 5])))
(is (= false (every? odd? [1 3 5 6])))

(is (= true (some even? [1 2 3])) 
    "some은 서술식이 아니다. ?로 안 끝나는 이름 주목.
    서술식을 만족하면 true 대신에 서술식이 적용된 결과를 리턴하기 때문")
(is (= nil (some even? [1 3 5])))

(is (= 1 (some identity [nil false 1 nil 2])))

(is (= true (not-every? even? (iterate inc 1))))
(is (= false (not-any? even? (iterate inc 1))))

;-------------------------------------------------------------------------------
; transform a predicate

(is (= '("<p>a</p>" "<p>b</p>" "<p>c</p>")
       (map #(format "<p>%s</p>" %) ["a" "b" "c"])))
(is (= '("<h1>a</h1>" "<h2>b</h2>" "<h3>c</h3>")
       (map #(format "<%s>%s</%s>" %1 %2 %1)
            ["h1" "h2" "h3"] ["a" "b" "c" "d" "e"]))
    "컬렉션 중 하나가 끝에 도달하면 변환을 중지")

(is (= 55 (reduce + (range 1 11))))
(is (= 3628800 (reduce * (range 1 11))))
(is (= 0 (reduce * (range 0 10))))

(is (= '(1 2 3 4 5) (sort [3 2 4 5 1])))
(is (= '(5 4 3 2 1) (sort > [3 2 4 5 1])))
(is (= '(1 11 42 7) (sort-by #(.toString %) [42 1 7 11])))
(is (= '({:grade 90} {:grade 83} {:grade 77})
       (sort-by :grade > [{:grade 77} {:grade 90} {:grade 83}])))

;-------------------------------------------------------------------------------
; list comprehension
; 조건 제시법을 이용해 기존 리스트에서 새로운 리스트를 만들어 내는 방법
; map, filter 함수보다 일반적인 개념을 담고 있기 때문에
; 대부분의 필터링과 변환을 구현할 수 있다.

; (is (= '("<p>a</p>" "<p>b</p>" "<p>c</p>")
;        (map #(format "<p>%s</p>" %) ["a" "b" "c"])))
(is (= '("<p>a</p>" "<p>b</p>" "<p>c</p>")
       (for [word ["a" "b" "c"]]
         (format "<p>%s</p>" word))))

; (is (= '(2 4 6 8 10) (take 5 (filter even? (iterate inc 1)))))
(is (= '(2 4 6 8 10) 
       (take 5 (for [n (iterate inc 1) :when (even? n)] n))))

(is (= '(0)
       (for [n (iterate inc 0) :while (even? n)] n))
    "while은 true일 때까지만 for 절을 평가")
(is (= '("a1" "a2" "b1" "b2" "c1" "c2")
       (for [file "abc" rank (range 1 3)]
         (format "%c%d" file rank)))
    "바인딩 표현은 오른쪽(rank)에 있는 시퀀스 원소부터 차례로 바인딩")

;-------------------------------------------------------------------------------
; lazy sequence

(def x (for [i (range 1 3)] (do (println i) i)))
; println을 통한 부수효과가 있지만 아무것도 출력하지 않는다.
; x가 실제로 원소를 사용하는 부분이 없기 때문에 원소를 만들어내지 않는다.

(def x2 (for [i (range 1 3)] i))
(is (= '(1 2) (doall x2))
    "doall로 지연 시퀀스를 차례로 계산하도록 강제")
(is (= nil (dorun x2))
    "dorun은 차례로 계산하되 이번에 방문한 원소는 메모리에 보존하지 않는다.
    메모리 용량을 초과하는 컬렉션의 원소도 차례대로 방문할 수 있다.")

; TODO: dorun은 시퀀스를 만들어내지 못하는데, 
; 부수효과가 있는 녀석에만 사용하는 걸까?
; doall, dorun은 부수효과를 사용하도록 도와주는 보기 드문 함수

;-------------------------------------------------------------------------------
; clojure make java seq-able

(is (= 104 (first (.getBytes "hello"))))
(is (= '(101 108 108 111) (rest (.getBytes "hello"))))

(is (= \h (first "hello")))
(is (= '(\e \l \l \o) (rest "hello")))
(is (= '(\h \e \l \l \o) (cons \h "ello")))

(is (= '(\o \l \l \e \h) (reverse "hello")))
(is (= "olleh" (apply str (reverse "hello"))))

; 클로저에서 정규식 매치 결과는 특별한 것이 아니다.
; 별도 전용함수가 필요하지 않다. 매치 결과는 그저 시퀀스.
(is (= '("a" "bcd" "efg") (re-seq #"\w+" "a bcd efg")))
(is (= '("efg") (drop 2 (re-seq #"\w+" "a bcd efg"))))

; 파일 시스템도 시퀀스로 다룰 수 있다.
; (import '(java.io File))
; (map #(.getName %) (.listFiles (File. ".")))
; (count (file-seq (File. ".")))

; 스트림을 시퀀스로 다루기
;
; reader를 닫지 않은 나쁜 예
; (take 2 (line-seq (clojure.java.io/reader "build.xml")))
;
; (with-open [rdr (clojure.java.io/reader "build.xml")]
;   (count (line-seq rdr)))

(def 
  xml-text 
  "<compositions>
    <composition composer='J. S. Bach'>
      <name>The Art of the Fugue</name>
    </composition>
    <composition composer='J. S. Bach'>
      <name>Musical Offering</name>
    </composition>
    <composition composer='W. A. Mozart'>
      <name>Requiem</name>
    </composition>
  </compositions>")
; TODO: XML

;-------------------------------------------------------------------------------
; list function

(is (= 1 (peek '(1 2 3))))
(is (= '(2 3) (pop '(1 2 3))))
(is (= true
       (try
         (pop () false)
         (catch Exception _ true)))
    "peek는 first와 같지만 pop은 rest와 다르다. rest와 다르게 비어있으면 예외")

;-------------------------------------------------------------------------------
; vector function

(is (= 3 (peek [1 2 3])))
(is (= [1 2] (pop [1 2 3])))

(is (= :b (get [:a :b :c] 1)))
(is (= nil (get [:a :b :c] 3)))

(is (= :b ([:a :b :c] 1))
    "벡터는 그 자체로 함수이기도 하다. 인덱스를 인자로 받을 수 있다.")
(is (= true
       (try
         ([:a :b :c] 3 false)
         (catch Exception _ true)))
    "get과 다르게 인덱스를 넘기면 예외를 던진다")

(is (= [4 5] (subvec [1 2 3 4 5] 3)))
(is (= [2 3] (subvec [1 2 3 4 5] 1 3)))

;-------------------------------------------------------------------------------
; map function

(is (= '(:key1 :key2) (sort (keys {:key1 "val1" :key2 "val2"}))))
(is (= '("val1" "val2") (sort (vals {:key1 "val1" :key2 "val2"}))))

(is (= "val2" (get {:key1 "val1" :key2 "val2"} :key2)))
(is (= nil (get {:key1 "val1" :key2 "val2"} :key3)))

(is (= "val2" ({:key1 "val1" :key2 "val2"} :key2))
    "맵은 그 자체로 함수. 키를 인자로 받을 수 있다.")
(is (= nil ({:key1 "val1" :key2 "val2"} :key3)))

(is (= "val2" (:key2 {:key1 "val1" :key2 "val2"}))
    "키워드 그 자체로 함수. 맵을 인자로 받을 수 있다.")
(is (= nil (:key3 {:key1 "val1" :key2 "val2"})))

; 값이 nil일 때, 맵에 있는 key인지 검사하는 방법
(is (= true (contains? {:key1 nil} :key1)))
(is (= :not-found (get {:key1 nil} :key2 :not-found)))

(def test-map {:key1 "val1" :key2 "val2"})
(is (= {:key1 "val1" :key2 "val2" :key3 "val3"}
       (assoc test-map :key3 "val3")))
(is (= {:key1 "val1"}
       (dissoc test-map :key2)))
(is (= {:key1 "val1" :key2 "val2"}
       (select-keys test-map [:key1 :key2])))
(is (= {:key1 "val1" :key2 "val2" :key3 "val3" :key4 "val4"}
       (merge test-map {:key3 "val3" :key4 "val4"})))

;-------------------------------------------------------------------------------
; set function

(def languages #{"java" "c" "d" "clojure"})
(def letters #{"a" "b" "c" "d" "e"})
(def beverages #{"java" "chai" "pop"})

(use 'clojure.set)
(is (= #{"java" "c" "d" "clojure" "chai" "pop"}
       (union languages beverages)))
(is (= #{"c" "d" "clojure"}
       (difference languages beverages)))
(is (= #{"java"}
       (intersection languages beverages)))
(is (= #{"c" "d"}
       (select #(= 1 (.length %)) languages)))

(def compositions
  #{{:name "the art of the fugue" :composer "j. s. bach"}
    {:name "musical offering" :composer "j. s. bach"}
    {:name "requiem" :composer "giuseppe verdi"}
    {:name "requiem" :composer "w. a. mozart"}})
(def composers
  #{{:composer "j. s. bach" :country "germany"}
    {:composer "w. a. mozart" :country "austria"}
    {:composer "giuseppe verdi" :country "italy"}})
(def nations
  #{{:nation "germany" :language "german"}
    {:nation "austria" :language "german"}
    {:nation "italy" :language "italian"}})

(is (= 
      #{{:title "the art of the fugue" :composer "j. s. bach"}
        {:title "musical offering" :composer "j. s. bach"}
        {:title "requiem" :composer "giuseppe verdi"}
        {:title "requiem" :composer "w. a. mozart"}}
      (rename compositions {:name :title})))
(is (=
     #{{:name "requiem" :composer "giuseppe verdi"}
       {:name "requiem" :composer "w. a. mozart"}}
     (select #(= (:name %) "requiem") compositions)))
(is (=
     #{{:name "the art of the fugue"}
       {:name "musical offering"} 
       {:name "requiem"}}
     (project compositions [:name])))
(is (=
     #{{:name "the art of the fugue" :composer "j. s. bach" :country "germany"}
       {:name "musical offering" :composer "j. s. bach" :country "germany"}
       {:name "requiem" :composer "giuseppe verdi" :country "italy"}
       {:name "requiem" :composer "w. a. mozart" :country "austria"}}
     (join compositions composers)))
(is (=
     #{{:composer "j. s. bach" :country "germany"
        :nation "germany" :language "german"}
       {:composer "w. a. mozart" :country "austria"
        :nation "austria" :language "german"}
       {:composer "giuseppe verdi" :country "italy"
        :nation "italy" :language "italian"}}
     (join composers nations {:country :nation}))
    "키가 일치하지 않는 경우 두 관계 사이에 대응하는 키를 맵으로 지정 가능")

(is (=
     #{{:country "italy"} {:country "austria"}}
     (project
       (join
         (select #(= (:name %) "requiem") compositions)
         composers)
       [:country])))

