import model.*;
import repository.VeiculoRepository;
import repository.VeiculoRepositoryImpl;
import service.EstacionamentoService;
import service.EstacionamentoServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("### Configuração do Estacionamento ###");
        System.out.print("Quantidade de vagas para carros: ");
        int vagasCarro = scanner.nextInt();

        System.out.print("Quantidade de vagas para motos: ");
        int vagasMoto = scanner.nextInt();

        System.out.print("Taxa de estacionamento para (CARRO): ");
        double valorHoraCarro = scanner.nextDouble();

        System.out.print("Taxa de estacionamento para (MOTO): ");
        double valorHoraMoto = scanner.nextDouble();

        System.out.print("Duração da fração (em minutos): ");
        double fracaoMinutos = scanner.nextDouble();

        System.out.print("Valor por fração (dinheiro): ");
        double valorFracaoMinutos = scanner.nextDouble();
        scanner.nextLine();

        List<Vaga> vagas = new ArrayList<>();
        for (int i = 0; i < vagasCarro; i++) vagas.add(new Vaga(i, "CARRO"));
        for (int i = 0; i < vagasMoto; i++) vagas.add(new Vaga(vagasCarro + i, "MOTO"));

        VeiculoRepository veiculoRepo = new VeiculoRepositoryImpl();
        EstacionamentoService estacionamento = new EstacionamentoServiceImpl(
                vagas, veiculoRepo, valorHoraCarro, valorHoraMoto, fracaoMinutos, valorFracaoMinutos);

        System.out.println("\n " +
                "...................................................................................\n" +
                ".................-+############################################*=:.................\n" +
                "...............:##################################################=................\n" +
                "..............-###################################################%+...............\n" +
                "..............=#######************#*************#************######*:..............\n" +
                "...........:::::::-###:..........:#............-#...........:###-:::::::...........\n" +
                "...........:::::::-###:...########%****-...+****#....###########-:::::::...........\n" +
                "..............+#######:...#############-...*#####....############%#*:..............\n" +
                "...........:*******###:..........+#####-...*#####...........####*******:...........\n" +
                "...........:*******###:...#############-...*#####....###########*******:...........\n" +
                "..............+###%###:...#######%%####-...*#####....%#########%%##*:..............\n" +
                "...........:*******###:...........#####-...*#####............*##*******:...........\n" +
                "...........:*******###:...........#####-...*#####............*##*******:...........\n" +
                "..............=####################################################*:..............\n" +
                "..............:#######*===+*+*+#=#=+-+*=+++#+#-+-+++*+=#++#########=...............\n" +
                "...............:##################################################-................\n" +
                ".................:=############################################+-..................\n" +
                "....................................CICERO DIAS....................................\n" +
                "......ALUNOS:.....LEVI DE OLIVEIRA - WILLIAM JOSE  - CIBELE COSTA..................\n");

        while (true) {
            System.out.println("\n### Estacionamento ###");
            System.out.println("1 - Cadastrar Veículo");
            System.out.println("2 - Estacionar Veículo");
            System.out.println("3 - Retirar Veículo");
            System.out.println("4 - Listar Vagas Livres");
            System.out.println("5 - Listar Vagas Ocupadas");
            System.out.println("6 - Pesquisar Veículo");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            String placa;
            Veiculo veiculo;

            switch (opcao) {
                case 1:
                    System.out.print("Digite a placa: ");
                    placa = scanner.nextLine();
                    System.out.print("Digite o modelo: ");
                    String modelo = scanner.nextLine();

                    String tipo;
                    while (true) {
                        System.out.print("Tipo (CARRO/MOTO): ");
                        tipo = scanner.nextLine().toUpperCase();
                        if (tipo.equals("CARRO") || tipo.equals("MOTO")) {
                            break;
                        } else {
                            System.out.println("Erro: Tipo de veículo inválido! Apenas CARRO ou MOTO são permitidos.");
                        }
                    }

                    estacionamento.cadastrarVeiculo(placa, modelo, tipo);
                    pausar(scanner);
                    break;

                case 2:
                    System.out.print("Digite a placa do veículo: ");
                    placa = scanner.nextLine();

                    veiculo = veiculoRepo.buscarPorPlaca(placa);
                    if (veiculo == null) {
                        System.out.println("Erro: Veículo não cadastrado! Primeiro, cadastre o veículo antes de estacioná-lo.");
                        pausar(scanner);
                        break;
                    }

                    ((EstacionamentoServiceImpl) estacionamento).listarVagasLivresPorTipo(veiculo.getTipo());

                    System.out.print("Número da vaga: ");
                    int numeroVaga = scanner.nextInt();

                    try {
                        estacionamento.estacionar(placa, numeroVaga, LocalDateTime.now());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erro ao estacionar: " + e.getMessage());
                    }
                    scanner.nextLine();
                    pausar(scanner);
                    break;

                case 3:
                    System.out.print("Digite a placa do veículo: ");
                    placa = scanner.nextLine();
                    veiculo = veiculoRepo.buscarPorPlaca(placa);

                    if (veiculo == null) {
                        System.out.println("Veículo não encontrado. Verifique a placa.");
                    } else {
                        Vaga vagaDoVeiculo = estacionamento.buscarVagaDoVeiculo(placa);

                        if (vagaDoVeiculo == null) {
                            System.out.println("Veículo não está estacionado.");
                        } else {
                            try {
                                double valor = estacionamento.sair(placa, LocalDateTime.now());
                                System.out.println("Veículo removido. Valor a pagar: R$" + valor);
                            } catch (IllegalArgumentException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                    pausar(scanner);
                    break;

                case 4:
                    System.out.println("Listando vagas livres...");
                    estacionamento.listarVagasLivres();
                    pausar(scanner);
                    break;

                case 5:
                    System.out.println("Listando vagas ocupadas...");
                    estacionamento.listarVagasOcupadas();
                    pausar(scanner);
                    break;

                case 6:
                    System.out.print("Digite a placa do veículo: ");
                    placa = scanner.nextLine();
                    estacionamento.pesquisarVeiculo(placa);
                    pausar(scanner);
                    break;

                case 0:
                    System.out.println("Saindo...");
                    return;

                default:
                    System.out.println("Opção inválida!");
                    pausar(scanner);
            }
        }
    }

    private static void pausar(Scanner scanner) {
        System.out.println("Pressione ENTER para voltar ao menu...");
        scanner.nextLine();
    }
}
