package com.example.tata_consultor;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.airbnb.lottie.LottieAnimationView;
import com.example.tata_consultor.Clases.Producto;
import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ConsultorPrecioActivity extends AppCompatActivity {

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;

    private int tipocodigo = 2;
    private boolean boleananimationview = true;
    private boolean boleananimationbusqeudaview = false;
    private boolean boleanlinearprecio = false;

    private OkHttpClient Pickinghttp;
    private String sucursal = "2";
    private String m_ip = "200.40.253.210";
    private Handler m_handler = new Handler(); // Main thread
    private Request RequestPicking;
    private ProgressDialog dialog;
    private EditText editcodigobarramanual;

    private TextView txtdescripcion1, txtdescripcion2, txtprecioproducto, txtcodigoproducto, txtcodigobarraproducto;

    ActionBar actionBar;
    ConstraintLayout constrain;
    LinearLayout linearprecio;
    ImageView imgescaner;
    LottieAnimationView animationview;
    LottieAnimationView animationbusquedaview;
    boolean visible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultor_precio);

        constrain = findViewById(R.id.constainlayot);
        actionBar = getSupportActionBar();
        linearprecio = findViewById(R.id.linear_precio);
        animationview = findViewById(R.id.animation_view);
        animationbusquedaview = findViewById(R.id.animationbusqueda_view);

        Button btnon = findViewById(R.id.btn_on);
        Button btnoff = findViewById(R.id.btn_off);
        editcodigobarramanual = findViewById(R.id.edit_codigo_barra_manual);


        Button test = findViewById(R.id.btntest);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                                EnableDialog(true, "cargando");
                                String a = "{\n" +
                                        "\"Codigo\": \"250490000\",\n" +
                                        "\"Nombre\": \"YERBA COMPUESTA SERENA CANARIA 1.00 K\",\n" +
                                        "\"Moneda\": \"moneda\",\n" +
                                        "\"Precio\": \"195.00\",\n" +
                                        "\"CodBarras\": \"7730241010294\",\n" +
                                        "\"Prioridad\": \"C\",\n" +
                                        "\"Aux\": \"\"\n" +
                                        "}";
                                Gson g = new Gson();
                                Producto p = g.fromJson(a, Producto.class);
                                p.setEstado("NO");
                                mostrar_datos_view(p);

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }


                                 EnableDialog(false, "mostrando");


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();


            }
        });
        txtdescripcion1 = findViewById(R.id.txt_descripcion_1);
        txtdescripcion2 = findViewById(R.id.txt_descripcion_2);
        txtprecioproducto = findViewById(R.id.txt_precio_producto);
        txtcodigoproducto = findViewById(R.id.txt_codigo_producto);
        txtcodigobarraproducto = findViewById(R.id.txt_codigo_barra_producto);

        constrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidebarras();
            }
        });

        btnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  showlinearprecio();

             // EnableDialog(true,"cargando",false);

            }
        });

        btnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  hidelinearprecio();
               // delayedHide(AUTO_HIDE_DELAY_MILLIS);
               // EnableDialog(true,"mostrando",false);

            }
        });


        editcodigobarramanual.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean procesado = false;

                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_TAB || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {


                    presionarboton();
                    procesado = true;

                }
                return procesado;
            }
        });

        hidebarras();
        cargardatospreference();
    }

    private void presionarboton() {

        String codigoimprimir = editcodigobarramanual.getText().toString().trim();
        if (!codigoimprimir.equals("")) {
            request(codigoimprimir);
            editcodigobarramanual.setText("");
            ocultarteclado();
        }
        if (editcodigobarramanual.isFocused()) {
            ocultarteclado();
        }
    }

    public void ocultarteclado() {

        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        hidebarras();
    }

    public void request(final String codigoverificar) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Pickinghttp = new OkHttpClient();
                MediaType mediaType = MediaType.parse("text/xml");

                RequestBody body = RequestBody.create(mediaType, "");

                RequestPicking = new Request.Builder()
                        .url("https://precioonline.tata.com.uy/articulos.php?tipocodigo= "+ tipocodigo+ "&codigo=" + codigoverificar)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();

                EnableDialog(true, "cargando");

                Pickinghttp.newCall(RequestPicking).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        EnableDialog(false, "limpiando");
                        DisplayPrintingStatusMessage("Conexion Fallo");
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (response.isSuccessful()) {
                            try {
                                final String myResponse = response.body().string();
                                Gson g = new Gson();
                                Producto a = g.fromJson(myResponse, Producto.class);
                                a.setEstado("NO");

                                if (!a.getNombre().equals(" ") || !a.getPrecio().equals(" ")|| !a.getNombre().isEmpty() || !a.getPrecio().isEmpty()){

                                    mostrar_datos_view(a);

                                }else{
                                    DisplayPrintingStatusMessage("Producto NO registrado");
                                }
                                EnableDialog(false, "mostrando");
                            } catch (IOException e) {
                                e.printStackTrace();
                                EnableDialog(false, "limpiando");
                            }
                        } else {
                            DisplayPrintingStatusMessage("Error con la conexion Wifi.. Reintentar");
                            EnableDialog(false, "limpiando");

                        }
                    }

                });
            }

        });

    }

    public void requestPruebaMejorado(final String codigoverificar) {


                Pickinghttp = new OkHttpClient();
                MediaType mediaType = MediaType.parse("text/xml");

                RequestBody body = RequestBody.create(mediaType, "");

                RequestPicking = new Request.Builder()
                        .url("https://precioonline.tata.com.uy/articulos.php?tipocodigo= "+ tipocodigo+ "&codigo=" + codigoverificar)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();

               // EnableDialog(true, "cargando", false);

                Pickinghttp.newCall(RequestPicking).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                       // EnableDialog(false, "limpiando", false);
                        DisplayPrintingStatusMessage("Conexion Fallo");
                    }

                    @Override
                    public void onResponse(Call call, final Response response){

                        if (response.isSuccessful()) {

                            try {
                                final String myResponse = response.body().string();
                                Gson g = new Gson();
                                Producto a = g.fromJson(myResponse, Producto.class);
                                a.setEstado("NO");

                                if (!a.getNombre().equals(" ") || !a.getPrecio().equals(" ")|| !a.getNombre().isEmpty() || !a.getPrecio().isEmpty()){

                                    mostrar_datos_view(a);

                                }else{
                                    DisplayPrintingStatusMessage("Producto NO registrado");
                                }

                                //EnableDialog(false, "mostrando", false);

                            } catch (IOException e) {
                                e.printStackTrace();
                                //EnableDialog(false, "limpiando", false);
                            }


                        } else {
                            DisplayPrintingStatusMessage("Error con la conexion Wifi.. Reintentar");
                            //EnableDialog(false, "limpiando", false);

                        }
                    }

                });
    }


    void hidebarras() {
        constrain.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if (actionBar != null) {
            actionBar.hide();
        }
        visible = true;
    }

    public void EnableDialog(final boolean value, final String mensaje) {
        m_handler.post(new Runnable() {
            @Override
            public void run() {
                if (mensaje.equals("cargando")) {

                    animationview.setVisibility(View.INVISIBLE);
                    animationbusquedaview.setVisibility(View.VISIBLE);
                    linearprecio.setVisibility(View.INVISIBLE);

                    boleananimationview = false;
                    boleananimationbusqeudaview = true;
                    boleanlinearprecio = false;

                    Log.e("ENTRO A","ENTRO1");

                } else if (mensaje.equals("mostrando")){

                    animationview.setVisibility(View.INVISIBLE);
                    animationbusquedaview.setVisibility(View.INVISIBLE);
                    linearprecio.setVisibility(View.VISIBLE);

                    boleananimationview = false;
                    boleananimationbusqeudaview = false;
                    boleanlinearprecio = true;

                    Log.e("ENTRO B","ENTRO1");
                    ocultando();

                }else{
                    //limpiando
                    animationview.setVisibility(View.VISIBLE);
                    animationbusquedaview.setVisibility(View.INVISIBLE);
                    linearprecio.setVisibility(View.INVISIBLE);

                    boleananimationview = true;
                    boleananimationbusqeudaview = false;
                    boleanlinearprecio = false;

                    Log.e("ENTRO C","ENTRO1");
                }
            }
        });
    }

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {

            animationview.setVisibility(View.VISIBLE);
            animationbusquedaview.setVisibility(View.INVISIBLE);
            linearprecio.setVisibility(View.INVISIBLE);

            boleananimationview = true;
            boleananimationbusqeudaview = false;
            boleanlinearprecio = false;

            Log.e("ENTRO H2","ENTRO1");
        }
    };

    private void cargardatospreference() {
        Bundle parametros = getIntent().getExtras();
        if (parametros != null) {
            sucursal = (parametros.getString("suc"));
            m_ip = (parametros.getString("ip"));

        } else {
            sucursal = (parametros.getString("suc"));
            m_ip = (parametros.getString("ip"));
            Toast.makeText(getApplicationContext(), "No hay datos a mostrar", Toast.LENGTH_LONG).show();
        }
    }

    private void ocultando() {
        Log.e("ENTRO H","ENTRO1");
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 20000);
    }

    public void DisplayPrintingStatusMessage(final String MsgStr) {

        m_handler.post(new Runnable() {
            public void run() {
                showToast(MsgStr);//2018 PH
            }// run()
        });

    }

    public void showToast(final String toast) {

        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();

    }

    private void mostrar_datos_view(final Producto a) {
        m_handler.post(new Runnable() {
            public void run() {

                if (a != null) {

                    try {

                        txtdescripcion1.setText(a.getNombre());
                        txtcodigoproducto.setText(a.getCodigo());
                        txtcodigobarraproducto.setText(a.getCodBarras());
                        txtprecioproducto.setText(a.getPrecio());



                        linearprecio.setBackground(ContextCompat.getDrawable(ConsultorPrecioActivity.this, R.drawable.ic_tag_60x30_amarillo));


                    } catch (Exception e) {
                        DisplayPrintingStatusMessage("No disponible");
                        txtdescripcion1.setText("");
                        txtdescripcion2.setText("");
                        txtcodigoproducto.setText("");
                        txtcodigobarraproducto.setText("");
                        txtprecioproducto.setText("");

                        // hidelinearprecio();
                    }


                }
            }// run()
        });


    }








}