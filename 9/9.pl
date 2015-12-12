place(faerun).
place(tristram).
place(tambi).
place(norrath).
place(snowdin).
place(straylight).
place(alphacentauri).
place(arbre).

dist(faerun, tristram, 65).
dist(faerun, tambi, 129).
dist(faerun, norrath, 144).
dist(faerun, snowdin, 71).
dist(faerun, straylight, 137).
dist(faerun, alphacentauri, 3).
dist(faerun, arbre, 149).
dist(tristram, tambi, 63).
dist(tristram, norrath, 4).
dist(tristram, snowdin, 105).
dist(tristram, straylight, 125).
dist(tristram, alphacentauri, 55).
dist(tristram, arbre, 14).
dist(tambi, norrath, 68).
dist(tambi, snowdin, 52).
dist(tambi, straylight, 65).
dist(tambi, alphacentauri, 22).
dist(tambi, arbre, 143).
dist(norrath, snowdin, 8).
dist(norrath, straylight, 23).
dist(norrath, alphacentauri, 136).
dist(norrath, arbre, 115).
dist(snowdin, straylight, 101).
dist(snowdin, alphacentauri, 84).
dist(snowdin, arbre, 96).
dist(straylight, alphacentauri, 107).
dist(straylight, arbre, 14).
dist(alphacentauri, arbre, 46).

distance(A,B,V) :- dist(A,B,V); dist(B,A,V).

places(Ps) :- setof(P, place(P), Ps).
trip([P|T]) :- places([P|Ps]), permutation(Ps, T).

tripdist(T, D) :- tripdist(T, 0, D).

tripdist([T1,T2|Ts], A, D) :- 
	distance(T1, T2, D1),
  	A1 is A + D1,
	tripdist([T2|Ts], A1, D).

tripdist([_], A, A).
tripdist([], A, A).

plan(T, D) :- trip(T), tripdist(T, D).

solution(X) :- setof(D-T, plan(T,D), P), keysort(P, [X|_]).









