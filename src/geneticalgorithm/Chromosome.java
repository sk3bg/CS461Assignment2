package geneticalgorithm;

import java.util.*;
import java.util.stream.Stream;

import records.DataListManager;
import records.RoomRecord;

//Genetic representation of a schedule
public final class Chromosome implements Probability {
    private final List<Gene> geneList;
    private double fitness = -1;
    private double matingProbability = -1; //start off with no possibility to mate
    private final SplittableRandom random;

    public Chromosome(List<Gene> geneList) {
        this.random = new SplittableRandom();
        this.geneList = geneList;
    }

    public void attemptMutation(double mutationRate) {
        for (Gene gene : geneList) {
            if (GeneticUtils.mutationOccurred(mutationRate)) {
                gene.mutateFacilitator();
            }
            if (GeneticUtils.mutationOccurred(mutationRate)) {
                gene.mutateRoom();
            }
            if (GeneticUtils.mutationOccurred(mutationRate)) {
                gene.mutateTime();
            }
        }
    }

    // 1 2 3 4 5 6
    // a b c d e f
    //
    // 1 2 3 4 e f
    // a b c d 5 6
    public List<Chromosome> crossoverWith(Chromosome other) {
        int indexOfDividingLine = random.nextInt(1, GeneticConstants.NUMBER_OF_GENES - 1);

        List<Gene> firstHalf1 = this.splitGenes(0, indexOfDividingLine);
        List<Gene> secondHalf1 = other.splitGenes(indexOfDividingLine, GeneticConstants.NUMBER_OF_GENES);
        List<Gene> firstHalf2 = other.splitGenes(0, indexOfDividingLine);
        List<Gene> secondHalf2 = this.splitGenes(indexOfDividingLine, GeneticConstants.NUMBER_OF_GENES);

        Chromosome chromosome1 =
                new Chromosome(Stream.concat(firstHalf1.stream(), secondHalf1.stream()).toList());
        Chromosome chromosome2 =
                new Chromosome(Stream.concat(firstHalf2.stream(), secondHalf2.stream()).toList());

        return List.of(chromosome1, chromosome2);
    }

    private List<Gene> splitGenes(int start, int end) {
        return geneList.subList(start, end)
                .stream()
                .map(Gene::copy)
                .toList();
    }

    public void attemptMutation() {
        attemptMutation(GeneticConstants.MUTATION_RATE);
    }

    public void calculateFitness() {
        double fitnessScore = geneList
                .stream()
                .mapToDouble(gene -> GeneticUtils.calculateFitnessOfGene(gene, this))
                .sum();

        /*
        The 2 sections of SLA 101 are more than 4 hours apart: + 0.5
        Both sections of SLA 101 are in the same time slot: -0.5
        The 2 sections of SLA 191 are more than 4 hours apart: + 0.5
        Both sections of SLA 191 are in the same time slot: -0.5
         */

        //The 2 sections of SLA 101 are more than 4 hours apart: + 0.5
        final Gene SLA100A = GeneticUtils.getGeneByName("SLA100A", this);
        final Gene SLA100B = GeneticUtils.getGeneByName("SLA100B", this);
        if (SLA100A.getTime().differenceBetween(SLA100B.getTime()) > 4) {
            fitnessScore += 0.5;
        }

        //Both sections of SLA 101 are in the same time slot: -0.5
        else if (SLA100A.getTime().equals(SLA100B.getTime())) {
            fitnessScore -= 0.5;
        }

        //The 2 sections of SLA 191 are more than 4 hours apart: + 0.5
        Gene SLA191A = GeneticUtils.getGeneByName("SLA191A", this);
        Gene SLA191B = GeneticUtils.getGeneByName("SLA191B", this);
        if (SLA191A.getTime().differenceBetween(SLA191B.getTime()) > 4) {
            fitnessScore += 0.5;
        }

        //Both sections of SLA 191 are in the same time slot: -0.5
        else if (SLA191A.getTime().equals(SLA191B.getTime())) {
            fitnessScore -= 0.5;
        }

        /*
        A section of SLA 191 and a section of SLA 101 are overseen in consecutive time slots (e.g., 10 AM & 11 AM): +0.5
        In this case only (consecutive time slots), one of the activities is in Roman or Beach, and the other isn’t: -0.4
        It’s fine if neither is in one of those buildings, of activity; we just want to avoid having consecutive activities being widely separated.
         */
        //4 possible combos: SLA100A with SLA191A, SLA100B with SLA191A, SLA100A with SLA191B, SLA100B with SLA191B
        final RoomRecord roman201 = DataListManager.getRoomByName("Roman 201");
        final RoomRecord beach201 = DataListManager.getRoomByName("Beach 201");

        if (SLA100A.getTime().differenceBetween(SLA191A.getTime()) == 1) {
            fitnessScore += 0.5;
            if (GeneticUtils.isRoomAvoidanceMet(SLA100A, SLA191A, roman201, beach201)) {
                fitnessScore -= 0.4;
            }
        } else if (SLA100B.getTime().differenceBetween(SLA191A.getTime()) == 1) {
            fitnessScore += 0.5;
            if (GeneticUtils.isRoomAvoidanceMet(SLA100B, SLA191A, roman201, beach201)) {
                fitnessScore -= 0.4;
            }
        } else if (SLA100A.getTime().differenceBetween(SLA191B.getTime()) == 1) {
            fitnessScore += 0.5;
            if (GeneticUtils.isRoomAvoidanceMet(SLA100A, SLA191B, roman201, beach201)) {
                fitnessScore -= 0.4;
            }
        } else if (SLA100B.getTime().differenceBetween(SLA191B.getTime()) == 1) {
            fitnessScore += 0.5;
            if (GeneticUtils.isRoomAvoidanceMet(SLA100B, SLA191B, roman201, beach201)) {
                fitnessScore -= 0.4;
            }
        }


        //A section of SLA 191 and a section of SLA 101 are taught separated by 1 hour (e.g., 10 AM & 12:00 Noon): + 0.25
        else if (SLA100A.getTime().differenceBetween(SLA191A.getTime()) > 1) {
            fitnessScore += 0.25;
        } else if (SLA100B.getTime().differenceBetween(SLA191A.getTime()) > 1) {
            fitnessScore += 0.25;
        } else if (SLA100A.getTime().differenceBetween(SLA191B.getTime()) > 1) {
            fitnessScore += 0.25;
        } else if (SLA100B.getTime().differenceBetween(SLA191B.getTime()) > 1) {
            fitnessScore += 0.25;
        }

        //A section of SLA 191 and a section of SLA 101 are taught in the same time slot: -0.25
        else if (SLA100A.getTime().equals(SLA191A.getTime())) {
            fitnessScore -= 0.25;
        } else if (SLA100B.getTime().equals(SLA191A.getTime())) {
            fitnessScore -= 0.25;
        } else if (SLA100A.getTime().equals(SLA191B.getTime())) {
            fitnessScore -= 0.25;
        } else if (SLA100B.getTime().equals(SLA191B.getTime())) {
            fitnessScore -= 0.25;
        }

        //If any facilitator scheduled for consecutive time slots: Same rules as for SLA 191 and SLA 101 in consecutive time slots—see below.
        Map<String, List<Gene>> facilitatorToActivityMap = new HashMap<>();
        for (Gene gene : geneList) {
            if (!facilitatorToActivityMap.containsKey(gene.getFacilitator())) {
                facilitatorToActivityMap.put(gene.getFacilitator(), new ArrayList<>(List.of(gene)));
            } else {
                List<Gene> genes = facilitatorToActivityMap.get(gene.getFacilitator());
                genes.add(gene);
                facilitatorToActivityMap.put(gene.getFacilitator(), genes);
            }
        }
        List<Gene> consecutiveGenes = Collections.emptyList();

        for (var entry : facilitatorToActivityMap.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); i++) {
                if (GeneticUtils.areAnyTimesConsecutive(entry.getValue())) {
                    consecutiveGenes = GeneticUtils.getConsecutiveTimes(entry.getValue());
                }
            }
        }

        if (!consecutiveGenes.isEmpty()) {
            fitnessScore += 0.5;
            if (GeneticUtils.isRoomAvoidanceMet(
                    consecutiveGenes.get(0),
                    consecutiveGenes.get(1),
                    roman201,
                    beach201)) {
                fitnessScore -= 0.4;
            }
        }
        fitness = fitnessScore;
    }

    @Override
    public String toString() {
        return "Chromosome{" +
                "geneList=" + geneList +
                '}';
    }

    public String toPrettyString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Schedule = {")
                .append("\n");
        for (Gene gene : geneList) {
            stringBuilder.append("\t").append(gene.toPrettyString()).append("\n");
        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    public List<Gene> geneList() {
        return geneList;
    }

    public void setMatingProbability(double matingProbability) {
        this.matingProbability = matingProbability;
    }

    public double getFitness() {
        return fitness;
    }

    @Override
    public double getProbability() {
        return matingProbability;
    }
}
