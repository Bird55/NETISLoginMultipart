package ru.netis.android.netisloginmultipart;

public class MultipartParameter {

    public MultipartParameter(String name, String contentType, String content) {
        this.name = name;
        this.contentType = contentType;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public String getName() {
        return name;
    }

    private String name;
    private String contentType;
    private String content;
}
