package za.co.joshuabakerg.bankimport.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import za.co.joshuabakerg.bankimport.domain.model.Period;

/**
 * @author Joshua Baker on 06/04/2020
 */
public final class PeriodUtil {

    private PeriodUtil() {
    }

    public static Collection<Period> generatePeriods(final int startDay, final int endDay, final int count) {
        final ArrayList<Period> periods = new ArrayList<>();
        final LocalDate start = LocalDate.now()
                .minusMonths(1)
                .withDayOfMonth(startDay);
        final LocalDate end = LocalDate.now()
                .withDayOfMonth(endDay);
        periods.add(Period.builder()
                .name(buildName(start, end))
                .start(start)
                .end(end)
                .build());

        for (int i = 1; i < count ; i++) {
            final LocalDate newEnd = LocalDate.from(end).minusMonths(i);
            final LocalDate newStart = LocalDate.from(start).minusMonths(i);
            periods.add(Period.builder()
                    .name(buildName(newStart, newEnd))
                    .start(newStart)
                    .end(newEnd)
                    .build());
        }
        return periods;
    }

    private static String buildName(final LocalDate start, final LocalDate end) {
        final String startString = String.format("%d %s %d", start.getDayOfMonth(), start.getMonth().name(), start.getYear());
        final String endString = String.format("%d %s %d", end.getDayOfMonth(), end.getMonth().name(), end.getYear());
        return String.format("%s - %s", startString, endString);
    }
}
