/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto.pkg4.ldp.laberinto;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
/**
 *
 * @author Nestor
 */

/*La clase tiempo, con su  propio hilo, lleva el control del tiempo del juego.
Se decrementa en 1 cada segundo, hasta que sea 0. No conseguí hacer que se escriba
en pantalla*/
class Tiempo implements Runnable{
    int tiempoMax = 90;
    
    @Override
    public void run(){
        while(tiempoMax > 0){
            tiempoMax -= 1;
            try {
                Thread.sleep((long) 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tiempo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void iniciarTemporizador(){
        Thread tiempoHilo = new Thread(this);
        tiempoHilo.start();
    }
    
    public void draw(Graphics2D g2){
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(30f));
        g2.drawString("" + tiempoMax, 0, 0);
    }
}

/*La clase JuegoPanel tendrá el bucle principal del juego y todo lo necesario para
actualizar información, y dibujar en pantalla.*/
class JuegoPanel extends JPanel implements Runnable{
    /*Constantes para el tamaño de la pantalla, una unidad (cuadrilla) tiene un
    tamaño por defecto de 16px. Esto se va a escalar para que se vea bien en 
    pantallas más grandes, por defecto la escala es 2. Pero se puede aumentar
    y todo el juego cambiará de tamaño automáticamente para ajustarse*/
    
    final int tamañoOriginal = 16;
    final int escala = 2;
    final int cuadricula = tamañoOriginal * escala;
    final int filasMax = 12;
    final int colMax = 12;
    final int tamañoHorizontal = colMax * cuadricula;
    final int tamañoVertical = filasMax * cuadricula;
    final int FPS = 3;
    
    ManejadorTeclas teclas = new ManejadorTeclas();
    Thread juegoHilo;
    Jugador jugador = new Jugador(cuadricula, this, teclas);
    Niebla niebla = new Niebla(cuadricula, teclas, this, jugador);
    Tiempo t = new Tiempo();
    
    
    //Creamos un panel de juego
    public JuegoPanel(){
        //Establecemos su dimensión
        this.setPreferredSize(new Dimension(tamañoHorizontal, tamañoVertical));
        //Su color de fondo
        this.setBackground(Color.BLACK);
        //Y aumentamos su rendimiento
        this.setDoubleBuffered(true);
        //Añadimos el manejador de teclas
        this.addKeyListener(teclas);
        //Con este mejor el Panel se concentra en recibir el input de las teclas
        this.setFocusable(true);
    }
    
    public void iniciarJuego(){
        juegoHilo = new Thread(this);
        //Este método llama a run() automáticamente
        juegoHilo.start();
        t.iniciarTemporizador();
    }
    
    /*En esta función estará la lógica principal del juego, que no parará de 
    ejecutarse hasta que se termine*/
    @Override
    public void run(){
        double intervalo = 1000000000/FPS; //En nanosegundos
        double siguienteDibujo = System.nanoTime() + intervalo;
        double tiempoSobrante;
        while (juegoHilo != null){
            /*
            1. Actualizar información
            2. Dibujar con la información actualizada*/
            actualizar();
            //tienes que usar repaint(), por alguna razón
            repaint();
            
            //Cuando este proceso se realiza, se duerme hasta que se cumpla el
            //tiempo necesario para el siguiente frame
            tiempoSobrante = siguienteDibujo - System.nanoTime();
            tiempoSobrante /= 1000000; //se convierte de nanosegundos a milisegundos
            
            if (tiempoSobrante > 0){
               try {
                    Thread.sleep((long) tiempoSobrante);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JuegoPanel.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
            
            siguienteDibujo += intervalo;
        }
    }
    
    //Método que se encarga de actualizar la información
    public void actualizar(){
        jugador.actualizar();
        niebla.actualizar();
    }
    
    //Método que se encarga de dibujar en pantalla
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        jugador.draw(g2);
        niebla.draw(g2);
        t.draw(g2);
        g2.dispose();
    }
}

/*La clase ManejadorTeclas se encarga de recibir el input por teclado*/
class ManejadorTeclas implements KeyListener{

    public boolean arriba, abajo, izquierda, derecha;
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int codigo = e.getKeyCode();
        switch(codigo){
            case KeyEvent.VK_LEFT:{
                izquierda = true;
                break;
            }
            case KeyEvent.VK_UP:{
                arriba = true;
                break;
            }
            case KeyEvent.VK_RIGHT:{
                derecha = true;
                break;
            }
            case KeyEvent.VK_DOWN:{
                abajo = true;
                break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int codigo = e.getKeyCode();        
        switch(codigo){
            case KeyEvent.VK_LEFT:{
                izquierda = false;
                break;
            }
            case KeyEvent.VK_UP:{
                arriba = false;
                break;
            }
            case KeyEvent.VK_RIGHT:{
                derecha = false;
                break;
            }
            case KeyEvent.VK_DOWN:{
                abajo = false;
                break;
            }
        }
    }
    
}

/*La superclase Entidad se usará para todos aquellos objetos en el juego, tiene
una posición x,y donde se dibujan, y una velocidad a la que moverse*/
class Entidad {
    int x;
    int y;
    int velocidad;
    
    /*Adicionalmente, pueden tener hasta 4 imágenes que los representen, según
    la dirección en la que miren*/
    BufferedImage spriteAbajo, spriteArriba, spriteDerecha, spriteIzquierda;
    String direccion;
}

/*Esta clase es el personaje jugable*/
class Jugador extends Entidad{
    JuegoPanel jp;
    ManejadorTeclas teclas;
    
    public Jugador(int velocidad, JuegoPanel jp, ManejadorTeclas m){
        this.x = 0;
        this.y = 0;
        this.velocidad = velocidad;
        this.jp = jp;
        this.teclas = m;
        this.direccion = "derecha";
        getImagen();
    }
    
    /*Este método carga las imágenes del sprite en la memoria del programa, en
    este proyecto me tomé la libertad de dibujarlo como un Tonberry de la saga
    Final Fantasy. Estas imágenes deben estar en la misma carpeta que este 
    archivo .java*/
    public void getImagen(){
        try{
            this.spriteDerecha = ImageIO.read(getClass().getResourceAsStream("laberinto tonberry derecha.png"));
            this.spriteIzquierda = ImageIO.read(getClass().getResourceAsStream("laberinto tonberry izquierda.png"));
            this.spriteAbajo = ImageIO.read(getClass().getResourceAsStream("laberinto tonberry abajo.png"));
            this.spriteArriba = ImageIO.read(getClass().getResourceAsStream("laberinto tonberry arriba.png"));
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /*Este método actualiza la posición del jugador según la tecla que se mantenga
    presionada, además actualiza la dirección en la que se dirige el jugador*/
    public void actualizar(){
        if(teclas.arriba && y > 0){
            this.y -= velocidad;
            this.direccion = "arriba";
        }else if(teclas.abajo && y < (jp.tamañoVertical - jp.cuadricula)){
            this.y += velocidad;
            this.direccion = "abajo";
        }else if(teclas.izquierda && x > 0){
            this.x -= velocidad;
            this.direccion = "izquierda";
        }else if(teclas.derecha && x < (jp.tamañoHorizontal - jp.cuadricula)){
            this.x += velocidad;
            this.direccion = "derecha";
        }
    }
    
    /*Este método verifica la dirección a la que se dirige el jugador y dibuja
    el sprite correspondiente*/
    public void draw(Graphics2D g2){
        switch(direccion){
            case "arriba":
                g2.drawImage(spriteArriba, x, y, jp.cuadricula, jp.cuadricula, null);
                break;
            case "abajo":
                g2.drawImage(spriteAbajo, x, y, jp.cuadricula, jp.cuadricula, null);
                break;
            case "izquierda":
                g2.drawImage(spriteIzquierda, x, y, jp.cuadricula, jp.cuadricula, null);
                break;
            case "derecha":
                g2.drawImage(spriteDerecha, x, y, jp.cuadricula, jp.cuadricula, null);
                break;
        }
    }
}

/*Esta clase es la niebla que obstruye la visión del Jugador, es una entidad en
si misma, por lo que tiene una posición en pantalla y velocidad propias.*/
class Niebla extends Entidad{
    int tamaño;
    JuegoPanel jp; 
    ManejadorTeclas teclas;
    Jugador jugador;
    
    
    public Niebla(int velocidad, ManejadorTeclas teclas, JuegoPanel jp, Jugador jugador){
        /*Estas posiciones (x,y) están pensadas para que el espacio cuadre con la posición
        inicial del jugador*/
        this.x = -504 * jp.escala;
        this.y = -504 * jp.escala;
        this.velocidad = velocidad;
        this.tamaño = 1024 * jp.escala;
        this.jp = jp;
        this.teclas = teclas;
        this.jugador = jugador;
        getImagen();
    }
    
    /*Este método carga la imagen de la niebla, esencialmente es un cuadrado de
    1024x1024 con un círculo transparente en el medio. Igual que con el jugador,
    debe estar en la misma carpeta que este archivo .java*/
    public void getImagen(){
        try{
            this.spriteDerecha = ImageIO.read(getClass().getResourceAsStream("laberinto fog.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /*Este método actualizar la posición de la niebla, la cual se moverá siempre
    que el jugador también se mueva*/
    public void actualizar(){
        if(teclas.arriba && jugador.y > 0){
            this.y -= velocidad;
        }else if(teclas.abajo && jugador.y < (jp.tamañoVertical - jp.cuadricula)){
            this.y += velocidad;
        }else if(teclas.izquierda && jugador.x > 0){
            this.x -= velocidad;
        }else if(teclas.derecha && jugador.x < (jp.tamañoHorizontal - jp.cuadricula)){
            this.x += velocidad;
        }
    }
    
    /*Esta función dibuja la niebla en pantalla*/
    public void draw(Graphics2D g2){
        g2.drawImage(spriteDerecha, x, y, tamaño, tamaño, null);
    }
}
public class Proyecto4LDPLaberinto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame ventana = new JFrame(); //Nueva ventana tipo JFrame
        //Para cerrar la ventana cuando se pulse la X
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Para que la ventana no se pueda cambiar de tamaño
        ventana.setResizable(false);
        //Su posición es el centro de la pantalla
        ventana.setLocation(0, 0);
        ventana.setTitle("El laberinto del Tonberry");
        
        
        JuegoPanel jp = new JuegoPanel();
        ventana.add(jp);
        ventana.pack();
        jp.iniciarJuego();
        //Visible
        ventana.setVisible(true);
        
        /*Realizado por: Nestor Aguilar (C.I : 28.316.308)
                
        Este proyecto se sintió como construir una casa sabiendo únicamente lo
        que es un bloque.
        
        Lo que pude implementar fue lo siguiente:
            1. Creación y movimiento del jugador simulando cuadriculas de una matriz
            2. Creación y movimiento de la niebla que obstruye la visión del jugador
            3. La carga de imágenes tanto para el jugador como para la niebla
        
        Con el resto de elementos tuve problemes:
        -Implementé el contador del tiempo, en un hilo aparte pero por alguna razón
        que desconozco no se dibuja en pantalla.
        
        -No supe cómo crear el laberinto en la pantalla, intenté hacerlo a partir
        de una matriz pero no parece ser compatible con el Grid de AWT.
        
        -Al no tener laberinto y sus paredes, no pude planear el movimiento de los 
        enemigos, a lo mucho podría haberlos hecho en el vacío, si tuviera más tiempo.
        
        -Al no tener tiempo ni enemigos, no hay condición de victoria ni derrota.
        Lo único que puede hacer en el juego tal como está es moverse en un 
        espacio negro y vacío.
        
        Entre otras cosas, los sprites del enemigos y paredes estaban preparados 
        e igual que el del jugador,son imágenes 16x16 pixeles, que se pueden 
        escalar para verse mejor o peor en pantallas de diferentes tamaños, igual
        que las "casillas" de movimiento. Por defecto la escala está puesta en 2,
        pero si quiere cambiarla, modifique la constante "escala" en la clase 
        JuegoPanel.
        
        Adicionalmente, los FPS están capados a 3, esto para simular mejor el
        movimiento en grid. Si quiere hacerlo más fluido aumente la constante
        "FPS" de la clase JuegoPanel.
        
        Sin más que añadir, gracias por sus clases, conocimientos y consejos.
        */
    }
    
}
