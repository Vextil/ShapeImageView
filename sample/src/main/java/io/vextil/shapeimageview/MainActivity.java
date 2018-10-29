
package io.vextil.shapeimageview;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import io.vextil.shapeimageview.ShapeImageView.Shape;

public class MainActivity extends AppCompatActivity {

    private int mDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText border = findViewById(R.id.border);
        final EditText padding = findViewById(R.id.padding);
        final ShapeImageView shape = findViewById(R.id.shape);
        final Button change = findViewById(R.id.change);
        RadioGroup decorations = findViewById(R.id.decorations);

        assert change != null;
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert border != null;
                String strBorder = border.getText().toString();
                strBorder = TextUtils.isEmpty(strBorder) ? "0" : strBorder;
                int intBorder = Integer.parseInt(strBorder);

                assert padding != null;
                String strPadding = padding.getText().toString();
                strPadding = TextUtils.isEmpty(strPadding) ? "0" : strPadding;
                int intPadding = Integer.parseInt(strPadding);

                assert shape != null;
                shape.changeShapeType(Shape.ROUNDED);
                shape.setBorderColor(Color.RED);
                shape.setBorderWidth(intBorder);

                if (mDirections != 0) {
                    Drawable decorations = getResources().getDrawable(R.drawable.triangle);
                    shape.setDecorations(mDirections, intPadding, decorations);
                } else {
                    shape.setDecorations(mDirections, intPadding, null);
                }
            }
        });

        assert decorations != null;
        decorations.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.a:
                        mDirections = 0;
                        break;
                    case R.id.b:
                        mDirections = 1;
                        break;
                    case R.id.c:
                        mDirections = 2;
                        break;
                    case R.id.d:
                        mDirections = 3;
                        break;
                    case R.id.e:
                        mDirections = 4;
                        break;

                }
            }
        });

        fillList();
    }

    private void fillList() {
        ListView grid = findViewById(R.id.list);
        assert grid != null;
        grid.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 10;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.item_list, null);
                }
                ShapeImageView shapeView = convertView
                        .findViewById(R.id.shape);
                switch (position % 2) {
                    case 0:
                        shapeView.changeShapeType(Shape.ROUNDED);
                        break;
                    case 1:
                        shapeView.changeShapeType(Shape.CIRCLE);
                        break;
                }

                if (position < 2) {
                    shapeView.setBorderWidth(0);
                } else if (position < 4) {
                    shapeView.setBorderWidth(2);
                } else if (position < 6) {
                    shapeView.setBorderWidth(4);
                } else if (position < 8) {
                    shapeView.setBorderWidth(6);
                } else if (position < 10) {
                    shapeView.setBorderWidth(8);
                }
                return convertView;
            }
        });
    }
}
