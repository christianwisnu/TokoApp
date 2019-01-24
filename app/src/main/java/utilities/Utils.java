package utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.project.tokoapp.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by christian on 09/02/18.
 */

public class Utils {

    private static final ImageLoader imgloader = ImageLoader.getInstance();

    public static void deleteAccessToken(final PrefUtil prefUtil) {
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (currentAccessToken == null){
                    //User logged out
                    prefUtil.clear();
                    LoginManager.getInstance().logOut();
                }
            }
        };
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    public static void getImage(String url, ImageView img, Context context){
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_person)
                .showImageForEmptyUri(R.drawable.img_person)
                .showImageOnFail(R.drawable.img_person)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();
        imgloader.init(ImageLoaderConfiguration.createDefault(context));
        imgloader.displayImage(url, img, options);
        return;
    }

    public static void GetCycleImage(String url,ImageView img,Context context){
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer((int) 50.5f))
                .showImageOnLoading(R.mipmap.ic_store_black_36dp)
                .showImageForEmptyUri(R.mipmap.ic_store_black_36dp)
                .showImageOnFail(R.mipmap.ic_store_black_36dp)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();
        imgloader.init(ImageLoaderConfiguration.createDefault(context));
        imgloader.displayImage(url, img, options);
        return;
    }

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();
        int buffSize = 1024;
        byte[] buff = new byte[buffSize];
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }
        return byteBuff.toByteArray();
    }
//======================================================================
    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }
}

