package group2.ictk59.moviedatabase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import group2.ictk59.moviedatabase.provider.MySuggestionProvider;

/**
 * Created by ZinZin on 4/24/2017.
 */

public class AlertDialogWrapper {

    public static void showAlertDialog(final Context mContext){
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("No Internet connection");
        alertDialog.setMessage("There is no network available. Would you like to visit the network settings page to set one up?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "NETWORK SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(Settings.ACTION_SETTINGS);
                        mContext.startActivity(intent);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void clearHistoryAlert(final Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Warning!");
        alertDialog.setMessage("Do you want to delete your search history?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(context,
                                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                        suggestions.clearHistory();
                        Toast.makeText(context, "Your search history has been deleted!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
