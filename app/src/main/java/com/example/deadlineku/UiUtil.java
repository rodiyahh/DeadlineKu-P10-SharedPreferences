package com.example.deadlineku;

import android.app.Activity;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Pembantu tampilan: memberi jarak otomatis di bawah status bar (atas)
 * dan di atas tombol navigasi / keyboard (bawah), supaya isi layar tidak tertutup.
 */
public class UiUtil {

    public static void applyInsets(Activity activity, View root, View body) {
        // ikon status bar putih agar terbaca di atas header berwarna
        WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        final int pl = body.getPaddingLeft();
        final int pt = body.getPaddingTop();
        final int pr = body.getPaddingRight();
        final int pb = body.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, 0);
            body.setPadding(pl, pt, pr, pb + bars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }
}
