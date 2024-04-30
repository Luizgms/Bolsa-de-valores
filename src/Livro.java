import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Livro {

    private static Map<Integer, String[]> ordensCompra;
    private static Map<Integer, String[]> ordensVenda;
    private static int cont;

    public Livro() {
        ordensCompra = new HashMap<>();
        ordensVenda = new HashMap<>();
    }

    public void processarOrdem(String[] menssagem) throws IOException {
        cont++;
        String aux[] = new String[5];

        String tipo = menssagem[0], ativo = menssagem[1];
        int quantidade = Integer.parseInt(menssagem[2]);
        double valor = Double.parseDouble(menssagem[3]);

        Map<Integer, String[]> ordensVendaaux = ordensVenda;

        Map<Integer, String[]> ordensCompraaux = ordensCompra;

        if (tipo.equals("compra")) {
            for(Integer num : ordensVenda.keySet()) {
                aux = ordensVenda.get(num);

                if (aux[1].equals(ativo) && Integer.parseInt(aux[2]) >= quantidade
                        && Double.parseDouble(aux[3]) <= valor) {
                    if (quantidade == Integer.parseInt(aux[2]) && Double.parseDouble(aux[3]) == valor) {
                        realizarTrasacao(menssagem, aux);
                        ordensVendaaux.remove(num);
                    } else {
                        ordensVendaaux.remove(num);
                        aux[2] = Integer.toString(quantidade - Integer.parseInt(aux[2]));
                        aux[3] = Double.toString(Double.parseDouble(aux[3]) - valor);

                        realizarTrasacao(menssagem, aux);

                        processarOrdem(aux);
                    }
                }
            }
            ordensVenda = ordensVendaaux;
            ordensCompra.put(cont, menssagem);

            atualizarLivro();
        } else if (tipo.equals("venda")) {
            for(Integer num : ordensCompra.keySet()) {
                aux = ordensCompra.get(num);

                if (aux[1].equals(ativo) && Integer.parseInt(aux[2]) <= quantidade
                        && Double.parseDouble(aux[3]) >= valor) {
                    if (quantidade == Integer.parseInt(aux[2]) && Double.parseDouble(aux[3]) == valor) {
                        realizarTrasacao(menssagem, aux);
                        ordensCompraaux.remove(num);
                    } else {
                        realizarTrasacao(menssagem, aux);
                        ordensCompraaux.remove(num);
                        aux[2] = Integer.toString(Integer.parseInt(aux[2]) - quantidade);
                        aux[3] = Double.toString(valor - Double.parseDouble(aux[3]));
                        processarOrdem(aux);
                    }
                }
            }
            ordensCompra = ordensCompraaux;
            ordensVenda.put(cont, menssagem);

            atualizarLivro();
        }
    }

    public static void atualizarLivro() throws IOException {
        FileWriter fileWriter = new FileWriter("livroDeOfertas.txt", false);
        printOrdemCompra(fileWriter);
        printOrdemVenda(fileWriter);
    }
    
    public static void printOrdemVenda(FileWriter fileWriter) {
        try {
            File myObj = new File("livroDeOfertas.txt");
            if (myObj.createNewFile()) {
                // Arquivo criado
                for(Integer num : ordensVenda.keySet()) {
                    fileWriter.write(num + " => " + Arrays.toString(ordensVenda.get(num)) + "\n");
                }
                fileWriter.close();
            } else {
                // Arquivo já existe
                for(Integer num : ordensVenda.keySet()) {
                    fileWriter.write(num + " => " + Arrays.toString(ordensVenda.get(num)) + "\n");
                }
                fileWriter.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao utilizar o arquivo.");
            e.printStackTrace();
        }
    }
    
    public static void printOrdemCompra(FileWriter fileWriter) {
        try {
            File myObj = new File("livroDeOfertas.txt");
            if (myObj.createNewFile()) {
                // Arquivo criado
                for(Integer num : ordensCompra.keySet()) {
                    fileWriter.write(num + " => " + Arrays.toString(ordensCompra.get(num)) + "\n");
                }
                fileWriter.close();
            } else {
                // Arquivo já existe
                for(Integer num : ordensCompra.keySet()) {
                    fileWriter.write(num + " => " + Arrays.toString(ordensCompra.get(num)) + "\n");
                }
                fileWriter.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao utilizar o arquivo.");
            e.printStackTrace();
        }
    }

    private static void realizarTrasacao(String[] menssagem, String[] aux) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'realizarTrasacao'");
    }

}
