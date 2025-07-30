import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;

public class InterrupcaoTeclado implements KeyListener {
    private Cesto cesto;

    public InterrupcaoTeclado(Cesto cesto) {
        this.cesto = cesto;
    }

    @Override
    public void keyPressed(KeyEvent e) {
    	
    	try {
    		//acesso à região crítica
    		this.cesto.Mutex.acquire();
    		
    		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
    			cesto.direita = true;
    		}
    		else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
    			cesto.esquerda = true;
    		}
    	}catch (InterruptedException e1) {
    		e1.printStackTrace();
    	}
    	
    	//liberar região crítica
    	cesto.Mutex.release();
    }
    
public void keyReleased(KeyEvent e) {
    	
    	try {
    		//acesso à região crítica
    		this.cesto.Mutex.acquire();
    		//verificar se as teclas "->" ou "<-" foram pressionadas
    		
    		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
    			cesto.direita = false;
    		}
    		else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
    			cesto.esquerda = false;
    		}
    	}catch (InterruptedException e1) {
    		e1.printStackTrace();
    	}
    	
    	//liberar região crítica
    	cesto.Mutex.release();
    }
    
    
    
    
    
    
    //@Override
    //public void keyPressed(KeyEvent e) {
        //if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            //cesto.moverEsquerda();
        //} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            //cesto.moverDireita();
        //}
    //}

    @Override public void keyTyped(KeyEvent e) {}
    //@Override public void keyReleased(KeyEvent e) {}
}
