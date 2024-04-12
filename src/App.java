import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(System.in);

        char[] nome;
        do {
            System.out.print("Nome da corretora (exatamente 4 caracteres): ");
            nome = scan.nextLine().toCharArray();
        } while(nome.length != 4);
        Corretora corretora = new Corretora(nome);
        int op = 1;
        while (op != 0) {
            System.out.println("Menu");
            System.out.println("1 - Comprar ");
            System.out.println("2 - Vender ");
            System.out.println("3 - Listar ações");
            System.out.println("4 - Seguir uma ação");
            System.out.println("0 - Sair\n");
            System.out.print("Código da operação: ");
            op = scan.nextInt();
            scan.nextLine();

            switch (op) {
                case 1: {
                    System.out.print("\nDigite a quantidade de ações: ");
                    int quantidade = scan.nextInt();
                    System.out.print("Digite o preço: ");
                    double val = scan.nextDouble();
                    System.out.print("Digite o código da ação: ");
                    String codigo = scan.next();
                    corretora.compra(codigo, quantidade, val, corretora.getNome());
                    System.out.println("Pedido de compra efetuado");
                    break;
                }
                case 2: {
                    System.out.print("\nDigite a quantidade de ações: ");
                    int quantidade = scan.nextInt();
                    System.out.print("Digite o preço: ");
                    double val = scan.nextDouble();
                    System.out.print("Digite o código da ação: ");
                    String codigo = scan.next();
                    corretora.venda(codigo, quantidade, val, corretora.getNome());
                    System.out.println("Pedido de venda efetuado");
                    break;
                }
                case 3: {
                    break;
                }
                case 4: {
                    break;
                }
                case 0:
                    System.out.println("Até logo! :)");
                    break;
                default:
                    System.out.println("Opção inválida");
                    break;
            }

        }

        scan.close();
        System.exit(0);
    }
}