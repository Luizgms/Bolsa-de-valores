import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(System.in);

        System.out.println("Qual o nome da sua corretora? \n");
        String nome = scan.next();
        Corretora corretora = new Corretora(nome);

        while (true) {
            System.out.println("Menu ");
            System.out.println("1 - Comprar ");
            System.out.println("2 - Vender ");
            System.out.println("3 - Listar ações");
            System.out.println("4 - Seguir uma ação");
            System.out.println("0 - Sair");
            System.out.print(": ");
            String op = scan.next();

            switch (op) {
                case "1": {
                    System.out.print("\nDigite a quantidade de ações: ");
                    int quantidade = scan.nextInt();
                    System.out.print("Digite o preço: ");
                    double val = scan.nextDouble();
                    System.out.print("Digite o código da ação: ");
                    String codigo = scan.next();
                    corretora.compra(quantidade, val, corretora.getNome());
                    System.out.println("Pedido de compra efetuado");
                }
                case "2": {
                    System.out.print("\nDigite a quantidade de ações: ");
                    int quantidade = scan.nextInt();
                    System.out.print("Digite o preço: ");
                    double val = scan.nextDouble();
                    System.out.print("Digite o código da ação: ");
                    String codigo = scan.next();
                    corretora.venda(quantidade, val, corretora.getNome());
                    System.out.println("Pedido de venda efetuado");
                }
                case "3": {
                    
                }
                case "4": {

                }
                case "0":
                    System.exit(0);
                default:
                    System.out.println("Opção inválida");
            }

        }

    }
}