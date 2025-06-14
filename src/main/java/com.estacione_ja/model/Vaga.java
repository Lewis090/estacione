package model;

public class Vaga {
    private int numero;
    private String tipo;
    private Veiculo veiculo;

    public Vaga(int numero, String tipo) {
        this.numero = numero;
        this.tipo = tipo;
    }

    public boolean isOcupada() { return veiculo != null; }
    public void ocupar(Veiculo veiculo) {
        this.veiculo = veiculo;
        veiculo.setHoraEntrada(java.time.LocalDateTime.now());
    }
    public void desocupar() { veiculo = null; }

    public Veiculo getVeiculo() { return veiculo; }
    public String getTipo() { return tipo; }
    public int getNumero() { return numero; }
}