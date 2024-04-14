import java.util.HashMap;
import java.util.Map;

public class Livro {

    private Map<Integer, String[]> ordensCompra;
    private Map<Integer, String[]> ordensVenda;
    private int cont;

    public Livro() {
        this.ordensCompra = new HashMap<>();
        this.ordensVenda = new HashMap<>();
    }

    private void processarOrdem(String[] menssagem) {
        cont++;
        String aux[] = new String[5];

        String tipo = menssagem[0], ativo = menssagem[1];
        int quantidade = Integer.parseInt(menssagem[2]);
        double valor = Double.parseDouble(menssagem[3]);

        Map<Integer, String[]> ordensVendaaux = ordensVenda;

        Map<Integer, String[]> ordensCompraaux = ordensCompra;

        if (tipo.equals("compra")) {
            for (int i = 0; i < ordensVenda.size(); i++) {
                aux = ordensVenda.get(i);

                if (aux[1].equals(ativo) && Integer.parseInt(aux[2]) >= quantidade
                        && Double.parseDouble(aux[3]) <= valor) {
                    if (quantidade == Integer.parseInt(aux[2]) && Double.parseDouble(aux[3]) == valor) {
                        realizarTrasacao(menssagem, aux);
                        ordensVendaaux.remove(i);
                    } else {
                        ordensVendaaux.remove(i);
                        aux[2] = Integer.toString(quantidade - Integer.parseInt(aux[2]));
                        aux[3] = Double.toString(Double.parseDouble(aux[3]) - valor);

                        realizarTrasacao(menssagem, aux);

                        processarOrdem(aux);
                    }
                }
            }
            ordensVenda = ordensVendaaux;
            ordensCompra.put(cont, menssagem);
        } else if (tipo.equals("venda")) {
            for (int i = 0; i < ordensCompra.size(); i++) {
                aux = ordensCompra.get(i);

                if (aux[1].equals(ativo) && Integer.parseInt(aux[2]) <= quantidade
                        && Double.parseDouble(aux[3]) >= valor) {
                    if (quantidade == Integer.parseInt(aux[2]) && Double.parseDouble(aux[3]) == valor) {
                        realizarTrasacao(menssagem, aux);
                        ordensCompraaux.remove(i);
                    } else {
                        realizarTrasacao(menssagem, aux);
                        ordensCompraaux.remove(i);
                        aux[2] = Integer.toString(Integer.parseInt(aux[2]) - quantidade);
                        aux[3] = Double.toString(valor - Double.parseDouble(aux[3]));
                        processarOrdem(aux);
                    }
                }
            }
            ordensCompra = ordensCompraaux;
            ordensVenda.put(cont, menssagem);
        }
    }

    private void realizarTrasacao(String[] menssagem, String[] aux) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'realizarTrasacao'");
    }

}
