package com.synopsys.integration.alert.channel.email.template;

import static org.junit.Assert.assertEquals;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.alert.common.message.model.DateRange;
import com.synopsys.integration.alert.common.util.DateUtils;

public class DateRangeTest {

    @Test
    public void testGetTimes() {
        OffsetDateTime start = DateUtils.createCurrentDateTimestamp();
        OffsetDateTime end = start.plusMinutes(5);
        DateRange dateRange = DateRange.of(start, end);

        assertEquals(start, dateRange.getStart());
        assertEquals(end, dateRange.getEnd());
    }
}
