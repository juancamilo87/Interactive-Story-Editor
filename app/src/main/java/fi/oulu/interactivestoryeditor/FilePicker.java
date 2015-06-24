package fi.oulu.interactivestoryeditor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FilePicker extends Activity {

    public final static String EXTRA_FILE_PATH = "file_path";
    public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";
    public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";
    private final static String DEFAULT_INITIAL_DIRECTORY = "/sdcard/";

    protected File directory;
    protected ArrayList<File> files;
    protected FilePickerListAdapter adapter;
    protected boolean ShowHiddenFiles = false;
    protected String[] acceptedFileExtensions;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_chooser);

        listView = (ListView) findViewById(R.id.file_list);
        listView.setEmptyView(findViewById(R.id.file_empty_view));

        ImageButton btn = (ImageButton) findViewById(R.id.file_back_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        // Set initial directory
        directory = Environment.getExternalStorageDirectory();

        // Initialize the ArrayList
        files = new ArrayList<File>();

        // Set the ListAdapter
        adapter = new FilePickerListAdapter(this, files);
        listView.setAdapter(adapter);

        // Initialize the extensions array to allow any file extensions
        acceptedFileExtensions = new String[] {};

        // Get intent extras
        if(getIntent().hasExtra(EXTRA_FILE_PATH))
            directory = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));

        if(getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES))
            ShowHiddenFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);

        if(getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {

            ArrayList<String> collection =
                    getIntent().getStringArrayListExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);

            acceptedFileExtensions = (String[])
                    collection.toArray(new String[collection.size()]);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                File newFile = (File)adapter.getItem(i);

                if(newFile.isFile()) {

                    Intent extra = new Intent();
                    extra.putExtra(EXTRA_FILE_PATH, newFile.getAbsolutePath());
                    setResult(RESULT_OK, extra);
                    finish();
                }
                else {

                    directory = newFile;
                    refreshFilesList();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        refreshFilesList();
        super.onResume();
    }

    protected void refreshFilesList() {

        files.clear();
        ExtensionFilenameFilter filter =
                new ExtensionFilenameFilter(acceptedFileExtensions);

        File[] files = directory.listFiles(filter);

        if(files != null && files.length > 0) {

            for(File f : files) {

                if(f.isHidden() && !ShowHiddenFiles) {

                    continue;
                }

                this.files.add(f);
            }

            Collections.sort(this.files, new FileComparator());
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {

        if(directory.getParentFile() != null) {

            directory = directory.getParentFile();
            refreshFilesList();
            return;
        }
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private class FilePickerListAdapter extends ArrayAdapter<File> {

        private List<File> mObjects;

        public FilePickerListAdapter(Context context, List<File> objects) {

            super(context, R.layout.list_item, objects);
            mObjects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = null;

            if(convertView == null) {

                LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                row = inflater.inflate(R.layout.list_item, parent, false);
            }
            else
                row = convertView;

            File object = mObjects.get(position);

            ImageView imageView = (ImageView)row.findViewById(R.id.file_picker_image);
            TextView textView = (TextView)row.findViewById(R.id.file_picker_text);
            textView.setSingleLine(true);
            textView.setText(object.getName());
            if(object.isFile())
            {
                String fileName = object.getName();
                String fileNameArray[] = fileName.split("\\.");
                String extension = fileNameArray[fileNameArray.length-1];
                switch (extension)
                {
                    case "aif":
                    case "iff":
                    case "m3u":
                    case "m4a":
                    case "mid":
                    case "mp3":
                    case "mpa":
                    case "ra":
                    case "wav":
                    case "wma":
                        imageView.setImageResource(R.drawable.audio);
                        break;
                    case "3g2":
                    case "3gp":
                    case "asf":
                    case "asx":
                    case "avi":
                    case "flv":
                    case "mov":
                    case "mp4":
                    case "mpg":
                    case "rm":
                    case "swf":
                    case "vob":
                    case "wmv":
                        imageView.setImageResource(R.drawable.video);
                        break;
                    case "bmp":
                    case "gif":
                    case "jpg":
                    case "jpef":
                    case "png":
                    case "psd":
                    case "pspimage":
                    case "thm":
                    case "tif":
                    case "yuv":
                        imageView.setImageResource(R.drawable.image);
                        break;
                    default:
                        imageView.setImageResource(R.drawable.file);
                        break;
                }
            }
            else
                imageView.setImageResource(R.drawable.folder);

            return row;
        }
    }

    private class FileComparator implements Comparator<File> {

        public int compare(File f1, File f2) {

            if(f1 == f2)
                return 0;

            if(f1.isDirectory() && f2.isFile())
                // Show directories above files
                return -1;

            if(f1.isFile() && f2.isDirectory())
                // Show files below directories
                return 1;

            // Sort the directories alphabetically
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }

    private class ExtensionFilenameFilter implements FilenameFilter {

        private String[] Extensions;

        public ExtensionFilenameFilter(String[] extensions) {

            super();
            Extensions = extensions;
        }

        public boolean accept(File dir, String filename) {

            if(new File(dir, filename).isDirectory()) {

                // Accept all directory names
                return true;
            }

            if(Extensions != null && Extensions.length > 0) {

                for(int i = 0; i < Extensions.length; i++) {

                    if(filename.endsWith(Extensions[i])) {

                        // The filename ends with the extension
                        return true;
                    }
                }
                // The filename did not match any of the extensions
                return false;
            }
            // No extensions has been set. Accept all file extensions.
            return true;
        }
    }
}