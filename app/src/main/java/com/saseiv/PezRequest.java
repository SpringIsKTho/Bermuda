package com.saseiv;

public class PezRequest {

    public String user_id;
    public String title;
    public String description;
    public String file_url;
    public String audio_url;

    public PezRequest(String user_id,
                       String title,
                       String description,
                       String file_url,
                       String audio_url) {
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.file_url = file_url;
        this.audio_url = audio_url;
    }
}
