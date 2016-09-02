package com.mycompany.chservicetime.data.source.local;

/**
 * The list of {@code Uri}s recognised by the {@code ContentProvider} of the app.
 * <p />
 * It is important to order them in the order that follows {@link android.content.UriMatcher}
 * matching rules: wildcard {@code *} applies to one segment only and it processes matching per
 * segment in a tree manner over the list of {@code Uri} in the order they are added. The first
 * rule means that {@code sessions / *} would not match {@code sessions / id / room}. The second
 * rule is more subtle and means that if Uris are in the  order {@code * / room / counter} and
 * {@code sessions / room / time}, then {@code speaker / room / time} will not match anything,
 * because the {@code UriMatcher} will follow the path of the first  and will fail at the third
 * segment.
 */
public enum CHServiceTimeUriEnum {
    TIMESLOTS(100, "time_slots", CHServiceTimeContract.TimeSlots.CONTENT_TYPE_ID, false,
            CHServiceTimeDatabase.Tables.TIME_SLOTS),
    TIMESLOTS_ID(102, "time_slots/*", CHServiceTimeContract.TimeSlots.CONTENT_TYPE_ID, true,
            null);

    public int code;

    /**
     * The path to the {@link android.content.UriMatcher} will use to match. * may be used as a
     * wild card for any text, and # may be used as a wild card for numbers.
     */
    public String path;

    public String contentType;

    public String table;

    CHServiceTimeUriEnum(int code, String path, String contentTypeId, boolean item, String table) {
        this.code = code;
        this.path = path;
        this.contentType = item ? CHServiceTimeContract.makeContentItemType(contentTypeId)
                : CHServiceTimeContract.makeContentType(contentTypeId);
        this.table = table;
    }


}
