import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertionSort {

    static class Registro {
        String dataHora;
        String nome;       
        String equipe;
        String tempoTexto;  
        long tempoMs;     
        
        Registro(String dataHora, String nome, String equipe, String tempoTexto) {
            this.dataHora = dataHora;
            this.nome = nome;
            this.equipe = equipe;
            this.tempoTexto = tempoTexto;
            this.tempoMs = converterTempo(tempoTexto);
        }

        @Override
        public String toString() {
            return dataHora + " - " + nome + " - " + equipe + " - " + tempoTexto;
        }
    }

    // "01:23:456" -> minutos*60000 + segundos*1000 + milissegundos
    static long converterTempo(String tempo) {
        String[] p = tempo.split(":");
        long min = Long.parseLong(p[0].trim());
        long seg = Long.parseLong(p[1].trim());
        long ms  = Long.parseLong(p[2].trim());
        return min * 60000 + seg * 1000 + ms;
    }

    // Retorna > 0 se a deve vir depois de b
    static int comparar(Registro a, Registro b) {
        if (a.tempoMs != b.tempoMs) {
            return Long.compare(a.tempoMs, b.tempoMs);       // menor tempo primeiro
        }
        return a.nome.compareToIgnoreCase(b.nome);           // empate: ordem alfabética
    }

    static List<Registro> lerArquivo(String caminho) throws IOException {
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

    static void insertionSort(List<Registro> lista) {
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

    static void gravarArquivo(String caminho, List<Registro> lista) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminho))) {
            for (Registro r : lista) {
                bw.write(r.toString());
                bw.newLine();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        List<Registro> lista = lerArquivo("Formula1_InsertionSort.txt");
        insertionSort(lista);

        for (Registro r : lista) {
            System.out.println(r);
        }

        gravarArquivo("tempos_ordenados.txt", lista);
    }
}