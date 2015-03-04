package edu.illinois.i3.emop.apps.hocr2text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Missing required argument");
            System.exit(1);
        }

        Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
        HOCR2Text hocr2Text = new HOCR2Text();
        String text = hocr2Text.getText(reader);
        System.out.println(text);
    }

}
