package net.mekajiki.lib.android.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

public class AsyncLoadableImageView extends ImageView {
    private int mHeight;
    private int mWidth;

    public AsyncLoadableImageView(Context context) {
        super(context);
    }

    public AsyncLoadableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AsyncLoadableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        mWidth = width;
        mHeight = height;
    }

    public void loadAsync(URL url){
        setVisibility(INVISIBLE);
        new AsyncLoader().execute(url);
    }

    class AsyncLoader extends AsyncTask<Object, Void, Bitmap> {
        private URL url;

        public AsyncLoader(){
        }

        private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            if (reqWidth == 0 && reqHeight == 0) {
                return 1;
            }
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        @Override
        protected Bitmap doInBackground(Object...urls) {
            this.url = (URL)urls[0];

            ImageView imageView = AsyncLoadableImageView.this;
            imageView.setTag(url.toString());

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            Bitmap bitmap = null;
            try {
                BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);

                options.inSampleSize = calculateInSampleSize(options, mHeight, mWidth);

                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            AsyncLoadableImageView imv = AsyncLoadableImageView.this;

            if (!imv.getTag().toString().equals(url.toString())) {
               /* The path is not same. This means that this
                  image view is handled by some other async task.
                  We don't do anything and return. */
                return;
            }

            if(result != null && imv != null){
                imv.setVisibility(View.VISIBLE);
                imv.setImageBitmap(result);
            }else{
                imv.setVisibility(View.INVISIBLE);
            }
        }
    }
}
