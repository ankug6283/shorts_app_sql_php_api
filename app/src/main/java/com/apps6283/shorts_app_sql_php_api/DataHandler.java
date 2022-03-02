package com.apps6283.shorts_app_sql_php_api;

public class DataHandler {

    String title,url;

    public DataHandler(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}
