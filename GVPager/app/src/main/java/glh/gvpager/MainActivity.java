package glh.gvpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import glh.gvpager.view.GVPager;
import glh.gvpager.view.IndicatorView;

public class MainActivity extends AppCompatActivity {

    private IndicatorView indicator;
    private GVPager mGVPager;
    private int[] resourceId = {R.drawable.demo1, R.drawable.demo2, R.drawable.demo3, R.drawable.demo1,R.drawable.demo2, R.drawable.demo3, R.drawable.demo1, R.drawable.demo2,R.drawable.demo3, R.drawable.demo1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridPagerAdapter gridPagerAdapter = new GridPagerAdapter(10);
        mGVPager = (GVPager) findViewById(R.id.gvp);
        indicator = (IndicatorView) findViewById(R.id.indicator);
        mGVPager.setIndicator(indicator);
        mGVPager.setAutoDuration(500);
        mGVPager.setPageTransformer(new CubeTransformer());
        mGVPager.setAdapter(gridPagerAdapter);
        mGVPager.play();
    }

    @Override
    protected void onDestroy() {
        mGVPager.stop();
        super.onDestroy();
    }

    public class GridPagerAdapter extends BaseAdapter {

        int mSize;

        public GridPagerAdapter(int size) {
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_gvp, null);
                viewHolder.iv_demo = (ImageView) convertView.findViewById(R.id.iv_demo);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.iv_demo.setImageResource(resourceId[position]);

            return convertView;
        }

    }

    static class ViewHolder {
        ImageView iv_demo;
    }
}
