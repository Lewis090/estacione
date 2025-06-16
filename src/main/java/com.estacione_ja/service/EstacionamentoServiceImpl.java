package service;

import model.Vaga;
import model.Veiculo;
import model.Carro;
import model.Motocicleta;
import repository.VeiculoRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class EstacionamentoServiceImpl implements EstacionamentoService {
    private List<Vaga> vagas;
    private VeiculoRepository veiculoRepo;
    private double valorCarro, valorMoto, fracaoMinutos, valorFracaoMinutos;

    public EstacionamentoServiceImpl(List<Vaga> vagas,
                                     VeiculoRepository veiculoRepo,
                                     double valorCarro,
                                     double valorMoto,
                                     double fracaoMinutos,
                                     double valorFracaoMinutos) {
        this.vagas = vagas;
        this.veiculoRepo = veiculoRepo;
        this.valorCarro = valorCarro;
        this.valorMoto = valorMoto;
        this.fracaoMinutos = fracaoMinutos;
        this.valorFracaoMinutos = valorFracaoMinutos;
    }

    @Override
    public void estacionar(String placa, int numeroVaga, LocalDateTime entrada) {
        Veiculo veiculo = veiculoRepo.buscarPorPlaca(placa);
        if (veiculo == null) {
            throw new IllegalArgumentException("Erro: Veículo não cadastrado! Primeiro, cadastre o veículo antes de estacioná-lo.");
        }

        Vaga vaga = vagas.stream()
                .filter(v -> v.getNumero() == numeroVaga)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Erro: Vaga inválida."));

        if (vaga.isOcupada()) throw new IllegalArgumentException("Erro: Vaga já ocupada.");
        if (!vaga.getTipo().equals(veiculo.getTipo())) throw new IllegalArgumentException("Erro: Tipo de vaga incompatível.");

        veiculo.setHoraEntrada(entrada);
        vaga.ocupar(veiculo);
        System.out.println("Veículo estacionado com sucesso!");
    }

    @Override
    public double sair(String placa, LocalDateTime saida) {
        Veiculo veiculo = veiculoRepo.buscarPorPlaca(placa);
        if (veiculo == null || veiculo.getHoraEntrada() == null) {
            throw new IllegalArgumentException("Veículo não está estacionado.");
        }

        Vaga vaga = vagas.stream()
                .filter(v -> v.isOcupada() && v.getVeiculo().getPlaca().equals(placa))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada para o veículo."));

        long minutosOcupado = Duration.between(veiculo.getHoraEntrada(), saida).toMinutes();
        if (minutosOcupado <= 0) minutosOcupado = 1;

        double valorBase = veiculo.getTipo().equals("CARRO") ? valorCarro : valorMoto;
        double valorFrações = Math.ceil(minutosOcupado / fracaoMinutos) * valorFracaoMinutos;
        double valorTotal = valorBase + valorFrações;

        vaga.desocupar();
        veiculo.setHoraEntrada(null);

        System.out.println("Veículo removido da vaga " + vaga.getNumero() + ". Tempo total: " + minutosOcupado + " min.");
        System.out.println("Valor a pagar: R$" + valorTotal);

        return valorTotal;
    }

    @Override
    public void listarVagasLivres() {
        System.out.println("Vagas livres:");
        boolean encontrouVagas = false;
        for (Vaga vaga : vagas) {
            if (!vaga.isOcupada()) {
                System.out.println("Vaga " + vaga.getNumero() + " - Tipo: " + vaga.getTipo());
                encontrouVagas = true;
            }
        }
        if (!encontrouVagas) {
            System.out.println("Nenhuma vaga disponível no momento.");
        }
    }

    @Override
    public void listarVagasOcupadas() {
        System.out.println("Vagas ocupadas:");
        boolean encontrouVagas = false;
        for (Vaga vaga : vagas) {
            if (vaga.isOcupada()) {
                Veiculo veiculo = vaga.getVeiculo();
                long minutosOcupado = Duration.between(veiculo.getHoraEntrada(), LocalDateTime.now()).toMinutes();
                System.out.println("Vaga " + vaga.getNumero() + " - " + veiculo.getPlaca() + " (" + veiculo.getModelo() + ") - Ocupada há " + minutosOcupado + " minutos");
                encontrouVagas = true;
            }
        }
        if (!encontrouVagas) {
            System.out.println("Nenhuma vaga ocupada no momento.");
        }
    }

    @Override
    public void listarVeiculosEstacionados() {
        System.out.println("Veículos atualmente estacionados:");
        for (Vaga vaga : vagas) {
            if (vaga.isOcupada()) {
                Veiculo veiculo = vaga.getVeiculo();
                System.out.println("Placa: " + veiculo.getPlaca() + ", Modelo: " + veiculo.getModelo() + ", Tipo: " + veiculo.getTipo() + ", Vaga: " + vaga.getNumero());
            }
        }
    }

    @Override
    public void pesquisarVeiculo(String placa) {
        Veiculo veiculo = veiculoRepo.buscarPorPlaca(placa);
        if (veiculo == null) {
            System.out.println("Erro: Veículo não encontrado. Verifique a placa e tente novamente.");
            return;
        }

        System.out.println("🔎 Detalhes do Veículo:");
        System.out.println("Placa: " + veiculo.getPlaca());
        System.out.println("Modelo: " + veiculo.getModelo());
        System.out.println("Tipo: " + veiculo.getTipo());

        if (veiculo.getHoraEntrada() != null) {
            long minutosEstacionado = Duration.between(veiculo.getHoraEntrada(), LocalDateTime.now()).toMinutes();
            if (minutosEstacionado <= 0) minutosEstacionado = 1;

            double valorBase = veiculo.getTipo().equals("CARRO") ? valorCarro : valorMoto;
            double valorFrações = Math.ceil(minutosEstacionado / fracaoMinutos) * valorFracaoMinutos;
            double valorTotal = valorBase + valorFrações;

            System.out.println("Status: Estacionado desde " + veiculo.getHoraEntrada());
            System.out.println("Tempo total estacionado: " + minutosEstacionado + " min.");
            System.out.println("Valor a pagar até o momento: R$" + valorTotal);
        } else {
            System.out.println("Status: Fora do estacionamento.");
        }

        Scanner scanner = new Scanner(System.in);
        int escolha = -1;

        while (escolha < 0 || escolha > 2) {
            System.out.println("Deseja editar (1) ou excluir (2) o veículo? Digite 0 para cancelar:");
            try {
                escolha = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Entrada inválida. Digite apenas 0, 1 ou 2.");
            }
        }

        if (escolha == 1) {
            System.out.print("Nova placa: ");
            String novaPlaca = scanner.nextLine();
            System.out.print("Novo modelo: ");
            String novoModelo = scanner.nextLine();

            String novoTipo;
            while (true) {
                System.out.print("Novo tipo (CARRO/MOTO): ");
                novoTipo = scanner.nextLine().toUpperCase();
                if (novoTipo.equals("CARRO") || novoTipo.equals("MOTO")) {
                    break;
                } else {
                    System.out.println("Erro: Tipo inválido! Apenas CARRO ou MOTO são permitidos.");
                }
            }

            atualizarVeiculo(placa, novaPlaca, novoModelo, novoTipo);
        } else if (escolha == 2) {
            excluirVeiculo(placa);
        }
    }

    @Override
    public void cadastrarVeiculo(String placa, String modelo, String tipo) {
        if (tipo == null || (!tipo.equalsIgnoreCase("CARRO") && !tipo.equalsIgnoreCase("MOTO"))) {
            throw new IllegalArgumentException("Erro: Tipo de veículo inválido! Apenas CARRO ou MOTO são permitidos.");
        }

        Veiculo novoVeiculo = tipo.equalsIgnoreCase("CARRO") ? new Carro(placa, modelo) : new Motocicleta(placa, modelo);
        veiculoRepo.salvar(novoVeiculo);
        System.out.println("Veículo cadastrado com sucesso!");
    }

    @Override
    public void atualizarVeiculo(String placa, String novaPlaca, String novoModelo, String novoTipo) {
        Veiculo veiculo = veiculoRepo.buscarPorPlaca(placa);
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo não encontrado!");
        }

        if (!novoTipo.equalsIgnoreCase("CARRO") && !novoTipo.equalsIgnoreCase("MOTO")) {
            throw new IllegalArgumentException("Erro: Tipo inválido! Apenas CARRO ou MOTO são permitidos.");
        }

        // Atualizar os dados diretamente no mesmo objeto
        veiculo.setPlaca(novaPlaca);
        veiculo.setModelo(novoModelo);
        veiculo.setTipo(novoTipo);

        // Se a placa mudar, ajustar o repositório
        if (!placa.equals(novaPlaca)) {
            veiculoRepo.excluirPorPlaca(placa);
            veiculoRepo.salvar(veiculo);
        }

        System.out.println("Veículo atualizado com sucesso!");
    }

    @Override
    public void excluirVeiculo(String placa) {
        Veiculo veiculo = veiculoRepo.buscarPorPlaca(placa);
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo não encontrado!");
        }

        if (veiculo.getHoraEntrada() != null) {
            throw new IllegalArgumentException("Veículo está estacionado! Remova-o antes de excluir.");
        }

        veiculoRepo.excluirPorPlaca(placa);
        System.out.println("Veículo excluído com sucesso!");
    }

    public void listarVagasLivresPorTipo(String tipoVeiculo) {
        System.out.println("Vagas disponíveis para " + tipoVeiculo + ":");
        boolean encontrouVagas = false;
        for (Vaga vaga : vagas) {
            if (!vaga.isOcupada() && vaga.getTipo().equals(tipoVeiculo)) {
                System.out.println("Vaga " + vaga.getNumero());
                encontrouVagas = true;
            }
        }
        if (!encontrouVagas) {
            System.out.println("Nenhuma vaga disponível para " + tipoVeiculo + ".");
        }
    }
}
