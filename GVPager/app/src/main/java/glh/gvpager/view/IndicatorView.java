package glh.gvpager.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import glh.gvpager.R;


/**
 * 指示器
 * Created by glh on 2016-11-07.
 */
public class IndicatorView extends View {
    private Bitmap selectorBitmap, bitmap;

    private int total, selectIndex = 0;
    private Canvas canvas;
    private int width, height;
    private int bitmapWidth, bitmapHeight;
    private int space = 8;
    private Context context;
    private LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public IndicatorView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }


    private void init(Context context) {
        selectorBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_point);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_pointlight);
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        setLayoutParams(params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        width = getWidth();
        height = getHeight();
        drawSelector(selectIndex);

    }

    Paint paint = new Paint();

    private void drawSelector(int index) {
        for (int i = 0; i < total; i++) {
            if (i == index) {
                canvas.drawBitmap(selectorBitmap, (bitmapWidth + space) * i, (height - bitmapHeight) / 2, paint);
                continue;
            }
            canvas.drawBitmap(bitmap, (bitmapWidth + space) * i, (height - bitmapHeight) / 2, paint);
        }
    }

    public void setTotal(int total) {
        this.total = total;
        width = bitmapWidth * total + space * (total - 1);
        height = bitmapHeight * 2;
        params.width = width;
        params.height = height;
        this.setLayoutParams(params);
    }


    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        invalidate();
    }


}
