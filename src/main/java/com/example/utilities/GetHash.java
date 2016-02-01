package com.example.utilities;


import java.io.FileInputStream;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
import java.nio.file.Path;
import java.nio.file.Paths;


public class GetHash {

    static final int KILO_BYTE = 1024;
    static final int MEGA_BYTE = 1024 * KILO_BYTE;


    public static void main( String[] args ) throws Exception {


        if ( args.length != 2 ) {
            System.out.println( "Help: GetHash has 2 required arguments. Proper Syntax is as follows.\n\n gethash filename algorithm\n\n Valid algorithms are:\n MD5\n SHA-1\n SHA-224\n SHA-256\n SHA-384\n SHA-512\n");
        }
        else {
            Path path = Paths.get( args[0]);
            MessageDigest md = MessageDigest.getInstance( args[1] );
            FileInputStream theFile = new FileInputStream( path.toFile() );
            byte[] fileChunk = new byte[ 100 * MEGA_BYTE ];
            int bytesRead;
            while (( bytesRead = theFile.read( fileChunk )) != -1 ) {
                md.update( fileChunk, 0, bytesRead );
            }

            byte[] rawHash = md.digest();

            String HashAsString = DatatypeConverter.printHexBinary( rawHash );

            System.out.println( args[1] + " HASH of " + args[0] + " : " +  HashAsString );
        }

    }

}
