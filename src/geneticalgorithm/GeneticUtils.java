package geneticalgorithm;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import records.ActivityRecord;
import records.RoomRecord;
import records.DataListManager;
import records.TimeRecord;

public class GeneticUtils {

    private static final Random random = new Random();

    public static double calculateFitnessOfGene(Gene targetGene, Chromosome chromosome) {
        double fitnessScore = 0;
        List<Gene> remainingGenes = getRemainingGenes(targetGene, chromosome);

        //Activity is scheduled at the same time in the same room as another of the activities: -0.5
        if(remainingGenes.stream().anyMatch(gene -> isActivitySamePlaceAndTime(gene, targetGene))) {
            fitnessScore -= 0.5;
        }

        /*
        Room size:
	    Activities is in a room too small for its expected enrollment: -0.5
	    Activities is in a room with capacity > 3 times expected enrollment: -0.2
	    Activities is in a room with capacity > 6 times expected enrollment: -0.4
	    Otherwise + 0.3
         */
        if(targetGene.getActivity().expectedEnrollment() > targetGene.getRoom().capacity()) {
            fitnessScore -= 0.5;
        }
        else if(targetGene.getRoom().capacity() > 6 * targetGene.getActivity().expectedEnrollment()) {
            fitnessScore -= 0.4;
        }
        else if(targetGene.getRoom().capacity() > 3 * targetGene.getActivity().expectedEnrollment()) {
            fitnessScore -= 0.2;
        }
        else {
            fitnessScore += 0.3;
        }

        /*
        Activities are overseen by a preferred facilitator: + 0.5
        Activities is overseen by another facilitator listed for that activity: +0.2
        Activities is overseen by some other facilitator: -0.1
         */
        if(targetGene.getActivity().preferredFacilitators().contains(targetGene.getFacilitator())) {
            fitnessScore += 0.5;
        }
        else if(targetGene.getActivity().otherFacilitators().contains(targetGene.getFacilitator())) {
            fitnessScore += 0.2;
        }
        else {
            fitnessScore -= 0.1;
        }

        /*
        Facilitator load:
        Activity facilitator is scheduled for only 1 activity in this time slot: + 0.2
        Activity facilitator is scheduled for more than one activity at the same time: - 0.2

        Facilitator is scheduled to oversee more than 4 activities total: -0.5
        Facilitator is scheduled to oversee 1 or 2 activities*: -0.4
        Exception: Dr. Tyler is committee chair and has other demands on his time.
        No penalty if he’s only required to oversee < 2 activities.

        If any facilitator scheduled for consecutive time slots: Same rules as for SLA 191 and SLA 101 in consecutive time slots—see below.
         */

        // Activity facilitator is scheduled for only 1 activity in this time slot: + 0.2
        if(remainingGenes.stream().noneMatch(gene -> doActivitiesHaveSameTimeAndFacilitator(gene, targetGene))) {
            fitnessScore += 0.2;
        }
        // Activity facilitator is scheduled for more than one activity at the same time: - 0.2
        else  {
            fitnessScore -= 0.2;
        }

        long scheduledActivityCountForFacilitator = remainingGenes.stream()
                .filter(gene -> gene.getFacilitator().equals(targetGene.getFacilitator()))
                .count() + 1; //The plus one is because this is just remaining genes. Target gene also has one.

        // Facilitator is scheduled to oversee more than 4 activities total: -0.5
        if(scheduledActivityCountForFacilitator > 4) {
            fitnessScore -= 0.5;
        }
        // Facilitator is scheduled to oversee 1 or 2 activities*: -0.4
        // Exception: Dr. Tyler is committee chair and has other demands on his time.
        // No penalty if he’s only required to oversee < 2 activities.
        else if(scheduledActivityCountForFacilitator < 3 && !targetGene.getFacilitator().equals("Tyler")) {
            fitnessScore -= 0.4;
        }

        return fitnessScore;
    }

    public static List<Gene> getRemainingGenes(Gene targetGene, Chromosome chromosome) {
        return chromosome.geneList()
                .stream()
                .filter(gene -> targetGene != gene)
                .collect(Collectors.toList());
    }

    public static boolean areAnyTimesConsecutive(List<Gene> geneList) {
        return IntStream
                .range(0, geneList.size())
                .anyMatch(i -> IntStream.range(i + 1, geneList.size() - 1)
                        .anyMatch(j -> geneList.get(i).getTime().differenceBetween(geneList.get(j).getTime()) == 1)
                );
    }

    public static List<Gene> getConsecutiveTimes(List<Gene> geneList) {
        for(int i = 0; i < geneList.size(); i++) {
            for(int j = i + 1; j < geneList.size() - 1; j++) {
                if(geneList.get(i).getTime().differenceBetween(geneList.get(j).getTime()) == 1) {
                    return List.of(geneList.get(i), geneList.get(j));
                }
            }
        }
        return Collections.emptyList();
    }

    public static boolean mutationOccurred(double mutationRate) {
        return (random.nextInt(1, (int)(1.0 / mutationRate))) == 1;
    }

    public static Gene createRandomGene(ActivityRecord activity) {
        return new Gene(activity, getRandomRoom(), getRandomTime(), getRandomFacilitator());
    }

    public static Gene getGeneByName(String name, Chromosome chromosome) {
        if(!DataListManager.ACTIVITY_NAMES.contains(name)) {
            throw new IllegalArgumentException("The name '" + name + "' is not a valid activity name");
        }
        return chromosome.geneList()
                .stream()
                .filter(gene -> gene.getActivity().name().equals(name))
                .toList()
                .get(0);
    }

    /*
        A section of SLA 191 and a section of SLA 101 are overseen in consecutive time slots (e.g., 10 AM & 11 AM): +0.5
        In this case only (consecutive time slots), one of the activities is in Roman or Beach, and the other isn’t: -0.4
        It’s fine if neither is in one of those buildings, of activity; we just want to avoid having consecutive activities being widely separated.
         */
    public static boolean isRoomAvoidanceMet(Gene gene1, Gene gene2, RoomRecord room1, RoomRecord room2) {
        boolean gene1HasOneOfTargetRooms = gene1.getRoom().equals(room1) || gene1.getRoom().equals(room2);
        boolean gene2HasOneOfTargetRooms = gene2.getRoom().equals(room1) || gene2.getRoom().equals(room2);

        return gene1HasOneOfTargetRooms && !gene2HasOneOfTargetRooms
                || gene2HasOneOfTargetRooms && !gene1HasOneOfTargetRooms;

    }




    private static RoomRecord getRandomRoom() {
        return DataListManager.ROOMS.get(random.nextInt(DataListManager.NUMBER_OF_ROOMS));
    }

    private static TimeRecord getRandomTime() {
        return DataListManager.TIMES.get(random.nextInt(DataListManager.NUMBER_OF_TIMES));
    }

    private static String getRandomFacilitator() {
        return DataListManager.FACILITATORS.get(random.nextInt(DataListManager.NUMBER_OF_FACILITATORS));
    }

    public static boolean isActivitySamePlaceAndTime(Gene gene, Gene targetGene) {
        return gene.getTime().equals(targetGene.getTime()) && gene.getRoom().equals(targetGene.getRoom());
    }

    public static boolean doActivitiesHaveSameTimeAndFacilitator(Gene gene, Gene targetGene) {
        return gene.getTime().equals(targetGene.getTime())
                && gene.getFacilitator().equals(targetGene.getFacilitator());
    }


}
