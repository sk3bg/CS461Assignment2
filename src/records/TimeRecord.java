package records;

public record TimeRecord(int hourValue, AmPmEnum period) {
    public TimeRecord copy() {
        return new TimeRecord(hourValue, period);
    }

    public int get24HourValue() {
        if(period.equals(AmPmEnum.PM) && this.hourValue < 12) {
            return hourValue + 12;
        }
        if(period.equals(AmPmEnum.AM) && this.hourValue == 12) {
            return 0;
        }
        return hourValue;
    }

    public int differenceBetween(TimeRecord other) {
        int convertedHourValue1 = this.get24HourValue();
        int convertedHourValue2 = other.get24HourValue();

        return Math.abs(convertedHourValue1 - convertedHourValue2);
    }

    public String toPrettyString() {
        return hourValue + period.name();
    }
}
