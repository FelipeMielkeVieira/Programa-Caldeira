import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TemperaturaThread extends Thread implements Runnable {

    private final Controlador controlador;
    public DatagramSocket clientSocket;
    public InetAddress IPAddress;
    public Integer porta;

    public TemperaturaThread(Controlador controlador) {
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
                atualizarTemperatura(enviarPacote("st-0"));
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void atualizarTemperatura(DatagramPacket pacote) {
        String resposta = new String(pacote.getData());
        resposta = resposta.substring(3);

        Double novaTemperatura = Double.parseDouble(resposta);
        this.controlador.temperatura.setText("Temperatura - " + (String.format("%.2f", novaTemperatura)));
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