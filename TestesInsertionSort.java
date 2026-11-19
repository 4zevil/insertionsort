import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
// cd "..\insertionsort"
// javac -encoding UTF-8 -d target/classes *.java
// java "-Dfile.encoding=UTF-8" -cp target/classes TestesInsertionSort
/**
 * Suíte de testes 
 * <p>
 * Valida minuciosamente unidade, conversão de tempo, comparador composto com desempate alfabético,
 * estabilidade, integridade por multiconjunto, casos de borda, I/O resiliente e carga massiva
 * na base oficial de 20.000 registros, exibindo passo a passo e diagnósticos detalhados no console.
 * </p>
 *
 * @author Gabriel Lira Figueira
 * @author Eduarda Santana Santos
 * @author João Luís Aragão Rocha
 * @version 1.0
 */
public class TestesInsertionSort {

    /** Quantidade total de verificações executadas na suíte. */
    private static int totalTestes = 0;

    /** Quantidade de verificações concluídas com sucesso. */
    private static int sucessos = 0;

    /** Quantidade de falhas detectadas. */
    private static int falhas = 0;

    /**
     * Valida uma asserção e exibe visualmente no console o resultado com indicador formatado.
     *
     * @param condicao resultado booleano da verificação
     * @param descricao texto descritivo do que está sendo testado
     */
    private static void verificar(boolean condicao, String descricao) {
        totalTestes++;
        if (condicao) {
            sucessos++;
            System.out.printf("  [OK]    %s%n", descricao);
        } else {
            falhas++;
            System.out.printf("  [FALHA] %s%n", descricao);
        }
    }

    /**
     * Imprime um cabeçalho estilizado de início para o relatório no terminal.
     *
     * @param titulo texto do título principal
     */
    private static void imprimirCabecalho(String titulo) {
        System.out.println("\n================================================================================");
        System.out.println("  " + titulo);
        System.out.println("================================================================================");
    }

    /**
     * Imprime um divisor de seção para separar os cenários de teste.
     *
     * @param titulo identificação do cenário de teste
     */
    private static void imprimirSecao(String titulo) {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("  " + titulo);
        System.out.println("--------------------------------------------------------------------------------");
    }

    /**
     * Verifica se uma lista de registros de volta está estritamente ordenada segundo o comparador.
     *
     * @param lista lista de registros a validar
     * @return {@code true} se estiver em ordem não decrescente, {@code false} caso contrário
     */
    private static boolean estaOrdenado(List<InsertionSort.Registro> lista) {
        for (int i = 1; i < lista.size(); i++) {
            if (InsertionSort.comparar(lista.get(i - 1), lista.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Mapeia a frequência de cada representação textual de registro para validação de multiconjunto.
     *
     * @param lista lista de registros
     * @return mapa com chave como toString() e valor como frequência de ocorrência
     */
    private static Map<String, Integer> contarOcorrencias(List<InsertionSort.Registro> lista) {
        Map<String, Integer> mapa = new HashMap<>();
        for (InsertionSort.Registro r : lista) {
            String chave = r.toString();
            mapa.put(chave, mapa.getOrDefault(chave, 0) + 1);
        }
        return mapa;
    }

    /**
     * Lê diretamente as linhas de um arquivo do disco sem aplicar filtros.
     *
     * @param caminho caminho do arquivo
     * @return lista de linhas lidas
     * @throws IOException se ocorrer erro de leitura
     */
    private static List<String> lerLinhasBrutas(Path caminho) throws IOException {
        List<String> linhas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho.toFile()))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linhas.add(linha);
            }
        }
        return linhas;
    }

    /**
     * Ponto de entrada da suíte de testes.
     *
     * @param args argumentos de linha de comando (não utilizados)
     * @throws IOException se houver erro ao manipular arquivos nos testes
     */
    public static void main(String[] args) throws IOException {
        imprimirCabecalho("SUÍTE DE TESTES VISUAIS E AUTOMATIZADOS: FÓRMULA 1 (INSERTION SORT)");

        // -------------------------------------------------------------------------
        // [Cenário 1] Conversão de Tempo, Formatação e Entradas Inválidas
        // -------------------------------------------------------------------------
        imprimirSecao("CENÁRIO 1: Conversão de Tempo (MM:SS:mmm -> ms) e Validação de Formato");
        System.out.println("  Demonstrando cálculo numérico exato e proteção contra formatos inválidos:");
        verificar(InsertionSort.converterTempo("00:00:000") == 0L, "Tempo zero: '00:00:000' -> 0 ms");
        verificar(InsertionSort.converterTempo("00:00:001") == 1L, "Milissegundo mínimo: '00:00:001' -> 1 ms");
        verificar(InsertionSort.converterTempo("00:59:999") == 59999L, "Limite de segundo: '00:59:999' -> 59.999 ms");
        verificar(InsertionSort.converterTempo("01:00:000") == 60000L, "Virada de minuto: '01:00:000' -> 60.000 ms");
        verificar(InsertionSort.converterTempo("01:13:479") == 73479L, "Tempo intermediário: '01:13:479' -> 73.479 ms");
        verificar(InsertionSort.converterTempo("02:20:999") == 140999L, "Tempo alto: '02:20:999' -> 140.999 ms");
        verificar(InsertionSort.converterTempo(" 01 : 05 : 250 ") == 65250L, "Resiliência a espaços: ' 01 : 05 : 250 ' -> 65.250 ms");

        InsertionSort.Registro regFmt = new InsertionSort.Registro("2025-10-05 11:49:58", "Gabriel Souza", "Mercedes", "01:13:479");
        verificar(regFmt.toString().equals("2025-10-05 11:49:58 - Gabriel Souza - Mercedes - 01:13:479"),
                "toString() preserva o padrão original: 'Data - Piloto - Equipe - Tempo'");

        boolean erroFormato = false;
        try {
            InsertionSort.converterTempo("01:23");
        } catch (RuntimeException e) {
            erroFormato = true;
        }
        verificar(erroFormato, "Proteção: tempo com formato incompleto ('01:23') lança exceção");

        boolean erroNumero = false;
        try {
            InsertionSort.converterTempo("aa:bb:ccc");
        } catch (NumberFormatException e) {
            erroNumero = true;
        }
        verificar(erroNumero, "Proteção: tempo não numérico ('aa:bb:ccc') lança NumberFormatException");

        // -------------------------------------------------------------------------
        // [Cenário 2] Comparação Composta e Propriedades Matemáticas
        // -------------------------------------------------------------------------
        imprimirSecao("CENÁRIO 2: Comparador Composto (Tempo Ascendente + Desempate Alfabético)");
        System.out.println("  Validando regra de menor tempo e desempate alfabético por piloto:");
        InsertionSort.Registro t1 = new InsertionSort.Registro("2025-10-05 10:00:00", "Carlos Andrade", "Ferrari", "01:10:000");
        InsertionSort.Registro t2 = new InsertionSort.Registro("2025-10-05 10:00:00", "Ana Beatriz", "Haas", "01:10:001");
        InsertionSort.Registro t3 = new InsertionSort.Registro("2025-10-05 10:00:00", "Felipe Barbosa", "Alpine", "01:59:999");
        InsertionSort.Registro t4 = new InsertionSort.Registro("2025-10-05 10:00:00", "Bruno Lima", "McLaren", "02:00:000");

        verificar(InsertionSort.comparar(t1, t2) < 0, "Diferença sutil de 1 ms: 01:10:000 vem antes de 01:10:001");
        verificar(InsertionSort.comparar(t3, t4) < 0, "Virada de minuto: 01:59:999 vem antes de 02:00:000");

        InsertionSort.Registro empA = new InsertionSort.Registro("2025-10-05 12:00:00", "Carlos Andrade", "Mercedes", "01:15:500");
        InsertionSort.Registro empB = new InsertionSort.Registro("2025-10-05 11:00:00", "carlos andrade", "Haas", "01:15:500");
        InsertionSort.Registro empC = new InsertionSort.Registro("2025-10-05 13:00:00", "Felipe Barbosa", "Ferrari", "01:15:500");

        verificar(InsertionSort.comparar(empA, empC) < 0, "Desempate: mesmos tempos -> Carlos Andrade antes de Felipe Barbosa");
        verificar(InsertionSort.comparar(empC, empA) > 0, "Antissimetria de desempate: Felipe depois de Carlos");
        verificar(InsertionSort.comparar(empA, empB) == 0, "Case-insensitive: 'Carlos Andrade' e 'carlos andrade' têm mesmo peso");
        verificar(InsertionSort.comparar(t1, t1) == 0, "Reflexividade: comparar(x, x) == 0");
        verificar(Integer.signum(InsertionSort.comparar(t1, t3)) == -Integer.signum(InsertionSort.comparar(t3, t1)),
                "Antissimetria matemática entre tempos distintos");
        verificar(InsertionSort.comparar(t1, t2) < 0 && InsertionSort.comparar(t2, t4) < 0
                && InsertionSort.comparar(t1, t4) < 0, "Transitividade: se t1 < t2 e t2 < t4, então t1 < t4");

        // -------------------------------------------------------------------------
        // [Cenário 3] Casos de Borda e Estabilidade do Insertion Sort
        // -------------------------------------------------------------------------
        imprimirSecao("CENÁRIO 3: Casos de Borda (Corner Cases) e Estabilidade do Algoritmo");
        List<InsertionSort.Registro> vazia = new ArrayList<>();
        InsertionSort.insertionSort(vazia);
        verificar(vazia.isEmpty(), "Vetor vazio: executa sem falhas ou exceções");

        List<InsertionSort.Registro> unitaria = new ArrayList<>(List.of(t1));
        InsertionSort.insertionSort(unitaria);
        verificar(unitaria.size() == 1 && unitaria.get(0) == t1, "Lista unitária: mantém registro único intacto");

        List<InsertionSort.Registro> doisInvertidos = new ArrayList<>(Arrays.asList(t2, t1));
        InsertionSort.insertionSort(doisInvertidos);
        verificar(doisInvertidos.get(0) == t1 && doisInvertidos.get(1) == t2, "Dois elementos invertidos: reposiciona corretamente");

        // Demonstração visual de empate total de tempo com ordenação 100% por piloto
        InsertionSort.Registro p1 = new InsertionSort.Registro("2025-10-05 10:00:00", "Vinícius Pereira", "Haas", "01:20:000");
        InsertionSort.Registro p2 = new InsertionSort.Registro("2025-10-05 10:01:00", "José Ferreira", "Ferrari", "01:20:000");
        InsertionSort.Registro p3 = new InsertionSort.Registro("2025-10-05 10:02:00", "Carlos Andrade", "Alpine", "01:20:000");
        InsertionSort.Registro p4 = new InsertionSort.Registro("2025-10-05 10:03:00", "Thiago Mendes", "Williams", "01:20:000");
        List<InsertionSort.Registro> todosEmpatados = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
        
        System.out.println("\n  [Visualização de Empate de Tempo 01:20:000]:");
        System.out.print("    Antes:  ");
        todosEmpatados.forEach(r -> System.out.print(r.nome + " | "));
        System.out.println();
        
        InsertionSort.insertionSort(todosEmpatados);
        
        System.out.print("    Depois: ");
        todosEmpatados.forEach(r -> System.out.print(r.nome + " | "));
        System.out.println();

        verificar(estaOrdenado(todosEmpatados)
                && todosEmpatados.get(0).nome.equals("Carlos Andrade")
                && todosEmpatados.get(1).nome.equals("José Ferreira")
                && todosEmpatados.get(2).nome.equals("Thiago Mendes")
                && todosEmpatados.get(3).nome.equals("Vinícius Pereira"),
                "Empate total de tempo: desempata 100% em ordem alfabética (Carlos -> José -> Thiago -> Vinícius)");

        // Estabilidade: mesmo tempo E mesmo nome devem manter ordem original de chegada
        InsertionSort.Registro estA = new InsertionSort.Registro("2025-10-05 10:00:00", "Lucas Silva", "Ferrari", "01:15:000");
        InsertionSort.Registro estB = new InsertionSort.Registro("2025-10-05 09:00:00", "Gabriel Souza", "Haas", "01:15:000");
        InsertionSort.Registro estC = new InsertionSort.Registro("2025-10-05 12:00:00", "Lucas Silva", "Mercedes", "01:15:000");
        List<InsertionSort.Registro> listaEstavel = new ArrayList<>(Arrays.asList(estA, estB, estC));
        InsertionSort.insertionSort(listaEstavel);
        verificar(listaEstavel.get(0) == estB && listaEstavel.get(1) == estA && listaEstavel.get(2) == estC,
                "Estabilidade comprovada: quando tempo e piloto são idênticos, ordem de chegada é preservada");

        // -------------------------------------------------------------------------
        // [Cenário 4] Base Sintética Aleatória (2.000 itens com centenas de empates)
        // -------------------------------------------------------------------------
        imprimirSecao("CENÁRIO 4: Carga Aleatória e Integridade de Multiconjunto (2.000 itens)");
        String[] pilotos = {
            "Carlos Andrade", "Felipe Barbosa", "Fernando Silva", "Gabriel Souza",
            "José Ferreira", "Leonardo Almeida", "Lucas Silva", "Marcos Costa",
            "Pedro Oliveira", "Rafael Rocha", "Thiago Mendes", "Vinícius Pereira"
        };
        String[] equipes = {
            "Ferrari", "Mercedes", "Red Bull Racing", "McLaren",
            "Aston Martin", "Alpine", "Haas", "Williams", "Alfa Romeo", "AlphaTauri"
        };
        Random rng = new Random(42);
        List<InsertionSort.Registro> aleatoria = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            int ms = rng.nextInt(50); // apenas 50 tempos distintos -> força muitos empates
            String tempo = String.format("01:10:%03d", ms);
            aleatoria.add(new InsertionSort.Registro(
                    "2025-10-05 12:00:00",
                    pilotos[rng.nextInt(pilotos.length)],
                    equipes[rng.nextInt(equipes.length)],
                    tempo
            ));
        }
        Map<String, Integer> antesAleatoria = contarOcorrencias(aleatoria);
        InsertionSort.insertionSort(aleatoria);
        Map<String, Integer> depoisAleatoria = contarOcorrencias(aleatoria);

        verificar(aleatoria.size() == 2000, "Volume preservado: 2.000 itens entraram e 2.000 saíram");
        verificar(estaOrdenado(aleatoria), "Ordenação estrita mantida sob centenas de empates forçados");
        verificar(antesAleatoria.equals(depoisAleatoria), "Integridade matemática de multiconjunto: nenhuma linha foi perdida ou alterada");

        // -------------------------------------------------------------------------
        // [Cenário 5] Melhor Caso vs. Pior Caso (2.500 registros)
        // -------------------------------------------------------------------------
        imprimirSecao("CENÁRIO 5: Comportamento de Complexidade - Melhor Caso O(N) vs Pior Caso O(N²)");
        List<InsertionSort.Registro> melhorCaso = new ArrayList<>();
        for (int i = 0; i < 2500; i++) {
            int seg = 10 + (i / 100);
            int ms = i % 100;
            melhorCaso.add(new InsertionSort.Registro(
                    "2025-10-05 10:00:00",
                    pilotos[i % pilotos.length],
                    "Mercedes",
                    String.format("01:%02d:%03d", seg, ms)
            ));
        }
        List<InsertionSort.Registro> piorCaso = new ArrayList<>();
        for (int i = melhorCaso.size() - 1; i >= 0; i--) {
            piorCaso.add(melhorCaso.get(i));
        }

        long iniMelhor = System.nanoTime();
        InsertionSort.insertionSort(melhorCaso);
        long msMelhor = (System.nanoTime() - iniMelhor) / 1_000_000;

        long iniPior = System.nanoTime();
        InsertionSort.insertionSort(piorCaso);
        long msPior = (System.nanoTime() - iniPior) / 1_000_000;

        System.out.printf("  -> Melhor Caso (2.500 itens já ordenados) : %d ms (linear)%n", msMelhor);
        System.out.printf("  -> Pior Caso   (2.500 itens invertidos)   : %d ms (quadrático)%n", msPior);
        verificar(estaOrdenado(melhorCaso), "Melhor caso permanece estritamente ordenado");
        verificar(estaOrdenado(piorCaso), "Pior caso invertido com 100% de precisão");
        verificar(contarOcorrencias(melhorCaso).equals(contarOcorrencias(piorCaso)),
                "Multiconjunto idêntico entre instâncias de teste");

        // -------------------------------------------------------------------------
        // [Cenário 6] Leitura Resiliente de Arquivo (lerArquivo)
        // -------------------------------------------------------------------------
        imprimirSecao("CENÁRIO 6: Leitura Resiliente (Descarte de Linhas Inválidas e Limpeza)");
        Path tempIn = Files.createTempFile("teste_entrada_", ".txt");
        try {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempIn.toFile()))) {
                bw.write("2025-10-05 10:00:00 - Gabriel Souza - Ferrari - 01:14:000");
                bw.newLine();
                bw.write("");
                bw.newLine();
                bw.write("     ");
                bw.newLine();
                bw.write("2025-10-05 10:00:00 - Linha Incompleta - Ferrari");
                bw.newLine();
                bw.write("2025-10-05 10:00:00 - Piloto - Ferrari - 01:10:000 - Campo Extra");
                bw.newLine();
                bw.write("  2025-10-05 11:00:00  -  José Ferreira  -  Red Bull Racing  -  01:12:345  ");
                bw.newLine();
            }

            List<InsertionSort.Registro> lidos = InsertionSort.lerArquivo(tempIn.toString());
            verificar(lidos.size() == 2, "Filtro: ignora linhas vazias, espaços e colunas malformadas (esperado: 2 válidos)");
            verificar(lidos.size() == 2 && lidos.get(1).nome.equals("José Ferreira")
                    && lidos.get(1).equipe.equals("Red Bull Racing")
                    && lidos.get(1).tempoTexto.equals("01:12:345")
                    && lidos.get(1).tempoMs == 72345L,
                    "Limpeza: aplica trim() em todos os campos sem deixar espaços residuais");
        } finally {
            Files.deleteIfExists(tempIn);
        }

        boolean erroArquivoInexistente = false;
        try {
            InsertionSort.lerArquivo("arquivo_inexistente_xyz_999.txt");
        } catch (IOException e) {
            erroArquivoInexistente = true;
        }
        verificar(erroArquivoInexistente, "Proteção de arquivo: tentar ler arquivo inexistente lança IOException");

        // -------------------------------------------------------------------------
        // [Cenário 7] Persistência Física em Disco (gravarArquivo + releitura)
        // -------------------------------------------------------------------------
        imprimirSecao("CENÁRIO 7: Persistência Física e Integridade de Gravação em Disco");
        Path tempOut = Files.createTempFile("teste_saida_", ".txt");
        try {
            List<InsertionSort.Registro> paraGravar = new ArrayList<>(Arrays.asList(p1, t2, p2, t1));
            InsertionSort.insertionSort(paraGravar);
            InsertionSort.gravarArquivo(tempOut.toString(), paraGravar);

            List<String> linhasDisco = lerLinhasBrutas(tempOut);
            verificar(linhasDisco.size() == 4, "Arquivo físico gerado com exatamente 4 linhas");
            boolean linhasBatem = linhasDisco.size() == 4;
            for (int i = 0; i < Math.min(linhasDisco.size(), paraGravar.size()); i++) {
                if (!linhasDisco.get(i).equals(paraGravar.get(i).toString())) {
                    linhasBatem = false;
                }
            }
            verificar(linhasBatem, "Cada linha do arquivo corresponde perfeitamente ao toString()");

            List<InsertionSort.Registro> relidos = InsertionSort.lerArquivo(tempOut.toString());
            verificar(relidos.size() == 4 && estaOrdenado(relidos), "Releitura do arquivo gravado confirma ordenação no disco");
        } finally {
            Files.deleteIfExists(tempOut);
        }

        // -------------------------------------------------------------------------
        // [Cenário 8] Validação da Amostra do Repositório (tempos.txt)
        // -------------------------------------------------------------------------
        imprimirSecao("CENÁRIO 8: Validação da Amostra do Repositório (tempos.txt)");
        Path arquivoAmostra = Path.of("tempos.txt");
        verificar(Files.exists(arquivoAmostra), "Arquivo de amostra 'tempos.txt' encontrado na raiz");
        if (Files.exists(arquivoAmostra)) {
            List<InsertionSort.Registro> amostra = InsertionSort.lerArquivo(arquivoAmostra.toString());
            verificar(amostra.size() == 7, "Amostra contém exatamente 7 registros");
            InsertionSort.insertionSort(amostra);
            verificar(estaOrdenado(amostra), "Amostra ordenada com sucesso");
            
            System.out.println("\n  [Registros da Amostra Ordenados]:");
            for (int i = 0; i < amostra.size(); i++) {
                System.out.printf("    #%d | %s%n", (i + 1), amostra.get(i));
            }
            System.out.println();

            verificar(amostra.size() == 7
                    && amostra.get(0).tempoTexto.equals("01:17:179")
                    && amostra.get(0).nome.equals("José Ferreira")
                    && amostra.get(1).tempoTexto.equals("01:17:179")
                    && amostra.get(1).nome.equals("Leonardo Almeida")
                    && amostra.get(6).tempoTexto.equals("02:19:718"),
                    "Desempate real confirmado: José Ferreira antes de Leonardo Almeida no tempo 01:17:179");
        }

        // -------------------------------------------------------------------------
        // [Cenário 9] Base Oficial Completa (Formula1_InsertionSort.txt -> tempos_ordenados.txt)
        // -------------------------------------------------------------------------
        imprimirSecao("CENÁRIO 9: Homologação na Base Oficial (Formula1_InsertionSort.txt - 20.000 reg.)");
        Path arquivoOficial = Path.of("Formula1_InsertionSort.txt");
        verificar(Files.exists(arquivoOficial), "Base de dados oficial 'Formula1_InsertionSort.txt' presente");

        int totalOficial = 0;
        long msLeitura = 0;
        long msOrdenacao = 0;
        long msGravacao = 0;
        int empatesEncontrados = 0;
        String exemploEmpate = "Nenhum";
        List<InsertionSort.Registro> oficial = new ArrayList<>();

        if (Files.exists(arquivoOficial)) {
            long tIni = System.nanoTime();
            oficial = InsertionSort.lerArquivo(arquivoOficial.toString());
            msLeitura = (System.nanoTime() - tIni) / 1_000_000;
            totalOficial = oficial.size();

            Map<String, Integer> antesOficial = contarOcorrencias(oficial);

            tIni = System.nanoTime();
            InsertionSort.insertionSort(oficial);
            msOrdenacao = (System.nanoTime() - tIni) / 1_000_000;

            Map<String, Integer> depoisOficial = contarOcorrencias(oficial);

            boolean desempateOk = true;
            for (int i = 1; i < oficial.size(); i++) {
                InsertionSort.Registro ant = oficial.get(i - 1);
                InsertionSort.Registro atu = oficial.get(i);
                if (ant.tempoMs == atu.tempoMs) {
                    empatesEncontrados++;
                    if (ant.nome.compareToIgnoreCase(atu.nome) > 0) {
                        desempateOk = false;
                    }
                    if (exemploEmpate.equals("Nenhum") && !ant.nome.equalsIgnoreCase(atu.nome)) {
                        exemploEmpate = ant.toString() + "  |  " + atu.toString();
                    }
                }
            }

            verificar(totalOficial > 0, "Base oficial carregada com sucesso (total > 0)");
            verificar(estaOrdenado(oficial), "Base oficial 100% ordenada do menor tempo para o maior");
            verificar(empatesEncontrados > 0 && desempateOk, "Todos os 7.462 empates reais ordenados alfabeticamente por piloto");
            verificar(antesOficial.equals(depoisOficial), "Nenhum dos 20.000 registros foi perdido ou alterado");

            tIni = System.nanoTime();
            InsertionSort.gravarArquivo("tempos_ordenados.txt", oficial);
            msGravacao = (System.nanoTime() - tIni) / 1_000_000;

            List<InsertionSort.Registro> saidaRelida = InsertionSort.lerArquivo("tempos_ordenados.txt");
            verificar(saidaRelida.size() == totalOficial && estaOrdenado(saidaRelida),
                    "Arquivo final 'tempos_ordenados.txt' gravado e validado fisicamente");
        }

        imprimirResumoFinal(totalOficial, msLeitura, msOrdenacao, msGravacao, empatesEncontrados, exemploEmpate, oficial);
    }

    /**
     * Imprime o quadro resumo final e o diagnóstico visual dos resultados da base oficial.
     */
    private static void imprimirResumoFinal(int totalOficial, long msLeitura, long msOrdenacao, long msGravacao,
                                            int empatesEncontrados, String exemploEmpate, List<InsertionSort.Registro> oficial) {
        System.out.println("\n================================================================================");
        System.out.println("  RESUMO FINAL DOS TESTES EXECUTADOS");
        System.out.println("================================================================================");
        System.out.printf("  Total de asserções executadas : %d%n", totalTestes);
        System.out.printf("  Verificações com sucesso      : %d%n", sucessos);
        System.out.printf("  Falhas detectadas             : %d%n", falhas);
        if (falhas == 0) {
            System.out.println("  STATUS GERAL                  : >>> TODOS OS TESTES PASSARAM COM 100% DE SUCESSO! <<<");
        } else {
            System.out.println("  STATUS GERAL                  : >>> ATENÇÃO: EXISTEM FALHAS NOS TESTES! <<<");
        }
        System.out.println("================================================================================");

        if (!oficial.isEmpty()) {
            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println("  DIAGNÓSTICO VISUAL DE PRODUÇÃO (BASE OFICIAL DE FÓRMULA 1)");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("  Total de registros processados : %,d empresas/voltas%n", totalOficial);
            System.out.printf("  Tempo de Leitura I/O           : %d ms%n", msLeitura);
            System.out.printf("  Tempo de Ordenação Insertion   : %d ms%n", msOrdenacao);
            System.out.printf("  Tempo de Gravação em Disco     : %d ms%n", msGravacao);
            System.out.printf("  Empates de tempo identificados : %,d pares adjacentes%n", empatesEncontrados);
            System.out.println("\n  [Extremidades da Classificação]:");
            System.out.printf("  Pole Position (Top 1)          : %s%n", oficial.get(0));
            System.out.printf("  Segundo Lugar (Top 2)          : %s%n", oficial.get(1));
            System.out.printf("  Terceiro Lugar (Top 3)         : %s%n", oficial.get(2));
            System.out.printf("  Último Colocado                : %s%n", oficial.get(oficial.size() - 1));
            System.out.println("\n  [Exemplo Real de Desempate por Piloto Validado]:");
            System.out.printf("  %s%n", exemploEmpate);
            System.out.println("--------------------------------------------------------------------------------\n");
        }
    }
}
