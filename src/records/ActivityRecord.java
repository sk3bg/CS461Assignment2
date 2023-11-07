package records;

import java.util.ArrayList;
import java.util.List;

public record ActivityRecord(String name,
                       int expectedEnrollment,
                       List<String> preferredFacilitators,
                       List<String> otherFacilitators) {

    public ActivityRecord copy() {
        return new ActivityRecord(
                name,
                expectedEnrollment,
                new ArrayList<>(preferredFacilitators),
                new ArrayList<>(otherFacilitators)
        );
    }

    public String toPrettyString() {
        return name;

    }
}
