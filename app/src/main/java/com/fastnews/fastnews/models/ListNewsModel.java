package com.fastnews.fastnews.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lucask on 08/05/17.
 */

public class ListNewsModel implements Serializable {
    public List<NewsModel> models;

    public ListNewsModel(List<NewsModel> models) {
        this.models = models;
    }
}
