package com.example.qrcodescannerc4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcodescannerc4.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // objek
    private Button buttonScanning;
    private TextView textViewName, textViewClass, textViewID;

    // QRCode Scanner
    private IntentIntegrator qrscan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View
        buttonScanning = (Button) findViewById(R.id.buttonScan);
        textViewName = (TextView) findViewById(R.id.textViewNama);
        textViewClass = (TextView) findViewById(R.id.textViewKelas);
        textViewID = (TextView) findViewById(R.id.textViewNIM);

        qrscan = new IntentIntegrator(this);

        buttonScanning.setOnClickListener(this);

    }

    // Fungsi untuk QRCode
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Not Scanned", Toast.LENGTH_SHORT).show();
            }
            else {
                // JSON
                try {

                    JSONObject jsonObject = new JSONObject(result.getContents());
                    textViewName.setText(jsonObject.getString("nama"));
                    textViewClass.setText(jsonObject.getString("kelas"));
                    textViewID.setText(jsonObject.getString("nim"));

                }  catch (JSONException e){
                    e.printStackTrace();
                }

                // DIAL UP, NOMOR TELEPON
                try {
                    Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse(result.getContents()));
                    startActivity(intent2);
                } catch (Exception e){
                    e.printStackTrace();
                }

                // QR code berisi lokasi geografis
                try {
                    String geoUri = result.getContents();
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    startActivity(geoIntent);
                } catch (Exception e){
                    e.printStackTrace();
                }

                // Email
                try{
                    String scannedContent = result.getContents();
                    // Cek apakah yang di-scan merupakan alamat email
                    if (scannedContent.contains("@")) {
                        // Kirim email
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", scannedContent.replace("http://", ""), null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QR Code Scanner");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Halo, ini email yang dihasilkan dari QR Code Scanner.");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    } else {
                        // Tampilkan hasil scan
                        Toast.makeText(this, "Scanned : " + scannedContent, Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                Toast.makeText(this, "Scanned : " + result.getContents(), Toast.LENGTH_SHORT).show();
            }

            // WEBVIEW
            if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(visitUrl);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        qrscan.initiateScan();
    }
}