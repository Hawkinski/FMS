package com.example.sachin.fms.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class DrawCanvas extends View {


    private Bitmap _Bitmap;
    private Canvas _Canvas;
    private Path _Path;
    private Paint _BitmapPaint;
    private Paint _paint;
    private float _mX;
    private float _mY;
    private float TouchTolerance = 4;
    private float LineThickness = 4;

    public DrawCanvas(Context context, AttributeSet attr) {
        super(context, attr);
        _Path = new Path();
        _BitmapPaint = new Paint(Paint.DITHER_FLAG);
        _paint = new Paint();
        _paint.setAntiAlias(true);
        _paint.setDither(true);
        _paint.setColor(Color.argb(255, 0, 0, 0));
        _paint.setStyle(Paint.Style.STROKE);
        _paint.setStrokeJoin(Paint.Join.ROUND);
        _paint.setStrokeCap(Paint.Cap.ROUND);
        _paint.setStrokeWidth(LineThickness);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        _Bitmap = Bitmap.createBitmap(w, (h > 0 ? h : ((View) this.getParent()).getHeight()), Bitmap.Config.ARGB_8888);
        _Canvas = new Canvas(_Bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(_Bitmap, 0, 0, _BitmapPaint);
        canvas.drawPath(_Path, _paint);
    }

    private void TouchStart(float x, float y) {
        _Path.reset();
        _Path.moveTo(x, y);
        _mX = x;
        _mY = y;
    }

    private void TouchMove(float x, float y) {
        float dx = Math.abs(x - _mX);
        float dy = Math.abs(y - _mY);

        if (dx >= TouchTolerance || dy >= TouchTolerance) {
            _Path.quadTo(_mX, _mY, (x + _mX) / 2, (y + _mY) / 2);
            _mX = x;
            _mY = y;
        }
    }

    private void TouchUp() {
        if (!_Path.isEmpty()) {
            _Path.lineTo(_mX, _mY);
            _Canvas.drawPath(_Path, _paint);
        } else {
            _Canvas.drawPoint(_mX, _mY, _paint);
        }

        _Path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                TouchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                TouchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                TouchUp();
                invalidate();
                break;
        }

        return true;
    }

    public void ClearCanvas() {
        _Canvas.drawColor(Color.WHITE);
        invalidate();
    }

    public byte[] getBytes() {
        HashMap<String, Bitmap> list;
        list = getBitmap();
        Bitmap b = list.get("sign");


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public HashMap<String, Bitmap> getBitmap() {
        View v = (View) this.getParent();
        Bitmap.Config config = Bitmap.Config.ARGB_8888;

        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), config);

        Bitmap orig = b.copy(config, false);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);

        HashMap<String, Bitmap> list = new HashMap<>();

        list.put("empty", orig);
        list.put("sign", b);

        return list;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (getParent() != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(event);
    }


	/*public int width;
    public int height;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	Context context;
	private Paint mPaint;
	private float mX, mY;
	private static final float TOLERANCE = 5;
	public Boolean cc = false;


    public DrawCanvas(Context c) {
        super(c);
        context = c;


        // we set a new Path
        mPath = new Path();

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
    }
	public DrawCanvas(Context c, AttributeSet attrs) {
		super(c, attrs);
		context = c;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setNestedScrollingEnabled(false);
        }
		// we set a new Path
		mPath = new Path();

		// and we set a new Paint with the desired attributes
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeWidth(4f);
	}


	// override onSizeChanged
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		// your Canvas will draw onto the defined Bitmap
		mBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	// override onDraw
	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		if(cc)
		{

			canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
			cc = false;

		}

		// draw the mPath with the mPaint on the canvas when onDraw
		canvas.drawPath(mPath, mPaint);
	}

	// when ACTION_DOWN start touch according to the x,y values
	private void startTouch(float x, float y) {
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	// when ACTION_MOVE move touch according to the x,y values
	private void moveTouch(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOLERANCE || dy >= TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	public void clearCanvas() {

		mPath = new Path();
		Paint clearPaint = new Paint();
		clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		mCanvas.drawRect(0, 0, 0, 0, clearPaint);

		cc = false;
	}



	private void upTouch() {
		mPath.lineTo(mX, mY);
	}

	//override the onTouchEvent
	@Override
	public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			startTouch(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:

			moveTouch(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:

			upTouch();
			invalidate();
			break;
		}
		return true;
	}

	public byte[] getBytes() {
		Bitmap b = getBitmap();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public Bitmap getBitmap() {
		View v = (View) this.getParent();
		Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
		v.draw(c);

		return b;
	}*/


}
