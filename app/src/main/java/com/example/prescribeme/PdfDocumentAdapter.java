package com.example.prescribeme;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class PdfDocumentAdapter extends PrintDocumentAdapter {

    Context context;
    String path, name;

    public PdfDocumentAdapter(Context context, String path, String name) {
        this.context = context;
        this.path = path;
        this.name =  name;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        if(cancellationSignal.isCanceled())
            callback.onLayoutCancelled();
        PrintDocumentInfo.Builder builder=new PrintDocumentInfo.Builder(name);
        builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build();
        callback.onLayoutFinished(builder.build(), !newAttributes.equals(oldAttributes));
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        InputStream in = null;
        OutputStream out = null;
        try{
            File file=new File(path);
            in = new FileInputStream(file);
            out = new FileOutputStream(destination.getFileDescriptor());
            byte[] buffer=new byte[16384];
            int size;
            while ((size=in.read(buffer))>0 && !cancellationSignal.isCanceled())
            {
                out.write(buffer, 0, size);
            }
            if(cancellationSignal.isCanceled())
                callback.onWriteCancelled();
            else
                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        }
        catch (Exception e)
        {
            callback.onWriteFailed(e.getMessage());
            Toast.makeText(context, "Error while writing PDF: "+e, Toast.LENGTH_LONG).show();
        }
        finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                Toast.makeText(context, "Error while ending processes: "+e, Toast.LENGTH_LONG).show();
            }
        }
    }
}
