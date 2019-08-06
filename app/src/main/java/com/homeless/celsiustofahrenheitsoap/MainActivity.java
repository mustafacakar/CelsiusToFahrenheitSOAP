package com.homeless.celsiustofahrenheitsoap;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    // <-------   SOAPAction: "https://www.w3schools.com/xml/CelsiusToFahrenheit"
    //             Servis sağlayıcımın İSİM ALANIdır. xml sonundaki / a kadar alınmalı     ---- >
    final String NAMESPACE = "https://www.w3schools.com/xml/";

    //Çağrılacak servis bilgisi. // https://www.w3schools.com/xml/tempconvert.asmx?op=CelsiusToFahrenheit
    // adres cubundan aldım.Soru işaretine kadarki kısım.
    final String URL = "https://www.w3schools.com/xml/tempconvert.asmx"   ;

    final String SOAP_ACTION = "https://www.w3schools.com/xml/CelsiusToFahrenheit";     //NAMESPACE + METHOD NAME
    final String METHOD_NAME =  "CelsiusToFahrenheit";


    //Tanımlamalarım

    Button btnConvert;
    TextView txtSonuc;
    EditText etGiris;
    String Celcius;
    String Fahreneit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConvert = (Button) findViewById(R.id.btnConvert);
        txtSonuc = (TextView) findViewById(R.id.txtSonuc);
        etGiris = (EditText) findViewById(R.id.etGiris);


        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etGiris.getText().length() != 0 && etGiris.getText().toString() !=null){
                    Celcius = etGiris.getText().toString();

                    //Asynctask.execute ();
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();

                }else{
                    txtSonuc.setText("Lütfen Celcius Değerini giriniz");
                }

            }
        });

    }

    //AsenkronTask
    private class AsyncCallWS extends AsyncTask<String,Void,Void>{  //AsyncTask<Parametreler ne türdeyse onları yazdım>

        //Asynctask fonksiyonlarım
        // onPreExecute => İşlemden önce
        // DoInBackground => İşlem
        // OnPostExecute => İşlemden Sonra
        // onProgressUpdate => İşlem devam ederken , dialog çıkarmaa vs.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtSonuc.setText("Hesaplanıyor");
        }

        @Override
        protected Void doInBackground(String... params) {     //  protected Object doInBackground(Object[] objects) ->  protected Void doInBackground(String... objects)
            //String değer döneceği için parametreyi string'e çevirdim.

            //getFahreneit(celcius)
            //İstekte buluunucam

            getFahreneit(Celcius);
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            txtSonuc.setText(Fahreneit+ "F");
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    public void getFahreneit(String celcius){
        // 3 Temel adım -> 1)Request i oluştur. 2)Mektubun içine Koy . 3)Gönder
        SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME); // Talebi olusturdum.
        PropertyInfo celciusPi = new PropertyInfo(); // Celcius Property Info
        celciusPi.setName("Celsius");       //  <Celsius>string</Celsius>       TagName i yazdık
        celciusPi.setValue(celcius);        // Bunlara karşılık kendi yolladıgım celcius değeri
        celciusPi.setType(double.class);    // Verimin tipi
        request.addProperty(celciusPi); // request'imi olusturdum yukarda ve bu ozellikleri requestime ekledim.

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);  //Requesti mektubun içine yerleştirdik .

        //Gönderim
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        try {
            androidHttpTransport.call(SOAP_ACTION,envelope);        //2 parametre alıyor. SOAP_ACTION ımın adı ve SoapSerializationEnvelope umun adı = envelope
          //  envelope.getResponse()  //object deger dondurdu.

            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();        //CEVABIMI ELDE ETTIGIM NOKTA BURASI = response
            Fahreneit = response.toString(); // Fahreneit değişkenime atadım cevabımı

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
