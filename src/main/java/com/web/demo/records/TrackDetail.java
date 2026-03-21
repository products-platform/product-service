package com.web.demo.records;

public record TrackDetail(
        int track_id,
        String name,
        int album_id,
        int media_type_id,
        int genre_id,
        String composer,
        int milliseconds,
        long bytes,
        double unit_price
) {
}
