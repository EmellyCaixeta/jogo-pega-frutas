import javax.swing.JFrame; //cria a janela grafica

//na classe principal se cria a janela do jogo cobrinha

public class Principal extends JFrame { //principal herda de JFrame

	public static void main(String[] args) {
		
		//criar uma instancia do jogo
		Jogo game = new Jogo();
		
		//criar a janela do jogo
		JFrame janelaprincipal = new JFrame("Pega Frutas");
		
		//adicionar o jogo na tela
		janelaprincipal.add(game);
		
		//permitir alterar dimensoes na tela de acordo com jogo
		janelaprincipal.pack();
		
		//limpar as configuracoes de posicionamento da tela
		janelaprincipal.setLocationRelativeTo(null);
		
		//encerrar o programa quando clicar no X da janela
		janelaprincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//não deixar o usuario alterar o tamanho da janela
		janelaprincipal.setResizable(false);
		
		//mostrar a tela
		janelaprincipal.setVisible(true);
		
		//iniciar a Thread(linha de execução) do jogo para que rode de forma parelela com o resto do jogo
		new Thread(game).start();
	}
}
