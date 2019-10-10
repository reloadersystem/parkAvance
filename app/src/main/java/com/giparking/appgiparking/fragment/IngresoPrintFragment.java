package com.giparking.appgiparking.fragment;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.giparking.appgiparking.ConverterToPDF.TemplatePDF;
import com.giparking.appgiparking.R;
import com.giparking.appgiparking.util.Save;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * A simple {@link Fragment} subclass.
 */
public class IngresoPrintFragment extends Fragment {

    View rootview;
    EditText edTexto;
    Button btnGerar, btnPrint;
    ImageView ivQRCode;
    Bitmap bitmap;
    Button btn_pdfView;
    private TemplatePDF templatePDF;

    private String shorText = "Hola";
    private String longText = "iOS Studio";

    int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;


    public IngresoPrintFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview= inflater.inflate(R.layout.fragment_ingreso_print, container, false);

        edTexto = rootview.findViewById(R.id.edTexto);
        btnGerar = rootview.findViewById(R.id.btnGerar);
        ivQRCode = rootview.findViewById(R.id.ivQRCode);
        btn_pdfView = rootview.findViewById(R.id.pdfView);


        int verificarPermisoWrite = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (verificarPermisoWrite != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                solicitarPermiso(); // sino  ha aceptado los  permisos
            } else
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }

        btnGerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gerarQRCode();

            }
        });


        btn_pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                templatePDF = new TemplatePDF(getContext(), bitmap);
                templatePDF.openDocument();
                templatePDF.addMetadata("Parqueando", "Resembrink", "Libros");
                templatePDF.addTitles("QR", "QRPrinter", "03/10/2019");
                templatePDF.addParagraph(shorText);
                templatePDF.addParagraph(longText);
                templatePDF.closeDocument();
                templatePDF.viewPDF();
            }
        });
        return rootview;
    }

    private void gerarQRCode() {

        String texto = edTexto.getText().toString();


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(texto, BarcodeFormat.QR_CODE, 2000, 2000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQRCode.setImageBitmap(bitmap);

// MARK : guarda imagen
            Save save = new Save();
            save.SaveImage(getContext(), bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }


    private void solicitarPermiso() {


        new AlertDialog.Builder(getContext())
                .setTitle("Autorización")
                .setMessage("Necesito permiso para Almacenar Archivos")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                    }


                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }


}
