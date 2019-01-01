package com.bateman.rich.rssgamer;

import java.util.Date;

/**
 * A simple representation of an RSS Entry.
 */
public class RssEntry {
    private String m_title;
    private String m_link;
    private String m_imgSrc;
    private Date m_date;

    public String getTitle() {
        return m_title;
    }

    public void setTitle(String title) {
        m_title = title;
    }

    public String getLink() {
        return m_link;
    }

    public void setLink(String link) {
        m_link = link;
    }

    public String getImgSrc() {
        return m_imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        m_imgSrc = imgSrc;
    }

    public Date getDate() {
        return m_date;
    }

    public void setDate(Date date) {
        m_date = date;
    }
}
