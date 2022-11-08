import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class SaidaThread extends Thread implements Runnable {

    private final Controlador controlador;
    public DatagramSocket clientSocket;
    public InetAddress IPAddress;
    public Integer porta;
    public double[] temposResposta;

    public SaidaThread(Controlador controlador) {
        this.controlador = controlador;
        this.porta = 9090;
        try {
            this.clientSocket = new DatagramSocket();
            this.IPAddress = InetAddress.getByName("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.temposResposta = new double[10];
    }

    @Override
    public String toString() {
        String texto = "{ ";
        for (int i = 0; i < temposResposta.length; i++) {
            texto += temposResposta[i] + " ";
            if(i < 9) {
                texto += "| ";
            }
        }
        texto += "}";
        return texto;
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

        Long inicio = System.currentTimeMillis();
        try {
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receberPacote(receiveData, inicio);
    }

    public DatagramPacket receberPacote(byte[] receiveData, Long inicio) {
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);

        try {
            clientSocket.receive(receivePacket);
            Long fim = System.currentTimeMillis();
            calcularData(inicio, fim);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receivePacket;
    }

    private void calcularData(Long inicio, Long fim) {
        Long tempo = fim - inicio;
        if(this.temposResposta.length < 10) {
            this.temposResposta[this.temposResposta.length] = tempo;
        } else {
            this.temposResposta = new double[10];
            this.temposResposta[0] = tempo;
        }
    }
}
