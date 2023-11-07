package geneticalgorithm;

import java.util.Random;

import records.ActivityRecord;
import records.RoomRecord;
import records.DataListManager;
import records.TimeRecord;

//This is the genetic representation of an activity
public class Gene {
    private final ActivityRecord activity;
    private RoomRecord room;
    private TimeRecord time;
    private String facilitator;

    public Gene(ActivityRecord activity, RoomRecord room, TimeRecord time, String facilitator) {
        this.activity = activity;
        this.room = room;
        this.time = time;
        this.facilitator = facilitator;
    }

    Gene copy() {
        return new Gene(activity, room, time, facilitator);
    }

    void mutateFacilitator() {
        Random random = new Random();
        String newFacilitator;
        do {
            newFacilitator = DataListManager.FACILITATORS.get(
                    random.nextInt(DataListManager.NUMBER_OF_FACILITATORS)
            );
        } while (newFacilitator.equals(facilitator));
        facilitator = newFacilitator;
    }

    void mutateTime() {
        Random random = new Random();
        TimeRecord newTime;
        do {
            newTime = DataListManager.TIMES.get(
                    random.nextInt(DataListManager.NUMBER_OF_TIMES)
            );
        } while(newTime.equals(time));
        time = newTime;
    }

    void mutateRoom() {
        Random random = new Random();
        RoomRecord newRoom;
        do {
            newRoom = DataListManager.ROOMS.get(
                    random.nextInt( DataListManager.NUMBER_OF_ROOMS)
            );
        } while (newRoom.equals(room));
        room = newRoom;
    }

    public ActivityRecord getActivity() {
        return activity;
    }

    public RoomRecord getRoom() {
        return room;
    }

    public TimeRecord getTime() {
        return time;
    }

    public String getFacilitator() {
        return facilitator;
    }

    @Override
    public String toString() {
        return "Gene{" +
                "activity=" + activity +
                ", room=" + room +
                ", time=" + time +
                ", facilitator='" + facilitator + '\'' +
                '}';
    }

    public String toPrettyString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(activity.name()).append(" = {").append("\n")
                .append("\t\t").append("Room = ").append(room.name()).append("\n")
                .append("\t\t").append("Time = ").append(time.toPrettyString()).append("\n")
                .append("\t\t").append("Facilitator = ").append(facilitator).append("\n")
                .append("\t}");
        return stringBuilder.toString();
    }
}
