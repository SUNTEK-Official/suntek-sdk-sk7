/**
 * File: MainActivity.java
 * Author: Xu Linrui <xdn@suntek.cloud>
 * Created on 21 June 2021
 **/
package android.app.smdt.apidemo.fragment;

import android.app.smdt.apidemo.R;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.TextView;

public class MainFragment extends BaseFragment {

    //view
    private TextView versionTv,resource;
    @Override
    public int getLayoutResource() {
        return R.layout.main_fragment;
    }

    @Override
    public void onInit() {
        initView();
        onUserResume();
    }

    @Override
    public void onUserResume() {
        versionTv.setText(smdtManagerNew.info_getApiVersion());
        smdtManagerNew.sys_setTouchReport(true);
        smdtManagerNew.sys_setKeyReport(true);
    }

    @Override
    public void onUserPause() {
        super.onUserPause();
    }

    private void initView() {
        versionTv = view.findViewById(R.id.version);
        resource = view.findViewById(R.id.resource);
        createURL("https://developers.suntekai.com");
    }

    private void createURL(String url){
 // SpannableString
        String str = mRes.getString(R.string.resource_msg);
        SpannableString sp = new SpannableString(str);

        sp.setSpan(new URLSpan(url),0,str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
 // SpannableString TextView
        resource.setText(sp);
 // TextView
        resource.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
