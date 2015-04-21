package menudroid.aybars.arslan.menudroid.main;

/**
 * Created by Aybars on 21.04.2015.
 */
public class RestaurantItem {
    private String title;
    private int imageUrl;

    public RestaurantItem(String title,int imageUrl){

        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
