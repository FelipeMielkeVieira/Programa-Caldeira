import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;

public class TemperaturaThread extends Thread implements Runnable {

    private final Controlador controlador;
    public DatagramSocket clientSocket;
    public InetAddress IPAddress;
    public Integer porta;
    public File arquivoTempos;
    public int[] temposRespostas;

    public TemperaturaThread(Controlador controlador) {
        this.controlador = controlador;
        this.porta = 9090;
        try {
            this.clientSocket = new DatagramSocket();
            this.IPAddress = InetAddress.getByName("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.arquivoTempos = new File("C:\\Users\\felipe_mielke-vieira\\Desktop\\tempos.txt");
        this.temposRespostas = new int[10];
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

        LocalTime inicio = java.time.LocalTime.now();
        try {
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receberPacote(receiveData, inicio);
    }

    public DatagramPacket receberPacote(byte[] receiveData, LocalTime inicio) {
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);

        try {
            clientSocket.receive(receivePacket);
            LocalTime fim = java.time.LocalTime.now();
            calcularData(inicio, fim);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receivePacket;
    }

    private void calcularData(LocalTime inicio, LocalTime fim) {
        Integer tempo = fim.getNano() - inicio.getNano();
        if(temposRespostas[9] >= 0) {
            if(tempo != 0) {
                editarArquivo(tempo);
                temposRespostas = new int[10];
            }
        } else {
            temposRespostas[temposRespostas.length] = tempo;
        }
    }

    private void editarArquivo(Integer tempo) {
        try {
            FileWriter fileWriter = new FileWriter(arquivoTempos, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write("Tempo: " + java.time.LocalTime.now() + " - " + tempo / 1000000 + " ms  |  " + tempo + " ns");
            writer.newLine();

            writer.close();
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
