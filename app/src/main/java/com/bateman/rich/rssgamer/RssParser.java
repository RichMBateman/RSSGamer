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
    private static final String DT_FORMAT_TIMEZONE="yyyy-MM-dd'T'HH:mm:ssZ";

    private final ArrayList<RssEntry> m_rssEntries = new ArrayList<>();

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
                      //  Log.d(TAG, "parse: Starting tag for " + tagName);
                        if(tagMainItem.equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new RssEntry();
                        } else if(tagLink.equalsIgnoreCase(tagName) && rssSource.isLinkIsInAttribute()) {
                            if(inEntry) {
                                // Parameters: Namespace, Name
                                String url = xpp.getAttributeValue(null, "href");
                             //   Log.d(TAG, "parse: Found link for entry: " + url);
                                currentRecord.setLink(url);
                            }
                        } else if("img".equalsIgnoreCase(tagName)) {
                            // Parameters: Namespace, Name
                            String imgSrc = xpp.getAttributeValue(null, "src");
                           // Log.d(TAG, "parse: Found img src for entry: " + imgSrc);
                            currentRecord.setImgSrc(imgSrc);
                        } else if("media:content".equalsIgnoreCase(tagName)) {
                            if(currentRecord.getImgSrc() == null) {
                                // Parameters: Namespace, Name
                                String imgSrc = xpp.getAttributeValue(null, "url");
                              //  Log.d(TAG, "parse: Found media:content url for entry: " + imgSrc);
                                currentRecord.setImgSrc(imgSrc);
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        textValue = textValue.replace("<![CDATA[", "");
                        textValue = textValue.replace("]]>", "");
                        textValue = textValue.trim();
                        break;
                    case XmlPullParser.END_TAG:
                       // Log.d(TAG, "parse: ending tag for " + tagName);
                        // Why do the hard-coded string first?  Because it cannot be null!
                        // It's possible tagName might be null.  (Although it shouldn't be)
                        if(tagMainItem.equalsIgnoreCase(tagName)) {
                            m_rssEntries.add(currentRecord);
                            inEntry = false;
                        } else if (inEntry) {
                            // Since the course, it seems like the xml tags are now like "<im:name>",
                            // "im:image", etc.
                            if (tagTitle.equalsIgnoreCase(tagName)) {
                             //   Log.d(TAG, "parse: End Tag for Name " + textValue);
                                currentRecord.setTitle(textValue);
                            } else if (tagDate.equalsIgnoreCase(tagName)) {
                                parseDate(rssSource, currentRecord, textValue);
                           } else if (tagLink.equalsIgnoreCase(tagName) && !rssSource.isLinkIsInAttribute()) {
                              //  Log.d(TAG, "parse: End Tag for Link " + textValue);
                                currentRecord.setLink(textValue);
                            } else {
                                if (textValue.contains("img src")) {
                                //    Log.d(TAG, "parse: embedded img src found in: " + textValue);
                                    final String imgSrcValue = "img src=";
                                    int indexOfImgSrcStart = textValue.indexOf(imgSrcValue) + imgSrcValue.length();
                                    char quoteCharacter = textValue.charAt(indexOfImgSrcStart);
                                 //   Log.d(TAG, "parse: quote character for img is: " + quoteCharacter);
                                    int indexOfImgSrcEnd = textValue.indexOf(quoteCharacter, indexOfImgSrcStart + 1);
                                    String imageSource = textValue.substring(indexOfImgSrcStart + 1, indexOfImgSrcEnd);
                                    if (imageSource.startsWith("//")) {
                                        imageSource = "http:" + imageSource;
                                    }
                                 //   Log.d(TAG, "parse: image url found: " + imageSource);
                                    currentRecord.setImgSrc(imageSource);
                                } else if (textValue.contains("img")) {
                                    // could be like img alt='....' src='...'
                                  //  Log.d(TAG, "parse: embedded img src found in: " + textValue);
                                    final String imgSrcValue = "src=";
                                    int indexOfImgSrcStart = textValue.indexOf(imgSrcValue) + imgSrcValue.length();
                                    char quoteCharacter = textValue.charAt(indexOfImgSrcStart);
                                  //  Log.d(TAG, "parse: quote character for img is: " + quoteCharacter);
                                    int indexOfImgSrcEnd = textValue.indexOf(quoteCharacter, indexOfImgSrcStart + 1);
                                    String imageSource = textValue.substring(indexOfImgSrcStart + 1, indexOfImgSrcEnd);
                                    if (imageSource.startsWith("//")) {
                                        imageSource = "http:" + imageSource;
                                    }
                                  //  Log.d(TAG, "parse: image url found: " + imageSource);
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

    private void parseDate(RssSource source, RssEntry entry, String textValue) {
        try {
            //  Log.d(TAG, "parse: End Tag for Date " + textValue);
            SimpleDateFormat dateFormatter = source.getDateFormatter();
            String dateFormatPattern = dateFormatter.toPattern();
            if (dateFormatPattern.equalsIgnoreCase(DT_FORMAT_TIMEZONE)) {
                textValue = textValue.replace("Z", "+00:00");
                textValue = textValue.substring(0, 22) + textValue.substring(23);
            }
            //  Log.d(TAG, "parse: parsing text date time of: " + textValue);
            Date result = dateFormatter.parse(textValue);
            entry.setDate(result);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
