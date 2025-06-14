package service;

import java.time.LocalDateTime;

public interface EstacionamentoService {
    void estacionar(String placa, int numeroVaga, LocalDateTime entrada);
    double sair(String placa, LocalDateTime saida);
    void listarVagasLivres();
    void listarVagasOcupadas();
    void listarVeiculosEstacionados();
    void pesquisarVeiculo(String placa);

    // Novos métodos:
    void cadastrarVeiculo(String placa, String modelo, String tipo);
    void atualizarVeiculo(String placa, String novaPlaca, String novoModelo, String novoTipo);
    void excluirVeiculo(String placa);
}