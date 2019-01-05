package com.bateman.rich.rssgamer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple representation of an RSS Entry.
 */
public class RssEntry implements Comparable<RssEntry> {
    private static final SimpleDateFormat m_dateDisplayFormatter = new SimpleDateFormat("yyyy.MM.dd hh:mm a");

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

    /**
     * Gets a formatted string representing the last time this article was updated.
     * @return
     */
    public String getFormattedLastUpdatedDate() {
        String formattedDate = "";
        if(m_date != null) {
            formattedDate = m_dateDisplayFormatter.format(m_date);
        }
        return formattedDate;
    }

    @Override
    public int compareTo(RssEntry rssEntry) {
        if(m_date == null) return  1;
        if(rssEntry.getDate() == null) return -1;
        return -1 * m_date.compareTo(rssEntry.getDate());
    }
}
