package com.grigorov.asparuh.probujdane;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity implements BesediUpdateDialogFragment.BesediUpdateDialogListener {

    private int SelectedBesediType;
    public final static int Nedelni_Besedi =1;
    public final static int OOK_Besedi =2;
    public final static int MOK_Besedi =3;
    public final static int Utrinni_Slova_Besedi =4;
    public final static int Syborni_Besedi =5;
    public final static int MladejkiSyborni_Besedi =6;
    public final static int Rilski_Besedi =7;
    public final static int PoslednotoSlovo_Besedi =8;
    public final static int PredSestrite_Besedi =9;
    public final static int PredRykovoditelite_Besedi =10;
    public final static int Izvynredni_Besedi =11;
    public final static int KlasNaDobrodetelite_Besedi =12;
    public final String besediDatabaseName = "besedi_sqlite.db";
    public final String besediDatabaseArchiveName = "besedi_sqlite.zip";
    private static Context context;
    private boolean BesediDatabaseOk;
    //private static String BesediDatbaseURL = "https://drive.google.com/file/d/0B7wdOuW-OvnhY2VVQktRM1hZcEE/view?usp=sharing";
    //private static String BesediDatbaseURL = "https://drive.google.com/uc?export=download&id=0B7wdOuW-OvnhY2VVQktRM1hZcEE";
    //private static String BesediDatbaseURL = "https://1fichier.com/?sfl4mtzwbi";
    //private static String BesediDatbaseURL = "https://1fichier.com/?6bvwzc10hj";
    //private static String BesediDatbaseURL = "http://www.filetolink.com/09f8b5b6ef";
    //private static String BesediDatbaseURL = "https://lookaside.fbsbx.com/file/besedi_sqlite.zip?token=AWzhWu9-f9d0j9miEHt8qyc-n_aZyj6TT_8zTXEjTUCAcz4IYtUVR3-lR7b3WgNXYo-lsRMbCipq0lyoaMslLudYUjDY1U8uH8U4b_DgRYSeFpV2HeP6lag99Pya-RqBMjnl55wNSA_u_-Egd52LxeJ3yRF7VZ7zEaaDDpmz0l1Ow-0X64sMbnJNIquHnLxc0mbq4y398E3NvTeKuExplXpA";
    //private static String BesediDatbaseURL = "https://drive.google.com/uc?export=download&confirm=vucr&id=0B7wdOuW-OvnhY2VVQktRM1hZcEE";
    //private static String BesediDatbaseURL = "https://drive.google.com/uc?export=download&id=0B7wdOuW-OvnhY2VVQktRM1hZcEE";
    //private static String BesediDatbaseURL = "https://drive.google.com/uc?export=download&amp;confirm=rL69&amp;id=0B7wdOuW-OvnhY2VVQktRM1hZcEE";
    //private static String BesediDatbaseURL = "https://drive.google.com/uc?export=download&confirm=rL69&id=0B7wdOuW-OvnhY2VVQktRM1hZcEE";
    private static String BesediDatbaseURL = "https://dl.dropboxusercontent.com/s/qfaxecicfvf1y34/besedi_sqlite.zip?dl=0";
    ProgressDialog mProgressDialog;
    private DownloadManager downloadManager;
    private DownloadManager downloadManagerChecker;
    private long downloadReference;

    private static double SPACE_KB = 1024;
    private static double SPACE_MB = 1024 * SPACE_KB;
    private static double SPACE_GB = 1024 * SPACE_MB;
    private static double SPACE_TB = 1024 * SPACE_GB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        BesediDatabaseOk = checkUpdateBesediDatabase();

        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }

    public void startBesediMenuTask (View view) {
        if (BesediDatabaseOk==false) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk==true) {
            Intent intent = new Intent(this, BesediMenuActivity.class);
            startActivity(intent);
        }
    }

    public void startDrugiBesediMenuTask (View view) {
        if (BesediDatabaseOk==false) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk==true) {
            Intent intent = new Intent(this, DrugiBesediMenuActivity.class);
            startActivity(intent);
        }
    }

    public void startMolitviTask (View view) {
        Intent intent = new Intent(this, MolitviMenuActivity.class);
        startActivity(intent);
    }

    public void startFormuliTask (View view) {
        Intent intent = new Intent(this, FormuliMenuActivity.class);
        startActivity(intent);
    }

    public void startMusicTask (View view) {
        Intent intent = new Intent(this, MusicMenuActivity.class);
        startActivity(intent);
    }

    public void startNotificationsTask (View view) {
        Intent intent = new Intent(this, NotificationsMenuActivity.class);
        startActivity(intent);
    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        startActivity(intent);
    }

    public void startOptionsMenuTask (View view) {
        Intent intent = new Intent(this, OptionsMenuActivity.class);
        startActivity(intent);
    }

    public void startNedelniBesediTask(View view) {
        SelectedBesediType = Nedelni_Besedi;
        startBesediListTask(view);
    }

    public void startOOKBesediTask(View view) {
        SelectedBesediType = OOK_Besedi;
        startBesediListTask(view);
    }

    public void startMOKBesediTask(View view) {
        SelectedBesediType = MOK_Besedi;
        startBesediListTask(view);
    }

    public void startUtrinniSlovaBesediTask(View view) {
        SelectedBesediType = Utrinni_Slova_Besedi;
        startBesediListTask(view);
    }

    public void startSyborniBesediTask(View view) {
        SelectedBesediType = Syborni_Besedi;
        startBesediListTask(view);
    }

    private void startBesediListTask(View view) {
        if (BesediDatabaseOk==false) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk==true) {
            Intent intent = new Intent(this, BesediListActivity.class);
            intent.putExtra("com.grigorov.asparuh.probujdane.SelectedBesediTypeVar", SelectedBesediType);
            startActivity(intent);
        }
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadOnly() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }


    private boolean checkUpdateBesediDatabase () {

        if (isExternalStorageWritable()==true) {
            File file = new File(context.getExternalFilesDir(null), besediDatabaseName);
            if (file.exists()==true) {
                return true;
            }
            else {
                if ( checkDownloadOngoing() == true ) {
                    showErrorMessage(getString(R.string.download_ongoing));
                    return false;
                } else {
                    DialogFragment newFragment = new BesediUpdateDialogFragment();
                    newFragment.show(getSupportFragmentManager(), "besediUpdateDialog");
                    return false;
                }
            }
        } else if (isExternalStorageReadOnly()==true) {
            File file = new File(context.getExternalFilesDir(null), besediDatabaseName);
            if (file.exists()==true) {
                return true;
            }
            else {
                showErrorMessage(getString(R.string.no_besedi_database_found_and_read_only_external_storage));
                return false;
            }
        } else {
            showErrorMessage(getString(R.string.no_external_storage_is_found));
            return false;
        }

    }

    private void showErrorMessage(String errorMesaage) {
        Toast toast = Toast.makeText(context, errorMesaage, LENGTH_LONG);
        toast.show();
    }

    private boolean checkDownloadOngoing () {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_PAUSED|
                DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_PENDING);
        Cursor cursor = downloadManager.query( query );
        for (int i = 0; i < cursor.getCount() ; i++)
        {
            cursor.moveToPosition(i);
            String iTitle = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
            String iDescription = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
            if (
                    ( iTitle.equals( getString(R.string.download_title))) &&
                    ( iDescription.equals( getString(R.string.download_description)))
                    )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBesediUpdateDialogNegativeClick(DialogFragment dialog) {
        showErrorMessage(getString(R.string.dont_download));
    }

    @Override
    public void onBesediUpdateDialogPositiveClick(DialogFragment dialog) {

        Uri Download_Uri = Uri.parse(BesediDatbaseURL);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        //Set the title of this download, to be displayed in notifications (if enabled).
        request.setTitle(getString(R.string.download_title));
        //Set a description of this download, to be displayed in notifications (if enabled)
        request.setDescription(getString(R.string.download_description));
        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(this,null,besediDatabaseArchiveName);
        //Enqueue a new download and same the referenceId
        downloadReference = downloadManager.enqueue(request);

        //mProgressDialog = new ProgressDialog(MainActivity.this);
        //mProgressDialog.setMessage(getString(R.string.download_progress_message));
        //mProgressDialog.setIndeterminate(true);
        //mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //mProgressDialog.setCancelable(true);

        //final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
        //downloadTask.execute(BesediDatbaseURL);

        //mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
        //    @Override
        //    public void onCancel(DialogInterface dialog) {
        //        downloadTask.cancel(true);
        //    }
        //});

    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId) {
                showErrorMessage(getString(R.string.download_done));

                // Unzip
                File zipFile = new File(context.getExternalFilesDir(null), besediDatabaseArchiveName);
                try {
                    unzip(zipFile, context.getExternalFilesDir(null));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                showErrorMessage(getString(R.string.database_ready));

                BesediDatabaseOk = true;

                // delete the archive file
                try {
                    zipFile.delete();
                }
                catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }

            }

        }

    };

    private static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
        }
    }





private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        long total;
        Integer fileLength;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                fileLength = new Integer(connection.getContentLength());

                File file = new File(context.getExternalFilesDir(null), besediDatabaseArchiveName);
                String filePath = new String(file.getAbsolutePath());

                // Check if there is enough space
                StatFs stat = new StatFs(filePath);
                long blockSize = stat.getBlockSizeLong();
                long availableBlocks = stat.getAvailableBlocksLong();
                long availableBytes = availableBlocks * blockSize;
                if ( availableBytes < fileLength) {
                    showErrorMessage(getString(R.string.not_enough_memeory)+"(" + bytes2String(fileLength) + ")");
                    return null;
                }

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(filePath);
                byte data[] = new byte[4096];
                total = new Long(0);
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
            mProgressDialog.setProgressNumberFormat((bytes2String(total)) + "/" + (bytes2String(fileLength)));
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }

        public String bytes2String(long sizeInBytes) {

            NumberFormat nf = new DecimalFormat();
            nf.setMaximumFractionDigits(2);

            try {
                if ( sizeInBytes < SPACE_KB ) {
                    return nf.format(sizeInBytes) + " Byte(s)";
                } else if ( sizeInBytes < SPACE_MB ) {
                    return nf.format(sizeInBytes/SPACE_KB) + " KB";
                } else if ( sizeInBytes < SPACE_GB ) {
                    return nf.format(sizeInBytes/SPACE_MB) + " MB";
                } else if ( sizeInBytes < SPACE_TB ) {
                    return nf.format(sizeInBytes/SPACE_GB) + " GB";
                } else {
                    return nf.format(sizeInBytes/SPACE_TB) + " TB";
                }
            } catch (Exception e) {
                return sizeInBytes + " Byte(s)";
            }

        }

    }


}
