package com.bateman.rich.rssgamer;

import java.util.ArrayList;

/**
 * Keeps track of all the Rss Feeds.  It uses hard-coded information to determine the RSS Feeds to include.
 */
public class RssFeedManager {
    public static final String KEY_DESTRUCTOID = "Destructoid";
    public static final String KEY_ESCAPIST ="Escapist";
    public static final String KEY_GAMASUTRA = "Gamasutra";
    public static final String KEY_GAMEINFORMER = "Game Informer";
    public static final String KEY_GAMESPOT = "Gamespot";
    public static final String KEY_GIANTBOMB = "Giant Bomb";
    public static final String KEY_IGN = "IGN";
    public static final String KEY_KOTAKU = "Kotaku";
    public static final String KEY_METACRITIC = "MetaCritic";
    public static final String KEY_METRO_CO_UK = "Metro.co.uk";
    public static final String KEY_POLYGON = "Polygon";
    public static final String KEY_VG247 = "VG247";
    public static final String KEY_VIDEOGAMER = "Videogamer";
    public static final String KEY_ROCKPAPERSHOTGUN = "Rock, Paper, Shotgun";

    private static final String RSS_FEED_URL_DESTRUCTOID = "http://feeds.feedburner.com/Destructoid";
    private static final String RSS_FEED_URL_ESCAPIST = "http://rss.escapistmagazine.com/news/0.xml";
    private static final String RSS_FEED_URL_GAMASUTRA = "http://feeds.feedburner.com/GamasutraNews";
    private static final String RSS_FEED_URL_GAMEINFORMER = "https://www.gameinformer.com/rss.xml";
    private static final String RSS_FEED_URL_GAMESPOT = "https://www.gamespot.com/feeds/news/";
    private static final String RSS_FEED_URL_GIANTBOMB = "https://www.giantbomb.com/feeds/reviews/";
    private static final String RSS_FEED_URL_IGN = "http://feeds.ign.com/ign/all";
    private static final String RSS_FEED_URL_KOTAKU = "https://kotaku.com/tag/kotakucore/rss";
    private static final String RSS_FEED_URL_METACRITIC = "https://www.metacritic.com/rss/features";
    private static final String RSS_FEED_URL_METRO_CO_UK = "https://metro.co.uk/entertainment/gaming/feed/";
    private static final String RSS_FEED_URL_POLYGON = "https://www.polygon.com/rss/stream/3550099";
    private static final String RSS_FEED_URL_ROCKPAPERSHOTGUN = "http://feeds.feedburner.com/RockPaperShotgun";
    private static final String RSS_FEED_URL_VG247 = "https://www.vg247.com/feed/";
    private static final String RSS_FEED_URL_VIDEOGAMER = "https://www.videogamer.com/rss/allupdates.xml";

    private final ArrayList<RssSource> m_rssSourceList = new ArrayList<>();

    public RssFeedManager() {
        m_rssSourceList.add(new RssSource(KEY_DESTRUCTOID, RSS_FEED_URL_DESTRUCTOID, "entry", "title", "link", true, "updated"));
        m_rssSourceList.add(new RssSource(KEY_ESCAPIST, RSS_FEED_URL_ESCAPIST, "item", "title", "link", false, "updated"));
        m_rssSourceList.add(new RssSource(KEY_GAMASUTRA, RSS_FEED_URL_GAMASUTRA, "item", "title", "link", false, "updated"));
        m_rssSourceList.add(new RssSource(KEY_GAMEINFORMER, RSS_FEED_URL_GAMEINFORMER, "item", "title", "link", false, "updated"));
        m_rssSourceList.add(new RssSource(KEY_GAMESPOT, RSS_FEED_URL_GAMESPOT, "item", "title", "link", false, "updated"));
        m_rssSourceList.add(new RssSource(KEY_GIANTBOMB, RSS_FEED_URL_GIANTBOMB, "item", "title", "link", false, "updated"));
        m_rssSourceList.add(new RssSource(KEY_IGN, RSS_FEED_URL_IGN, "item", "title", "link", false, "updated"));
        m_rssSourceList.add(new RssSource(KEY_KOTAKU, RSS_FEED_URL_KOTAKU, "item", "title", "link", false, "updated"));
        m_rssSourceList.add(new RssSource(KEY_METACRITIC, RSS_FEED_URL_METACRITIC, "item", "title", "link", false, "updated"));
        m_rssSourceList.add(new RssSource(KEY_METRO_CO_UK, RSS_FEED_URL_METRO_CO_UK, "item", "title", "link", false, "updated"));
        m_rssSourceList.add(new RssSource(KEY_POLYGON, RSS_FEED_URL_POLYGON, "entry", "title", "link", true, "updated"));
        m_rssSourceList.add(new RssSource(KEY_ROCKPAPERSHOTGUN, RSS_FEED_URL_ROCKPAPERSHOTGUN, "item", "title", "link", false, "updated"));
        m_rssSourceList.add(new RssSource(KEY_VG247, RSS_FEED_URL_VG247, "item", "title", "link", false, "updated"));
        m_rssSourceList.add(new RssSource(KEY_VIDEOGAMER, RSS_FEED_URL_VIDEOGAMER, "entry", "title", "link", true, "updated"));
    }

    /**
     * Returns the list of RssSources, which are ordered by the user-friendly key.
     */
    public ArrayList<RssSource> getRssSourceList() { return m_rssSourceList;}
}
