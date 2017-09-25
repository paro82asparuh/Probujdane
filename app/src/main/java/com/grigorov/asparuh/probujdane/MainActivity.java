package com.grigorov.asparuh.probujdane;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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
    private static String BesediDatbaseURL = "https://1fichier.com/?sfl4mtzwbi";
    ProgressDialog mProgressDialog;

    private static double SPACE_KB = 1024;
    private static double SPACE_MB = 1024 * SPACE_KB;
    private static double SPACE_GB = 1024 * SPACE_MB;
    private static double SPACE_TB = 1024 * SPACE_GB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        BesediDatabaseOk = checkUpdateBesediDatabase();
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
                showErrorMessage("База данни ок");
                return true;
            }
            else {
                DialogFragment newFragment = new BesediUpdateDialogFragment();
                newFragment.show(getSupportFragmentManager(), "besediUpdateDialog");
                return false;
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

    @Override
    public void onBesediUpdateDialogNegativeClick(DialogFragment dialog) {
        showErrorMessage("Недей сваля");
    }

    @Override
    public void onBesediUpdateDialogPositiveClick(DialogFragment dialog) {

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage(getString(R.string.download_progress_message));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
        downloadTask.execute(BesediDatbaseURL);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });

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
