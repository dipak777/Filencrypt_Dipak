package com.example.filencrypt.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.filencrypt.NotificationHelper;
import com.example.filencrypt.R;
import com.example.filencrypt.Settings;
import com.example.filencrypt.encryption.EncryptionEngine;
import com.example.filencrypt.models.ConcreteFilencryptFile;
import com.example.filencrypt.models.FilencryptFile;
import com.example.filencrypt.models.VirtualFilencryptFile;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FileBrowserActivity {
    private EncryptionEngine encryptionEngine;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.notificationHelper = new NotificationHelper(this);
        UserInterfaceHandler uiHandler = new UserInterfaceHandler(this.actionButton, this.listAdapter, notificationHelper);

        this.encryptionEngine = new EncryptionEngine(uiHandler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            if (hasShareIntent()) this.notificationHelper.toast("Share to Filencrypt cancelled.");
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void initialize() {
        handleShareIntent();
        super.initialize();
    }

    @Override
    protected void onFileClicked(FilencryptFile file) {
        if (file.isEncrypted()) {
            this.notificationHelper.toast("File is encrypted, decrypt it before opening.");
        } else {
            openFileInExternalApp(file);
        }
    }

    @Override
    protected void onActionButtonClicked() {
        if (this.listAdapter.getSelectedFiles().size() == 0) {
            this.notificationHelper.toast("Select files to process.");
            return;
        }
        this.encryptionEngine.work(this.listAdapter.getSelectedFiles(), PasscodeActivity.passcode, true, null);
    }

    @Override
    protected String getRootTitle() {
        return getString(R.string.app_name);
    }

    @Override
    protected int getMenuItemId() {
        return R.id.logout;
    }

    @Override
    public void onSelectionChanged(List<FilencryptFile> selectedFiles) {
        boolean onlyEncrypted = true;

        for (FilencryptFile f : selectedFiles) {
            if (!f.isEncrypted()) {
                onlyEncrypted = false;
                break;
            }
        }
        if (onlyEncrypted && !selectedFiles.isEmpty()) {
            this.actionButton.setImageResource(R.drawable.ic_lock_open_white);
        } else {
            this.actionButton.setImageResource(R.drawable.ic_lock_white);
        }
    }

    private boolean hasShareIntent() {
        Intent intent = getIntent();

        if (intent == null) return false;
        if (!Intent.ACTION_SEND.equals(intent.getAction())) return false;
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) return false;
        if (intent.hasExtra("com.example.filencrypt.Consumed")) return false;

        return true;
    }

    private void handleShareIntent() {
        if (!hasShareIntent()) return;
        if (!ensureShareTargetDirectory()) return;

        this.listAdapter.setCurrentDirectory(Settings.getShareTargetDirectory(this));

        Uri uri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
        final FilencryptFile file = getFileFromUri(uri);
        this.encryptionEngine.work(new ArrayList<FilencryptFile>() {{
                                       add(file);
                                   }}, PasscodeActivity.passcode,
                false, Settings.getShareTargetDirectory(this));

        getIntent().putExtra("com.example.filencrypt.Consumed", true);
    }

    private FilencryptFile getFileFromUri(Uri uri) {
        String[] requestedFields = new String[]{MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE};

        try (Cursor cursor = getContentResolver().query(uri, requestedFields,
                null, null, null)) {

            cursor.moveToFirst();
            String name = cursor.getString(0);
            String path = cursor.getString(1);
            int size = cursor.getInt(2);

            if (path != null) {
                return new ConcreteFilencryptFile(path);
            } else {
                return new VirtualFilencryptFile(name, size, getContentResolver().openInputStream(uri));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean ensureShareTargetDirectory() {
        if (Settings.hasShareTargetDirectory(this)) return true;
        Intent intent = new Intent(this, DirectoryChooserActivity.class);
        startActivityForResult(intent, 0);
        return false;
    }

    private void openFileInExternalApp(FilencryptFile file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setData(file.getUri(this));

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            this.notificationHelper.toast("Found no app capable of opening the selected file.");
        }
    }
}
