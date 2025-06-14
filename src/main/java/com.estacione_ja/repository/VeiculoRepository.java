package repository;

import model.Veiculo;
import java.util.Collection;

public interface VeiculoRepository {
    void salvar(Veiculo veiculo);
    Veiculo buscarPorPlaca(String placa);
    Collection<Veiculo> listarTodos();
    void excluirPorPlaca(String placa); // Novo método para exclusão
}