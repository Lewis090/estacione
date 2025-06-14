package repository;

import model.Veiculo;
import java.util.*;

public class VeiculoRepositoryImpl implements VeiculoRepository {
    private Map<String, Veiculo> veiculos = new HashMap<>();

    public void salvar(Veiculo veiculo) {
        veiculos.put(veiculo.getPlaca(), veiculo);
    }

    public Veiculo buscarPorPlaca(String placa) {
        return veiculos.get(placa);
    }

    public Collection<Veiculo> listarTodos() {
        return veiculos.values();
    }

    @Override
    public void excluirPorPlaca(String placa) {
        veiculos.remove(placa);
    }
}