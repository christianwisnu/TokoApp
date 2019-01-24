package utilities;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by christian on 21/03/18.
 */

public class FontManager {

    public static final String ROOT = "fonts/",
    FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

}
