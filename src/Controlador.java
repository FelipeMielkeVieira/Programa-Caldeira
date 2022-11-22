import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controlador extends JFrame implements Runnable {
    private JPanel painelPrincipal;
    private JButton sairButton;
    public JLabel temperatura;
    public JLabel volume;
    public JLabel saida;
    private JButton iniciarButton;
    private JButton pararButton;
    private JButton encerrarButton;

    public Controlador() {
        criarComponentes();

        TemperaturaThread temperaturaThread = new TemperaturaThread(this);
        temperaturaThread.start();
        TanqueThread tanqueThread = new TanqueThread(this);
        tanqueThread.start();
        SaidaThread saidaThread = new SaidaThread(this);
        saidaThread.start();

        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });
    }

    public void criarComponentes() {
        setContentPane(painelPrincipal);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    @Override
    public void run() {
        if (!isVisible()) {
            this.setVisible(true);
        } else {
            System.out.println("Janela já está aberta");
        }
    }

    public static void main(String[] args) {
        Controlador controlador = new Controlador();
        controlador.run();
    }
}
