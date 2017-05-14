package com.cjae.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by Jedidiah on 13/05/2017.
 */

public interface MoviesColumns {

    @DataType(TEXT) @PrimaryKey
    String COLUMN_MOVIE_ID = "movie_id";

    @DataType(TEXT) @NotNull
    String COLUMN_MOVIE_TITLE = "title";

    @DataType(TEXT) @NotNull
    String COLUMN_MOVIE_COVER = "movie_cover";

    @DataType(TEXT) @NotNull
    String COLUMN_MOVIE_POSTER = "movie_poster";

    @DataType(TEXT) @NotNull
    String COLUMN_SYNOPSIS = "synopsis";

    @DataType(TEXT) @NotNull
    String COLUMN_USER_RATING = "user_rating";

    @DataType(TEXT) @NotNull
    String COLUMN_RELEASE_DATE = "release_date";

    @DataType(TEXT) @NotNull
    String COLUMN_ORIGINAL_LANGUAGE = "language";

    @DataType(TEXT) @NotNull
    String COLUMN_GENRE = "genre";
}
