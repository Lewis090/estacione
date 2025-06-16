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

            switch (opcao) {
                case 1:
                    System.out.print("Digite a placa: ");
                    String placa = scanner.nextLine();
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
                    //System.out.println("Veículo cadastrado com sucesso!");
                    break;


                case 2:
                    System.out.print("Digite a placa do veículo: ");
                    placa = scanner.nextLine();


                    Veiculo veiculo = veiculoRepo.buscarPorPlaca(placa);
                    if (veiculo == null) {
                        System.out.println("Erro: Veículo não cadastrado! Primeiro, cadastre o veículo antes de estacioná-lo.");
                        break;
                    }


                    ((EstacionamentoServiceImpl) estacionamento).listarVagasLivresPorTipo(veiculo.getTipo());

                    System.out.print("Número da vaga: ");
                    int numeroVaga = scanner.nextInt();

                    try {
                        estacionamento.estacionar(placa, numeroVaga, LocalDateTime.now());
                        //System.out.println("Veículo estacionado!");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erro ao estacionar: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("Digite a placa do veículo: ");
                    placa = scanner.nextLine();
                    double valor = estacionamento.sair(placa, LocalDateTime.now());
                    System.out.println("Veículo removido. Valor a pagar: R$" + valor);
                    break;

                case 4:
                    System.out.println("Listando vagas livres...");
                    estacionamento.listarVagasLivres();
                    break;

                case 5:
                    System.out.println("Listando vagas ocupadas...");
                    estacionamento.listarVagasOcupadas();
                    break;

                case 6:
                    System.out.print("Digite a placa do veículo: ");
                    placa = scanner.nextLine();
                    estacionamento.pesquisarVeiculo(placa);
                    break;

                case 0:
                    System.out.println("Saindo...");
                    return;

                default:
                    System.out.println("Opção inválida!");
            }
        }
    }
}