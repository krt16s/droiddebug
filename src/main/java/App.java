/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import java.lang.*;
import java.io.*;

import uk.gov.nationalarchives.droid.core.*;
import uk.gov.nationalarchives.droid.core.interfaces.*;
import uk.gov.nationalarchives.droid.core.interfaces.resource.FileSystemIdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;

public class App {

    public static void main(String[] args) {
        try {
            long filesize = new File(args[1]).length();
            for (long sl = 2 * filesize; sl > filesize - 1000L; sl = sl - 1000L) {
                doDroidIdent(args[0], args[1], sl);
                System.out.println("---");
            }
            for (long sl = filesize; sl >= -1L; sl = sl - 1000L) {
                doDroidIdent(args[0], args[1], sl);
                System.out.println("---");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(-1);
        }
    }

    public static void doDroidIdent(String sigfile, String idfile, long sizelimit) throws Exception {
        File file = new File(idfile);
        System.out.println("MEMINFO: " + Runtime.getRuntime().maxMemory() + " max, " + Runtime.getRuntime().freeMemory() + " free, " + Runtime.getRuntime().totalMemory() + " total, " + sizelimit + " sizelimit, " + file.length() + " filesize");

        BinarySignatureIdentifier mysig = new BinarySignatureIdentifier();
        mysig.setSignatureFile(sigfile);
        mysig.init();

        IdentificationResultCollection results = null;

        //sizelimit = file.length();
        RequestMetaData metadata = new RequestMetaData(sizelimit, file.lastModified(), file.getName());
        RequestIdentifier identifier = new RequestIdentifier(file.toURI());
        FileSystemIdentificationRequest request = null;

        request = new FileSystemIdentificationRequest(metadata, identifier);
        request.open(file);
        results = mysig.matchBinarySignatures(request);

        if (request != null) {
            System.out.println("INFO: request != null");
            request.close();
        }

        if (results.getResults().size() == 0) {
            System.out.println("INFO: .size() == 0");
            results = mysig.matchExtensions(request, false);
        }

        for (IdentificationResult res : results.getResults()) {
            System.out.println("RESULT: droid-mimetype:" + res.getMimeType() + ", droid-typename:" + res.getName() + ", droid-puid:" + res.getPuid() + ", droid-x-version:" + res.getVersion() + ", droid-x-extid:" + res.getExtId());
        }

        System.out.println("INFO: remove lower hits");
        mysig.removeLowerPriorityHits(results);
        for (IdentificationResult res : results.getResults()) {
            System.out.println("RESULT: droid-mimetype:" + res.getMimeType() + ", droid-typename:" + res.getName() + ", droid-puid:" + res.getPuid() + ", droid-x-version:" + res.getVersion() + ", droid-x-extid:" + res.getExtId());
        }

        System.out.println("MEMINFO: " + Runtime.getRuntime().maxMemory() + " max, " + Runtime.getRuntime().freeMemory() + " free, " + Runtime.getRuntime().totalMemory() + " total, " + sizelimit + " sizelimit, " + file.length() + " filesize");
    }

}
