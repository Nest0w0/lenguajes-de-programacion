{-

Realizado por: Nestor Aguilar (C.I : 28.316.308)

El programa puede compilarse y ejecutarse mediante su .exe o ejecutarse mediante ghci y llamándo a la función main.
La entrada y la salida del programa, así como sus formatos, están específicados en la documentación de la función main.

-}

-- PRIM --
prim :: [[(Char,Char,Int)]] -> Int -> [(Char,Char,Int)]
prim xss n = (recursion xss visitados aristas tam n)
        where tam = (length xss)
              aristas = []
              visitados = [(getNombre n, getNombre n,0)]

--Esta función realiza el proceso recursivo del algoritmo de Prim
recursion :: [[(Char,Char,Int)]] -> [(Char,Char,Int)] -> [(Char,Char,Int)] -> Int -> Int -> [(Char,Char,Int)]
recursion xss vis aris t n = if (length vis == t) then
                                vis
                            else
                                recursion xss visitados aristas t sig

                        where   aristas = aris ++ (getAristasNoNulas xss n)
                                menor = getAristaMenor aristas vis
                                visitados = vis ++ [menor]
                                sig = getNodo (segundo menor)


--Función que retorna todas las aristas no nulas de un nodo dado
getAristasNoNulas :: [[(Char,Char,Int)]] -> Int -> [(Char,Char,Int)]
getAristasNoNulas xs pos = [x | x <- (xs !! pos), (tercero x) /= 0]

                       
--Función que retorna la arista con el menor peso de una lista
--de aristas dadas
getAristaMenor :: [(Char,Char,Int)] -> [(Char,Char,Int)] -> (Char,Char,Int)
getAristaMenor xs vs = getAristaMenor' xs vs minimo
                where minimo = quicksort [tercero y | y <- xs]
     
getAristaMenor' :: [(Char,Char,Int)] -> [(Char,Char,Int)] -> [Int] -> (Char,Char,Int)
getAristaMenor' xs vs (m:ms) =  if (null res) then
                                        (getAristaMenor' xs vs ms)
                                else
                                        (head res)
                where visitados = [segundo v | v <- vs]
                      res = [x | x <- xs, tercero x == m, notElem (segundo x) visitados]


--Función para ordenar una lista
quicksort :: (Ord a) => [a] -> [a]
quicksort [] = []
quicksort (x:xs) =
        let masPequenos = quicksort [a | a <- xs, a <= x]
            masGrandes = quicksort [a | a <- xs, a > x]
        in masPequenos ++ [x] ++masGrandes


--FUNCIONES AUXILIARES PARA TODO EL PROGRAMA

--Función que retorna el índice del nodo dado su caracter
--identificador
getNodo :: Char -> Int
getNodo char
        | char == 'A' = 0
        | char == 'B' = 1
        | char == 'C' = 2
        | char == 'D' = 3
        | char == 'E' = 4
        | char == 'F' = 5
        | char == 'G' = 6
        | char == 'H' = 7
        | char == 'I' = 8
        | char == 'J' = 9
        | char == 'K' = 10
        | char == 'L' = 11
        | char == 'M' = 12
        | char == 'N' = 13
        | char == 'O' = 14
        | char == 'P' = 15
        | char == 'Q' = 16
        | char == 'R' = 17
        | char == 'S' = 18
        | char == 'T' = 19
        | char == 'U' = 20
        | char == 'V' = 21
        | char == 'W' = 22
        | char == 'X' = 23
        | char == 'Y' = 24
        | char == 'Z' = 25

--Función que retorna el nombre o caracter
--identificador de un nodo dado su índice
getNombre :: Int -> Char
getNombre n
        | n == 0 = 'A'
        | n == 1 = 'B'
        | n == 2 = 'C'
        | n == 3 = 'D'
        | n == 4 = 'E'
        | n == 5 = 'F'
        | n == 6 = 'G'
        | n == 7 = 'H'
        | n == 8 = 'I'
        | n == 9 = 'J'
        | n == 10 = 'K'
        | n == 11 = 'L'
        | n == 12 = 'M'
        | n == 13 = 'N'
        | n == 14 = 'O'
        | n == 15 = 'P'
        | n == 16 = 'Q'
        | n == 17 = 'R'
        | n == 18 = 'S'
        | n == 19 = 'T'
        | n == 20 = 'U'
        | n == 21 = 'V'
        | n == 22 = 'W'
        | n == 23 = 'X'
        | n == 24 = 'Y'
        | n == 25 = 'Z'

--Funciones para las tripletas
primero (x,_,_) = x

segundo (_,y,_) = y

tercero (_,_,z) = z


--FUNCIONES PARA EL PARSEO DE LA ENTRADA--
parsearMatriz :: [[Int]] -> Int -> [[(Char,Char,Int)]]
parsearMatriz [] _ = []
parsearMatriz (x:xs) n = [parsearFila x n 0] ++ parsearMatriz xs (n + 1)

parsearFila :: [Int] -> Int -> Int -> [(Char,Char,Int)]
parsearFila [] _ _ = []
parsearFila (x:xs) n m = [(inicio,fin,x)] ++ parsearFila xs n (m + 1)
                where inicio = getNombre n
                      fin = getNombre m

-- DJIKSTRA --
djikstra :: [[(Char,Char,Int)]] -> Int -> Int -> [(Char,Char,Int)]
djikstra xss n m = djikstra' xss n m visitados distancias
                where   visitados = [(getNombre n, getNombre n, 0)]
                        distancias = generarDistancias (xss !! n) (getNombre n)


djikstra' :: [[(Char,Char,Int)]] -> Int -> Int -> [(Char,Char,Int)] -> [(Char,Char,Int)] -> [(Char,Char,Int)]
djikstra' xss n m vis dis = if (length vis == length xss) then
                                nuevasDistancias
                        else 
                                djikstra' xss n m visitados nuevasDistancias
                                
                        
                        where   menor = getAristaMenor dis vis
                                sig = getAristasNoNulas xss (getNodo (primero menor))
                                nombreVisitados = [primero a | a <- vis]
                                noVisitados = [x | x <- sig, notElem (segundo x) nombreVisitados]
                                aristas = [(primero menor, segundo y,(tercero y + tercero menor)) | y <- noVisitados]
                                nuevasDistancias = actualizarDistancias dis aristas
                                visitados = vis ++ [menor]

--Función que actualiza las distancias actuales dada una lista de aristas
actualizarDistancias :: [(Char,Char,Int)] -> [(Char,Char,Int)] -> [(Char,Char,Int)]
actualizarDistancias xs [y] = actualizadas
                        where actualizadas = actualizarDistancias' (xs) (y)
actualizarDistancias xs ys = actualizarDistancias actualizadas (tail ys) 
                        where actualizadas = actualizarDistancias' (xs) (head ys)

actualizarDistancias' :: [(Char,Char,Int)] -> (Char,Char,Int) -> [(Char,Char,Int)]
actualizarDistancias' [] y = []
actualizarDistancias' (x:xs) y = if (segundo x == segundo y) && (tercero x > tercero y) then
                                        [(primero y, segundo x, tercero y)] ++ actualizarDistancias' xs y
                                else
                                        [x] ++ actualizarDistancias' xs y

--Función que genera las distancias iniciales del método (Del nodo inicial a sí mismo; infinito.
--Mientras que del nodo inicial a los demás; infinito)
generarDistancias :: [(Char,Char,Int)] -> Char -> [(Char,Char,Int)]
generarDistancias xs c = [(c,c,0)] ++ [(c,y,999) | y <- nodos, y /= c]
                where   nodos = [segundo a | a <- xs]


main = do
        print "Introduzca la matriz de adyacencia en forma de lista de listas de enteros: "
        --Ejemplo en este formato para el grafo del documento en el que nos asignó el proyecto: 
        --[[0,4,3,7,0,0,0],[4,0,0,1,0,4,0],[3,0,0,3,5,0,0],[7,1,3,0,2,2,7],[0,0,5,2,0,0,2],[0,4,0,2,0,0,4],[0,0,0,7,2,4,0]]
        entrada <- getLine
        let adyacencia = read entrada :: [[Int]]
        let matriz = parsearMatriz adyacencia 0
        
        
        print "Introduzca el numero del nodo de inicio para el ARM: "
        --Por ejemplo, si quiere empezar el ARM desde el primer nodo, introduzca 1. Si quiere empezar desde el segundo nodo, introduzca 2. Y así sucesivamente
        inicio <- getLine
        let n = read inicio :: Int
        
        
        print "El arbol recorredor minimo es: "
        print (tail (prim matriz (n-1)))
        --La salida será una lista con Tripletas de la forma ('A','B',2) que representan aristas. 
        --Donde 'A' es el nodo de inicio, 'B' es el nodo de llegada y 2 es el peso de la arista



        --Aquí es donde llamaría a Djikstra, pero no me salió.