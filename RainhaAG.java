import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RainhaAG {
    static final int total_populacao = 100;
    static final double mutacao = 0.3;
    static final double cruzamento = 0.9;
    static final int totalGeracao = 1000;

    public static void main(String[] args) throws IOException{
        long msI = System.currentTimeMillis();
        int n = 30;//Mude aqui para determinar a quantidade de rainhas
        int execucao = 400;
        List<Resultado> resultados = new ArrayList<>();

        for(int i=0; i< execucao; i++){
            Resultado resultado = algoGene(n);
            resultados.add(resultado);
        }
        gerarCSV(resultados, "resultado.csv");
        long msF = System.currentTimeMillis();
        System.out.println(msF - msI + "ms");
    }

    static Resultado algoGene(int n){
        List<int[]> populacao = initPopulation(n);
        int bestIndividuo[] = null;
        int bestFit = Integer.MAX_VALUE;

        for(int geracao= 0; geracao < totalGeracao; geracao++){
            List<int[]> newPopulation = new ArrayList<>();
            while(newPopulation.size() < total_populacao){
                int pai[] = selecionar(populacao);
                int pai1[] = selecionar(populacao);
                
                int filhos[][] = cruzar(pai, pai1, n);
                newPopulation.add(mutar(filhos[0], n));
                newPopulation.add(mutar(filhos[1], n));
            }

            populacao = newPopulation;
            
            for(int individuo[]:populacao){
                int fit = fit(individuo);
                if(fit < bestFit){
                    bestFit = fit;
                    bestIndividuo = individuo;
                }
                if(fit ==0){
                    return new Resultado(true, bestIndividuo, bestFit,  meanFit(populacao));
                }
            }
        }

        return new Resultado(false, bestIndividuo, bestFit, meanFit(populacao));
    }

    public static List<int[]> initPopulation(int n) {
        List<int[]> populacao = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < total_populacao; i++) {
            int individuo[] = new int[n];
            for (int j = 0; j < n; j++) {
                individuo[j] = rand.nextInt(n);
            }
            populacao.add(individuo);
        }

        return populacao;
    }

    public static int[] selecionar(List<int[]> populaList){
        Random rand = new Random();
        int individuo[] = populaList.get(rand.nextInt(populaList.size()));
        int individuo1[] =  populaList.get(rand.nextInt(populaList.size()));
        return fit(individuo) < fit(individuo1) ? individuo : individuo1;
    }


    public static int[] mutar(int[] individuo, int n) {
        Random rand = new Random();
        if (Math.random() < mutacao) {
            int posicao = rand.nextInt(n);
            individuo[posicao] = rand.nextInt(n);
        }
        return individuo;
    }

    public static int[][] cruzar(int[] pai, int[] pai1, int n) {
        Random rand = new Random();
        int divisao = rand.nextInt(n);
        int filho[] = new int[n];
        int filho1[] = new int[n];

        System.arraycopy(pai, 0, filho, 0, divisao);
        System.arraycopy(pai1, divisao, filho, divisao, n - divisao);
        System.arraycopy(pai1, 0, filho1, 0, divisao);
        System.arraycopy(pai, divisao, filho1, divisao, n - divisao);

        return new int[][] { filho, filho1 };
    }

    public static int fit(int[] individuo) {
        int conflitos = 0;
        for(int i= 0; i<individuo.length;i++){
            for(int j= i+1; j<individuo.length; j++){
                if(individuo[i]==individuo[j]|| Math.abs(individuo[i] - individuo[j]) == Math.abs(i-j)){
                    conflitos ++;
                }
            }
        }
        return conflitos;
    }

    public static double meanFit(List<int[]> populacao){
        int sumFit = 0;
        for(int[] individuo : populacao){
            sumFit += fit(individuo);
        }
        return sumFit / (double) populacao.size();
    }


    public static void gerarCSV(List<Resultado> resultados, String arquivo) throws IOException {
        FileWriter escritor = new FileWriter(arquivo);

        escritor.write("Nº Execução,Encontrado, MelhorFit, MeadiaFit\n");
        int execucao = 1;
        for (Resultado resultado : resultados) {
            escritor.write(execucao + "," +
                    resultado.encontrou + "," +
                    resultado.melhorFit + "," +
                    String.format("%.2f", resultado.meanFit) + "\n");
            execucao++;
        }
        escritor.close();
        System.out.println("Resolução salvo em: " + arquivo);
    }
}

class Resultado {
    boolean encontrou;
    int[] melhorIndividuo;
    int melhorFit;
    double meanFit;

    Resultado(boolean encontrou, int[] melhorIndividuo, int melhorFit, double meanFit) {
        this.encontrou = encontrou;
        this.melhorIndividuo = melhorIndividuo;
        this.melhorFit = melhorFit;
        this.meanFit = meanFit;
    }

}