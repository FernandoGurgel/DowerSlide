package br.edu.ifam.dowerslide;

/**
 * Created by fernando on 26/06/17.
 */

class ModeloConexao {

    private String ip;
    private String mensagem;
    private int porta;

    public ModeloConexao(String ip, String s,int porta) {
        this.ip = ip;
        this.porta = porta;
        this.mensagem = s;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }
}
