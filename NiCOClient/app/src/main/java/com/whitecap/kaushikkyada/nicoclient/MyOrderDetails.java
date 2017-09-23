package com.whitecap.kaushikkyada.nicoclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class MyOrderDetails extends AppCompatActivity {

    String order_id;
    String item;
    String quantity;
    String order_time;
    TextView txt_order_id;
    ImageView img_qrcode;
    TextView txt_item;
    TextView txt_quantity;
    TextView txt_time;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;
    Thread thread ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);

        Intent i=getIntent();
        order_id=i.getExtras().getString("order_id");
        item=i.getExtras().getString("item");
        quantity=i.getExtras().getString("quantity");
        order_time=i.getExtras().getString("order_time");
        Toast.makeText(getApplication(),order_id+","+item,Toast.LENGTH_SHORT).show();

        txt_order_id=(TextView)findViewById(R.id.txt_order_id);
        img_qrcode=(ImageView) findViewById(R.id.img_qrcode);
        txt_item=(TextView)findViewById(R.id.txt_item);
        txt_quantity=(TextView)findViewById(R.id.txt_quantity);
        txt_time=(TextView)findViewById(R.id.txt_time);

        txt_order_id.setText("Order Id: "+order_id);
        txt_item.setText(item);
        txt_quantity.setText("Quantity: "+quantity);
        txt_time.setText("Oreder Time: "+order_time);
        try {
            bitmap = TextToImageEncode(order_id);

            img_qrcode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
