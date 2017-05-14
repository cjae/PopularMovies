package com.cjae.popularmovies.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jedidiah on 09/05/2017.
 */

public class ReviewsWrapper {

    private Number id;
    private Number page;
    private ArrayList<Review> results;
    private Number total_pages;
    private Number total_results;

    public ArrayList<Review> getResults() {
        return results;
    }
}
