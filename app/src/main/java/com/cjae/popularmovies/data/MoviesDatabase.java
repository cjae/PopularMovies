package com.cjae.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Jedidiah on 13/05/2017.
 */

@Database(version = MoviesDatabase.VERSION)
final class MoviesDatabase {

    static final int VERSION = 1;

    @Table(MoviesColumns.class)
    static final String MOVIES = "movies";
}
