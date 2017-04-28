package group2.ictk59.moviedatabase.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by ZinZin on 4/24/2017.
 */

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "group2.ictk59.moviedatabase.provider.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
