package corpex.shureader.dataModels;

/**
 * Created with love by Corpex on 15/01/2017.
 */

public class ThreadItem {
    private final String username;
    private final String avatarURL;
    private final String userDescrip;
    private final String postDate;
    private final String postContent;


    public ThreadItem(String username, String avatarURL, String userDescrip, String postDate, String postContent) {
        this.username = username;
        this.avatarURL = avatarURL;
        this.userDescrip = userDescrip;
        this.postDate = postDate;
        this.postContent = postContent;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public String getUserDescrip() {
        return userDescrip;
    }

    public String getPostDate() {
        return postDate;
    }

    public String getPostContent() {
        return postContent;
    }
}
