import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Fruta {
    private int x, y;
    private final int tamanho = 60;
    private final int velocidade = 5;
    private BufferedImage imagem;

    public Fruta(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            imagem = ImageIO.read(new File("src/imagens/morango.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void atualizar() {
        y += velocidade;
    }

    public int getY() {
        return y;
    }

    public Rectangle getRetangulo() {
        return new Rectangle(x, y, tamanho, tamanho);
    }

    public void desenhar(Graphics g) {
        if (imagem != null) {
            g.drawImage(imagem, x, y, tamanho, tamanho, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillOval(x, y, tamanho, tamanho);
        }
    }
}