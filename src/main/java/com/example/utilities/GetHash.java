package com.example.utilities;

import sun.nio.ch.FileKey;

import java.io.FileInputStream;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.nio.file.attribute.AclEntry;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.attribute.FileStoreAttributeView;
import java.security.acl.*;
import java.util.List;
import java.util.Map;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

import javax.swing.filechooser.FileSystemView;



public class GetHash {

    static final int KILO_BYTE = 1024;
    static final int MEGA_BYTE = 1024 * KILO_BYTE;
    static final int GIGA_BYTE = 1024 * MEGA_BYTE;

    static final String fileName = "/WS/JweReferenceIds.zip";
    static final Path path = Paths.get( fileName );


    public static void main( String[] args ) throws Exception {

//        String fileName = "/Users/xby099/Downloads/Installs/ideaIU-15.0.3-custom-jdk-bundled.dmg";


        Map myMap = FileSystems.getDefault().provider().readAttributes(path, "*", LinkOption.NOFOLLOW_LINKS);


//******************************************** Getting the list of drives

        File[] files;
        FileSystemView fsv = FileSystemView.getFileSystemView();

// returns pathnames for files and directory
        // files = File.listRoots();

        files = new File("/Volumes").listFiles();


// for each pathname in pathname array
        for(File file:files) {
            FileStore fileStore  = Files.getFileStore( Paths.get( file.toURI() ) );

            Map myMap2 = FileSystems.getDefault().provider().readAttributes( Paths.get( file.toURI() ) , "*", LinkOption.NOFOLLOW_LINKS);



            long totalSpace = fileStore.getTotalSpace() / GIGA_BYTE;
            long usableSpace = fileStore.getUsableSpace() / GIGA_BYTE;
            long usedSpace = (fileStore.getTotalSpace() - fileStore.getUnallocatedSpace()) / GIGA_BYTE;
            String fsName = fileStore.name();
            String fsType = fileStore.type();
            boolean readOnly = fileStore.isReadOnly();

            System.out.println("File Store Details");
            System.out.println("------------------");
            System.out.println("Total Space : " + totalSpace);
            System.out.println("Used Space : " + usedSpace);
            System.out.println("Un-Used Space : " + usableSpace);
            System.out.println("File Store Name : " + fsName);
            System.out.println("File Store Type :" + fsType);
            System.out.println("Is this read only : " + readOnly);


            // prints file and directory paths
            System.out.println("Drive Name: "+ file );
            System.out.println("Description: "+fsv.getSystemTypeDescription( file ));
        }

//******************************************** Getting the list of drives



//*********************************************************************************************************************

        try {
            FileStore fs = Files.getFileStore(path);


            boolean supported = fs.supportsFileAttributeView( BasicFileAttributeView.class );
            System.out.format("%s is  supported: %s%n", BasicFileAttributeView.class.getSimpleName(), supported);
            if (supported) {
                BasicFileAttributeView bfav = Files.getFileAttributeView(path, BasicFileAttributeView.class);


                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);


                String text = attributes.toString();

                System.out.println("Basic File Attribute View : " + text);
            }

            getAttribClass( fs, PosixFileAttributeView.class);


        } catch (IOException ex) {
            ex.printStackTrace();
        }
//*********************************************************************************************************************




        fileStoreExample();

        MessageDigest md = MessageDigest.getInstance("SHA-256");
//        FileInputStream theFile = new FileInputStream( args[0] );
        FileInputStream theFile = new FileInputStream( fileName );

        byte[] fileChunk = new byte[ 100 * MEGA_BYTE ];

        int bytesRead = 0;
        while (( bytesRead = theFile.read( fileChunk )) != -1 ) {
            md.update( fileChunk, 0, bytesRead );
        };

        byte[] rawHash = md.digest();

        String HashAsString = DatatypeConverter.printHexBinary( rawHash );

//        System.out.println("SHA-256 HASH of " + args[0] + " : " +  HashAsString );

        System.out.println("SHA-256 HASH of " + fileName + " : " +  HashAsString );

    }




    public static void fileStoreExample() throws Exception {

        FileSystem fileSystem = FileSystems.getDefault();

        for (FileStore fileStore : fileSystem.getFileStores()) {
            long totalSpace = fileStore.getTotalSpace() / GIGA_BYTE;
            long usableSpace = fileStore.getUsableSpace() / GIGA_BYTE;
            long usedSpace = (fileStore.getTotalSpace() - fileStore.getUnallocatedSpace()) / GIGA_BYTE;
            String fsName = fileStore.name();
            String fsType = fileStore.type();
            boolean readOnly = fileStore.isReadOnly();

            URI dirUri = new URI("file:///WS/");

            System.out.println("File Store Details");
            System.out.println("------------------");
            System.out.println("Total Space : " + totalSpace);
            System.out.println("Used Space : " + usedSpace);
            System.out.println("Un-Used Space : " + usableSpace);
            System.out.println("File Store Name : " + fsName);
            System.out.println("File Store Type :" + fsType);
            System.out.println("Is this read only : " + readOnly);

        }
    }




    public static <T extends FileAttributeView> T getAttribClass(FileStore fs, Class<T> attribClass) {
        boolean supported = fs.supportsFileAttributeView(attribClass);
        System.out.format("%s is  supported: %s%n", attribClass.getSimpleName(),
                supported);
        if (supported) {
            return attribClass.cast( Files.getFileAttributeView( path, attribClass)   );
         }

        return null;

    }





        public static int makeVolumeFingerprint(URI uri) throws IOException {
            FileStore fstore = getFileStore(uri);
            Path path = Paths.get(uri);
            Path volumeRoot = findTopResourceInVolume(fstore, path);
            File fr = volumeRoot.toFile();
            String s1 = FileSystemView.getFileSystemView().getSystemDisplayName(fr);

            boolean b1 = FileSystemView.getFileSystemView().isDrive(fr);

            String s2 = FileSystemView.getFileSystemView()
                    .getSystemTypeDescription(fr);
            System.out.println( "getSystemDisplayName: {}" + s1);
            System.out.println( "getSystemTypeDescription: {}" + s2);

//            if ("/".equals(volumeRoot.toFile().getPath())) {
//                return 0; // fake hash key for root linux filesystem
//            }
            System.out.println("Found volume root: {}" + volumeRoot);
            int hashvalue = 17;
            long oldestFileCreation = -1;
            File[] files = volumeRoot.toFile().listFiles();
            if (files != null) {
                for (File f : files) {
                    // catch windows java bug with paths end in spaces
                    if(!f.getName().trim().equals(f.getName())) continue;
                    hashvalue = 37 * hashvalue + f.getName().hashCode();
                    Path p = FileSystems.getDefault().getPath(f.getPath());
                    BasicFileAttributeView v = FileSystems
                            .getDefault()
                            .provider()
                            .getFileAttributeView(p,
                                    BasicFileAttributeView.class,
                                    LinkOption.NOFOLLOW_LINKS);
                    BasicFileAttributes basic = v.readAttributes();
                    long test = basic.creationTime().toMillis();
                    if (test < oldestFileCreation) {
                        oldestFileCreation = test;
                    }
                }
            }
            String name = volumeRoot.toString();
            hashvalue = 37 * hashvalue + name.hashCode();
            int timestampHash = (int)(oldestFileCreation ^ (oldestFileCreation >>> 32));
            hashvalue = 37 * hashvalue + timestampHash;
            System.out.println( "Found volume root name: {}" + name);
            System.out.println( "Found volume oldest file: {}" + oldestFileCreation);
            return hashvalue;
        }

        public static URI getTopResourceInVolume(URI location) throws IOException {
            Path path = Paths.get(location);
            FileStore fstore = getFileStore(location);
            Path p = findTopResourceInVolume(fstore, path);
            try {
                URI result = new URI(location.getScheme(), location.getUserInfo(),
                        location.getHost(), location.getPort(),
                        p.toUri().getPath(), location.getQuery(),
                        location.getFragment());
                return result;
            } catch (URISyntaxException e) {
                throw new Error(e);
            }
        }

        private static Path findTopResourceInVolume(FileStore fstore, Path path)
                throws IOException {
            Path parent = path.getParent();
            if (parent == null) {
                return path; // root will do for default file system
            } else {
                FileStore parentStore = FileSystems.getDefault().provider()
                        .getFileStore(parent);
                if (fstore.equals(parentStore)) {
                    return findTopResourceInVolume(fstore, parent);
                } else {
                    return path;
                }
            }
        }

        public static FileStore getFileStore(URI uri) throws IOException {
            Path path = Paths.get(uri);
            FileStore fstore = path.getFileSystem().provider().getFileStore(path);
            return fstore;
        }




}
