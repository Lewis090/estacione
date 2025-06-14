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
        // 🚨 Validação: verificar se o veículo está cadastrado antes de estacionar
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

        // Encontrar a vaga ocupada pelo veículo
        Vaga vaga = vagas.stream()
                .filter(v -> v.isOcupada() && v.getVeiculo().getPlaca().equals(placa))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada para o veículo."));

        // Calcular o tempo de ocupação em minutos
        long minutosOcupado = Duration.between(veiculo.getHoraEntrada(), saida).toMinutes();
        if (minutosOcupado <= 0) minutosOcupado = 1;

        // Definir o valor fixo para estacionar baseado no tipo do veículo
        double valorBase = veiculo.getTipo().equals("CARRO") ? valorCarro : valorMoto;

        // Calcular frações necessárias
        double valorFrações = Math.ceil(minutosOcupado / fracaoMinutos) * valorFracaoMinutos;

        // Calcular valor total (Base + Frações)
        double valorTotal = valorBase + valorFrações;

        // 🚨 Importante: Liberar a vaga corretamente!
        vaga.desocupar();

        // Remover a hora de entrada do veículo
        veiculo.setHoraEntrada(null);

        System.out.println("Veículo removido da vaga " + vaga.getNumero() + ". Tempo total: " + minutosOcupado + " min.");
        System.out.println("Valor a pagar: R$" + valorTotal);

        return valorTotal;
    }

    @Override
    public void listarVagasLivres() {
        System.out.println("Vagas livres:");

        boolean encontrouVagas = false; // Variável para verificar se há vagas disponíveis

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

        boolean encontrouVagas = false; // Variável para verificar se há vagas ocupadas

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

        // Perguntar ao usuário se deseja editar ou excluir o veículo
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

            // Validação do novo tipo
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

            // Se o tipo for alterado, migrar para vaga compatível
            if (!novoTipo.equals(veiculo.getTipo())) {
                System.out.println("O veículo será migrado para uma vaga compatível...");

                final String tipoFinal = novoTipo; // 🔥 Correção: Variável final para usar na lambda
                Vaga novaVaga = vagas.stream()
                        .filter(v -> !v.isOcupada() && v.getTipo().equals(tipoFinal))
                        .findFirst()
                        .orElse(null);

                if (novaVaga != null) {
                    novaVaga.ocupar(veiculo);
                    System.out.println("Veículo movido para a vaga " + novaVaga.getNumero());
                } else {
                    System.out.println("Nenhuma vaga disponível para o novo tipo! O veículo foi removido da vaga.");

                    Vaga vagaAntiga = vagas.stream()
                            .filter(v -> v.isOcupada() && v.getVeiculo().getPlaca().equals(placa))
                            .findFirst()
                            .orElse(null);

                    if (vagaAntiga != null) {
                        vagaAntiga.desocupar();
                    }
                }
            }

            atualizarVeiculo(placa, novaPlaca, novoModelo, novoTipo);
            System.out.println("Veículo atualizado com sucesso!");

        } else if (escolha == 2) {
            excluirVeiculo(placa);
            System.out.println("Veículo excluído com sucesso!");
        }
    }

    @Override
    public void cadastrarVeiculo(String placa, String modelo, String tipo) {
        // 🚨 Validação: aceitar somente "CARRO" ou "MOTO"
        if (tipo == null || (!tipo.equalsIgnoreCase("CARRO") && !tipo.equalsIgnoreCase("MOTO"))) {
            throw new IllegalArgumentException("Erro: Tipo de veículo inválido! Apenas CARRO ou MOTO são permitidos.");
        }

        // Criar veículo conforme o tipo validado
        Veiculo novoVeiculo;
        if (tipo.equalsIgnoreCase("CARRO")) {
            novoVeiculo = new Carro(placa, modelo);
        } else {
            novoVeiculo = new Motocicleta(placa, modelo);
        }

        // Salvar no repositório
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

        Veiculo atualizado = novoTipo.equalsIgnoreCase("CARRO") ? new Carro(novaPlaca, novoModelo) : new Motocicleta(novaPlaca, novoModelo);
        veiculoRepo.salvar(atualizado);
        veiculoRepo.excluirPorPlaca(placa);
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
    }
    public void listarVagasLivresPorTipo(String tipoVeiculo) {
        System.out.println("Vagas disponíveis para " + tipoVeiculo + ":");

        boolean encontrouVagas = false; // Variável para verificar se há vagas disponíveis

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