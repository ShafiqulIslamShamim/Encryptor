package com.decryptor.encryptor;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import com.google.android.material.R; // Material3 attributes
import com.google.android.material.color.MaterialColors;

public class CustomColorPickerView extends View {

  private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint huePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint alphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint alphaPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint borderPaint;
  private Paint boxBorderPaint;
  private Paint checkerPaint;

  private float hue = 0f;
  private float sat = 1f;
  private float val = 1f;
  private int alpha = 255;
  private int selectedColor = Color.RED;

  private int hueBarWidth;
  private int alphaBarHeight;
  private int padding;
  private float pointerRadius;

  private int squareWidth;
  private int squareHeight;
  private int viewWidth, viewHeight;

  private Bitmap hueBitmap;
  private Bitmap alphaBitmap;
  private Bitmap checkerBitmap;

  private OnColorChangedListener listener;

  public interface OnColorChangedListener {
    void onColorChanged(int color);
  }

  public CustomColorPickerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);

    // Theme-based colors
    int primaryColor = MaterialColors.getColor(context, R.attr.colorPrimary, Color.RED);
    int onPrimaryColor = MaterialColors.getColor(context, R.attr.colorOnPrimary, Color.WHITE);
    int outlineColor = MaterialColors.getColor(context, R.attr.colorOutline, Color.DKGRAY);
    int surfaceVariantColor =
        MaterialColors.getColor(context, R.attr.colorSurfaceVariant, Color.LTGRAY);

    // Convert dp to px
    padding = dpToPx(12);
    hueBarWidth = dpToPx(20);
    alphaBarHeight = dpToPx(20);
    pointerRadius = dpToPx(10);

    // Pointer paints
    pointerPaint.setStyle(Paint.Style.FILL);
    pointerPaint.setColor(onPrimaryColor);
    pointerPaint.setAntiAlias(true);
    pointerPaint.setShadowLayer(dpToPx(2), 0, 0, primaryColor);
    setLayerType(LAYER_TYPE_SOFTWARE, pointerPaint);

    alphaPointerPaint.setStyle(Paint.Style.FILL);
    alphaPointerPaint.setColor(onPrimaryColor);
    alphaPointerPaint.setAntiAlias(true);
    alphaPointerPaint.setShadowLayer(dpToPx(2), 0, 0, primaryColor);
    setLayerType(LAYER_TYPE_SOFTWARE, alphaPointerPaint);

    // Borders
    borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeWidth(dpToPx(1));
    borderPaint.setColor(outlineColor);

    boxBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    boxBorderPaint.setStyle(Paint.Style.STROKE);
    boxBorderPaint.setStrokeWidth(dpToPx(1));
    boxBorderPaint.setColor(outlineColor);

    // Checkerboard background for alpha bar
    checkerPaint = new Paint();
    createCheckerboardBitmap(dpToPx(6), surfaceVariantColor, outlineColor);
  }

  private void createCheckerboardBitmap(int size, int lightColor, int darkColor) {
    checkerBitmap = Bitmap.createBitmap(size * 2, size * 2, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(checkerBitmap);
    Paint light = new Paint();
    light.setColor(lightColor);
    Paint dark = new Paint();
    dark.setColor(darkColor);
    canvas.drawRect(0, 0, size, size, light);
    canvas.drawRect(size, size, size * 2, size * 2, light);
    canvas.drawRect(size, 0, size * 2, size, dark);
    canvas.drawRect(0, size, size, size * 2, dark);

    BitmapShader shader =
        new BitmapShader(checkerBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    checkerPaint.setShader(shader);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    viewWidth = w;
    viewHeight = h;
    squareHeight = viewHeight - alphaBarHeight - 2 * padding;
    squareWidth = viewWidth - hueBarWidth - 2 * padding;
    createHueBitmap();
    createAlphaBitmap();
  }

  private void createHueBitmap() {
    if (squareHeight <= 0 || hueBarWidth <= 0) return;
    hueBitmap = Bitmap.createBitmap(hueBarWidth, squareHeight, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(hueBitmap);
    for (int y = 0; y < squareHeight; y++) {
      float h = (360f * y) / squareHeight;
      huePaint.setColor(Color.HSVToColor(new float[] {h, 1f, 1f}));
      canvas.drawRect(0, y, hueBarWidth, y + 1, huePaint);
    }
  }

  private void createAlphaBitmap() {
    if (squareWidth <= 0) return;
    int alphaWidth = squareWidth + hueBarWidth + padding;
    alphaBitmap = Bitmap.createBitmap(alphaWidth, alphaBarHeight, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(alphaBitmap);

    canvas.drawRect(0, 0, alphaWidth, alphaBarHeight, checkerPaint);

    LinearGradient alphaGradient =
        new LinearGradient(
            0,
            0,
            alphaWidth,
            0,
            Color.HSVToColor(0, new float[] {hue, sat, val}),
            Color.HSVToColor(255, new float[] {hue, sat, val}),
            Shader.TileMode.CLAMP);
    alphaPaint.setShader(alphaGradient);
    canvas.drawRect(0, 0, alphaWidth, alphaBarHeight, alphaPaint);
    alphaPaint.setShader(null);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    LinearGradient satGradient =
        new LinearGradient(
            padding,
            padding,
            padding + squareWidth,
            padding,
            Color.WHITE,
            Color.HSVToColor(new float[] {hue, 1f, 1f}),
            Shader.TileMode.CLAMP);
    paint.setShader(satGradient);
    canvas.drawRect(padding, padding, padding + squareWidth, padding + squareHeight, paint);
    paint.setShader(null);

    LinearGradient valGradient =
        new LinearGradient(
            0,
            padding,
            0,
            padding + squareHeight,
            Color.TRANSPARENT,
            Color.BLACK,
            Shader.TileMode.CLAMP);
    overlayPaint.setShader(valGradient);
    canvas.drawRect(padding, padding, padding + squareWidth, padding + squareHeight, overlayPaint);
    overlayPaint.setShader(null);

    float hueLeft = padding + squareWidth + padding;
    float hueRight = hueLeft + hueBarWidth;
    if (hueBitmap != null) canvas.drawBitmap(hueBitmap, hueLeft, padding, null);

    float alphaTop = padding + squareHeight + padding;
    float alphaBottom = alphaTop + alphaBarHeight;
    float alphaWidth = squareWidth + hueBarWidth + padding;
    if (alphaBitmap != null) canvas.drawBitmap(alphaBitmap, padding, alphaTop, null);

    float px = padding + sat * squareWidth;
    float py = padding + (1f - val) * squareHeight;
    canvas.drawCircle(px, py, pointerRadius, pointerPaint);
    canvas.drawCircle(px, py, pointerRadius, borderPaint);

    float huePointerY = padding + (hue / 360f) * squareHeight;
    float hueCenterX = (hueLeft + hueRight) / 2;
    canvas.drawCircle(hueCenterX, huePointerY, pointerRadius, pointerPaint);
    canvas.drawCircle(hueCenterX, huePointerY, pointerRadius, borderPaint);

    float alphaPointerX = padding + ((float) alpha / 255f) * alphaWidth;
    float alphaCenterY = (alphaTop + alphaBottom) / 2;
    canvas.drawCircle(alphaPointerX, alphaCenterY, pointerRadius, alphaPointerPaint);
    canvas.drawCircle(alphaPointerX, alphaCenterY, pointerRadius, borderPaint);

    canvas.drawRect(
        padding, padding, padding + squareWidth, padding + squareHeight, boxBorderPaint);
    canvas.drawRect(hueLeft, padding, hueRight, padding + squareHeight, boxBorderPaint);
    canvas.drawRect(padding, alphaTop, padding + alphaWidth, alphaBottom, boxBorderPaint);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    float x = event.getX();
    float y = event.getY();

    float colorSquareLeft = padding;
    float colorSquareRight = padding + squareWidth;
    float colorSquareTop = padding;
    float colorSquareBottom = padding + squareHeight;

    float hueLeft = colorSquareRight + padding;
    float hueRight = hueLeft + hueBarWidth;
    float alphaTop = colorSquareBottom + padding;
    float alphaBottom = alphaTop + alphaBarHeight;

    if (y >= colorSquareTop && y <= colorSquareBottom) {
      if (x >= colorSquareLeft && x <= colorSquareRight) {
        sat = clamp((x - colorSquareLeft) / squareWidth);
        val = 1f - clamp((y - colorSquareTop) / squareHeight);
        createAlphaBitmap();
      } else if (x >= hueLeft && x <= hueRight) {
        hue = 360f * clamp((y - colorSquareTop) / squareHeight);
        createHueBitmap();
        createAlphaBitmap();
      }
    } else if (y >= alphaTop && y <= alphaBottom) {
      float alphaWidth = squareWidth + hueBarWidth + padding;
      alpha = (int) (clamp((x - padding) / alphaWidth) * 255f);
    }

    selectedColor = Color.HSVToColor(alpha, new float[] {hue, sat, val});
    invalidate();

    if (listener != null) listener.onColorChanged(selectedColor);
    return true;
  }

  public void setColor(int color) {
    float[] hsv = new float[3];
    Color.colorToHSV(color, hsv);
    hue = hsv[0];
    sat = hsv[1];
    val = hsv[2];
    alpha = Color.alpha(color);
    selectedColor = color;

    if (viewWidth > 0 && viewHeight > 0) {
      createHueBitmap();
      createAlphaBitmap();
    }
    invalidate();
  }

  public int getSelectedColor() {
    return selectedColor;
  }

  public void setOnColorChangedListener(OnColorChangedListener listener) {
    this.listener = listener;
  }

  private float clamp(float val) {
    return Math.max(0f, Math.min(1f, val));
  }

  private int dpToPx(float dp) {
    return Math.round(
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
  }
}
