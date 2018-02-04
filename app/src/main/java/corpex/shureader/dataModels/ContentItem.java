package corpex.shureader.dataModels;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created with love by Corpex on 20/07/2017.
 */

public class ContentItem {
    private final String itemUrl;

    private final String forumName;

    private final int pageCount;
    private final ArrayList<String> pageUrlList;

    private final String userCreatorName;
    private final String userCreatorUrl;

    private final String threadTitle;
    private final String threadDescription;

    private final String dateLastMessage;

    private final String userLastTalkerName;
    private final String userLastTalkerUrl;

    private final String threadResponses;
    private final String threadVisits;


    public ContentItem(String itemUrl, String forumName, int pageCount, ArrayList<String> pageUrlList, String userCreatorName, String userCreatorUrl, String threadTitle, String threadDescription, String dateLastMessage, String userLastTalkerName, String userLastTalkerUrl, String threadResponses, String threadVisits) {
        this.itemUrl = itemUrl;
        this.forumName = forumName;
        this.pageCount = pageCount;
        this.pageUrlList = pageUrlList;
        this.userCreatorName = userCreatorName;
        this.userCreatorUrl = userCreatorUrl;
        this.threadTitle = threadTitle;
        this.threadDescription = threadDescription;
        this.dateLastMessage = dateLastMessage;
        this.userLastTalkerName = userLastTalkerName;
        this.userLastTalkerUrl = userLastTalkerUrl;
        this.threadResponses = threadResponses;
        this.threadVisits = threadVisits;
    }


    public String getItemUrl() {
        return itemUrl;
    }

    public String getForumName() {return forumName;}

    public int getPageCount() {
        return pageCount;
    }

    public ArrayList<String> getPageUrlList() {
        return pageUrlList;
    }

    public String getUserCreatorName() {
        return userCreatorName;
    }

    public String getUserCreatorUrl() {
        return userCreatorUrl;
    }

    public String getThreadTitle() {
        return threadTitle;
    }

    public String getThreadDescription() {
        return threadDescription;
    }

    public String getDateLastMessage() {
        return dateLastMessage;
    }

    public String getUserLastTalkerName() {
        return userLastTalkerName;
    }

    public String getUserLastTalkerUrl() {
        return userLastTalkerUrl;
    }

    public String getThreadResponses() {
        return threadResponses;
    }

    public String getThreadVisits() {
        return threadVisits;
    }
}
