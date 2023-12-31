package com.hbb20;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;


import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.widget.AppCompatTextView;


/**
 * declare attributes in attrs file
 * <p>
 * <declare-styleable name="CustomView">
 * <attr name="font_type" format="string" />
 * </declare-styleable>
 */
public class CustomTextView extends AppCompatTextView {

    /*
      * Caches typefaces based on their file path and name, so that they don't have to be created
      * every time when they are referenced.
      */
    private static Map<String, Typeface> mTypefaces;

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, attrs);
    }

    /**
     * Apply Custom Fonts
     *
     * @param context context
     * @param attrs   attribute set
     */
    private void setCustomFont(Context context, AttributeSet attrs) {
        if (mTypefaces == null) {
            mTypefaces = new HashMap<String, Typeface>();
        }

        // prevent exception in Android Studio / ADT interface builder
        if (this.isInEditMode()) {
            return;
        }

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.montserrat);
        if (array != null) {
            int font = array.getInteger(R.styleable.montserrat_font_type, 3);
            final String typefaceAssetPath = "fonts/" + getResources().getStringArray(R.array.montserrat)[font];

            Typeface typeface = null;

            if (mTypefaces.containsKey(typefaceAssetPath)) {
                typeface = mTypefaces.get(typefaceAssetPath);
            } else {
                AssetManager assets = context.getAssets();
                typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
                mTypefaces.put(typefaceAssetPath, typeface);
            }

            setTypeface(typeface);
            array.recycle();
        }
    }

}
