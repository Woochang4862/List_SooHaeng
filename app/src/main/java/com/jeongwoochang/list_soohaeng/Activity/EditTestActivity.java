package com.jeongwoochang.list_soohaeng.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.jeongwoochang.list_soohaeng.Model.DBAdapter;
import com.jeongwoochang.list_soohaeng.Model.Schema.Content;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.Model.Schema.TestGroup;
import com.jeongwoochang.list_soohaeng.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

public class EditTestActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_STORAGE_GRANT = 1;
    private Button fileSelectButton, addBtn;
    private FilePickerDialog filePickerDialog;
    private DatePickerDialog datePickerDialog;
    private TextView fileName, testDate;
    private DBAdapter dbAdapter;
    private Spinner group;
    private TextInputEditText title, subject, expectedDate;

    private ArrayList<TestGroup> testGroups;
    private TestGroup currTestGroup;
    private DateTime currTestDate;
    private Content currContent;
    private boolean isUpdate;
    private Test data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);

        dbAdapter = DBAdapter.getInstance();

        dialogInit();

        currContent = new Content();

        group = findViewById(R.id.group_name);
        group.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, loadItems()));
        group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currTestGroup = testGroups.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        title = findViewById(R.id.title);
        subject = findViewById(R.id.subject);
        testDate = findViewById(R.id.test_date);
        testDate.setOnClickListener(v -> datePickerDialog.show());
        expectedDate = findViewById(R.id.expected_date);

        fileName = findViewById(R.id.file_name);
        fileName.setOnClickListener(v -> {
            try {
                //byte array to actual file
                File file = new File(getDataDir(EditTestActivity.this) + "/" + currContent.getFullFileName());
                if (!file.exists())
                    if (isPermissionGrantedForStorage())
                        file.createNewFile();

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bos.write(currContent.getContent());
                bos.flush();
                bos.close();

                //Open file
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                InputStream is = new BufferedInputStream(new ByteArrayInputStream(currContent.getContent()));
                String mimeType = URLConnection.guessContentTypeFromStream(is);
                Uri uri = FileProvider.getUriForFile(EditTestActivity.this, getApplicationContext().getPackageName() + ".fileprovider", file);
                intent.setDataAndType(uri, mimeType);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        fileSelectButton = findViewById(R.id.button);
        fileSelectButton.setOnClickListener(v -> filePickerDialog.show());

        addBtn = findViewById(R.id.add_button);
        addBtn.setOnClickListener(v -> {
            if (title.getText().toString().isEmpty() ||
                    subject.getText().toString().isEmpty() ||
                    currContent.getContent() == null ||
                    currContent.getFileName() == null ||
                    currContent.getExtension() == null ||
                    expectedDate.getText().toString().isEmpty()) {
                return;
            }
            if (isUpdate)
                updateTest();
            else
                addTest();
        });

        Intent intent = getIntent();
        data = (Test) intent.getSerializableExtra("test");
        if (data != null) {
            currTestGroup = getTestGroup(data.getGroup());
            group.setSelection(testGroups.indexOf(currTestGroup));
            title.setText(data.getName());
            subject.setText(data.getSubject());
            testDate.setText(data.getDateString());
            currTestDate = data.getDate();
            expectedDate.setText(data.getExpectedTimeOfString());
            currContent = data.getContent();
            fileName.setText(currContent.getFullFileName());

            isUpdate = true;
            addBtn.setText(getResources().getText(R.string.edit_button_text));
        }
    }

    private TestGroup getTestGroup(int _id) {
        DBAdapter.connect(this);
        TestGroup testGroup = dbAdapter.getTestGroup(_id);
        dbAdapter.close();
        return testGroup;
    }


    private ArrayList<String> loadItems() {
        DBAdapter.connect(this);
        ArrayList<String> result = new ArrayList<>();
        for (TestGroup testGroup : testGroups = dbAdapter.getTestGroup()) {
            result.add(testGroup.getName());
        }
        dbAdapter.close();
        return result;
    }

    private void dialogInit() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        filePickerDialog = new FilePickerDialog(EditTestActivity.this, properties);
        filePickerDialog.setTitle("Select a File");
        filePickerDialog.setDialogSelectionListener(files -> {
            File file = new File(files[0]);
            int size = (int) file.length();
            byte[] tmp = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(tmp, 0, tmp.length);
                buf.close();
                fileName.setText(file.getName());
                currContent = new Content(file.getName().split("\\.")[0], file.getName().split("\\.")[1], tmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        currTestDate = DateTime.now();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                currTestDate = new DateTime()
                        .withYear(year)
                        .withMonthOfYear(month + 1)
                        .withDayOfMonth(dayOfMonth);
                testDate.setText(DateTimeFormat.forPattern("yyyy-MM-dd").print(currTestDate));
            }
        }, currTestDate.getYear(), currTestDate.getMonthOfYear() - 1, currTestDate.getDayOfMonth());
    }

    private void addTest() {
        Intent intent = new Intent();
        intent.putExtra("test", new Test(
                currTestGroup.get_id(),
                title.getText().toString(),
                subject.getText().toString(),
                currTestDate,
                currContent,
                Integer.parseInt(expectedDate.getText().toString()) * 86400000));
        intent.putExtra("isUpdate", isUpdate);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateTest() {
        Intent intent = new Intent();
        intent.putExtra("test", new Test(
                data.get_id(),
                currTestGroup.get_id(),
                title.getText().toString(),
                subject.getText().toString(),
                currTestDate,
                currContent,
                Integer.parseInt(expectedDate.getText().toString()) * 86400000,
                data.getPub_date()
        ));
        intent.putExtra("isUpdate", isUpdate);
        setResult(RESULT_OK, intent);
        finish();
    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (filePickerDialog != null) {   //Show filePickerDialog if the read permission has been granted.
                        filePickerDialog.show();
                    }
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(EditTestActivity.this, "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show();
                }
            }
            case WRITE_EXTERNAL_STORAGE_GRANT: {
                try {
                    File file = new File(getDataDir(EditTestActivity.this) + currContent.getFullFileName());
                    if (!file.exists())
                        if (isPermissionGrantedForStorage())
                            file.createNewFile();
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bos.write(currContent.getContent());
                    bos.flush();
                    bos.close();

                    //Open file
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    InputStream is = new BufferedInputStream(new ByteArrayInputStream(currContent.getContent()));
                    String mimeType = URLConnection.guessContentTypeFromStream(is);
                    intent.setDataAndType(Uri.fromFile(file), mimeType);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getDataDir(Context context) throws Exception {
        return context.getPackageManager()
                .getPackageInfo(context.getPackageName(), 0)
                .applicationInfo.dataDir;
    }

    public boolean isPermissionGrantedForStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_GRANT);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}
