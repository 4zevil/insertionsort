import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ordena registros de tempos de corrida usando o algoritmo insertion sort.
 */
public class InsertionSort {

    /**
     * Representa um registro de tempo obtido do arquivo de entrada.
     */
    public static class Registro {
        /** Data e hora do registro. */
        public String dataHora;
        /** Nome do piloto ou participante. */
        public String nome;
        /** Nome da equipe. */
        public String equipe;
        /** Representacao textual do tempo no formato mm:ss:ms. */
        public String tempoTexto;
        /** Tempo convertido para milissegundos. */
        public long tempoMs;

        /**
         * Cria um registro e converte seu tempo para milissegundos.
         *
         * @param dataHora data e hora do registro
         * @param nome nome do piloto ou participante
         * @param equipe nome da equipe
         * @param tempoTexto tempo no formato "mm:ss:ms"
         */
        public Registro(String dataHora, String nome, String equipe, String tempoTexto) {
            this.dataHora = dataHora;
            this.nome = nome;
            this.equipe = equipe;
            this.tempoTexto = tempoTexto;
            this.tempoMs = converterTempo(tempoTexto);
        }

        /**
         * Retorna o registro em formato legivel.
         *
         * @return registro formatado com data, nome, equipe e tempo
         */
        @Override
        public String toString() {
            return dataHora + " - " + nome + " - " + equipe + " - " + tempoTexto;
        }
    }

    /**
     * Converte um tempo no formato "mm:ss:ms" para milissegundos.
     *
     * @param tempo tempo no formato "minutos:segundos:milissegundos"
     * @return tempo convertido para milissegundos
     */
    public static long converterTempo(String tempo) {
        String[] p = tempo.split(":");
        long min = Long.parseLong(p[0].trim());
        long seg = Long.parseLong(p[1].trim());
        long ms  = Long.parseLong(p[2].trim());
        return min * 60000 + seg * 1000 + ms;
    }

    /**
     * Compara dois registros pelo tempo e, em caso de empate, pelo nome.
     *
     * @param a primeiro registro a comparar
     * @param b segundo registro a comparar
     * @return valor negativo quando a vem antes de b, zero quando os registros
     * são equivalentes para a ordenação, ou valor positivo quando a vem depois de b
     */
    public static int comparar(Registro a, Registro b) {
        if (a.tempoMs != b.tempoMs) {
            return Long.compare(a.tempoMs, b.tempoMs);       // menor tempo primeiro
        }
        return a.nome.compareToIgnoreCase(b.nome);           // empate: ordem alfabética
    }

    /**
     * Le registros de um arquivo de texto.
     *
     * @param caminho caminho do arquivo a ser lido
     * @return lista com os registros validos encontrados no arquivo
     * @throws IOException se ocorrer um erro durante a leitura
     */
    public static List<Registro> lerArquivo(String caminho) throws IOException {
        List<Registro> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] c = linha.split(" - ");
                if (c.length != 4) continue;
                lista.add(new Registro(c[0].trim(), c[1].trim(), c[2].trim(), c[3].trim()));
            }
        }
        return lista;
    }

    /**
     * Ordena uma lista de registros pelo algoritmo insertion sort.
     *
     * @param lista lista de registros a ser ordenada
     */
    public static void insertionSort(List<Registro> lista) {
        for (int i = 1; i < lista.size(); i++) {
            Registro chave = lista.get(i);
            int j = i - 1;

            // desloca para a direita os elementos maiores que a chave
            while (j >= 0 && comparar(lista.get(j), chave) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, chave);
        }
    }

    /**
     * Grava os registros em um arquivo de texto.
     *
     * @param caminho caminho do arquivo de destino
     * @param lista registros que serao gravados
     * @throws IOException se ocorrer um erro durante a escrita
     */
    public static void gravarArquivo(String caminho, List<Registro> lista) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminho))) {
            for (Registro r : lista) {
                bw.write(r.toString());
                bw.newLine();
            }
        }
    }

    /**
     * Executa a leitura, ordenação e gravação dos registros.
     *
     * @param args argumentos da linha de comando
     * @throws IOException se houver erro ao ler ou gravar os arquivos
     */
    public static void main(String[] args) throws IOException {
        List<Registro> lista = lerArquivo("Formula1_InsertionSort.txt");
        insertionSort(lista);

        for (Registro r : lista) {
            System.out.println(r);
        }

        gravarArquivo("tempos_ordenados.txt", lista);
    }
}
