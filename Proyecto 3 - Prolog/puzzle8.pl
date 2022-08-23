%Otra vez matrices en prolog :(

concatenar([],L,L).
concatenar([X|XS],YS,[X|L]) :- concatenar(XS,YS,L).

%Esta función intercambia todas las posiciones adyacentes de una lista
intercambiar([X,Y|T],[Y,X|T]).
intercambiar([H1|T1],T2) :- intercambiar(T1,L) , concatenar([H1],L,T2).

%Esta función verifica si el espacio está en esta lista
tiene_espacio([H]) :- not(number(H)).
tiene_espacio([H|_]) :- not(number(H)).
tiene_espacio([_|T]) :- tiene_espacio(T).

transpuesta([[]|_],[]).
transpuesta([Fila|M],[Columna|R]) :- columna([Fila|M], Resto, Columna) , transpuesta(Resto,R).

sustituir(A,[_,Y,Z],1,[A,Y,Z]).
sustituir(B,[X,_,Z],2,[X,B,Z]).
sustituir(C,[X,Y,_],3,[X,Y,C]).

/*
La idea del algoritmo es la siguiente:
1. Buscar el espacio vacio
2. Intercambiar su posición, como son listas de 3 elementos solo hay dos intercambios posibles
   2.a) Se puede hacer los intercambios por la fila, en dado caso es inmediato
   2.b) Se puede hacer los intercambios por la columna, en dado caso se saca la transpuesta de la matriz y se realiza como si fuera por fila
3. Sustituir la lista intercambiada en la matriz
4. Aumentar en 1 el contador
5. Repetir el proceso, llamando a la regla nuevamente con la matriz cambiada
6. Hasta que la matriz modificada y la final sean iguales.
*/

puzzle8(L,L,0) :- !.
puzzle8([X1,X2,X3],[Y1,Y2,Y3],R) :- tiene_espacio(X1), intercambiar(X1,R1), sustituir(R1,[X1,X2,X3],1,N),puzzle8(N,[Y1,Y2,Y3], S), R is S + 1, ! .
puzzle8([X1,X2,X3],[Y1,Y2,Y3],R) :- tiene_espacio(X2), intercambiar(X2,R2), sustituir(R2,[X1,X2,X3],2,N), puzzle8(N,[Y1,Y2,Y3],S), R is S + 1, ! .
puzzle8([X1,X2,X3],[Y1,Y2,Y3],R) :- tiene_espacio(X3), intercambiar(X3,R3), sustituir(R3,[X1,X2,X3],3,N), puzzle8(N,[Y1,Y2,Y3],S), R is S + 1, ! .
puzzle8([X1,X2,X3],[Y1,Y2,Y3],R) :- transpuesta([X1,X2,X3],[B1,B2,B3]) ,tiene_espacio(B1), intercambiar(B1,R1), sustituir(R1,[B1,B2,B3],1,M), transpuesta(M,N), puzzle8(N,[Y1,Y2,Y3], S), R is S + 1, ! .
puzzle8([X1,X2,X3],[Y1,Y2,Y3],R) :- transpuesta([X1,X2,X3],[B1,B2,B3]) ,tiene_espacio(B2), intercambiar(B2,R2), sustituir(R2,[B1,B2,B3],2,M), transpuesta(M,N), puzzle8(N,[Y1,Y2,Y3], S), R is S + 1, ! .
puzzle8([X1,X2,X3],[Y1,Y2,Y3],R) :- transpuesta([X1,X2,X3],[B1,B2,B3]) ,tiene_espacio(B3), intercambiar(B3,R3), sustituir(R3,[B1,B2,B3],3,M), transpuesta(M,N), puzzle8(N,[Y1,Y2,Y3], S), R is S + 1, ! .

/*Tal como está ahora, resuelve algunos casos concretos con pocos pasos. Resulta que para casos más complejos, que requieren iterar por filas y columnas, no funciona.
Por lo que puedo ver, resulta por el hecho de que Prolog siempre evalúa la primera condición que se cumpla, por lo que se queda repitiendo los casos más sencillos en lugar de buscar otras opciones ¿Como podría decirle a prolog que no tome movimientos que ya tomó en el pasado? No lo sé, pero me estoy quedando sin tiempo*/
