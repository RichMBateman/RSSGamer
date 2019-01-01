package com.bateman.rich.rssgamer;

/**
 * Represents a single RSS Feed.  Has a friendly Key so a user can easily disable or enable this feed.
 */
public class RssSource {
    private final String m_friendlyKey;
    private final String m_url;
    private final String m_xmlTagMainItem;
    private final String m_xmlTagTitle;
    private final String m_xmlTagLink;
    private final String m_xmlTagDate;


    private String m_rssFeedXml;

    public RssSource(String userFriendlyKey, String url, String xmlTagMainItem,
                     String xmlTagTitle, String xmlTagLink, String xmlTagDate) {
        m_friendlyKey = userFriendlyKey;
        m_url = url;
        m_xmlTagMainItem = xmlTagMainItem;
        m_xmlTagTitle = xmlTagTitle;
        m_xmlTagLink = xmlTagLink;
        m_xmlTagDate = xmlTagDate;
    }

    public String getFriendlyKey() {
        return m_friendlyKey;
    }

    public String getUrl() {
        return m_url;
    }

    public String getXmlTagMainItem() {
        return m_xmlTagMainItem;
    }

    public String getRssFeedXml() {
        return m_rssFeedXml;
    }

    public void setRssFeedXml(String rssFeedXml) {
        m_rssFeedXml = rssFeedXml;
    }

    public String getXmlTagTitle() {
        return m_xmlTagTitle;
    }

    public String getXmlTagLink() {
        return m_xmlTagLink;
    }

    public String getXmlTagDate() {
        return m_xmlTagDate;
    }
}
