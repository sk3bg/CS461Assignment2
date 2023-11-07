package assignment2Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import geneticalgorithm.GeneticConstants;
import geneticalgorithm.Population;


public class Main {
    public static void main(String[] args) throws IOException {
        Population population = new Population();

        for(int i = 0; i < GeneticConstants.NUMBER_OF_GENERATIONS; i++) {
            population.runGeneration();
            population.printBestIndividualInformation();
        }

        double previousAverageFitnessScore;
        do {
            previousAverageFitnessScore = population.getAverageFitness();
            population.runGeneration();
            population.printBestIndividualInformation();
        } while (population.getAverageFitness() > 1.01 * previousAverageFitnessScore);


        Files.writeString(Paths.get("schedule_output.txt"), population.getBestIndividual().toPrettyString());


    }
}