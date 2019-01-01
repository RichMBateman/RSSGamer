package com.bateman.rich.rssgamer;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A class for parsing RSS Feeds into RssEntries.
 */
public class RssParser {
    private static final String TAG = "RssParser";
    private final ArrayList<RssEntry> m_rssEntries = new ArrayList<>();
    private final DateFormat m_dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public ArrayList<RssEntry> getRssEntries() {
        return m_rssEntries;
    }

    public void parse(RssSource rssSource) {
        String xmlData = rssSource.getRssFeedXml();
        String tagMainItem = rssSource.getXmlTagMainItem();
        String tagTitle = rssSource.getXmlTagTitle();
        String tagDate = rssSource.getXmlTagDate();
        String tagLink = rssSource.getXmlTagLink();

        RssEntry currentRecord = null;
        boolean inEntry = false;
        String textValue = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        if(tagMainItem.equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new RssEntry();
                        } else if(tagLink.equalsIgnoreCase(tagName)) {
                            if(inEntry) {
                                // Parameters: Namespace, Name
                                String url = xpp.getAttributeValue(null, "href");
                                Log.d(TAG, "parse: Found link for entry: " + url);
                                currentRecord.setLink(url);
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: ending tag for " + tagName);
                        // Why do the hard-coded string first?  Because it cannot be null!
                        // It's possible tagName might be null.  (Although it shouldn't be)
                        if(tagMainItem.equalsIgnoreCase(tagName)) {
                            m_rssEntries.add(currentRecord);
                            inEntry = false;
                        } else if (inEntry) {
                            // Since the course, it seems like the xml tags are now like "<im:name>",
                            // "im:image", etc.
                            if(tagTitle.equalsIgnoreCase(tagName)) {
                                Log.d(TAG, "parse: End Tag for Name " + textValue);
                                currentRecord.setTitle(textValue);
                            } else if(tagDate.equalsIgnoreCase(tagName)) {
                                Log.d(TAG, "parse: End Tag for Date " + textValue);
                                textValue = textValue.replace("Z", "+00:00");
                                textValue = textValue.substring(0, 22) + textValue.substring(23);
                                Log.d(TAG, "parse: parsing text date time of: " + textValue);
                                Date result = m_dateFormatter.parse(textValue);
                                currentRecord.setDate(result);
//                            } else if(tagLink.equalsIgnoreCase(tagName)) {
//                                Log.d(TAG, "parse: End Tag for Link " + textValue);
//                            }
                            } else {
                                if(textValue.contains("img src")) {
                                    final String imgSrcValue = "img src=\"";
                                    int indexOfImgSrcStart = textValue.indexOf(imgSrcValue) + imgSrcValue.length();
                                    int indexOfImgSrcEnd = textValue.indexOf("\"", indexOfImgSrcStart);
                                    String imageSource = textValue.substring(indexOfImgSrcStart, indexOfImgSrcEnd);
                                    Log.d(TAG, "parse: image url found: " + imageSource);
                                    currentRecord.setImgSrc(imageSource);
                                }
                            }
                            }
                        break;
                    default:
                        // nothing to do.
                    } // end switch

                    eventType = xpp.next();
                } // end while

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
