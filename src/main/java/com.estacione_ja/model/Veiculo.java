package model;

import java.time.LocalDateTime;

public abstract class Veiculo {
    private String placa;
    private String modelo;
    private String tipo;
    private LocalDateTime horaEntrada;

    public Veiculo(String placa, String modelo, String tipo) {
        this.placa = placa;
        this.modelo = modelo;
        this.tipo = tipo;
    }

    public String getPlaca() { return placa; }
    public String getModelo() { return modelo; }
    public String getTipo() { return tipo; }
    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }
}