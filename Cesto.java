import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class Cesto {
	public boolean direita, esquerda; // indicam se o jogador apertou a tecla direita ou esquerda
    private int x, y;
    private final int largura = 180, altura = 100;
    private final int velocidade = 15;
    private BufferedImage imagem;
    public Semaphore Mutex;

    public Cesto(int x, int y) {
        this.x = x;
        this.y = y;
        Mutex = new Semaphore(1);
        try {
            imagem = ImageIO.read(new File("src/imagens/cesto.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void AtualizarPosicao() throws InterruptedException
    {
    	//regiao critica
    	Mutex.acquire();
    	
    	if(direita)
    	{
    		x += velocidade;
    		if (x + largura > 600) x = 600 - largura;
    	}
    	else if(esquerda)
    	{
    		x -= velocidade;
    		if (x < 0) x = 0;
    	}
    	
    	Mutex.release(); //liberar a regiao critica
    	
    }


    public Rectangle getRetangulo() {
        // Ajuste fino da área de colisão para alinhar com a “boca” visível do cesto
        int offsetY = 70; // distância do topo do cesto até a área de colisão
        int novaAltura = 25; // altura da área de colisão
        return new Rectangle(x + 20, y + offsetY, largura - 40, novaAltura);
    }

    public void desenhar(Graphics g) {
        if (imagem != null) {
            g.drawImage(imagem, x, y, largura, altura, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, largura, altura);
        }
        
        
        
        
        
        /*
        // Visualização da área de colisão (para testes)
        g.setColor(Color.RED);
        Rectangle r = getRetangulo();
        g.drawRect(r.x, r.y, r.width, r.height);
        */
    }
}
