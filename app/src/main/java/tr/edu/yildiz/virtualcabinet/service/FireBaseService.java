package tr.edu.yildiz.virtualcabinet.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import tr.edu.yildiz.virtualcabinet.BuildConfig;
import tr.edu.yildiz.virtualcabinet.R;
import tr.edu.yildiz.virtualcabinet.models.ActivityModel;
import tr.edu.yildiz.virtualcabinet.models.Clothes;
import tr.edu.yildiz.virtualcabinet.models.Combine;
import tr.edu.yildiz.virtualcabinet.models.Drawer;

public class FireBaseService {
    public static void uploadSingleFile(Uri uri, String folder, String fileName) {
        if (uri == null) return;
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(folder).child(fileName).putFile(uri);
    }

    public static void restoreSingleDatabase(Context context, int mode, String folder, String fileName) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference islandRef = storageReference.child(folder + "/" + fileName);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            try {
                ArrayList<String> infoList = Tools.blobToArrayList(bytes);
                switch (fileName) {
                    case "drawers":
                        for (String info : infoList) {
                            Database.addDrawer(context, mode, new Drawer(info));
                        }
                        break;
                    case "clothes":
                        for (String info : infoList) {
                            Database.addClothes(context, mode, new Clothes(info));
                        }
                        break;
                    case "combines":
                        for (String info : infoList) {
                            Database.addCombine(context, mode, new Combine(info));
                        }
                        break;
                    case "activities":
                        for (String info : infoList) {
                            Database.addActivity(context, mode, new ActivityModel(info));
                        }
                        break;
                    case "paths":
                        for (String imagePath : infoList) {
                            restoreSingleImage(context, "images", getFileName(imagePath));

                        }
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(exception -> System.out.println("Error while downloading " + fileName + " database"));
    }

    public static void deleteFirebaseImages() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference islandRef = storageReference.child("database/paths");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            try {
                ArrayList<String> infoList = Tools.blobToArrayList(bytes);
                for (String path : infoList) {
                    removeFirebaseFile("images", getFileName(path));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            removeFirebaseFile("database", "drawers");
            removeFirebaseFile("database", "clothes");
            removeFirebaseFile("database", "activities");
            removeFirebaseFile("database", "combines");
            removeFirebaseFile("database", "paths");
        }).addOnFailureListener(exception -> System.out.println("Error while getting image file names database"));
    }

    public static void restoreSingleImage(Context context, String folder, String fileName) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference islandRef = storageReference.child(folder + "/" + fileName);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Uri uri = Tools.getImageUri(context, bitmap);
            Tools.storeAndGetPath(context, uri, fileName);
            // Data for "images/island.jpg" is returns, use this as needed
        }).addOnFailureListener(exception -> System.out.println("Error while downloading image"));
    }

    public static void uploadAllImages(Context context, int mode) {
        ArrayList<String> paths = Database.getClothesPathList(context, mode);
        if (paths == null) return;
        for (String path : paths) {
            System.out.println(paths);
            Uri uri = Tools.getUriFromStringPath(path, context);
            uploadSingleFile(uri, "images", getFileName(path));
        }
    }

    public static void signIn(Context context) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword("mdesenturk@gmail.com", "virtual_cabinet_999")
                .addOnSuccessListener(authResult -> Toast.makeText(context, context.getString(R.string.connection_successful), Toast.LENGTH_SHORT).show()).
                addOnFailureListener(e -> Toast.makeText(context, context.getString(R.string.connection_failed), Toast.LENGTH_SHORT).show());
    }

    public static String getFileName(String path) {
        int i = path.length() - 1;
        while (i >= 0 && path.charAt(i) != '/') {
            i--;
        }
        if (i != -1 && i < path.length() - 1) {
            return path.substring(i + 1);
        } else
            return "";
    }

    public static void uploadDataBase(Context context, int mode) {
        try {
            ArrayList<Drawer> allDrawers = Database.getDrawers(context, mode);
            byte[] drawersBlob = Tools.arrayListToBlob(allDrawers);
            uploadSingleFile(generateOutput(context, drawersBlob, "drawers"), "database", "drawers");
            ArrayList<Clothes> allClothes = Database.getAllClothes(context, mode);
            byte[] clothesBlob = Tools.arrayListToBlob(allClothes);
            uploadSingleFile(generateOutput(context, clothesBlob, "clothes"), "database", "clothes");
            ArrayList<Combine> allCombines = Database.getCombines(context, mode);
            byte[] combinesBlob = Tools.arrayListToBlob(allCombines);
            uploadSingleFile(generateOutput(context, combinesBlob, "combines"), "database", "combines");
            ArrayList<ActivityModel> allActivities = Database.getActivities(context, mode);
            byte[] activitiesBlob = Tools.arrayListToBlob(allActivities);
            uploadSingleFile(generateOutput(context, activitiesBlob, "activities"), "database", "activities");
            ArrayList<String> paths = Database.getClothesPathList(context, mode);
            byte[] pathsBlob = Tools.arrayListToBlob(paths);
            uploadSingleFile(generateOutput(context, pathsBlob, "paths"), "database", "paths");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Uri generateOutput(Context context, byte[] object, String fileName) {
        if (object == null) return null;
        try {
            File path = context.getCacheDir();
            File textFile = new File(path, fileName);
            try (FileOutputStream stream = new FileOutputStream(textFile)) {
                stream.write(object);
            }
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", textFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void restore(Context context, int mode) {
        restoreSingleDatabase(context, mode, "database", "drawers");
        restoreSingleDatabase(context, mode, "database", "clothes");
        restoreSingleDatabase(context, mode, "database", "combines");
        restoreSingleDatabase(context, mode, "database", "activities");
        restoreSingleDatabase(context, mode, "database", "paths");
    }

    public static void removeFirebaseFile(String folder, String file) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        StorageReference desertRef = storageReference.child(folder).child(file);

        desertRef.delete().addOnSuccessListener(aVoid -> {
        }).addOnFailureListener(exception -> {
        });
    }

    public static void clearFirebase() {
        deleteFirebaseImages();

    }

}