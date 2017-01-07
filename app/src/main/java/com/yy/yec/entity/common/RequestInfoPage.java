package com.yy.yec.entity.common;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zzq 9-25
 */
public class RequestInfoPage<T> implements Serializable {
    private List<T> items;
    private String nextPageToken;
    private String prevPageToken;
    private PageInfo pageInfo;

    public long getTotalResults() {
        return totalResults;
    }

    private long totalResults;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public String getFirstPageToken() {
        return prevPageToken;
    }

    public void setFirstPageToken(String firstPageToken) {
        this.prevPageToken = firstPageToken;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static class PageInfo implements Serializable {
        private int totalResults;
        private int resultsPerPage;

        public int getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(int totalResults) {
            this.totalResults = totalResults;
        }

        public int getResultsPerPage() {
            return resultsPerPage;
        }

        public void setResultsPerPage(int resultsPerPage) {
            this.resultsPerPage = resultsPerPage;
        }
    }
}