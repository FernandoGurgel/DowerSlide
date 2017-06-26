package br.edu.ifam.dowerslide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class TelaEntrada extends AppCompatActivity {

    private ModeloConexao conexao;
    private String resposta = null;
    private boolean vida;
    private EditText ip;
    private Button button;
    private ProgressBar progressBar;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_entrada);

        ip = (EditText) findViewById(R.id.txt_ip);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        layout = (RelativeLayout) findViewById(R.id.fundo_progressBar);
        button = (Button) findViewById(R.id.btn_ok);
        conexao = new ModeloConexao(ip.getText().toString(),"aceita",5000);
    }

    public void onClickValida(View view){
        if (ip.length() > 8){
            componetProgressBar(true);
            new Thread(new EnviandoSolicitacao()).start();
            recebeSolicitacao();
        }else{
            ip.setText("");
            ip.hasFocus();
            Toast.makeText(this,"Ip inválido",Toast.LENGTH_SHORT).show();
        }

    }

    private void recebeSolicitacao() {
        vida = true;
        try {
            new Thread(new ReceberMensagem()).start();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            vida = false;
            validaMensagem();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validaMensagem() {
        if(resposta != null){
            Intent intent = new Intent(TelaEntrada.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            componetProgressBar(false);
        }
    }

    private void componetProgressBar(boolean b) {
        if(b){
            button.setEnabled(false);
            ip.setEnabled(false);
            ip.setFocusable(false);
            ip.setFocusableInTouchMode(true);
            progressBar.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
        }else{
            button.setEnabled(true);
            ip.setEnabled(true);
            ip.setText("");
            ip.setFocusable(true);
            ip.setFocusableInTouchMode(true);
            progressBar.setVisibility(View.INVISIBLE);
            layout.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Computador não encontrado",Toast.LENGTH_SHORT).show();
        }
    }


    private class ReceberMensagem implements Runnable {

        byte[] dadosReceber = new byte[255];
        DatagramSocket socket = null;

        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("Fernando", "Recebendo mensagem");
            while (vida) {
                try {
                    socket = new DatagramSocket(conexao.getPorta());
                } catch (SocketException ex) {
                    System.out.println(ex.getMessage());
                }

                while (vida) {
                    dadosReceber = new byte[255];
                    DatagramPacket pacoteRecebido = new DatagramPacket(dadosReceber, dadosReceber.length);
                    try {
                        socket.receive(pacoteRecebido);
                        byte[] b = pacoteRecebido.getData();
                        String s = "";
                        for (int i = 0; i < b.length; i++) {
                            if (b[i] != 0) {
                                s += (char) b[i];
                            }
                        }
                        resposta = s;

                    } catch (Exception e) {
                        System.out.println("erro");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            }

        }
    }

    private class EnviandoSolicitacao implements Runnable {

        private byte[] mensagem = new byte[255];

        @Override
        public void run() {
            try{
                String s = "aceita";
                mensagem = s.getBytes();
                DatagramSocket observadoSocket = new DatagramSocket();
                InetAddress endereco = InetAddress.getByName(conexao.getIp());
                DatagramPacket pacote = new DatagramPacket(mensagem,mensagem.length,endereco,conexao.getPorta());
                observadoSocket.send(pacote);
                observadoSocket.close();
                Log.d("Fernando","Mensagem de solicitacao enviada");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
