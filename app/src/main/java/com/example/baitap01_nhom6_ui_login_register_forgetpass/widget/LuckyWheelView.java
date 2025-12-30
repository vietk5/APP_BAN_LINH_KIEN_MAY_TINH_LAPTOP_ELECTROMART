package com.example.baitap01_nhom6_ui_login_register_forgetpass.widget;


import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LuckyWheelView extends View {

    private final Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private List<String> items = new ArrayList<>();

    public LuckyWheelView(Context c, AttributeSet a) {
        super(c, a);
        arcPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setItems(List<String> items) {
        this.items = items;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (items == null || items.isEmpty()) return;

        int w = getWidth(), h = getHeight();
        int size = Math.min(w, h);
        float cx = w / 2f, cy = h / 2f;
        float r = size / 2f - 8;

        RectF rect = new RectF(cx - r, cy - r, cx + r, cy + r);

        float sweep = 360f / items.size();
        float start = 0f;

        for (int i = 0; i < items.size(); i++) {
            // đổi màu xen kẽ
            arcPaint.setColor(i % 2 == 0 ? Color.parseColor("#111A2E") : Color.parseColor("#16213A"));
            canvas.drawArc(rect, start, sweep, true, arcPaint);

            // vẽ text ở giữa cung
            float angle = (float) Math.toRadians(start + sweep / 2f);
            float tx = cx + (float) (Math.cos(angle) * r * 0.62);
            float ty = cy + (float) (Math.sin(angle) * r * 0.62);

            String t = items.get(i);
            // nhỏ lại cho vừa
            if (t.length() > 14) t = t.substring(0, 14) + "…";
            canvas.drawText(t, tx, ty, textPaint);

            start += sweep;
        }

        // viền
        Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(6f);
        border.setColor(Color.parseColor("#FF9800"));
        canvas.drawCircle(cx, cy, r, border);
    }
}
