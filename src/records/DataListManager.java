package records;

import java.util.ArrayList;
import java.util.List;

public class DataListManager {

    public static final List<String> FACILITATORS = List.of(
            "Lock",
            "Glen",
            "Banks",
            "Richards",
            "Shaw",
            "Singer",
            "Uther",
            "Tyler",
            "Numen",
            "Zeldin"
    );

    public static final int NUMBER_OF_FACILITATORS = 10;

    public final static List<String> ACTIVITY_NAMES = List.of(
            "SLA100A",
            "SLA100B",
            "SLA191A",
            "SLA191B",
            "SLA201",
            "SLA291",
            "SLA303",
            "SLA304",
            "SLA394",
            "SLA449",
            "SLA451"
    );

    public final static List<List<String>> PREFERRED_FACILITATORS = List.of(
            List.of("Glen", "Lock", "Banks", "Zeldin"),
            List.of("Glen", "Lock", "Banks", "Zeldin"),
            List.of("Glen", "Lock", "Banks", "Zeldin"),
            List.of("Glen", "Lock", "Banks", "Zeldin"),
            List.of("Glen", "Banks", "Zeldin", "Shaw"),
            List.of("Lock", "Banks", "Zeldin", "Singer"),
            List.of("Glen", "Zeldin", "Banks"),
            List.of("Glen", "Banks", "Tyler"),
            List.of("Tyler", "Singer"),
            List.of("Tyler", "Singer", "Shaw"),
            List.of("Tyler", "Singer", "Shaw")
    );

    public final static List<List<String>> OTHER_FACILITATORS = List.of(
            List.of("Numen", "Richards"),
            List.of("Numen", "Richards"),
            List.of("Numen", "Richards"),
            List.of("Numen", "Richards"),
            List.of("Numen", "Richards", "Singer"),
            List.of("Numen", "Richards", "Shaw", "Tyler"),
            List.of("Numen", "Singer", "Shaw"),
            List.of("Numen", "Singer", "Shaw", "Richards", "Uther", "Zeldin"),
            List.of("Richards", "Zeldin"),
            List.of("Zeldin", "Uther"),
            List.of("Zeldin", "Uther", "Richards", "Banks")
    );

    public final static List<Integer> ENROLLMENTS = List.of(
            50, 50, 50, 50, 50, 50, 60, 25, 20, 60, 100
    );

    public final static List<String> ROOM_NAMES = List.of(
            "Slater 003",
            "Roman 216",
            "Loft 206",
            "Roman 201",
            "Loft 310",
            "Beach 201",
            "Beach 301",
            "Logos 325",
            "Frank 119"
    );

    public final static List<Integer> ROOM_CAPACITIES = List.of(
            45, 30, 75, 50, 108, 60, 75, 450, 60
    );

    public final static List<RoomRecord> ROOMS = initRooms();

    public final static List<TimeRecord> TIMES = List.of(
            new TimeRecord(10, AmPmEnum.AM),
            new TimeRecord(11, AmPmEnum.AM),
            new TimeRecord(12, AmPmEnum.PM),
            new TimeRecord(1, AmPmEnum.PM),
            new TimeRecord(2, AmPmEnum.PM),
            new TimeRecord(3, AmPmEnum.PM)
    );

    public final static int NUMBER_OF_ROOMS = 9;

    public final static int NUMBER_OF_ACTIVITIES = 11;

    public final static int NUMBER_OF_TIMES = 6;

    public static final List<ActivityRecord> ACTIVITIES = initActivities();

    private static List<RoomRecord> initRooms() {
        List<RoomRecord> roomList = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_ROOMS; i++) {
            roomList.add(new RoomRecord(ROOM_NAMES.get(i), ROOM_CAPACITIES.get(i)));
        }
        return roomList;
    }

    private static List<ActivityRecord> initActivities() {
        List<ActivityRecord> activityList = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_ACTIVITIES; i++) {
            String name = ACTIVITY_NAMES.get(i);
            int expectedEnrollment = ENROLLMENTS.get(i);
            List<String> preferredFacilitators = PREFERRED_FACILITATORS.get(i);
            List<String> otherFacilitators = OTHER_FACILITATORS.get(i);

            activityList.add(
                    new ActivityRecord(name, expectedEnrollment, preferredFacilitators, otherFacilitators)
            );
        }
        return activityList;
    }

	public static RoomRecord getRoomByName(String name) {
	    if(!ROOM_NAMES.contains(name)) {
	        throw new IllegalArgumentException("The name '" + name + "' is not a valid room name");
	    }
	    return ROOMS
	            .stream()
	            .filter(room -> room.name().equals(name))
	            .toList()
	            .get(0);
	}

}
