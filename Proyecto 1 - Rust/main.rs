//Realizado por: Nestor Aguilar (C.I: 28.316.308)

// GRAMÁTICA EN EBNF
//==S==
//<oración> ::= <sujeto> <predicado>
//
//==N==
//<sujeto> ::= <nombre> | <articulo> <sustantivo>
//<predicado> ::= <verbo> [[<sujeto>] [<adjetivo>] | [<adverbio>] [<preposición> <sujeto> | <sustantivo> | <adjetivo>] ]
//<nombre> ::=  Rosa | María | Carlota | Lucia | Juan | Diego | Luis | Jesús
//<articulo> ::= La | Las | El | Los | Un | Una | Unos | Unas
//<suntantivo> ::= Fruta | Perro | Perra | Gato | Gata | Pelota | Niño | Niña | Árbol | Frutas | Perros | Perras | Gatos | Gatas | Pelotas | Niños | Niñas | Árboles
//<verbo> ::= Juega | Juegan | Come | Comen | Quiere | Quieren | Es | Son | Corre | Corren | Llora | Lloran
//<preposición> ::= A | Con | Como | Por
//<adverbio> ::= Poco | Poca | Mucho | Mucha | Muy
//<adjetivo> ::= Rápido | Rápida | Grande | Grandes | Verde | Verdes | Roja | Rojas | Pequeño | Pequeños

use std::fs::File;
use std::io::Read;
use std::io::Write;


fn main(){
    //Declaración de los arreglos necesarios para la gramática.
    //Declaración del símbolo inicial
    let oracion: Vec<&str> = vec!["sujeto predicado"];

    //Declaración de los precendentes con no terminales como consecuentes
    let sujeto: Vec<&str> = vec!["nombre", "articulo sustantivo"];
    let predicado: Vec<&str> = vec!["verbo", "verbo sujeto", "verbo sujeto adjetivo", "verbo adjetivo", "verbo preposicion sujeto", "verbo adverbio preposicion sujeto", "verbo adverbio sustantivo", "verbo adverbio adjetivo"];

    //Declaración de precedentes con terminales como consecuentes
    let nombre:Vec<&str> = vec!["Rosa", "Maria", "Carlota", "Lucia", "Juan", "Diego", "Luis", "Jesus"];
    let articulo:Vec<&str> = vec!["La", "Las", "El", "Los", "Un", "Una", "Unos", "Unas"];
    let sustantivo:Vec<&str> = vec!["Fruta", "Perro", "Perra", "Gato" , "Gata" , "Pelota" ,  "Niño" , "Niña" , "Árbol" , "Frutas" , "Perros" , "Perras" , "Gatos" , "Gatas" , "Pelotas", "Niños" , "Niñas" , "Árboles"];
    let verbo:Vec<&str> = vec!["Juega" , "Juegan" , "Come" , "Comen" , "Quiere" , "Quieren" , "Es" , "Son" , "Corre" , "Corren" , "Llora" , "Lloran"];
    let preposicion:Vec<&str> = vec!["A", "Con", "Como", "Por"];
    let adverbio:Vec<&str> = vec!["Poco", "Poca", "Mucho", "Mucha", "Muy"];
    let adjetivo:Vec<&str> = vec!["Rapido" , "Rapida" , "Grande" , "Grandes" , "Verde" , "Verdes" , "Roja" , "Rojas" , "Pequeño" , "Pequeños"];
    
    //El léxico es el conjunto de terminales
    let lexico:Vec<Vec<&str>>= vec![nombre.clone(), articulo.clone(), sustantivo.clone(), verbo.clone(), preposicion.clone(), adverbio.clone(), adjetivo.clone()];
    
    //let gramatica: Vec<&Vec<&str>> = vec![&nombre, &articulo, &sustantivo, &verbo, &preposicion, &adverbio, &adjetivo, &sujeto, &predicado, &oracion];
    let gramatica:Vec<Vec<&str>> = vec![nombre.clone(), articulo.clone(), sustantivo.clone(), verbo.clone(), preposicion.clone(), adverbio.clone(), adjetivo.clone(), sujeto.clone(), predicado.clone(), oracion.clone()];


    //Lógica principal
    //Primero se abre el archivo, y se copia su contenido en una cadena
    //Posteriormente, se separa esta cadena por salto de linea, obteniendo 
    //oraciones. Entonces se envía cada oración a la función análisis para
    //obtener una conclusión sobre la misma


    let nom_archivo = String::from("src/oraciones.txt");
    let contenido = leer_archivo(&nom_archivo);

    let oraciones = separar_cadena(&contenido, '\n');
    let rango: usize = oraciones.len().try_into().unwrap();
    let mut resultado:String = String::new();

    for i in 0..rango{
        let j = i + 1;
        if analisis(&gramatica, &lexico, &oraciones[i]){
            resultado.push_str("Oración ");
            resultado.push_str(&j.to_string());
            resultado.push_str(": OK\n");
        }else{
            resultado.push_str("Oración ");
            resultado.push_str(&j.to_string());
            resultado.push_str(": Error de Sintaxis \n");
            
        }
    }
    escribir_archivo("src/analisis.txt",&resultado);
}


//Esta función realiza el análisis general de un parser, primero llama al lexer
//y si la cadena pasa el examen léxico, se pasa el examen sintáctico. Retorna
//verdadero o falso dependiendo de si la cadena cumple con todos los requisitos
fn analisis(gramatica: &Vec<Vec<&str>>, lexico: &Vec<Vec<&str>>,linea: &str) -> bool{
    if lexer(&lexico, &linea){
        let mut oracion: String = String::from(linea);
        oracion.pop();
        oracion.pop();
        for i in 1..20{
            oracion = sintaxer(&gramatica, &oracion);
            if oracion == String::from("oracion"){
                return true
            }
        }
        return false
    }else{
        return false
    }
}

//Esta función abre el archivo (que debe estar en el mismo directorio)
//lo transforma en una cadena y lo devuelve
fn leer_archivo(filename: &String) -> String{
    let mut f = File::open(filename)
    .expect("Hubo un problema cargando el archivo, intente de nuevo");
    let mut cont = String::new();
    
    f.read_to_string(&mut cont);
    cont
}

//Esta función se encarga de escribir toda la salida en el archivo
fn escribir_archivo(filename: &str, linea: &str){
    let mut f = File::create(filename)
    .expect("Hubo un problema con la creación de el archivo, intente de nuevo");
    write!(f,"{}",linea);
}

//Esta función recibe una cadena y un caracter. Usa ese caracter como
//separador de la cadena, y devuelve un vector con los resultados.
fn separar_cadena(cadena: &String, caracter: char) -> Vec<String>{
    let res: Vec<String> = cadena.split(caracter).map(|s| s.to_string()).collect();
    res
}

//Esta función realiza el análisis léxico. Primero verifica que la oración
//termine en punto. Si lo hace, entonces verifica que cada una de sus palabras
//pertenezcan al léxico. Si la oración cumple todos estos requisitos, retorna
//true, en caso contrario retorna falso
fn lexer(lexico: &Vec<Vec<&str>>, linea: &str) -> bool{
    let mut oracion: String = String::from(linea);
    oracion.pop();
    if oracion.pop().unwrap() == '.'{
        let palabras = separar_cadena(&oracion, ' ');
        for i in palabras{
            if pertenece_al_lexico(&lexico, &i) == false{
                return false
            }
        }
        return true
    }else{
        return false
    }
    return true
}

//Esta función verifica si una palabra pertenece al léxico. En caso afirmativo
//retorna true, en caso negativo retorna falso
fn pertenece_al_lexico(lexico: &Vec<Vec<&str>>, palabra: &str) -> bool{
    let mut valida: bool = false;
    for i in lexico{
        for j in i{
            if j.to_lowercase() == palabra.to_lowercase() {
                valida = true;
            }
        }
    }
    valida
}

//Esta función realiza el análisis sintáctico, funciona sustituyendo los
//consecuentes existentes en la cadena por sus precedentes, como si 
//ascendieran por el árbol sintáctico
fn sintaxer(gramatica: &Vec<Vec<&str>>, linea: &str) -> String{
    let mut oracion:String = String::from(linea);
    let palabras = separar_cadena(&oracion, ' ');
    let mut aux: String = String::from("");
    let mut contador: u8 = 0;
    let total_palabras: u8 = palabras.len().try_into().unwrap();
    let mut cant_palabras: u8 = 1;
    while cant_palabras <= total_palabras{
        for i in &palabras{

            if contador == 0{
                aux.push_str(&i);
            }else{
                aux.push_str(" ");
                aux.push_str(&i);
            }
            contador += 1;
    
            if contador % cant_palabras == 0{
                oracion = oracion.replace(&aux, &sustituir(&gramatica, &aux));
                aux = String::from("");
                contador = 0;
            }

        }
        cant_palabras += 1;
    }
    return oracion
}
//O al menos, eso es lo que debería hacer. Según las pruebas que hice
//parece funcionar para oraciones de 3 palabras o menos. Pero no para
//oraciones de 4 palabras o más. No logré determinar el error, mis disculpas

//Esta función recibe un consecuente y retorna por su precedente
fn sustituir(gramatica: &Vec<Vec<&str>>, cadena: &str) -> String{
    let consecuente:String = String::from(cadena);
    for j in gramatica{
        for k in j{
            if consecuente.to_lowercase() == k.to_string().to_lowercase(){
                return get_precedente(&gramatica, &j);
                //let pre = get_precedente(&gramatica, &j);
                //println!("Coincidió {} con {}", i, &pre);
                //oracion = oracion.replace(&consecuente, &pre);
            }
        }
    }
    return consecuente
}

//Esta función devuelve el nombre de los precedentes
fn get_precedente(gramatica: &Vec<Vec<&str>>, vector: &Vec<&str>) -> String{
    if vector == &gramatica[0]{
        return String::from("nombre")
    }else if vector == &gramatica[1]{
        return String::from("articulo")
    }else if vector == &gramatica[2]{
        return String::from("sustantivo")
    }else if vector == &gramatica[3]{
        return String::from("verbo")
    }else if vector == &gramatica[4]{
        return String::from("preposicion")
    }else if vector == &gramatica[5]{
        return String::from("adverbio")
    }else if vector == &gramatica[6]{
        return String::from("adjetivo")
    }else if vector == &gramatica[7]{
        return String::from("sujeto")
    }else if vector == &gramatica[8]{
        return String::from("predicado")
    }else if vector == &gramatica[9]{
        return String::from("oracion")
    }
    return String::new()
}
