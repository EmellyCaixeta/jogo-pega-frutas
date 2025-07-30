// Importações de bibliotecas necessárias para gráficos, som, imagens, eventos, etc.
import javax.imageio.ImageIO; 
import javax.sound.sampled.*; 
import javax.swing.*; // Componentes gráficos (JPanel, Timer, etc.)
import java.awt.*; // Classes gráficas (Graphics, Color, Font, etc.)
import java.awt.event.MouseAdapter; 
import java.awt.event.MouseEvent; 
import java.awt.image.BufferedImage; 
import java.io.File; // Para manipular arquivos
import java.io.IOException; // Exceções de entrada/saída
import java.util.ArrayList; // Lista dinâmica de frutas e bombas
import java.util.Iterator; // Para percorrer listas com segurança
import java.util.List; // Interface de listas
import java.util.concurrent.Semaphore; // Controle de acesso simultâneo (thread-safe)


//painel gráfico e também uma thread.
public class Jogo extends JPanel implements Runnable {

    // Declaração dos atributos (variáveis do jogo)
    private Cesto cesto; // Objeto que representa o cesto controlado pelo jogador
    private List<Fruta> frutas; // Lista de frutas que caem na tela
    private List<Bomba> bombas; // Lista de bombas que caem na tela
    private int pontuacao = 0; 
    private int tempoRestante = 60; 
    private boolean fimDeJogo = false; // Flag para saber se o jogo terminou
    private final Semaphore mutex = new Semaphore(1); // Controle de acesso para evitar conflito entre threads
    private Thread threadPrincipal; 
    private BufferedImage imagemFim; // Imagem do botão de sair
    private Rectangle areaImagemFim; // Área clicável do botão de sair
    private final int tamanhoBotaoFim = 50; // Tamanho do botão de sair

    
    // Construtor da classe Jogo
    public Jogo() {
        setPreferredSize(new Dimension(600, 800)); // Define o tamanho do painel
        setFocusable(true); // Permite que o painel receba foco de teclado
        cesto = new Cesto(250, 700); // Cria o cesto na posição inicial
        frutas = new ArrayList<>(); // Cria a lista de frutas vazia
        bombas = new ArrayList<>(); // Cria a lista de bombas vazia
        addKeyListener(new InterrupcaoTeclado(cesto)); // Adiciona o controle do teclado

        // Temporizador que reduz o tempo a cada 1 segundo
        new Timer(1000, e -> {
            if (!fimDeJogo) {
                tempoRestante--; // Diminui o tempo
                if (tempoRestante <= 0) { // Se o tempo acabou
                    fimDeJogo = true;
                    tocarSom("src/sons/tekenologia.wav"); 
                    pararJogo(); // Encerra a thread
                }
                repaint(); // Redesenha a tela
            }
        }).start(); // Inicia o timer
        
        // Tenta carregar a imagem do botão de sair
        try {
            imagemFim = ImageIO.read(new File("src/imagens/sair.png"));
        } catch (IOException e) {
            e.printStackTrace(); // Exibe erro se não encontrar a imagem
        }

        // Detecta clique do mouse para encerrar o jogo
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (fimDeJogo || areaImagemFim != null && areaImagemFim.contains(e.getPoint())) {
                    System.exit(0); // Encerra o jogo
                }
            }
        });
    }

    // Método da interface Runnable: loop principal do jogo
    @Override
    public void run() {
        threadPrincipal = Thread.currentThread(); // Salva a thread atual
        while (!fimDeJogo && !Thread.currentThread().isInterrupted()) {
            try {
                atualizarEstadoJogo(); // Atualiza posição dos objetos
                repaint(); // Redesenha a tela
                Thread.sleep(30); // Aguarda 30 milissegundos
            } catch (InterruptedException e) {
                break; // Encerra o loop se for interrompido
            }
        }
    }

    // Método que interrompe a thread principal do jogo
    private void pararJogo() {
        if (threadPrincipal != null && threadPrincipal.isAlive()) {
            threadPrincipal.interrupt(); // Envia sinal de parada
        }
    }

    // Método para tocar efeitos sonoros
    private void tocarSom(String caminho) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(caminho)); // Lê o som
            Clip clip = AudioSystem.getClip(); // Cria um "tocador"
            clip.open(audioStream); // Abre o som
            clip.start(); // Toca o som
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace(); // Exibe erro se falhar
        }
    }
    
    // Atualiza frutas, bombas, pontuação e detecta colisões
    private void atualizarEstadoJogo() throws InterruptedException {

    	cesto.AtualizarPosicao();
        // Adiciona frutas aleatoriamente com probabilidade de 2%
        if (Math.random() < 0.02) {
            frutas.add(new Fruta((int) (Math.random() * 550), 0));
        }

        // Adiciona bombas aleatoriamente com probabilidade de 1%
        if (Math.random() < 0.01) {
            bombas.add(new Bomba((int) (Math.random() * 550), 0));
        }

        // Atualiza cada fruta na tela
        Iterator<Fruta> iterador = frutas.iterator();
        while (iterador.hasNext()) {
            Fruta fruta = iterador.next();
            fruta.atualizar(); // Move a fruta
            if (fruta.getY() > 900) {
                iterador.remove(); // Remove se saiu da tela
            } else if (fruta.getRetangulo().intersects(cesto.getRetangulo())) {
                iterador.remove(); // Remove se colidiu com o cesto
                pontuacao++; // Soma ponto
                tocarSom("src/sons/gnome.wav"); // Som de pegar fruta
            }
        }

        // Atualiza cada bomba na tela
        Iterator<Bomba> itBomba = bombas.iterator();
        while (itBomba.hasNext()) {
            Bomba bomba = itBomba.next();
            bomba.atualizar(); // Move a bomba
            if (bomba.getY() > 900) {
                itBomba.remove(); // Remove se saiu da tela
            } else if (bomba.getRetangulo().intersects(cesto.getRetangulo())) {
                fimDeJogo = true; // Termina o jogo se acertar bomba
                tocarSom("src/sons/explosao.wav"); // Som de explosão
                pararJogo(); // Para o jogo
                break; // Sai do loop
            }
        }
    }

    // Método que desenha tudo na tela
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Limpa a tela

        // Fundo branco
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Exibe pontuação e tempo
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Pontuação: " + pontuacao, 20, 30);
        g.drawString("Tempo: " + tempoRestante, 450, 30);

        // Desenha o cesto
        cesto.desenhar(g);

        // Desenha cada fruta
        for (Fruta fruta : frutas) {
            fruta.desenhar(g);
        }

        // Desenha cada bomba
        for (Bomba bomba : bombas) {
            bomba.desenhar(g);
        }

        // Desenha botão de sair
        if (imagemFim != null) {
            int x = (getWidth() - tamanhoBotaoFim) / 2;
            int y = 10; // distância do topo

            g.drawImage(imagemFim, x, y, tamanhoBotaoFim, tamanhoBotaoFim, null);
            areaImagemFim = new Rectangle(x, y, tamanhoBotaoFim, tamanhoBotaoFim);
        }

        // Exibe mensagem de fim de jogo
        if (fimDeJogo) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(Color.RED);
            g.drawString("FIM DE JOGO", 180, 400);
        }
    }
}
