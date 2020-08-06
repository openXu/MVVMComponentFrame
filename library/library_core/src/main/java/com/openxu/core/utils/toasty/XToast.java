package com.openxu.core.utils.toasty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.*;
import com.openxu.core.R;
import com.openxu.core.utils.XUtils;

/**
 * This file is part of Toasty.
 * <p>
 * Toasty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Toasty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Toasty.  If not, see <http://www.gnu.org/licenses/>.
 */

@SuppressLint("InflateParams")
public class XToast {
    private static final Typeface LOADED_TOAST_TYPEFACE = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
    private static Typeface currentTypeface = LOADED_TOAST_TYPEFACE;
    private static int textSize = 16; // in SP

    private static boolean tintIcon = true;
    private static boolean allowQueue = false;

    private static Toast lastToast = null;

    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    private XToast() {
        // avoiding instantiation
    }
    static Context context;
    static{
        context = XUtils.getApp();
    }
    @CheckResult
    public static Toast normal(@StringRes int message) {
        return normal(context.getString(message), Toast.LENGTH_SHORT, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull CharSequence message) {
        return normal(message, Toast.LENGTH_SHORT, null, false);
    }

    @CheckResult
    public static Toast normal(@StringRes int message, Drawable icon) {
        return normal(context.getString(message), Toast.LENGTH_SHORT, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull CharSequence message, Drawable icon) {
        return normal(message, Toast.LENGTH_SHORT, icon, true);
    }

    @CheckResult
    public static Toast normal(@StringRes int message, int duration) {
        return normal(context.getString(message), duration, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull CharSequence message, int duration) {
        return normal(message, duration, null, false);
    }

    @CheckResult
    public static Toast normal(@StringRes int message, int duration,
                               Drawable icon) {
        return normal(context.getString(message), duration, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull CharSequence message, int duration,
                               Drawable icon) {
        return normal(message, duration, icon, true);
    }

    @CheckResult
    public static Toast normal(@StringRes int message, int duration,
                               Drawable icon, boolean withIcon) {
        return custom(context.getString(message), icon, ToastyUtils.getColor(context, R.color.toast_normalColor),
                ToastyUtils.getColor(context, R.color.toast_defaultTextColor), duration, withIcon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull CharSequence message, int duration,
                               Drawable icon, boolean withIcon) {
        return custom(message, icon, ToastyUtils.getColor(context, R.color.toast_normalColor),
                ToastyUtils.getColor(context, R.color.toast_defaultTextColor), duration, withIcon, true);
    }

    @CheckResult
    public static Toast warning(@StringRes int message) {
        return warning(context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast warning(@NonNull CharSequence message) {
        return warning(message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast warning(@StringRes int message, int duration) {
        return warning(context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast warning(@NonNull CharSequence message, int duration) {
        return warning(message, duration, true);
    }

    @CheckResult
    public static Toast warning(@StringRes int message, int duration, boolean withIcon) {
        return custom(context.getString(message), ToastyUtils.getDrawable(context, R.drawable.toast_ic_error_outline_white_24dp),
                ToastyUtils.getColor(context, R.color.toast_warningColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast warning(@NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(message, ToastyUtils.getDrawable(context, R.drawable.toast_ic_error_outline_white_24dp),
                ToastyUtils.getColor(context, R.color.toast_warningColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast info(@StringRes int message) {
        return info(context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast info(@NonNull CharSequence message) {
        return info(message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast info(@StringRes int message, int duration) {
        return info(context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast info(@NonNull CharSequence message, int duration) {
        return info(message, duration, true);
    }

    @CheckResult
    public static Toast info(@StringRes int message, int duration, boolean withIcon) {
        return custom(context.getString(message), ToastyUtils.getDrawable(context, R.drawable.toast_ic_info_outline_white_24dp),
                ToastyUtils.getColor(context, R.color.toast_infoColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast info(@NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(message, ToastyUtils.getDrawable(context,R.drawable.toast_ic_info_outline_white_24dp),
                ToastyUtils.getColor(context, R.color.toast_infoColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast success(@StringRes int message) {
        return success(context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast success(@NonNull CharSequence message) {
        return success(message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast success(@StringRes int message, int duration) {
        return success(context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast success(@NonNull CharSequence message, int duration) {
        return success(message, duration, true);
    }

    @CheckResult
    public static Toast success(@StringRes int message, int duration, boolean withIcon) {
        return custom(context.getString(message), ToastyUtils.getDrawable(context,R.drawable.toast_ic_check_white_24dp),
                ToastyUtils.getColor(context, R.color.toast_successColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast success(@NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(message, ToastyUtils.getDrawable(context,R.drawable.toast_ic_check_white_24dp),
                ToastyUtils.getColor(context, R.color.toast_successColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast error(@StringRes int message) {
        return error(context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast error(@NonNull CharSequence message) {
        return error(message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast error(@StringRes int message, int duration) {
        return error(context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast error(@NonNull CharSequence message, int duration) {
        return error(message, duration, true);
    }

    @CheckResult
    public static Toast error(@StringRes int message, int duration, boolean withIcon) {
        return custom(context.getString(message), ToastyUtils.getDrawable(context,R.drawable.toast_ic_clear_white_24dp),
                ToastyUtils.getColor(context, R.color.toast_errorColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast error(@NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(message, ToastyUtils.getDrawable(context,R.drawable.toast_ic_clear_white_24dp),
                ToastyUtils.getColor(context, R.color.toast_errorColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast custom(@StringRes int message, Drawable icon,
                               int duration, boolean withIcon) {
        return custom(context.getString(message), icon, -1, ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, false);
    }

    @CheckResult
    public static Toast custom(@NonNull CharSequence message, Drawable icon,
                               int duration, boolean withIcon) {
        return custom(message, icon, -1, ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, false);
    }

    @CheckResult
    public static Toast custom(@StringRes int message, @DrawableRes int iconRes,
                               @ColorRes int tintColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context.getString(message), ToastyUtils.getDrawable(context, iconRes),
                ToastyUtils.getColor(context, tintColorRes), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, shouldTint);
    }

    @CheckResult
    public static Toast custom(@NonNull CharSequence message, @DrawableRes int iconRes,
                               @ColorRes int tintColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(message, ToastyUtils.getDrawable(context, iconRes),
                ToastyUtils.getColor(context, tintColorRes), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, shouldTint);
    }

    @CheckResult
    public static Toast custom(@StringRes int message, Drawable icon,
                               @ColorRes int tintColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context.getString(message), icon, ToastyUtils.getColor(context, tintColorRes),
                ToastyUtils.getColor(context, R.color.toast_defaultTextColor), duration, withIcon, shouldTint);
    }

    @CheckResult
    public static Toast custom(@StringRes int message, Drawable icon,
                               @ColorRes int tintColorRes, @ColorRes int textColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context.getString(message), icon, ToastyUtils.getColor(context, tintColorRes),
                ToastyUtils.getColor(context, textColorRes), duration, withIcon, shouldTint);
    }

    @SuppressLint("ShowToast")
    @CheckResult
    public static Toast custom(@NonNull CharSequence message, Drawable icon,
                               @ColorInt int tintColor, @ColorInt int textColor, int duration,
                               boolean withIcon, boolean shouldTint) {
        final Toast currentToast = Toast.makeText(context, "", duration);
        final View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.core_toast_layout, null);
        final ImageView toastIcon = toastLayout.findViewById(R.id.toast_icon);
        final TextView toastTextView = toastLayout.findViewById(R.id.toast_text);
        Drawable drawableFrame;

        if (shouldTint)
            drawableFrame = ToastyUtils.tint9PatchDrawableFrame(context, tintColor);
        else
            drawableFrame = ToastyUtils.getDrawable(context,R.drawable.core_toast_frame);
        ToastyUtils.setBackground(toastLayout, drawableFrame);

        if (withIcon) {
            if (icon == null)
                throw new IllegalArgumentException("Avoid passing 'icon' as null if 'withIcon' is set to true");
            ToastyUtils.setBackground(toastIcon, tintIcon ? ToastyUtils.tintIcon(icon, textColor) : icon);
        } else {
            toastIcon.setVisibility(View.GONE);
        }

        toastTextView.setText(message);
        toastTextView.setTextColor(textColor);
        toastTextView.setTypeface(currentTypeface);
        toastTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        currentToast.setView(toastLayout);

        if (!allowQueue){
            if (lastToast != null)
                lastToast.cancel();
            lastToast = currentToast;
        }

        return currentToast;
    }

    public static class Config {
        private Typeface typeface = XToast.currentTypeface;
        private int textSize = XToast.textSize;

        private boolean tintIcon = XToast.tintIcon;
        private boolean allowQueue = true;

        private Config() {
            // avoiding instantiation
        }

        @CheckResult
        public static Config getInstance() {
            return new Config();
        }

        public static void reset() {
            XToast.currentTypeface = LOADED_TOAST_TYPEFACE;
            XToast.textSize = 16;
            XToast.tintIcon = true;
            XToast.allowQueue = true;
        }

        @CheckResult
        public Config setToastTypeface(@NonNull Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        @CheckResult
        public Config setTextSize(int sizeInSp) {
            this.textSize = sizeInSp;
            return this;
        }

        @CheckResult
        public Config tintIcon(boolean tintIcon) {
            this.tintIcon = tintIcon;
            return this;
        }

        @CheckResult
        public Config allowQueue(boolean allowQueue) {
            this.allowQueue = allowQueue;
            return this;
        }

        public void apply() {
            XToast.currentTypeface = typeface;
            XToast.textSize = textSize;
            XToast.tintIcon = tintIcon;
            XToast.allowQueue = allowQueue;
        }
    }
}
