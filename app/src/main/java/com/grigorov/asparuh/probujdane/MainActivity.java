package com.grigorov.asparuh.probujdane;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import java.io.BufferedInputStream;
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
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class MainActivity extends AppCompatActivity implements BesediUpdateDialogFragment.BesediUpdateDialogListener {

    public final static int MY_PERMISSIONS_REQUEST_INTERNET=0;
    public final static int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE=1;
    public final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=2;
    public final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=3;
    public final static int MY_PERMISSIONS_REQUEST_WAKE_LOCK=4;
    public final static int MY_PERMISSIONS_REQUEST_STORAGE=5;

    public final static int MY_UPDATE_REQUEST_CODE = 1;

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
    public final String formuliDatabaseName = "formuli_sqlite.db";
    public final String molitviDatabaseName = "molitvi_sqlite.db";
    public final String musicDatabaseName = "music_sqlite.db";
    public final String naukaVyzDatabaseName = "nauka_vyzpitanie_sqlite.db";
    public final String zavetDatabaseName = "zavet_sqlite.db";
    public final String besediDatabaseArchiveName = "besedi_sqlite.zip";
    private static Context context;
    private boolean BesediDatabaseOk;
    private boolean unzipOngoing;
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
    private static final String BesediDatbaseURL = "https://dl.dropboxusercontent.com/s/qfaxecicfvf1y34/besedi_sqlite.zip?dl=0";
    ProgressDialog mProgressDialog;
    private DownloadManager downloadManager;
    private long downloadReference;

    private static final double SPACE_KB = 1024;
    private static final double SPACE_MB = 1024 * SPACE_KB;
    private static final double SPACE_GB = 1024 * SPACE_MB;
    private static final double SPACE_TB = 1024 * SPACE_GB;

    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        handlePermissions();
        checkAppUpdate();

        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        stopMusicDownloads();
        unzipOngoing = false;
        BesediDatabaseOk = checkUpdateBesediDatabase();

        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (Build.VERSION.SDK_INT >= 26) {
            registerReceiver(downloadReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(downloadReceiver, filter);
        }
    }

    // Checks that the update is not stalled during 'onResume()'.
    // However, you should execute this check at all entry points into the app.
    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            IMMEDIATE,
                                            this,
                                            MY_UPDATE_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }

    public void startBesediMenuTask (View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            Intent intent = new Intent(this, BesediMenuActivity.class);
            startActivity(intent);
        }
    }

    public void startDrugiBesediMenuTask (View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            Intent intent = new Intent(this, DrugiBesediMenuActivity.class);
            startActivity(intent);
        }
    }

    public void startMolitviTask (View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            Intent intent = new Intent(this, MolitviMenuActivity.class);
            startActivity(intent);
        }
    }

    public void startFormuliTask (View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            Intent intent = new Intent(this, FormuliMenuActivity.class);
            startActivity(intent);
        }
    }

    public void startMusicTask (View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.grigorov.asparuh.probujdane.musicState", "1");
            editor.putString("com.grigorov.asparuh.probujdane.musicStateOld", "1");
            Intent intent = new Intent(this, MusicEntireActivity.class);
            intent.putExtra("com.grigorov.asparuh.probujdane.musicActivitySourceVar", "MainActivity");
            startActivity(intent);
        }
    }

    public void startNotificationsTask (View view) {
        Intent intent = new Intent(this, NotificationsMenuActivity.class);
        startActivity(intent);
    }

    public void startSearchMenuTask (View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            Intent intent = new Intent(this, SearchMenuActivity.class);
            intent.putExtra("com.grigorov.asparuh.probujdane.searchSource", "SEARCH_SOURCE_GLOBAL");
            startActivity(intent);
        }
    }

    public void startOptionsMenuTask (View view) {
        Intent intent = new Intent(this, OptionsMenuActivity.class);
        startActivity(intent);
    }

    public void startNedelniBesediTask(View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            SelectedBesediType = Nedelni_Besedi;
            startBesediListTask(view);
        }
    }

    public void startOOKBesediTask(View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            SelectedBesediType = OOK_Besedi;
            startBesediListTask(view);
        }
    }

    public void startMOKBesediTask(View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            SelectedBesediType = MOK_Besedi;
            startBesediListTask(view);
        }
    }

    public void startUtrinniSlovaBesediTask(View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            SelectedBesediType = Utrinni_Slova_Besedi;
            startBesediListTask(view);
        }
    }

    public void startSyborniBesediTask(View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            SelectedBesediType = Syborni_Besedi;
            startBesediListTask(view);
        }
    }

    private void startBesediListTask(View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            Intent intent = new Intent(this, BesediListActivity.class);
            intent.putExtra("com.grigorov.asparuh.probujdane.SelectedBesediTypeVar", SelectedBesediType);
            startActivity(intent);
        }
    }

    public void startKnigiMenuTask (View view) {
        if (!BesediDatabaseOk) {
            BesediDatabaseOk = checkUpdateBesediDatabase();
        }
        if (BesediDatabaseOk) {
            Intent intent = new Intent(this, KnigiMenuActivity.class);
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

    private boolean checkDatabasesPresent () {
        File file1 = new File(context.getExternalFilesDir(null), besediDatabaseName);
        if (!file1.exists()) {
            return false;
        }
        File file2 = new File(context.getExternalFilesDir(null), formuliDatabaseName);
        if (!file2.exists()) {
            return false;
        }
        File file3 = new File(context.getExternalFilesDir(null), molitviDatabaseName);
        if (!file3.exists()) {
            return false;
        }
        File file4 = new File(context.getExternalFilesDir(null), musicDatabaseName);
        if (!file4.exists()) {
            return false;
        }
        File file5 = new File(context.getExternalFilesDir(null), naukaVyzDatabaseName);
        if (!file5.exists()) {
            return false;
        }
        File file6 = new File(context.getExternalFilesDir(null), zavetDatabaseName);
        return file6.exists();
    }

    private boolean checkUpdateBesediDatabase () {

        if (isExternalStorageWritable()) {
            File file = new File(context.getExternalFilesDir(null), besediDatabaseName);
            if (checkDatabasesPresent()) {
                return true;
            }
            else {
                if (checkDownloadOngoing()) {
                    showErrorMessage(getString(R.string.download_ongoing));
                    return false;
                } else if (unzipOngoing) {
                    showErrorMessage(getString(R.string.unzip_ongoing));
                    return false;
                } else {
                    DialogFragment newFragment = new BesediUpdateDialogFragment();
                    newFragment.show(getSupportFragmentManager(), "besediUpdateDialog");
                    return false;
                }
            }
        } else if (isExternalStorageReadOnly()) {
            File file = new File(context.getExternalFilesDir(null), besediDatabaseName);
            if (checkDatabasesPresent()) {
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
                if (!cursor.isClosed())  {
                    cursor.close();
                }
                return true;
            }
        }
        if (!cursor.isClosed())  {
            cursor.close();
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

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId) {
                showErrorMessage(getString(R.string.download_done));

                unzipOngoing = true;

                // Unzip
                File zipFile = new File(context.getExternalFilesDir(null), besediDatabaseArchiveName);
                try {
                    unzip(zipFile, context.getExternalFilesDir(null));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                unzipOngoing = false;
                if (checkDatabasesPresent()) {
                    showErrorMessage(getString(R.string.database_ready));
                    BesediDatabaseOk = true;
                }

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
                String canonicalPath = file.getCanonicalPath();
                if (!canonicalPath.startsWith(String.valueOf(targetDirectory))) {
                    // SecurityException
                } else {
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

        private final Context context;
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
                fileLength = Integer.valueOf(connection.getContentLength());

                File file = new File(context.getExternalFilesDir(null), besediDatabaseArchiveName);
                String filePath = file.getAbsolutePath();

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
                byte[] data = new byte[4096];
                total = Long.valueOf(0);
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



    private void handlePermissions () {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_INTERNET);

                // MY_PERMISSIONS_REQUEST_INTERNET is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                        MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
            }
        } else {
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        } else {
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WAKE_LOCK)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK},
                        MY_PERMISSIONS_REQUEST_WAKE_LOCK);
            }
        } else {
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_INTERNET) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("com.grigorov.asparuh.probujdane.musicState", "1");
        editor.commit();

        super.onStop();
    }

    private void stopMusicDownloads () {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_PAUSED|
                DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_PENDING);
        Cursor cursor = downloadManager.query( query );
        for (int i = 0; i < cursor.getCount() ; i++)
        {
            cursor.moveToPosition(i);
            String iTitle = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
            String iDescription = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
            String downloadID = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
            if ( iTitle.equals( getString(R.string.music_download_title_string))) {
                downloadManager.remove(Long.parseLong(downloadID));
            }
        }
        if (!cursor.isClosed())  {
            cursor.close();
        }
    }

    private void checkAppUpdate () {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(context);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                // Request the update
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            IMMEDIATE,
                            this,
                            // Include a request code to later monitor this update request.
                            MY_UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(downloadReceiver);

//        // Clear downloads
//        if (downloadManager!=null) {
//            Cursor cursor = null;
//            try {
//                DownloadManager.Query query = new DownloadManager.Query();
//                query.setFilterByStatus(DownloadManager.STATUS_PAUSED |
//                        DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_PENDING);
//                cursor = downloadManager.query(query);
//                for (int i = 0; i < cursor.getCount(); i++) {
//                    cursor.moveToPosition(i);
//                    String downloadID = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
//                    downloadManager.remove(Long.parseLong(downloadID));
//                }
//            } finally {
//                if  (cursor!=null) {
//                    if (!cursor.isClosed()) {
//                        cursor.close();
//                    }
//                }
//            }
//        }
        super.onDestroy();
    }


}
