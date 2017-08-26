package com.cgutman;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;


import com.cgutman.adblib.AdbBase64;
import com.cgutman.adblib.AdbConnection;
import com.cgutman.adblib.AdbCrypto;
import com.cgutman.adblib.AdbStream;


public class AdbTest {
    // This implements the AdbBase64 interface required for AdbCrypto
    public static AdbBase64 getBase64Impl() {
        return new AdbBase64() {
            @Override
            public String encodeToString(byte[] arg0) {
                return Base64.encodeToString(arg0, Base64.DEFAULT);
//				return Base64.encodeBase64String(arg0);
            }
        };
    }

    // This function loads a keypair from the specified files if one exists, and if not,
    // it creates a new keypair and saves it in the specified files
    private static AdbCrypto setupCrypto(String pubKeyFile, String privKeyFile)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        Log.d("big", "file:" + pubKeyFile);
        File pub = new File(pubKeyFile);
        File priv = new File(privKeyFile);
        AdbCrypto c = null;

        // Try to load a key pair from the files
        if (pub.exists() && priv.exists()) {
            try {
                c = AdbCrypto.loadAdbKeyPair(AdbTest.getBase64Impl(), priv, pub);
            } catch (IOException e) {
                // Failed to read from file
                c = null;
            } catch (InvalidKeySpecException e) {
                // Key spec was invalid
                c = null;
            } catch (NoSuchAlgorithmException e) {
                // RSA algorithm was unsupported with the crypo packages available
                c = null;
            }
        }

        if (c == null) {
            // We couldn't load a key, so let's generate a new one
            c = AdbCrypto.generateAdbKeyPair(AdbTest.getBase64Impl());

            // Save it
            c.saveAdbKeyPair(priv, pub);
            System.out.println("Generated new keypair");
        } else {
            System.out.println("Loaded existing keypair");
        }

        return c;
    }

    public static void main(final Context context) {
        Scanner in = new Scanner(System.in);
        AdbConnection adb;
        Socket sock;
        AdbCrypto crypto;

        // Setup the crypto object required for the AdbConnection
        try {
            String dir = context.getCacheDir().getAbsolutePath() + "/";
            crypto = setupCrypto(dir + "pub.key", dir + "priv.key");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Connect the socket to the remote host
        System.out.println("Socket connecting...");
        try {
            sock = new Socket(/*"192.168.1.104"*/"127.0.0.1", 5555);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Socket connected");

        // Construct the AdbConnection object
        try {
            adb = AdbConnection.create(sock, crypto);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Start the application layer connection process
        System.out.println("ADB connecting...");
        Log.e("big", "ADB connecting...");
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,"connecting",Toast.LENGTH_LONG).show();
            }
        });
        try {
            adb.connect();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("big", "IOException:" + e);
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("big", "InterruptedException:" + e);
            return;
        }
        System.out.println("ADB connected");
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,"connected",Toast.LENGTH_LONG).show();
            }
        });
        Log.e("big", "ADB connected");
        // Open the shell stream of ADB
        final AdbStream stream;
        try {
            stream = adb.open("shell:");
//            stream = adb.open("shell monkey -p net.myvst.v2 50");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        // Start the receiving thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stream.isClosed())
                    try {
                        // Print each thing we read from the shell stream
                        System.out.print(new String(stream.read(), "US-ASCII"));
                        Log.d("big", "out:" + new String(stream.read(), "US-ASCII"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
            }
        }).start();

        // We become the sending thread
        /*for (; ; ) {
            try {
                stream.write(in.nextLine() + '\n');
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }*/
        try {
            stream.write("monkey -f /sdcard/monkey.txt 50" + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
