package lecho.lib.hellocharts.samples;

import android.app.Activity;
import android.app.ProgressDialog;

public class DialogHelper {
    
    private Activity activity;
    private ProgressDialog pDialog;
    
    public DialogHelper(Activity activity){
        this.activity = activity;
    }
    
    public void showPdialog(String msg, Boolean isCancelable){
        pDialog = new ProgressDialog(activity);
        pDialog.setCancelable(isCancelable);
        if(!pDialog.isShowing()){
            pDialog.setMessage(msg);
            pDialog.show();
        }
        
    }
    public void hidePdialog(){
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
    

}