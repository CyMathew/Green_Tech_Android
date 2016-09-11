package app.greentech.Links;

/**
 * Class created to store information about each Link object
 * @author Cyril Mathew
 */
public class LinksItem {

    private String title;
    private String subTitle;
    private int image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subTitle;
    }

    public void setSubtitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}