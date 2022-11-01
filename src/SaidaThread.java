import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SaidaThread extends Thread implements Runnable {

    private final Controlador controlador;
    public DatagramSocket clientSocket;
    public InetAddress IPAddress;
    public Integer porta;

    public SaidaThread(Controlador controlador) {
        this.controlador = controlador;
        this.porta = 9090;
        try {
            this.clientSocket = new DatagramSocket();
            this.IPAddress = InetAddress.getByName("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                atualizarSaida(enviarPacote("sno0"));
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void atualizarSaida(DatagramPacket pacote) {
        String resposta = new String(pacote.getData());
        resposta = resposta.substring(3);

        Double novoVolume = Double.parseDouble(resposta);
        this.controlador.saida.setText("Saída de Água - " + (String.format("%.2f", novoVolume)));
    }

    public DatagramPacket enviarPacote(String mensagem) {
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        sendData = mensagem.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length, IPAddress, porta);

        try {
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receberPacote(receiveData);
    }

    public DatagramPacket receberPacote(byte[] receiveData) {
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);

        try {
            clientSocket.receive(receivePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receivePacket;
    }
}
